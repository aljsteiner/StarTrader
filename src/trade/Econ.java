
/*
 Copyright (C) 2012 Albert Steiner

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package trade;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Albert Steiner
 *
 * Documentation hints' {@link url} .name
 * <p>
 * @parm url comments
 * @parm name comments
 * @return returns This class is the root of assets for this planet or ship. The
 * Assets and AssetsYr classes hold the assets and history of an economy Class E
 * holds static tables and user changeable values and tables and enums Each
 * economy belongs to a clan, which can establish levels of friendship with
 * others Planaet economys are started before any ships are started. At
 * yearStart, planets in AssetsYr calculate a best tactics for this years health
 * and growth after a planets growth it is ready for trading, and then at
 * yearEnd health decides on death. Stars search for a Planet to trade with as a
 * function of distance and planet trading history. at each trade Ships receive
 * the planets trading history, update it with ship history and store it on the
 * planet again. Only x number of years of history are kept. Random numbers are
 * calculated for each economy before yearStart or planetSearch. The random
 * numbers remain constant until the start of the next year cycle.
 * 
 */
public class Econ {

  StarTrader st;
  E eE = new E();
  EM eM;
  String aPre = "&V";
  int lev = History.informationMinor9;
  int blev = History.dl;
  int blev1 = History.dl;  // for addOHist
  int blev2 = History.dl;  // for addHist
  protected String name;
  protected int clan;
  double xpos, ypos, zpos;
  double[] xyz = {xpos, ypos, zpos};
  // neighbors from
  //Neighbor[] neighbors = new Neighbor[20];
  protected int pors;
  protected int year;  // year of StarTrader
  protected int age = -1;   // age of this economy, increased by year start
  int dage = -1;          // years dead
  protected Assets as;
  protected int econCnt = 0;
  static int keepHist = 4;  // keep hist for econCnt up to 5;
  protected double trand[] = new double[E.lrand];
//  protected E D
  // sum of guest, trainees, workers, faculty, researchers with biases
  protected double percentDifficulty;
  protected double sourceReqBias;
  protected double health = 1.0;
  protected double resourcePri[] = {0., 0., 0., 0., 0., 0., 0.};
  protected double sumInitPri = 0, sumUserPri = 0;
  double tworth;
  double wealth;
  double knowledge;
  double colonists;
  double res;
  boolean didYearEnd = false;
  protected int logM[] = {0, 0};  // initial hist row to be display per display level
  protected int logLev[] = {E.logDefaultLev[0], E.logDefaultLev[1]};
  protected int logLen[] = {E.logDefaultLen[0], E.logDefaultLen[1]};
  protected ArrayList<History>[] hists = new ArrayList[1];
  protected ArrayList<History> hist;
  protected ArrayList<Offer> offers = new ArrayList<Offer>();  // planet veriable
  // ArrayList<ArrayList<Offer>> planetOffers
 //                              = new ArrayList<ArrayList<Offer>>(); // ships list of planets
   ArrayList<TradeRecord> planetList = new ArrayList<TradeRecord>();

  ARow sectorPri;
  ArrayList<Offer> myPlanetOffers; // list of offers for this planet
  static int yearsKeep[] = {7, 7, 7, 7, 7};  // keep others offers by clan
  static int myYearsKeep[] = {12, 12, 12, 12, 12}; // keep my offers
  // int yearLeast = eM.year - yearsKeep[clan]; // oldest (least) year we keep
  // int myYearLeast = eM.year - myYearsKeep[clan]; // oldest on my list
  // [life,struct,energy,propel,defense,gov,colonist,consumers,guests,cargo]
  // see StarTrader.ConsumerNames
  NumberFormat dFrac = NumberFormat.getNumberInstance();
  NumberFormat whole = NumberFormat.getNumberInstance();
  NumberFormat exp = new DecimalFormat("0.#####E0");

  //E.clan myClan;
  /**
   * simple constructor
   *
   */
  public Econ() {
    hist
    = hists[0] = new ArrayList<History>();
  }

