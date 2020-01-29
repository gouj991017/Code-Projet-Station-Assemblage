/**
 * Write a description of class AppBase here.
 * 
 * @author (Jeremy Goulet) 
 * @version (29-11-2019)
 */

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.*;

import org.fusesource.hawtbuf.AsciiBuffer;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;
import java.sql.*;

public class Test
{
    static final int NB_BACS = 20; //Constante du nombre de bacs relie au Raspberry pi.
    // instance variables - replace the example below with your own
    static GpioPinDigitalOutput t_outputIOs[] = new GpioPinDigitalOutput[NB_BACS];
    static GpioPinDigitalInput t_inputIOs[] = new GpioPinDigitalInput[NB_BACS];

    /**
     * Constructor for objects of class AppBase
     */
    public Test()
    {
        
    }

    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public static void main() throws Exception
    {
        int numPageCourante = 1;
        
        //Create gpio controller
        GpioController gpio = GpioFactory.getInstance();
        
        //Provision gpio pin 1,3,5,7 as an output pin
        /*
        t_outputIOs[0] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, "Bac1", PinPullResistance.PULL_DOWN);
        t_outputIOs[1] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01, "Bac2", PinPullResistance.PULL_DOWN);
        t_outputIOs[2] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_03, "Bac3", PinPullResistance.PULL_DOWN);
        t_outputIOs[3] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_04, "Bac4", PinPullResistance.PULL_DOWN);
        t_outputIOs[4] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_05, "Bac5", PinPullResistance.PULL_DOWN);
        t_outputIOs[5] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_06, "Bac6", PinPullResistance.PULL_DOWN);
        t_outputIOs[6] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_07, "Bac7", PinPullResistance.PULL_DOWN);
        t_outputIOs[7] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_08, "Bac8", PinPullResistance.PULL_DOWN);
        t_outputIOs[8] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_09, "Bac9", PinPullResistance.PULL_DOWN);
        t_outputIOs[9] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_10, "Bac10", PinPullResistance.PULL_DOWN);
        t_outputIOs[10] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_11, "Bac11", PinPullResistance.PULL_DOWN);
        t_outputIOs[11] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_12, "Bac12", PinPullResistance.PULL_DOWN);
        t_outputIOs[12] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_13, "Bac13", PinPullResistance.PULL_DOWN);
        t_outputIOs[13] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_14, "Bac14", PinPullResistance.PULL_DOWN);
        t_outputIOs[14] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_15, "Bac15", PinPullResistance.PULL_DOWN);
        t_outputIOs[15] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_16, "Bac16", PinPullResistance.PULL_DOWN);
        t_outputIOs[16] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_17, "Bac17", PinPullResistance.PULL_DOWN);
        t_outputIOs[17] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_18, "Bac18", PinPullResistance.PULL_DOWN);
        t_outputIOs[18] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_19, "Bac19", PinPullResistance.PULL_DOWN);
        t_outputIOs[19] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_20, "Bac20", PinPullResistance.PULL_DOWN);
        t_outputIOs[20] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_21, "Bac21", PinPullResistance.PULL_DOWN);
        t_outputIOs[21] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_22, "Bac22", PinPullResistance.PULL_DOWN);
        t_outputIOs[22] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_23, "Bac23", PinPullResistance.PULL_DOWN);
        t_outputIOs[23] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_24, "Bac24", PinPullResistance.PULL_DOWN);
        t_outputIOs[24] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_25, "Bac25", PinPullResistance.PULL_DOWN);
        t_outputIOs[25] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_26, "Bac26", PinPullResistance.PULL_DOWN);
        t_outputIOs[26] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_27, "Bac27", PinPullResistance.PULL_DOWN);
        t_outputIOs[27] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_28, "Bac28", PinPullResistance.PULL_DOWN);
        */
        //Provision gpio pin 0,2,4,6 as an input pin with its internal pull down resistor enabled
        t_inputIOs[0] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, "Bac1", PinPullResistance.PULL_DOWN);
        t_inputIOs[1] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01, "Bac2", PinPullResistance.PULL_DOWN);
        t_inputIOs[2] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_03, "Bac3", PinPullResistance.PULL_DOWN);
        t_inputIOs[3] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_04, "Bac4", PinPullResistance.PULL_DOWN);
        t_inputIOs[4] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_05, "Bac5", PinPullResistance.PULL_DOWN);
        t_inputIOs[5] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_06, "Bac6", PinPullResistance.PULL_DOWN);
        t_inputIOs[6] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_07, "Bac7", PinPullResistance.PULL_DOWN);
        t_inputIOs[7] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_08, "Bac8", PinPullResistance.PULL_DOWN);
        t_inputIOs[8] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_09, "Bac9", PinPullResistance.PULL_DOWN);
        t_inputIOs[9] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_10, "Bac10", PinPullResistance.PULL_DOWN);
        t_inputIOs[10] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_11, "Bac11", PinPullResistance.PULL_DOWN);
        t_inputIOs[11] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_12, "Bac12", PinPullResistance.PULL_DOWN);
        t_inputIOs[12] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_13, "Bac13", PinPullResistance.PULL_DOWN);
        t_inputIOs[13] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_14, "Bac14", PinPullResistance.PULL_DOWN);
        t_inputIOs[14] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_15, "Bac15", PinPullResistance.PULL_DOWN);
        t_inputIOs[15] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_16, "Bac16", PinPullResistance.PULL_DOWN);
        t_inputIOs[16] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_17, "Bac17", PinPullResistance.PULL_DOWN);
        t_inputIOs[17] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_18, "Bac18", PinPullResistance.PULL_DOWN);
        t_inputIOs[18] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_19, "Bac19", PinPullResistance.PULL_DOWN);
        t_inputIOs[19] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_20, "Bac20", PinPullResistance.PULL_DOWN);
        t_inputIOs[20] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_21, "Bac21", PinPullResistance.PULL_DOWN);
        t_inputIOs[21] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_22, "Bac22", PinPullResistance.PULL_DOWN);
        t_inputIOs[22] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_23, "Bac23", PinPullResistance.PULL_DOWN);
        t_inputIOs[23] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_24, "Bac24", PinPullResistance.PULL_DOWN);
        t_inputIOs[24] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_25, "Bac25", PinPullResistance.PULL_DOWN);
        t_inputIOs[25] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_26, "Bac26", PinPullResistance.PULL_DOWN);
        t_inputIOs[26] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_27, "Bac27", PinPullResistance.PULL_DOWN);
        t_inputIOs[27] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_28, "Bac28", PinPullResistance.PULL_DOWN);
        
        /* *********************Publishing du message d'initialisation******************* */
        for(int i=0;)
        
    }
}
