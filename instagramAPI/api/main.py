from flask import Flask, jsonify, request, abort
from instagrapi import Client
from config import *
import requests
from random import randint
import json
import re
from converthours import ConvertHours
# instanciar o app flask e fazendo login no instagram

API = Flask(__name__)
grams = []

for session in CONTAS:

    gram = Client()
    gram.login_by_sessionid(session)
    grams.append(gram)

# rota de pesquisa de perfil

@API.route("/search")

def search():
    """rota da api para encotrar perfis.

    parametros:
    ----------
    query: str


    retorno
    --------

    dict com lista de usuarios
    """
    try:

        query = request.args.get("query").replace("@", "")
        try: limit = int(request.args.get("limit"))
        except: limit = False
        conut = 0
        cookie = CONTAS[randint(0, len(CONTAS) -1)]
        response = requests.get(f"{API_INSTA_URL}{query}", headers=HEADERS , cookies={"sessionid":cookie})
        result = {"users":[]}

        if response.status_code == 200 :

            for user in json.loads(response.text)["users"]:
                
                if type(limit) == int and conut == limit:

                    break;


                result["users"].append(

                {"name":user["user"]['full_name'],
                "username":user["user"]["username"],
                "is_private":user["user"]["is_private"],
                "pk_id":user["user"]["pk_id"],
                "profile_pic_url":user["user"]["profile_pic_url"],
                "profile_url":f"https://instagram.com/{user['user']['username']}?igshid={user['user']['is_private']}"
                
                })
                conut += 1

        if len(result["users"]) == 0:

            return abort(500)
                
        else:
            return result,200

    except ValueError as erro:

        print(erro)
        return abort(500)

        

# rota de publicações

@API.route("/post")

def post():

    """baixar publicações do instagram.
    
    parametros:
    ----------
    post_url: str (link da publicação do insatgram)

    retorno:
    --------

    dict com link para download de foto(s) ou video(s) do instagram.
    """

    try:

        post_url = request.args.get("post_url")
        posts = {'posts':[], 'subtitle':'', "total_fotos":0 , "total_videos":0}
        gram = grams[randint(0, len(grams) -1)]
        media_pk = gram.media_pk_from_url(post_url)
        posts_obj = gram.media_info(media_pk)
        posts['subtitle'] = posts_obj.caption_text # legenda

        for publication in posts_obj.resources:

            # publicação com varias medias

            if publication.media_type == 1:

                posts["posts"].append({"url":publication.thumbnail_url, "type":"foto"})
                posts["total_fotos"] += 1
            else:

                posts["posts"].append({"url": publication.video_url, "type":"video"})
                posts["total_videos"] += 1

        # publicação com media unica

        if len(posts["posts"]) == 0:

            if posts_obj.media_type == 1:

                posts["posts"].append({"url":posts_obj.thumbnail_url, "type":"foto"})
                posts["total_fotos"] += 1

            else:

                posts["posts"].append({"url": posts_obj.video_url, "type":"video"})
                posts["total_videos"] += 1

        if len(posts["posts"]) == 0:
            return abort(500)
        
        else:

            return posts

    except:

        pass

# rota para videos do reel

@API.route("/reels")

def reels():
    """baixar videos do reels.
    

    parametros:
    -----------

    video_url: str (link do video do instagram)

    retorno:
    -------

    dict com o link para download do video e a legenda da publicação.
    """

    try:

        video_url = request.args.get("video_url")
        gram = grams[randint(0, len(grams) -1)]
        fetch_id = gram.media_pk_from_url(video_url)
        info = gram.media_info(fetch_id).dict()
        return jsonify(video_url=info['video_url'], caption_text=info["caption_text"]),200
        


    except:

        abort(500)


# rota para stories

@API.route("/stories")

def stories():
    """ pegar stories do instagram (anonimô)
    

    parametros:
    -----------

    username: str (nome de usuario do perfil do instagram)
    porfile_url: str (link do do perfil do instagram)

    retorno:
    -------

    dict com o link para download de video(s) e foto(s) dos stories .
    """
    try:

        try:

            username = request.args["username"].replace('@', '')

        except:
            
            regex = "instagram.com/(.*)[/?].*"
            resp = re.search (regex, request.args.get("profile_url"))
            username = resp.group(1).replace('@', '')
            
        stories_ = {"stories":[]}
        gram = grams[randint(0, len(grams) -1)]
        pk_id = gram.user_id_from_username(username)
        results = gram.user_stories(pk_id)

        for storie in results:

            if storie.media_type == 1:

                stories_["stories"].append({"type":"foto", "url":storie.thumbnail_url, "posted_to":ConvertHours(storie.taken_at)})
            
            else:

                stories_["stories"].append({"type":"video", "url":storie.video_url, "posted_to":ConvertHours(storie.taken_at)})
        
        if len(stories_["stories"]) == 0:
            return abort(500)
        
        else:
            
            return stories_

    except Exception as e:

        print(e)
        abort(500)

# iniciar a API

if __name__ == "__main__":

    API.run(port=PORT , debug=False)