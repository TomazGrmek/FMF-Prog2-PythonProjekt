import requests, re, json, itertools


months = {'Jan' : 1,'Feb' : 2,'Mar' : 3,'Apr' : 4,'May' : 5,'Jun' : 6,'Jul' : 7,'Aug' : 8,'Sep' : 9, 'Oct' : 10,'Nov' : 11,'Dec' : 12}

class APIException(Exception):
    def __init__(self):
      Exception.__init__(self)

class DateException(Exception):
    def __init__(self):
        Exception.__init__(self)


class Movie:
    '''Razred za film, hrani vse potrebne podatke o filmu: id, igralci, datum izdaje, igralci, direktor, zaslužek'''
    def __init__(self, title = None, movie_id = None):
        if title is not None:
            movie = requests.get("http://www.omdbapi.com/?t="+title+"&apikey=2ab2d5e2").json()
            self.imdbid = movie["imdbID"]

            # za vhodni film potrebujemo tudi imdbidje igralcev in direktorjev, dobimo jih z strani filma
            m = re.search(r'<script type="application/ld\+json">([^<]+)</script>',
                          requests.get("https://www.imdb.com/title/" + self.imdbid + "/").text, re.S)
            res = json.loads(m.group(1))
            self.actors = []
            self.directors = []
            if res.get("director") is not None:
                if isinstance(res["director"], list):
                    for el in res["director"]:
                        self.directors.append(el["url"].split("/")[2])
                else:
                    self.directors.append(res["director"]["url"].split("/")[2])

            for el in res["actor"]:
                self.actors.append(el["url"].split("/")[2])

        elif movie_id is not None:
            movie = requests.get("http://www.omdbapi.com/?i="+movie_id+"&apikey=2ab2d5e2").json()
            if movie["Response"] == "False":
                raise APIException()
        else:
            raise Exception("Napačni vhodni podatki")

        if movie["Response"] == "False":
            raise Exception(movie["Error"])


        if movie["Released"] != "N/A": #datum spremenimo v format YYYY-MM-DD
            self.releasedate = movie["Released"].split(" ")[::-1]
            self.releasedate[1] = months[self.releasedate[1]]
            self.releasedate = "-".join(map(str,self.releasedate))
        else:
            raise DateException()

        self.title = movie["Title"]
        self.cast_names = movie["Actors"].split(",") + movie["Director"].split(",")
        self.imdbid = movie["imdbID"]
        self.genres = movie["Genre"].split(",")

            
        if movie["BoxOffice"] == "N/A":
            self.boxoffice = 0
        else:
            self.boxoffice = ''.join(d for d in movie["BoxOffice"] if d.isdigit()) #zaslužek spremenimo v številko
        

    def weight(self, movie):
        '''utež, kako podoben je nek film z podanim, izemrimo z tem, koliko enakih igralcev imata in razliko
        med letnicama izdaje filmov'''
        st = len(set(movie.cast_names) & set(self.cast_names))
        if self.releasedate is not None and movie.releasedate is not None:
            year_diff = int(movie.releasedate.split("-")[0]) - int(self.releasedate.split("-")[0])
        else:
            year_diff = 0
        return max(5*st - year_diff, 0)
    


title = input("Vpiši ime filma: ")
m = Movie(title)


movie_ids = set()
combi = []
for i in range(1, len(m.actors+m.directors)+1): #dobimo vse možne kombinacije direktorjev in igralcev
    combi.extend(list(itertools.combinations(m.actors+m.directors, i)))

for el in reversed(combi): #za vsako kombinacijo izvedemo search in dodamo najdene filme v množico
    if len(movie_ids) >= 100:
        break
    search = requests.get("https://www.imdb.com/search/title?title_type=feature,tv_movie&release_date=,"+str(m.releasedate)+"&genres="+ (",".join(m.genres)) + "&role="+ (",".join(el))+"&count=100").text
    res = re.findall(r'data-tconst="[^"]+"' , search , re.S)
    for entry in res:
        new_id = entry.split('"')[1]
        movie_ids.add(new_id)

if m.imdbid in movie_ids:
    movie_ids.remove(m.imdbid)

st = 0
im = 1
for el in movie_ids: # za vsak najden film izračunamo utež inzaslužek dodamo k vsoti
    try:
        movie_el = Movie(movie_id = el)
        weig = float(movie_el.weight(m))
        st += weig * float(movie_el.boxoffice)
        im += weig
    except APIException:
        print("OMDBApi ne najde filma " + el)
        continue
    except DateException:
        print("Neveljaven release date za " + el)
        continue
if im > 1:
    im -= 1
print(st/im)
    
