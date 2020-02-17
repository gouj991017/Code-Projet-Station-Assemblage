TITLE: Code du casque de réalité augmentée possédant une interface usager.
DESCRIPTION: Il s'agit du programme présentant les instruction pour l'opérateur de la station d'assemblage. Les instructions sont présentées une à la fois. Il est possible de choisir la couleur du texte dans l'onglet Edit->Couleur du texte et de changer la source des messages dans Edit->Adresse IP:. Les messages sont reçus par MQTT sur le topic: /scal/scal_reponse_requete .

Pour executer le programme sans IDE: $java -jar [.../dist/Terminal_Casque.jar].

Systèmes: developpé sur: Windows 10 Famille  deployé sur: raspbian buster full 2019-09-26
Language: Java
IDE: Netbeans
Le logiciel NetBeans:
-Permet la création d'interface.
-Possède un intellisense de base permettant de repérer les erreurs plus facilement.
-Pour compiler, il suffit d'appuyer sur le bouton "Clean build" ou "build" ou d'exécuter le programme en mode de dépannage (dans les options du menus en haut de la fenêtre).
-Le programme s'exécute normalement en appuyant sur la flèche verte près de l'option de dépannage dans les options du menus en haut de la fenêtre.
-Pour exécuter le programme, il faut les librairies:
	-json-20180813.jar
	-mqtt-client-1.7-uber.jar
	-mysql.jar
	-mysql-connector.jar
MATERIAL NEEDED:
-(1x) RaspBerry Pi Zéro W
-(1x) Lunette VUFINE (lunette et/ou casque pour l'opérateur)
VERSION: V2.0
DATE: 16-02-20
AUTHOR: Guillaume Beaudoin