  /**
   * initialize Econ
   *
   * @param st reference StarTrader and cur in it
   * @param name economy name
   * @param clan clan membership
   * @param planetOrShip 0:planet , 1:ship
   * @param xpos
   * @param percentDifficulty difficulty for this economy
   * @param initWorth worth to be divided between cash, knowledge r c s g
   *
   */
  void init(StarTrader ast, EM aeM, String name, int clan, int econCnt, int planetOrShip,
          double xpos, double percentDifficulty, double initWorth
  ) {
    this.pors = planetOrShip;
    eM = aeM;
    this.clan = clan;
    this.name = name;
    this.st = ast;
    this.year = eM.year;
    this.econCnt = econCnt;
    double res1=1.0,tworth2=0.;
    tworth = initWorth;
    double wworth = Math.max(eM.initialWealthFrac[pors] * tworth,eM.initialWealth[pors]);
  
    dFrac.setMaximumFractionDigits(2);

    double sworth = (colonists =Math.max(eM.initialColonists[pors], tworth * eM.initialColonistFrac[pors]) * eM.nominalWealthPerStaff[pors])* (1.0 + eM.initialReserve[pors]);  // 1300 + reserve by staff wealth
    
    double rworth = (res = Math.max(eM.initialResources[pors],tworth * eM.initialResourceFrac[pors])) * (1.0 + eM.initialReserve[pors]) * eM.nominalWealthPerResource[pors];

    knowledge  = Math.max(eM.initialKnowledge[pors], tworth * eM.initialCommonKnowledgeFrac[pors] );
    wealth = Math.max(tworth-sworth-rworth-knowledge*eM.nominalWealthPerCommonKnowledge[pors], wworth); // wealth now remainder

    if (xpos > E.nzero) {
      this.xpos = xpos;
    }
    
    // now recalculate values by fractions regardless of value of initWorth
    tworth = initWorth* eM.upWorth[pors];
    double partsSum = eM.initialCommonKnowledgeFrac[pors]*eM.nominalWealthPerCommonKnowledge[pors]+ eM.initialResourceFrac[pors] * (1.0 + eM.initialReserve[pors])*eM.nominalWealthPerResource[pors] + eM.initialColonistFrac[pors] * (1.0 + eM.initialReserve[pors])*eM.nominalWealthPerStaff[pors] + eM.initialWealthFrac[pors];
    double partsSumFrac = 1/partsSum; // change to mult
    knowledge = tworth *  eM.initialCommonKnowledgeFrac[pors]* partsSumFrac/eM.nominalWealthPerCommonKnowledge[pors];
    wealth = tworth * eM.initialWealthFrac[pors]*partsSumFrac;
    colonists = tworth *eM.initialColonistFrac[pors]* (1.0 + eM.initialReserve[pors]) * partsSumFrac / eM.nominalWealthPerStaff[pors];
    res = tworth * partsSum * eM.initialResourceFrac[pors] * (1.0 + eM.initialReserve[pors]) / eM.nominalWealthPerResource[pors];
    
    System.out.println(new Date().toString() + "Init year" + year + (pors == E.P ? " planet " : " ship ") + name + " clan" + clan + " econCnt=" + econCnt  + " worth=" + dFrac.format(tworth)  + " wealth=" + dFrac.format(wealth) + " resources=" + dFrac.format(res) + " colonists=" + dFrac.format(colonists) + " knowledge=" + dFrac.format(knowledge));
    // do not set a new position/change position if Assets was already instanted
    if (as == null) {
    //    System.out.println("137 start as == null");
      // if this.xpos > E.nzero than set to the next position
      if (E.newPlanetPosition[1] >= E.newPlanetPosition[0] && E.newPlanetPosition[2] >= E.newPlanetPosition[1]) {
        this.xpos = (E.newPlanetPosition[0] += 2.7) - 1.2 + Math.random() * 2.7;
        E.newPlanetPosition[1] = E.newPlanetPosition[2] = -2.7; // back to initial value
      }
      else {
        this.xpos = E.newPlanetPosition[0] - 1.2 + Math.random() * 2.7;
      }
      if (E.newPlanetPosition[2] >= E.newPlanetPosition[1]) {
        this.ypos = (E.newPlanetPosition[1] += 2.7) - 1.2 + Math.random() * 2.7;
        E.newPlanetPosition[2] = -2.7; // back to initial value
      }
      else {
        this.ypos = E.newPlanetPosition[1] - 1.2 + Math.random() * 2.7;
      }
      this.zpos = (E.newPlanetPosition[2] += 2.7) - 1.2 + Math.random() * 2.7;
    } else {
      System.out.println(String.format("as != null, using a previous location. xpos%5.2f, ypos%5.2f, zpos=%5.2f",xpos,ypos,zpos));
    }
    System.out.println(new Date().toString() + "200 Econ.init mid this.xpos=" + this.xpos + " ypos=" + this.ypos + " zpos=" + this.zpos);

    hist.add(new History(20, "Start", "0Life", "1Struct", "2Energy", "3Propel", "4Defense", "5Gov", "6Col", "Min", "Sum", "Ave"));
    this.percentDifficulty = percentDifficulty;
    dFrac.setMaximumFractionDigits(2);
    whole.setMaximumFractionDigits(0);

    int[] apris = {0, 1, 2, 3, 4, 5, 6};
    Set<Integer> pris = new HashSet<Integer>(8);
    /**
     * populate pris with 0-6,for ensuring all 7 sectors hae priority
     */
    for (int m = 0; m < E.lsecs; m++) {
      pris.add(m);
      resourcePri[m] = 0.;
    }
    int sec = -1;
    double remainingPri = 100;
    double paddition[] = new double[7];
    int secs[] = {9, 9, 9, 9, 9, 9, 9};
    // set priority values
    for (int m = 0; m < E.lsecs - 1; m++) {
      Integer[] prar = pris.toArray(new Integer[0]);
      secs[m] = sec = (int) (prar[(int) (Math.random() * 500 % (pris.size()))]);
      paddition[m] = Math.random() * eM.userPriorityRanMult[m][pors][clan];
      if (m < 4) {
        resourcePri[sec] = eM.nomPriAdjustment[m][pors] + paddition[m];
      }
      else {
        resourcePri[sec] = remainingPri * .3 + paddition[m];
      }
      remainingPri -= resourcePri[sec]; // reduce available pri by this pri
      pris.remove(sec);
    }
    // set the last sec to what is left from 100.
    resourcePri[(int) pris.toArray()[0]] = remainingPri;
    as = new Assets(); //instantiacte new Assets even if we reused econ
    //  and before instantiating any ARow or A6Rowa
    sectorPri = new ARow();
    for (int i = 0; i < resourcePri.length; i++) {
      sectorPri.set(i, resourcePri[i]);
      // sumInitPri += resourcePri[i];
      // sumUserPri += E.userPriorityAdjustment[planetOrShip][clan][i];
    }
    // sectorPri.divby(100. / sectorPri.sum());
    //System.out.println(new Date().toString() + "Econ.init 211 before new Assets");
    // throw away any previous Assets, the new one will be alive not dead.
    //  as = new Assets(this, st, name, clan, planetOrShip, hist, wealth, resourcePri, res, colonists, knowledge, percentDifficulty, trand);

    //   System.out.println("Econ.init 200 did new Assets");
    as.assetsInit(econCnt, this, st, eM, name, clan, planetOrShip, hist, tworth,wealth, sectorPri, res, colonists, knowledge, percentDifficulty, trand);
    System.out.println(new Date().toString() + "Econ.init 202 did AssetsInit");
    //  as.calcEfficiency();
  }
  
