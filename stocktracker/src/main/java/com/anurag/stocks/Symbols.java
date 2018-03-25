package com.anurag.stocks;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

 public class Symbols {
    
    String sym = "AMBUJACEM,ASIANPAINT,AUROPHARMA,AXISBANK,BAJFINANCE,BHARTIARTL,BPCL,CIPLA,COALINDIA,DRREDDY," +
            "GAIL,HCLTECH,HDFC,HDFCBANK,HEROMOTOCO,HINDALCO,HINDPETRO,HINDUNILVR,IBULHSGFIN,ICICIBANK,INDUSINDBK,INFRATEL," +
            "INFY,IOC,ITC,KOTAKBANK,LT,LUPIN,M&M,MARUTI,NTPC,ONGC,POWERGRID,RELIANCE,SBIN,SUNPHARMA," +
            "TATAMOTORS,TATASTEEL,TCS,TECHM,ULTRACEMCO,UPL,VEDL,WIPRO,YESBANK,ZEEL,"+

            "ABB,ACC,ASHOKLEY,BAJAJFINSV,BEL,BHEL,BRITANNIA,CADILAHC,COLPAL,CONCOR,CUMMINSIND,DABUR,DLF," +
            "DMART,EMAMILTD,GLAXO,GLENMARK,GODREJCP,HAVELLS,HINDZINC,ICICIPRULI,IDEA,INDIGO,JSWSTEEL," +
            "LICHSGFIN,MARICO,MOTHERSUMI,NHPC,NMDC,OFSS,OIL,PEL,PETRONET,PFC,PGHH,PIDILITIND,PNB," +
            "RECLTD,SAIL,SHREECEM,SIEMENS,SRTRANSFIN,SUNTV,TATAPOWER,TITAN,TORNTPHARM,"+

            "AJANTPHARM,AMARAJABAT,APOLLOHOSP,APOLLOTYRE,ARVIND,BERGEPAINT,BHARATFORG," +
            "BIOCON,CASTROLIND,CENTURYTEX,CESC,DALMIABHA,DISHTV,DIVISLAB,ENGINERSIN,EXIDEIND,FEDERALBNK,GMRINFRA," +
            "GODREJIND,IDFC,IDFCBANK,IGL,IRB,JINDALSTEL,JSWENERGY,L&TFH,M&MFIN,MINDTREE,MRPL,MUTHOOTFIN," +
            "PCJEWELLER,RBLBANK,SRF,STAR,TATACHEM,TATAGLOBAL,TVSMOTOR,UBL,"+
            
            "KSCL,COROMANDEL,VSTTILLERS,ASHIANA,PIIND,AJANTPHARM,DIVISLAB,ALLCARGO,CERA,ICICIPRULI,HDFCLIFE,KAJARIACER,VOLTAS,ESCORTS,MFSL,TTKPRESTIG,VIPIND,ASTRAL,PAGEIND,ABCAPITAL";
    
    String temp = "AMBUJACEM,ASIANPAINT,AUROPHARMA,AXISBANK,BAJFINANCE,BHARTIARTL";
    
    String t = "BHARTIARTL";
    
    //BAJAJ-AUTO
    public List<String> getSymbols(){
        List<String> l = new ArrayList<String>();
        StringTokenizer s1 = new StringTokenizer(sym,",") ;
        while(s1.hasMoreTokens()){
            l.add(s1.nextToken());
        }

        return l;
    }

}


