package terminal_base;

import com.phidget22.*; //Librairie du kit phidget.
/**
 * @author Guillaume Beaudoin
 * @brief Classe pour l'objet "Box" représantant un bac de pièces et ses informations.
 * @version 1.1
 */
public class Box
{
    double TRIGGER_IR = 1.5;   //Constante de trigger pour les capteurs infrarouges.
    double poidItem = 1;
    double erreurcell = 1.0;
    int boxNumber;
    int hubSerialNumber;
    double cellOffset = 0.0;
    VoltageInput Ir1;
    VoltageInput Ir2;
    VoltageRatioInput Cell;
    VoltageOutput DEL;
    //Constructeur
    Box(int HubSerialNumber, int BoxNumber, VoltageInputVoltageChangeListener event, double trigger, int poidPiece)
    {
        try
        {   //Instanciation des variables membres utiles à l'éxterieur de la classe.
            boxNumber = BoxNumber;
            hubSerialNumber = HubSerialNumber;
            TRIGGER_IR = trigger;
            poidItem = poidPiece;
            //Instanciation des capteurs et témoins du bac.
            Ir1 = new VoltageInput();
            Ir2 = new VoltageInput();
            Cell = new VoltageRatioInput();
            DEL = new VoltageOutput();
            Net.enableServerDiscovery(ServerType.DEVICE_REMOTE);    //Autorisation du "server discovery" pour touver le serveur du VINT.
        }catch(Exception ex){System.out.println("[Error] Fatal error" + ex.getMessage());}
        //Initialisation individuelle.
        try
        {
            Ir1.setDeviceSerialNumber(HubSerialNumber);
            Ir1.setHubPort(0);
            Ir1.setChannel(BoxNumber*2);
            //Ir1.setIsHubPortDevice(true);   //Le type d'objet VoltageInput doit avoir un paramètre IsPortDevice mis à 'true' si connecté directement au hub.    (À RETIRER EN VERSION FINALE)
            Ir1.setIsRemote(true);  //Le hub VINT est sans-fil et requiert ce paramètre pour chaque channel connectés.
            Ir1.addVoltageChangeListener(event);    //Association de l'event.
            Ir1.open(5000); //Timout de 5s.
            Ir1.setVoltageChangeTrigger(TRIGGER_IR);
        }catch(Exception ex){System.out.println("[Error] Capteur IR1 non initialisé: " + ex.getMessage());}
        try
        {
            Ir2.setDeviceSerialNumber(HubSerialNumber);
            Ir2.setHubPort(0);
            Ir2.setChannel((BoxNumber*2)+1);
            //Ir2.setIsHubPortDevice(true);   //Le type d'objet VoltageInput doit avoir un paramètre IsPortDevice mis à 'true' si connecté directement au hub.    (À RETIRER EN VERSION FINALE)
            Ir2.setIsRemote(true);  //Le hub VINT est sans-fil et requiert ce paramètre pour chaque channel connectés.
            Ir2.addVoltageChangeListener(event);    //Association de l'event.
            Ir2.open(5000); //Timout de 5s.
            Ir2.setVoltageChangeTrigger(TRIGGER_IR);
        }catch(Exception ex){System.out.println("[Error] Capteur IR2 non initialisé: " + ex.getMessage());}
        try
        {
            Cell.setDeviceSerialNumber(HubSerialNumber);
            if(BoxNumber%4 == 2 || BoxNumber%4 == 3)    //Réglage du port du hub selon le numéro du bac.
            {
                Cell.setHubPort(3);
            }
            else
            {
                Cell.setHubPort(2);
            }
            Cell.setChannel(BoxNumber%2);
            Cell.setIsRemote(true);  //Le hub VINT est sans-fil et requiert ce paramètre pour chaque channel connectés.
            Cell.open(5000); //Timout de 5s.
        }catch(Exception ex){System.out.println("[Error] Capteur CF non initialisé: " + ex.getMessage());}
        try
        {
            DEL.setDeviceSerialNumber(HubSerialNumber);
            DEL.setHubPort(1);
            DEL.setChannel(BoxNumber);
            DEL.setIsRemote(true);  //Le hub VINT est sans-fil et requiert ce paramètre pour chaque channel connectés.
            DEL.open(5000); //Timout de 5s.
        }catch(Exception ex){System.out.println("[Error] DEL non initialisé: " + ex.getMessage());}
        calibrer();
        System.out.println("[Info] Initialisation du bac #"+ (BoxNumber+1) +" du hub "+ HubSerialNumber +" terminée.");
    }

    /*
    brief: Retourne l'état du capteur IR.
    param: aucuns
    return: (boolean) état du capteur IR.
    */
    public boolean update_IR()
    {
        boolean temp = false;
        try
        {
            if(Ir1.getVoltage() > TRIGGER_IR)   //Lecture du capteur Ir1.
            {
                temp = true;
            }
        }catch(PhidgetException ex){}   
        return temp;
    }
    
    /*
    brief: Si les 2 cellules sont initialisés, on retourne une moyenne des poids, sinon on retourne la valeur du capteur initialisé si présent.
    param: (double) weightConvertionRatio: le ratio de convertion en V/V*10e-5 par gramme.
    return: (int) Nombre.
    */
    int getItemCount(double weightConvertionRatio)
    {
        double result;
        try
        {
            int i = 0;
            double temp = 0.0;
            result = ((Cell.getVoltageRatio()-cellOffset)/weightConvertionRatio)*100000;  //Calcul de conversion de V/V*10e-5 en gramme.
            temp = result*erreurcell;
            while(temp >= poidItem)    //Compte le nombre d'items.
            {
                temp = temp - poidItem;
                i++;
            }
            if(i != 0)
            {
                //erreurcell = (i*poidItem/result)+0.02;   //Calcule l'erreur(+2%).
            }
            return i;
        }catch(Exception ex){}
        return 0;
    }
    
    /*
    brief: Ferme les objets Phidgets.
    param: aucuns
    */
    void stop()
    {
        try
        {
            Ir1.close();
            Ir2.close();
            Cell.close();
            DEL.close();
        }catch(PhidgetException ex){}
    }
    
    /*
    brief: Calibre les cellules de charge.
    param: aucuns
    */
    void calibrer()
    {
        try
        {
            cellOffset = Cell.getVoltageRatio();
        } catch (Exception ex){}
    }
}
