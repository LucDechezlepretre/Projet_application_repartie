#!/bin/bash

echo "Création de l'annuaire pour les services"
rmiregistry -J-Djava.class.path=.:./Proxy/:./ServiceDonneesBloquees/:./Restaurant/service/ &

#Compilation des fichiers interface

echo "Compilation des interfaces"
javac ./ServiceDonneesBloquees/*.java
javac -cp .:ojdbc10.jar:json-20131018.jar ./Restaurant/service/*.java

#Déplacement des fichiers nécessaire dans chaque dossier

cp ./Restaurant/service/ServiceBD.class ./Proxy/
cp ./ServiceDonneesBloquees/ServiceDonneesBloquees.class ./Proxy/

#Compilation du reste des fichiers java

javac -cp .:json-20131018.jar:./Proxy/ ./Proxy/*.java

echo "Compilation du code du serveur et des proxy"

#Lancement des services
echo "Lancement des services"
java -cp .:json-20131018.jar:ojdbc10.jar:./Restaurant/service/ LancerService &
java -cp .:json-20131018.jar:./ServiceDonneesBloquees/ ServiceDB &

#Lancement du serveur http

echo "Lancement du serveur HTTP"
java -cp .:json-20131018.jar:./Proxy HttpRequete localhost