  Econ newCopy(EM oldEM,EM newEM,E aE,StarTrader ast) {
    Econ rtnEcon = new Econ();
    // lots more
    // Assets
    return rtnEcon;
  }

  double getXyz(int p) {
    return xyz[p];
  }

  /**
   * return value of current loop n
   *
   * @return
   */
  int getN() {
    return as.getN();
  }

  String getName() {
    return name;
  }

  /** get how long Econ existed
   * 
   * @return 
   */
  int getAge() {
    return age;
  }

  /** get how long Econ has been dead
   * 
   * @return years dead
   */
  int getDAge() {
    return dage;
  }

  /**
   * return reference to Goods, force calculation of a valid goods
   *
   * @return tradingGoods value of goods to be traded and requested
   */
  A2Row getTradingGoods() {
    return as.getTradingGoods();
  }

  /**
   * get nominal worth of critical sectors of the offered trade force
   * calculation of a valid goods and worth
   *
   * @return nominal worth of critical sectors in offered trade
   */
  double getTradingWorth() {
    return as.getTradingWorth();
  }

  /**
   * get the number of successful trades this year
   *
   * @return number of successful trades
   */
  int getTradedSuccessTrades() {
    return as.getTradedSuccessTrades();
  }

  /**
   * get the number of trades tried this year
   *
   * @return the number of trades tried this year
   */
  int getTradedShipsTried() {
    return as.getTradedShipsTried();
  }
  
