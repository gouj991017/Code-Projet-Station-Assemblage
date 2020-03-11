
TITLE: Première version du code de la base
DESCRIPTION: Il s'agit de la version de code servant de fondations au code sur NetBeans. Il a servi à tester la communication MQTT entre les deux Raspberry Pi ainsi que la logique entourant le fonctionnement de la base de la station d'assemblage.
Le code a été programmé en Java.

Emplacement et nom du fichier contenant le corps du code:
-Code_BlueJ\AppBase.java

Le logiciel BlueJ:
-Est disponible et utilisable automatiquement dans un environnement Raspbian sur Raspberry Pi.
-Ne contient pas de code nécessaire à la création et à la gestion de l'interface usager de la base.
-Ne possède pas d'intellisense très poussé permettant de repérer les erreurs plus facilement.
-Pour compiler, il suffit d'appuyer sur l'option «compile» en haut à gauche de la fenêtre.
-On ne peut placer des points d'arrêt que si le code est compilé et lorsque celui-ci est recompilé, les points d'arrêt peuvent disparaitre.
-Pour exécuter le programme, il faut les librairies:
	-json-20180813.jar
	-mqtt-client-1.7-uber.jar
	-mysql.jar
	-mysql-connector.jar

MATERIAL NEEDED:
-(2x) RaspBerry Pi Zéro W
-(1x) Lunette VUFINE (lunette et/ou casque pour l'opérateur)
VERSION: V2.8
DATE: 15-02-20
AUTHORS: Jérémy Goulet
