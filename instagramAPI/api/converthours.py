import re
from datetime import datetime
import pytz



def ConvertHours(date):
    """ * este modulo converte o datetime do instagram para string e depois descobre
        * o tempo que passou deste de que o stories foi publicado com auxilio de regex. """
        
    today = str(datetime.now())
    stories_date = str(date)

    # postado hoje

    if date != "":

        hours_stories = re.search("\d{2}:\d{2}:\d{2}\+\d{2}:\d{2}", stories_date ).group(0).split(":")[0]
        min_stories = re.search("\d{2}:\d{2}:\d{2}\+\d{2}:\d{2}", stories_date ).group(0).split(":")[1]
        
        gmt_hours = re.search("\d{2}:\d{2}:\d{2}", str(datetime.now(tz=pytz.timezone("GMT"))) ).group(0).split(":")[0]
        local_min = re.search("\d{2}:\d{2}:\d{2}", today ).group(0).split(":")[1]
        
        # postado há algumas horas

        if int(gmt_hours) != int(hours_stories):

            return f"{int(gmt_hours) - int(hours_stories)  } horas ".replace("-", "").replace("1 horas", "1 hora")

        # postado há alguns minutos

        else:

            return f"{int(local_min) - int(min_stories) } minutos ".replace("-", "").replace("1 minutos", "1 minuto")

    else:

        return "indisponivel"
