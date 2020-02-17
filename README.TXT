
PROJECT TITLE: Code de gestion d'une station d'assemblage
DESCRIPTION: Projet visant à créer une station d'assemblage à installer sur une chaîne de montage. La station se compose en deux parties: la lunette/casque et la base.
La BASE s'occupe de recevoir, traiter et renvoyer des données au reste de la station et des appareils qui lui sont attachés. La base reçoit la commande d'assemblage d'un produit (par exemple, deux portes-cellulaires de couleur rouge avec deux porte-crayon) qu'elle décortique pour faciliter le traitement des données et créer des instructions d'assemblages personnalisées au produit demandé. Exemple d'instruction: Étape #1 - Prenez la base de type 1 de couleur rouge dans le bac #4.
Dans cet exemple, le programme demande à l'opérateur (la personne travaillant à la station) de prendre une pièce dans un bac bien précis et ce bac, lors de l'affichage de l'instruction, se verra indiqué par une lumière verte. Ainsi, à chaque étape, le bac désigné sera mis en évidence. Si l'opérateur met sa main dans le mauvais bac, alors la lumière attaché à ce bac allumera en rouge et une nouvelle instruction avertira l'opérateur qu'il n'a pas pris le bon bac et lui désignera une fois de plus le bon bac à prendre. Le système changera automatiquement d'étape s'il détecte que la pièce désignée a été retiré du bac.
Toutes les informations relatives aux instructions d'assemblages, à la détection des bons et des mauvais bacs, aux différentes informations du produit à assembler et même au poids des bacs sont affichées sur un interface près de l'opérateur pour l'assister dans l'assemblage du produit. Il est également possible, sur l'interface, de passer manuellement d'une étape à l'autre dans les cas de dépannage.
La LUNETTE ou CASQUE est composé d'un petit projecteur affichant diverses informations directement dans l'oeil de l'opérateur. La lunette sert, en fait, à afficher les instructions d'assemblages envoyées via une communication sans-fil MQTT par la base sous un format JSON.

FOLDER OF PROJECT:
-Le Dossier «Code_BaseProjet» contient deux versions fonctionnelles du code gérant la base de la station d'assemblage. Le fichier README du dossier vous donneras plus de détails sur les distinctions entre les deux versions.
-Le Dossier «Terminal_Casque» contient la version fonctionnel du code du casque dans lequel sera affiché les instructions de la station d'assemblage. De plus amples informations sont disponibles à l'intérieur du dossier.
-Le Dossier «Tests_Phidget» contient des codes de tests servant à tester la fonctionnalité des différents appareils Phidgets utilisé lors de la réalisation du projet. De plus amples informations sont disponibles à l'intérieur du dossier.
MATERIAL NEEDED:
-(2x) RaspBerry Pi Zéro W
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
VERSION: V2.9
DATE: 15-02-20
AUTHORS: Jérémy Goulet, Guillaume Beaudoin