  /** decide is this planet is available for another visit to attempt a trade
   * Check the number of tradedShipsTried against several requirements.
   * check against:<ul><li>ships per planet--visited ships per visited planets</li>
   * <li>clan ships per clan planets -- visited clan ships -- visited clan planets</li>
   * <li>clan ships per all planets -- clan ship visits per all planets</li></ul>
   * 
   * @return true if planet can have a visit to trade
   */
  boolean planetCanTrade(){
    boolean go = true; // can go to trade
    go &= pors > 0; // any ship trades
    boolean go1 = EM.porsCnt[E.P] < 3;  //allow go beginnning of game
    if(E.debugCanTrade && (go || go1)){
     E.sysmsg("in planetCanTrade " + (go?"yes to ship":"maybe" ) + (go1?"yes in firstYear":"check more"));
    }
    if((go || go1)) return true;
    boolean ret=false;
    int myVisits = getTradedShipsTried();
    double planetsVisitedPerEcon = EM.visitedCnt == 0?0.0:EM.porsCnt[E.P] /EM.visitedCnt;
    double planetsFrac = 1.0 - eM.gameShipFrac[E.P];
    go = planetsVisitedPerEcon < (planetsFrac *1.1);  // allow a little extra planets
    if(E.debugCanTrade && !go){ 
      System.out.print("Not planetCanTrade planetsVisitedPerEcon=" + planetsVisitedPerEcon  + " planetsFrac *1.1=" + mf(planetsFrac *1.1));
    }
    return go;
    /*
    go = myVisits < (int)Math.floor(shipsPerPlanets  - .7);
    if(E.debugCanTrade && !go){ 
      E.sysmsg("in planetCanTrade myVisits=" + myVisits + "
    }
    */
     /*
     double[] gameShipFrac = {.501, .498};
  static double[][] mGameShipFrac = {{.35, .65}, {.35, .65}};
  double[][] clanShipFrac = {{.501, .501, .501, .501, .6}, {.498, .498, .498, .498, .6}}; // .3-.7 clan choice of clan ships /econs
  static double[][] mClanShipFrac = {{.3, .7}, {.3, .7}};
  double[][] clanAllShipFrac = {{.501, .501, .501, .501, .501}, {.5, .5, .5, .5, .5}};
     */
    
    
     /*
    double visitedShipsPerPlanets = EM.porsVisited[E.P] == 0?0.0:EM.porsVisited[E.S]/EM.porsVisited[E.P];
    if(visitedShipsPerPlanets > shipsPerPlanets) return false;
    //double clanShipsPerClanPlanets = EM.porsClanCnt[E.S][clan]/EM.porsClanCnt[E.P][clan];
    double clanShipsPerClanPlanets = EM.porsClanCnt[E.S][clan] == 0?0.0:EM.porsClanCnt[E.S][clan]/EM.porsClanCnt[E.P][clan];
    if(myVisits > Math.floor(clanShipsPerClanPlanets)) return false;
    //double visitedClanShipsPerClanPlanets2 = EM.porsClanVisited[E.S][clan] /EM.porsClanVisited[E.P][clan];
    double visitedClanShipsPerClanPlanets = EM.porsClanVisited[E.S][clan] == 0?0.0:EM.porsClanVisited[E.S][clan] /EM.porsClanVisited[E.S][clan];

    if(visitedClanShipsPerClanPlanets > clanShipsPerClanPlanets) return false;
    return true; //OK past all limits
    */
  }

  ;
  ARow getNewKnowledge() {
    return as.getNewKnowledge();
  }

  ;
  ARow getCommonKnowledge() {
    return as.getCommonKnowledge();
  }

