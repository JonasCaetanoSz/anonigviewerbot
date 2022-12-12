
# anonigviewerbot
veja stories do instagram de forma anonima!




## Instalação

Instale todas as dependecias da API usando pip:

```bash
cd instagramAPI/api && pip install -r requirements.txt
```

## configurando a API

no arquivo config.py, defina na constante **CONTAS** a lista de sessionID de todas as contas que vai usar. recomendo no minimo 5 mais uma já é suficiente!  


## Como conseguir o sessionID?


veja aqui <a href='https://valvepress.com/how-to-get-instagram-session-cookie'/> como encontrar o sessionID do instagram </a>. 

## iniciando a api

para iniciar a API digite e rode o comando:

```bash
python main.py
```

um servidor local em <a href="http://127.0.0.1:1238/" >local rost </a> irá abrir na porta **1238**. você pode trocar a porta no arquivo de configuração configs.py.

## configurando o bot

um arquivo na pasta principal **configs.json** guarda dados importantes para o bot, como por exemplo o token do seu bot no telegram. 
então lá você dever colocar o token do telegram e caso tenha mudado a porta da API defina a nova porta no campo **api_url** .

feito todos os passos anteriores você pode copilar o codigo fonte do bot e inicia ele. a classe principal é App.java.
## Funcionalidades

- Assistir stories anonimo
- Baixar video do reels
- Baixar publicações
- buscar perfil


## Licença

distribuido sobre [apache](https://www.apache.org/licenses/LICENSE-2.0) licença

