/*
 * Copyright (C) 2020 albert Steiner
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.

* TradeRecord is the result of a trade between 2 bartering economies. 
* TradeRecord contains the data needed to evaluate trades to enable
* deriving the projected value of a repeated trade.
*/
package trade;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;

/**This is the copy of the Offer that was traded
 * This is the record that goes into the tradedList of both the ship and the other Econ.  The purpose of the records is to enable a ship to choose which planet to attempt the next trade.
 * Both the ship and the other econ (planet or possible ship) contain a traddedList.  The new offer is always added to the end of each tradedList if the offer is with a planet.  Traded lists only contain records of trades with planets.  Trades with ships are dropped, but the lists are still merged.
 *
 * @author albert
 */
public class TradeRecord {
  Econ cn; //0=planet or ship,1=primaryShip no part of copy record
  String cnName = "aPlanetOther";
  A2Row goods = new A2Row(); //only one instance of goods, for both cn's  
 // int prevTerm = 60;
  int age = 1;  // age of planet, we don't calc for ships
  int year = -10;  // year of the offer for the planet
 double[] xyzs = {-40, -41, -42}; // location of planet
 double startWorth = 0.;// planet worth before the trade.
 double endWorth = 0.; // planet worth after trade
 double strategicValue = 0.; // received/sent;
  int clan = 0; // clan of planet
  NumberFormat dFrac = NumberFormat.getNumberInstance();
  NumberFormat whole = NumberFormat.getNumberInstance();
  NumberFormat dfo = dFrac;
  EM eM;
  
  TradeRecord(){
    this.eM = StarTrader.eM;
    year = eM.year;
  }
  
/** constructor with Offer
 * 
 * @param aa offer for values
 */
   TradeRecord(Offer aa){
    eM = StarTrader.eM;
    cn = aa.cn[0];  // planet
    cnName = aa.cnName[0];
    goods = aa.goods;
   // prevTerm = aa.prevTerm[[0];
    age = aa.age[0];
    year = aa.year;
    xyzs = aa.xyzs[0];  // an array of 3 location numbers
    clan = aa.clans[0];  // planet
    dFrac = aa.dFrac;
    whole = aa.whole;
    dfo = aa.dfo;
    startWorth = aa.startWorth[0];
    endWorth = aa.endWorth[0];
    strategicValue = aa.strategicValue[0];
  }
   
   /** constructor Make a copy) from TradeRecord
 * 
 * @param aa offer for values
 */
   TradeRecord(TradeRecord aa){
    eM = StarTrader.eM;
    cn = aa.cn;
    cnName = aa.cnName;
    goods = aa.goods;
    age = aa.age;
    year = aa.year;
    xyzs = aa.xyzs;
    clan = aa.clan;
    dFrac = aa.dFrac;
    whole = aa.whole;
    dfo = aa.dfo;
    startWorth = aa.startWorth;
    endWorth = aa.endWorth;
    strategicValue = aa.strategicValue;
  }
   
   // list to output a record for checking correctness
   void listRec(){
     if((eM.year == 5 || eM.year == 6) && (year == 1 || year == 2  || year == 6) ){
     System.out.println(eM.year + ":" + year + ":" + age + " " + cn.getName() + ", clan" + clan + ", g=" + cn.mf(goods.plusSum() - goods.negSum()) + ", w=" + cn.mf(startWorth) + ":" + cn.mf(endWorth) + ", sv=" + cn.mf(strategicValue) + ", xyz=" + cn.mf(xyzs[0]) + ":" + cn.mf(xyzs[1]) + ":" + cn.mf(xyzs[2]));
     
         }}
   
   /** this is older than ownrR
    * Year 8 &lt; Year 10  Year 8 Is older 
    * P00009 &lt; S00008 P00009 is older
    * P00005 &lt P00007 P00005 is older
    * 
    * @param ownR the compared
    * @return true if otherRec is older than ownR
    */
   boolean isOlderThan(TradeRecord ownR){
     boolean ret = year < ownR.year; // smaller older
     // S00005 > P00007, P00008 > P00007
     ret |=  year == ownR.year && (cnName.compareTo(ownR.cnName) > 0);
     return ret;
   }
  
   /** merge lists in descending order older t0 new, z to a, return a newShipList, in the new lists leave an original copy of any TradeRecord from the destination previous list, (E.G. entries from the old ownerList are moved to the newOwnerList but are copied from the otherList.
    * 
    * @param ownerList owner econ for which list is being made.
    * @param otherList from the other Econ doing the trade
    * @param aOffer new offer to be added to list
    * @return newOwnerList only containing ships
    */
  ArrayList<TradeRecord> mergeLists(ArrayList<TradeRecord> ownerList,ArrayList<TradeRecord> otherList,Offer aOffer){
    // construct newOwnerList
    ArrayList<TradeRecord> newOwnerList = new ArrayList<TradeRecord>();
    int lOwnerList = ownerList.size();
    int yearsTooEarly = (int)(eM.year - eM.yearsToKeepTradeRecord[0][0]);
    // put new offer at the end
    ownerList.add(new TradeRecord(aOffer));
    
    Iterator<TradeRecord> iterOther = otherList.iterator();
   TradeRecord otherRec;
     for(TradeRecord ownerRec:ownerList){
       while( iterOther.hasNext() && 
      (otherRec = iterOther.next()).isOlderThan(ownerRec)){
         
         if(year > yearsTooEarly && otherRec.cnName.startsWith("P"))
         { 
           newOwnerList.add(new TradeRecord(otherRec));
         }       
    }// end while
       if(year > yearsTooEarly && ownerRec.cnName.startsWith("P")){ newOwnerList.add(ownerRec);}
     } // end for
    return newOwnerList;
  }
}
