# FriendsLocation

## Запуск
Необходимо в local properties, создаваемых при открытие проекта в android studio дописать строчку (иначе будет ошибка сборки проекта):

```MAPS_API_KEY= $Your generated key$```

А также в sdk tools добавить, поставить галочку напротив "Gogle Play Services"

Этого должно хватить, но если хотите узать подробнее, вот ссылка : https://developers.google.com/maps/documentation/android-sdk/map-with-marker?hl=ru

FireBase вроде должен подключится при обычной синхронизации, предлогаемой при открытие gradle файлика.
