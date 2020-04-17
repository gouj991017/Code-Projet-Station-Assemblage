
PROJECT TITLE: Code de gestion d'une application client de la station d'assemblage
DESCRIPTION: Application Node-Red servant à passer des commandes qui seront envoyées dans une base de données InfluxDB. Ces données seront ensuite envoyées via une communication sans-fil
MQTT vers le code de la base sous un format JSON.

Le logiciel Node-Red:
-Permet, entre autre, la création d'application web.
-Possède un intellisense de base permettant de repérer les erreurs.
-Pour lancer l'application, il suffit de cliquer sur «Deploy» (bouton rouge en haut à droite de la fenêtre).
-Pour exécuter le programme sous Windows ou sous Linux (Raspberry Pi), il faut les extensions:
	-node-red-contrib-influxdb
	-node-red-dashboard

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
VERSION: V3
DATE: 17-04-20
AUTHORS: Jérémy Goulet, Guillaume Beaudoin