  /**
   * get the count of manuals
   *
   * @return manuals count
   */
  ARow getManuals() {
    return as.getManuals();
  }

  /**
   * getSOS value
   *
   * @return true if SOS
   */
  boolean getSOS() {
    return as.getSOS();
  }

  /**
   * get the number of trades started this year
   *
   * @return as.getYrTradesStarted()
   */
  int getYrTradesStarted() {
    return as.getYrTradesStarted();
  }

 
  /**
   * generate an array of random numbers using . E.gameRandomFrac and
   * E.clanRisk[pors][clan] if E.gameRandomFrac == 0, always trand[] to 1. noop
   * otherwise set to a random number between .1 &lt random &lt 1.9
   * @param trand  this is actually ignored
   *
   * @return a new trand of E.lrand length
   */
  protected double[] newRand(double[] trand) {
    trand = new double[E.lrand];
    // make range (0->.7 + 0->1*0->.5) = (0->1.2)
    rMult = (eM.randFrac[pors][0] + eM.clanRisk[pors][clan] * eM.gameClanRiskMult[pors][0]) ;
    rMult = Math.max(eM.randMin,Math.min(eM.randMax, rMult));  // set max multiplier,  .001 < random < 1.9 
    rCent = rMult+eM.randMin;

    for (int ii = 0; ii < E.lrand; ii++) {
      if (eM.randFrac[pors][0] == 0.0) {// if 0, set all trand values 1
        trand[ii] = 1.;
        double aaaa = trand[1];
      }
      else {
        double aaaa = (rMult-eM.randMin)*(Math.random() - .5); //- .5 *rmult < aaaa < +.5*rmult
        double aaab = Math.max(eM.randMin,Math.min(eM.randMax,aaaa + rMult+eM.randMin)); // randMin < rand < randMax
        trand[ii] = aaab; // centered around rMult+eM.randMin
      }
      double aaac = trand[0];
    }
    return trand;
  }
   double rMult = 0.;
  double rCent = 0.;

  /**
   * get a preassigned random value at randx,  reduce randomicity by
   * mRand %lt; 1.0
   *
   * @param randx index folded into the length of array trand so for length=50
   * randx 55 = 5
   * @param mRand multiplier %lt; 1.0 reducing the difference from 1.0 of the random value
   * @return  a random number centered around 1.0, possibly reduced by rMult
   */
  protected double cRand(int randx, double rMult) {
    if (eM.randFrac[pors][0] <= E.pzero) {
      return 1.;
    }
    
    int ix = randx%trand.length;
    double myCent = rCent*rMult;
    double myRand = trand[ix]*rMult; // now centered around myCent
    double uRand = Math.max(eM.randMin,Math.min(eM.randMax,myRand + (1.0 - myCent))); // randMin < rand < randMax
    return uRand;
  } // cRand
  
  /**
   * get a preassigned random value at randx,  reduce randomicity by
   * mRand %lt; 1.0
   *
   * @param randx index folded into the length of array trand so for length=50
   * randx 55 = 5
   * @param mRand multiplier %lt; 1.0 reducing the difference from 1.0 of the random value
   * @return  a random number centered around 1.0, possibly reduced by rMult
   */
  protected double doRand(int randx, double rMult) {
    int ix = randx%trand.length;
    double myCent = rCent*rMult;
    double myRand = trand[ix]*rMult; // now centered around myCent
    double uRand = Math.max(eM.randMin,Math.min(eM.randMax,myRand + (1.0 - myCent))); // randMin < rand < randMax
    return uRand;
  } // cRand

  /** get the rand value
   * 
   * @param trand  rand array
   * @param randIx index into rand array of values
   * @return 
   */
  protected double cRand(double[] trand, int randIx) {
    return cRand(randIx, 1.0);
  } // cRand

  /**
   *
   * @return
   */
  protected double getHealth() {
    return as.getHealth();
  }

  /** get worth of all assets from Assets
   * 
   * @return total asset worth
   */
  protected double getWorth() {
    return as.getWorth();
  }
/** get die flag from Assets
 * 
 * @return die flag
 */
  protected boolean getDie() {
    return as.getDie();
  }
/** get guest ARow from Assets
 * 
 * @return guest ARow
 */
  ARow getGuests() {
    return as.getGuests();
  }
  
