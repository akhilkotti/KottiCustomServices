package com.kotti.apps.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceConstants {

    public static final Map<String, String> getMyTheatreShort;

    static {
        Map<String, String> aMap = new HashMap<>();
        aMap.put("PVR: Hyderabad Central Mall", "PVR-C");
        aMap.put("BIG Cinemas: Ameerpet", "BIG Cinemas");
        aMap.put("PVR: Banjara Hills", "PVR-B");
        aMap.put("Prasads: Large Screen", "PrasadsL");
        aMap.put("Prasads: Hyderabad", "Prasads");
        aMap.put("INOX: GVK One, Banjara Hills", "INOX");
        aMap.put("Asian Lakshmikala Cinepride: Moosapet", "Lakshmikala");
        getMyTheatreShort = Collections.unmodifiableMap(aMap);
    }
    public static final List<String> getMyTheatreList = Collections
            .unmodifiableList(Arrays.asList("PVR: Hyderabad Central Mall",
                    "BIG Cinemas: Ameerpet",
                    "PVR: Banjara Hills",
                    "Prasads: Large Screen",
                    "Prasads: Hyderabad",
                    "INOX: GVK One, Banjara Hills",
                    "Asian Lakshmikala Cinepride: Moosapet"));

    public static final List<String> getMailingList = Collections
            .unmodifiableList(Arrays.asList("kottisaisriharsha0941@gmail.com",
                    "akhilkotti@yahoo.com",
                    "akhilben@gmail.com"));

    public static final List<String> getMobileList = Collections
            .unmodifiableList(Arrays.asList("9553497413",
                    "9247887123",
                    "9866552288"
            ));

//	public static final List<String> getMobileList = Collections
//			.unmodifiableList(Arrays.asList("9553497413", 
//											"9247887123",
//											"9866552288",
//											"9948333321",
//											"9848111482",
//											"9885727284",
//											"9848111482",
//											"9701454478",
//											"8143372430",
//											"9293151075",
//											"9032450045"
//											));
//	
//	public static final List<String> getMailingList = Collections
//			.unmodifiableList(Arrays.asList("akhilkotti@yahoo.com",
//					"kottisaisriharsha0941@gmail.com", 
//					"akhilben@gmail.com",
//					"swarnalatha.bachu@gmail.com",
//					"anil.benerji@yahoo.in", 
//					"rajeshvarre@hotmail.com",
//					"aneesha.nishi2235@gmail.com",
//					"satish.kotti@gmail.com",
//					"rameshbabu.lingam@gmail.com",
//					"juvvakiran123@gmail.com",
//					"sree540@gmail.com",
//					"anil.benerji@yahoo.in",
//					"varrerakesh@yahoo.com",
//					"sarat.settipalli@gmail.com",
//					"prathyushanaidu@gmail.com"));
    public static final String getBookMyShowEmailTemplate = "Hi,\n"
            + "\tMovie tickets are available for {moviename}, for the following theatres in bookmyshow website\n"
            + "\t{theatres}\n\n"
            + "\tPlease click the below link for booking {moviename}, from bookmyshow.\n"
            + "\t{bookmyshowURL}\n\n"
            + "\tMake it fast.........\n\n"
            + "\tNote:\n"
            + "\t\tCurrently system is looking only for the below theatres\n\n"
            + "\t\t{searchtheatres}\n\n"
            + "\t\tWelcome your suggestions for any updates.\n\n"
            + "Thanks\n"
            + "Akhil Kotti";

    public static final String getBookMyShowSMSTemplate = "Book tickets - {moviename}\n"
            + "{theatres}\n\n"
            + "http://in.bookmyshow.com/buytickets/\n\n"
            + "Thanks\n"
            + "Akhil Kotti";
}
