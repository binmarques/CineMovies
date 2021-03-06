# CineMovies
Projeto desafio que tem por objetivo consumir a API Rest The Movie Database fazendo uso do padrão de arquitetura MVP e das bibliotecas Retrofit, RxJava para programação reativa, features do Java 8 como expressões lambda, paginação e scroll infinito e mais...

# Como funciona o fonte
Para utilização e funcionamento do app é necessário uma chave de API, para conseguir a chave é necessário a criação de uma conta em https://www.themoviedb.org/.
Com a chave em mãos basta baixar ou clonar o app e no arquivo build.gradle a nível de módulo coloque a chave de API no local com o texto: "YOUR_API_KEY_HERE".

# Telas

![ScreenShot](https://raw.github.com/binmarques/CineMovies/master/art/movies.png)
![ScreenShot](https://raw.github.com/binmarques/CineMovies/master/art/movie_details.png)
![ScreenShot](https://raw.github.com/binmarques/CineMovies/master/art/all_movies.png)
![Movies Gif](art/my_gif.gif)

# Bibliotecas utilizadas 

* AppCompat - biblioteca de suporte a versões mais antigas do Android

* MaterialComponents - biblioteca de design da google para componentes do material design 

* CardView - biblioteca para exibição de cards

* RecyclerView - biblioteca para uso do recyclerview para as listas presentes no app

* ConstraintLayout - biblioteca para uso de layouts mais ajustáveis e de melhor desempenho

* ButterKnife - biblioteca responsável pelo bind das views com a classe, diminuindo o boilerplate code

* Retrofit - biblioteca cliente para as chamadas HTTP no app

* RXJava - biblioteca de programação reativa no app

* Glide - biblioteca de manipulação e armazenamento em cash das imagens presentes no app

* EventBus - biblioteca usada para simplificar a comunicação entre fragments e activities e manter o estado dos objetos quando o dispositivo é rotacionado

# Referências 

* AppCompat - https://github.com/codepath/android_guides/wiki/Migrating-to-the-AppCompat-Library

* MaterialComponents - https://github.com/material-components

* ButterKnife - http://jakewharton.github.io/butterknife/

* ButterKnife - https://github.com/JakeWharton/butterknife

* Retrofit - https://github.com/square/retrofit

* RXJava - https://github.com/ReactiveX/RxJava

* Glide - https://github.com/bumptech/glide

* EventBus - https://github.com/greenrobot/EventBus