  /** get guest grades from Assets
   * 
   * @return guest grades
   */
  double[][] getGuestGrades(){
    return as.getGuestGrades();
  }

  ARow getCargo() {
    return as.getCargo();
  }

  int getPors() {
    return pors;
  }

  int getClan() {
    return clan;
  }

  double addCash(double cash) {
    return as.addCash(cash);
  }

  protected ArrayList<History> getHist() {
    return hists[0];
  }

  /**
   * get the number of ships this planet traded with this year
   *
   * @return
   */
  int getShipOrdinal() {
    return as.getShipOrdinal();
  }

  


  /**
   * start the year for this economy. This includes all of the bartering, Assume
   * that ships have done select planet first, gotten light years
   *  
   * @param lightYears  lightYearsTraveled for a ship
   */
  protected void yearStart(double lightYears) {
    age++; // move -1 to 0 for the first year
    // age the hists file, move 4->5, 3->4, 2->3, 1->2, new 1
    // except for the first year, or if the env is dead
    year = eM.year;
    didYearEnd = false;
    if (!as.getDie()) {
      if(clearHist()) {
        hist.clear();
      } else if(age > 0) { // keep the initialization hist
        // move hists up, keep a few
      for (int i = hists.length - 1; i > 0; i--) {
        if (hists[i - 1] != null) {
          hists[i] = hists[i - 1];
        }
        
      }
     
      hist = hists[0] = new ArrayList<History>(); // wipe out previous hist
      E.hist = hist; // save for later display
      if (this == eM.logEnvirn[0]) {
        eM.hists[0] = hist;
      }
      else if (this == eM.logEnvirn[1]) {
        eM.hists[1] = hist;
      }
      if (hists.length > 1) { //no valid only 1 hist
        hist.add(new History(2, "restart year=" + eM.year + " age=" + age, ">>>>>>>>>", "h0=" + wh(hists[0].size()), "h1=" + wh(hists[1].size()), "h2=" + wh(hists[2].size()), "h3=" + wh(hists[3].size()), "h4=" + wh(hists[4].size()), "<<<<<<<<<<"));
        int n2 = 0, n3 = 0;
        for (History hh : hists[1]) {
          int ll = hh.level;
          if (ll < 3) {
            hists[0].add(hh);
            n2++;
          }
          else {
            n3++;
          }
        }
        hist.add(new History(2, "copy lines=" + n2 + " skp=" + n3, ">>>>>>>>>", "h0=" + wh(hists[0].size()), "h1=" + wh(hists[1].size()), "h2=" + wh(hists[2].size()), "h3=" + wh(hists[3].size()), "h4=" + wh(hists[4].size()), "<<<<<<<<<<"));
        hist.add(new History(4, "after copy=" + n2 + " n=" + n3, ">>>>>>>>>", "h0=" + wh(hists[0].size()), "h1=" + wh(hists[1].size()), "h2=" + wh(hists[2].size()), "h3=" + wh(hists[3].size()), "h4=" + wh(hists[4].size()), "<<<<<<<<<<"));
      }
      }
      logLev[0] = E.logDefaultLev[0];
      logLev[1] = E.logDefaultLev[1];
      logLen[0] = E.logDefaultLen[0];
      logLen[1] = E.logDefaultLen[1];
    }
    trand = newRand(trand);  // generate the random array

    as.yearStart(trand, hist,lightYears);
    // health = R.yearStart(lightYears,trand);
    ArrayList<History> yy = hist;
    ArrayList<History> yz = hists[0];
  }
/** clear hist if this economy is %ge; keepHist and hist %ge; 20
 * 
 * @return true if hist is not to be kept;
 */
  boolean saveHist = false;
  boolean myClearHist=false;
  boolean clearHist(){
    if(saveHist) { return false; }
    if(myClearHist){ return true; }
    int iKeepMax = eM.keepHistsByYear.length-1;
    int iXKeep = (eM.year > iKeepMax?iKeepMax:eM.year);
      E.myTest(iXKeep > 2,"iXKeep=%d > 2, eM.year=%d, iKeepMax=%d, keepHistsByYear.lenth=%d",iXKeep, eM.year, iKeepMax, eM.keepHistsByYear.length);
    if(econCnt >= eM.keepHistsByYear[(eM.year > iKeepMax?iKeepMax:eM.year)] ){ 
      if(hist.size() > 20) {
      hist.clear();
      }
      return myClearHist = true;
    }
    return false;
  }
  int yyyee1=0,yyyee2=0,yyyee3=0,yyyee4=0;
  /** pass yearEnd on to trade.Assets
   * But only do it once a year
   * Ignore a second or more call to yearEnd in a given year
   */
  protected void yearEnd() {
    // avoid second yearEnd in a given year
    if(!didYearEnd){
      didYearEnd = true;
    as.yearEnd();
    EM.wasHere = "after as.yearEnd yyyee1=" + yyyee1++;
    if (as.getDie()) {
      dage++;
    }
    EM.wasHere = "after as.getDie() yyyee2=" + yyyee2++;
    if (clearHist()) {
      hist.clear(); // wipe out previous hist
    }
    }
    EM.wasHere = "at econ.yearEnd end yyyee3=" + yyyee3++;
  }
  
