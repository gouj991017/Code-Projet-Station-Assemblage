
TITLE: Code de gestion pour la base d'une station d'assemblage
DESCRIPTION: S'occupe d'envoyer les différentes informations liées aux étapes de montage d'un produit à une lunette de réalité augmenté via une communnication MQTT entre deux RaspBerry PiZéroW et affiche l'étape actuel à laquelle l'opérateur est arrivé ainsi qu'une copie des étapes envoyées à la lunette sur une interface usager. Les bacs contenant les pièces nécessaires à la réalisation du produit sont mis en évidence selon l'étape de montage à laquelle l'opérateur se trouve, cette information sera également affiché sur l'interface usager.
Des appareils Phidgets (capteurs infrarouges, capteur de poids, ports digital, etc...) servent à ajouter des fonctionnalitées supplémentaires à la base ainsi qu'à fournir un plus grand contrôle de celle-ci (manipulations plus précises lors de l'exécution du programme).

FOLDER OF PROJECT:
-Le Dossier «Code_BlueJ» contient la première version fonctionnel du code de la base fait à partir de l'IDE BlueJ. Le code ne permet pas de générer d'interface. De plus amples informations sont disponibles à l'intérieur du dossier.
-Le Dossier «Code_NetBeans» contient la version la plus avancée du code de la base fait à partir de l'IDE NetBeans. Le code permet la génération d'une interface usager. De plus amples informations sont disponibles à l'intérieur du dossier.
MATERIAL NEEDED:
-(2x) RaspBerry Pi Zéro W (OU 1x Raspberry Pi Zéro W et un ordinateur)
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
VERSION: V2.8
DATE: 15-02-20
AUTHORS: Jérémy Goulet
