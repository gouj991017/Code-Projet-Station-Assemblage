TITLE: Code de la base possédant un interface usager plus complet que "Code_NetBeans".
DESCRIPTION: S'occupe des fonctions expliquées dans le «README.TXT» précédant.
Il s'agit d'une version de code spécifique à la station d'assemblage. Hors de ce contexte, ce programme peut être mésadapté.
Contient le code nécessaire à la création et à la gestion de l'interface usager de la base. Ceci comprend une fenêtre principale
rapportant les messages d'étapes et le menu d'actions et quelques fenêtres secondaires présentant individuellement la commande à
assembler, l'image de l'étape, l'état des bacs et le nombre de pièces dans les bacs.
Le code a été programmé en Java.

Emplacement et nom du fichier contenant le corps du code:
-Terminal_Base\src\terminal_base\UI_Base.java
Nom des classes et packages nécéssaires (Terminal_Base\src\terminal_base)
-Box.java
-ObjCommande.java
-images (package d'images)

Le logiciel NetBeans:
-Permet la création d'interface.
-Possède un intellisense de base permettant de repérer les erreurs plus facilement.
-Pour compiler, il suffit de sauvegarder le code ou d'exécuter le programme en mode de dépannage (dans les options du menus en haut de la fenêtre).
-Le programme s'exécute normalement en appuyant sur la flèche verte près de l'option de dépannage dans les options du menus en haut de la fenêtre.
-Pour exécuter le programme sous Windows, il faut les librairies:
	-json-20180813.jar
	-mqtt-client-1.7-uber.jar
	-phidget22.jar
	-JDK 14
-Pour exécuter le programme sous Linux (Raspberry Pi), il faut les « drivers » de Phidgets compatible avec Linux:
	-libphidget22 (Obligatoire)
	-libphidget22extra (librairie obligatoire si l'on veut ajouter les options de serveurs réseau et d'outils administrateurs)
	-phidget22networkserver (Permet l'utilisation d'appareils Phidgets sur ton réseau)
	-libphidget22java (libraries java pour Phidget22)
MATERIAL NEEDED:
-(1x) RaspBerry Pi Zéro W et (1x) RaspBerry Pi 3 / (OU 1x Raspberry Pi Zéro W et un ordinateur)
-Des appareils Phidgets (capteurs infrarouges, capteur de poids, ports digital, etc...):
	- 1 Wireless VINT Hub
	- 2 Power Supply-12VDC 2A
	- 2 Micro Load Cell (0-780g)	
	- 2 Micro Load Cell (0-5kg)
	- 2 Wheatstone Bridge Phidget
	- 6 Phidget Cable 120cm
	- 2 IR Distance Adapter
	- 1 Isolated 12-bit Voltage Output Phidget
	- 1 8x Voltage Input Phidget
-(1x) Lunette VUFINE (lunette et/ou casque pour l'opérateur)
VERSION: V1.0
DATE: 20-04-20
AUTHORS: Guillaume Beaudoin