  /** add a line of History to ohist unless
   *  clearHist(), hh == null, hh.level > blev1
   * @param oHist  name of hist file
   * @param hh result of new History(...)
   */
  void addOHist(ArrayList<History> oHist,History hh){
    // do nothing if hist is being cleared or the hist level is > bLev highest allowed level
    if(clearHist()|| hh == null || hh.level > blev1 ) {
      return;
    }
    oHist.add(hh);
    
  }
  
  /** add a line of History to ohist unless
   *  clearHist(), hh == null, hh.level > blev2
   * @param oHist  name of hist file
   * @param hh result of new History(...)
   */
  void addHist(ArrayList<History> hist,History hh){
    // do nothing if hist is being cleared or the hist level is > bLev highest allowed level
    if(clearHist()|| hh == null || hh.level > blev2 ) return;
    hist.add(hh);
    
  }
  
  /** add a line of History to ohist unless
   *  clearHist(), hh == null, hh.level > bLev
   * @param oHist  name of hist file
   * @param bLev   do not add line if hh.level > bLev
   * @param hh result of new History(...)
   */
  void addHist(ArrayList<History> hist,int bLev,History hh){
    // do nothing if hist is being cleared or the hist level is > bLev highest allowed level
    if(clearHist()|| hh == null || hh.level > bLev ) {
      int bb=hh.level;
      return;
    }
    hist.add(hh);
    
  }

  protected Econ selectPlanet(Econ[] wilda) {
    String wildS = "in selectPlanet for:" + name + " names=";
    for (Econ ww : wilda) {
      wildS += " " + ww.name + " distance=" + calcLY(this, ww);
    }
    double wildar = Math.random() * 5.3 % wilda.length;
    int r = (int) Math.floor(wildar);
    wildS += " selected:" + mf(wildar) + " :" + wilda[r].name;
    System.out.println(wildS);
    return wilda[r];
  }

  protected double calcLY(Econ cur, Econ cur2) {
    double x = (cur.xpos - cur2.xpos);
    double y = (cur.ypos - cur2.ypos);
    double z = (cur.zpos - cur2.zpos);
    double xyz = Math.pow(x, 2.) + Math.pow(y, 2.) + Math.pow(z, 2.);
    return Math.sqrt(xyz);
  }
  /** format the value
   * <ol><li)v very close to 0 => 0</li>
   * <li>v at floating 0 -=> 9.9</li>
   * <li>v large => exp format</li>
   * <li>v fairly large => number.3 digit frac</li>
   * <li>v small < abs .001 => 0.7 digits frac</li>
   * </ol>
 * 
 * @param v input value
 * @return value as a string
 */
 public  String mf(double v){
      if(v%1 > E.NNZERO && v%1 < E.PPZERO){
        return whole.format(v);
      }
      if(v ==.0 || v == -0){ // very close to zero
        dFrac.setMinimumFractionDigits(0);
        dFrac.setMaximumFractionDigits(1);
      return dFrac.format(v);
      } else if((v > -999999. && v < -.001) || (v > .001 && v < 999999.)){
       dFrac.setMinimumFractionDigits(2);
      dFrac.setMaximumFractionDigits(3);
      return dFrac.format(v);
      } else if((v > -.001 && v < E.NNZERO) || (v > E.PPZERO && v < .001)){
       dFrac.setMinimumFractionDigits(2);
      dFrac.setMaximumFractionDigits(7);
      return dFrac.format(v);
    } else {
      return exp.format(v);
    }
   }
  /** format the value
 * 
 * @param v input value
 * @return value as a string
 */
  protected String df(double v) {
    return mf(v);
  }
/** return a whole number in the string
 * 
 * @param n  a number to convert
 * @return n as a whole number no fraction digits
 */
  protected String wh(double n) {
    return whole.format(n);
  }

