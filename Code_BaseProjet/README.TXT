
TITLE: Code de gestion pour la base d'une station d'assemblage
DESCRIPTION: S'occupe d'envoyer les différentes informations liées aux étapes de montage d'un produit à une lunette de réalité augmenté via une communnication MQTT entre deux RaspBerry PiZéroW et affiche l'étape actuel à laquelle l'opérateur est arrivé ainsi qu'une copie des étapes envoyées à la lunette sur une interface usager. Les bacs contenant les pièces nécessaires à la réalisation du produit sont mis en évidence selon l'étape de montage à laquelle l'opérateur se trouve, cette information sera également affichée sur l'interface usager.
Des appareils Phidgets (capteurs infrarouges, capteur de poids, ports digital, etc...) servent à ajouter des fonctionnalitées supplémentaires à la base ainsi qu'à fournir un plus grand contrôle de celle-ci (manipulations plus précises lors de l'exécution du programme).

FOLDER OF PROJECT:
-Le dossier «Code_BlueJ» contient la première version fonctionnel du code de la base fait à partir de l'IDE BlueJ. Le code ne permet pas de générer d'interface. De plus amples informations sont disponibles à l'intérieur du dossier.
-Le dossier «Code_NetBeans» contient la version la plus avancée du code de la base fait à partir de l'IDE NetBeans. Le code permet la génération d'une interface usager. De plus amples informations sont disponibles à l'intérieur du dossier.
-Le dossier «Terminal_base» contient la version automatisée du code de la base fait à partir de l'IDE NetBeans. Le code permet la génération d'une interface usager plus complète que celle du dossier «Code_NetBeans» et offre des fonctionnalitées plus poussées. De plus amples informations sont disponibles à l'intérieur du dossier.
-Le dossier «Générateur d'instructions» contient la première version fonctionnelle du code du générateur d'instructions fait à partir de l'IDE NetBeans. Le code permet la génération d'instructions personnalisées pour le code Terminal_base_V2.0 . De plus amples informations sont disponibles à l'intérieur du dossier.
-Le dossier «Terminal_base_V2.0» contient une version du code de la base (Terminal_base) adaptée aux instructions du code générateur d'instructions fait à partit de l'IDE Netbeans. De plus amples informations sont disponibles à l'intérieur du dossier.
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
VERSION: V3.0
DATE: 20-04-20
AUTHORS: Jérémy Goulet, Guillaume Beaudoin