  /** return a whole number in the string
 * 
 * @param n  a number to convert
 * @return n as a whole number no fraction digits
 */
  protected String wh(int n) {
    return whole.format(n);
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
         
         if(otherRec.year > yearsTooEarly && otherRec.cnName.startsWith("P"))
         { 
           newOwnerList.add(otherRec);
           if(E.debugTradeRecord) { otherRec.listRec(); }
         }       
    }// end while
       if(ownerRec.year > yearsTooEarly && ownerRec.cnName.startsWith("P")){ 
         newOwnerList.add(ownerRec); 
         if(E.debugTradeRecord) { ownerRec.listRec();}
       }
     } // end for
    return newOwnerList;
  }

  /**
   * ship start trade after selecting a planet with selectPlanet and getting a
   * planet econ. Do the planet first because it has the resources that the ship
   * will need, hopefully the ship will have resources/staff to trade that the
   * planet thinks will help its economy
   * @see Assets.CashFlow.barter() and Assets.Cashflow.Trades.barter()
   *
   * @param ship
   * @param planet
   */
  
  protected void sStartTrade(Econ ship, Econ planet) {  // only called for ships
    Econ myCur = eM.curEcon;  // save eM.curEcon for after trade
    Econ cn[] = {planet, ship};
    if (!ship.getDie()) {
      eM.curEcon = ship;
      eM.otherEcon = planet;
      Offer aOffer = new Offer(eM.year, eM.barterStart, eM, ship, planet);

      int bb = 1; // start barter with planet,
      // for will alternate bb++ each time to start with planet
      int bb1 = bb;

      // barter between economies planet/ship until accept or reject,
      // aoffer.term is always set in the Econ.barter ...
      // term = -3 means done 
      // term starts at eM.barterStart
      // see Assets.CashFlow.barter for the flow of term
      for (int i = aOffer.getTerm(); i > -3; i = aOffer.getTerm()) {
        bb1 = bb;
        bb = ++bb % 2;
        //send loop to both histories
        cn[0].hist.add(new History(History.loopMinorConditionals5,"T" + aOffer.getTerm() + " " + cn[bb].getName() +  " loop>>>>> ",  "i=" + i, "bb=" + bb1, "cur name=" ,cn[bb1].getName(), "ship=" + ship.getName(), "planet=" , planet.getName(),"<<<<<<<<<<<<<<<<<"));
        cn[1].hist.add(new History(History.loopMinorConditionals5, "**loop aOffer=", wh(aOffer.getTerm()), "i=" + i, "bb=" + bb1, "name=" + cn[bb1].getName(), "ship=" + ship.getName(), "planet=" + planet.getName()));
        eM.curEcon = cn[bb1];
        E.sysmsg("in " + cn[1].name + ".sStartTrade , " + cn[bb1].name + ".barter term=" + aOffer.getTerm() + "\n");
        aOffer = cn[bb1].barter(aOffer,cn[bb]);
       // aOffer = cn[bb1].as.barter(aOffer); // first barter with planet
        //   ship.hist.add(new History(3,"Env finish ship Trade"));
        //   planet.hist.add(new History(3,"Env oofinish planet Trade"));

      } // end for
      if(clearHist()){hist.clear();}
      eM.curEcon = myCur; // reset curEcon to its entry value
    }
  }
  
  Offer barter(Offer aOffer,Econ otherEcon){
    Offer ret = as.barter(aOffer);
    if(ret.getTerm() < 1){
    planetList = mergeLists(planetList,otherEcon.planetList,ret);
    }
    return ret;
  }

}
