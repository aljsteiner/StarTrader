/*
 * Copyright (C) 2015 albert Steiner
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

 Assets hold the assets for each economy.  The economies are divided into 7
 sectors, E.lsecs,  The log displays only hold 7 sectors width.  Assets hold the values that continue from one year to the next, it also hold temporary values between the multiple times that CashFlow is instantiated for bartering with different ecconomies, and finally to swap or repurposing values from one financial sector to another and determining the possible survival and possible growth of an economy.  Year temporary values are deleted at the end of year in method yearEnd, preparing Assets for starting the next year.

 Assets contains a subClass  CashFlow  which contains the yearly processing of assets.  CashFlow instances are also used to contain values for previous years and previous "n" in the swap or rearrange asset sectors process.

 CashFlow  contains subclasses SubAssets for the resource and reserved resoure cargo, and for staff and reserved staff guests.  In addition, CashFlow contains the subclass Trades, this class uses many members of CashFlow, but is only instantiated during the trading process this economy and another economy.  Within the game no more than 2 Economies are in the process of trade at any one time.  

The game attempts to minimize the storage used by the game by allocatting full storage for no more than two economies at a time.

 classs and subclasses within this file
 Assets
 Assets.HCashFlow:startYr1..StartYr7,xitCalcCosts..prev7n,startYrs[7],prevns[7],ysgLooped,ysgCosts,ysGrowed,ysEndyr,traded,growLooped, growCosts,growed,endyr;
 Assets.HCashFlow.SubAssets:r=resource,c=cargo,s=staff,g=guests
 Assets.CashFlow:cur
 Assets.CashFlow.SubAssets:r=resource,c=cargo,s=staff,g=guests
 Assets.CashFlow.Trades:myTrade

 /** StarTrader contains the used set of stats descriptors
   * 
  static public String statsButton0Tip = "0: Current Game Worths";
  static public String statsButton1Tip = "1: Favors and trade effects";
  static public String statsButton2Tip = "2: Catastrophies, deaths, randoms, forward fund";
  static public String statsButton3Tip = "3: years 0,1,2,3 worth inc, costs, efficiency,knowledge,phe";
  static public String statsButton4Tip = "4: years 4,5,6,7 worth inc, costs, efficiency,knowledge,phe ";
  static public String statsButton5Tip = "5: years 8->15 worth inc, costs, efficiency,knowledge,phe ";
  static public String statsButton6Tip = "6: years 16->31 worth inc, costs, efficiency,knowledge,phe ";
  static public String statsButton7Tip = "7: years 32+ worth inc, costs, efficiency,knowledge,phe ";
  static public String statsButton8Tip = "8: swap factors";
  static public String statsButton9Tip = "9: Health, poor Health effect";
  static public String statsButton10Tip = "10: Knowledge, low knowledge effectss";
  static public String statsButton11Tip = "11: Fertility and Growth";
 */
 /* 0:worths,1:trade favor,2:random,crisis,deaths,forward,34567 ages,8:swap,9 rcsg bal,10:growth,cost,11:fertility health effect
 */
package trade;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 *
 * @author albert Steiner
 */
public class Assets {

  StarTrader st;
  String name;
  Econ ec;
  EM eM;
  CashFlow cur;
  int clan;
  int pors;
  int yrStart;
  int year;
  // int age = -1;
  int econCnt;
  boolean died = false;
  boolean sos = false;
  double health = 2.;
  double sumTotWorth = 0.; // sum of SubAsset worth + cash + knowledge
  String otherName;   // blank at year start, name of trading partner
  ArrayList<History> hist;
  //double[] sectorPri;
  String aPre = "@A";
  static final int LSECS = E.LSECS;
  static final String[] aChar = E.aChar; //{"r", "c", "s", "g"};
  static String[] rcsg = aChar;
  static String[] aNames = E.aNames; //{"resource", "cargo", "staff", "guests"};
  static String[] rcsqName = E.aNames;
  static int[] spluss = E.spluss; //{0, 0, LSECS, LSECS};
  static String[] sChar = E.sChar; //{"r", "r", "s", "s"};
  static String[] rcNsq = E.rcNsq; //{"rc", "sq"};
  static String[] rNc = E.rNc; //{"r", "c"};
  static String[] sNg = E.sNg; //{"s", "g"};
  static String[] rNs = E.rNs; //{"r", "s"};
  static String[] cNg = E.cNg; //{"c", "g"};
  static int[] rorss = E.rorss; //{0, 0, 2, 2};  // resorce or staff, also place or costs see
  static int[] d01 = E.d01; //{0, 1};
  static int[] balsIxA = E.balsIxA; //{0, 1, 2, 3};
  static final int[] IA01 = d01;
  static final int[] A01 = {0, 1};
  static final int[] MR = {0, LSECS};
  static int[] dlsecs = E.alsecs;
  static final int[] ASECS = E.alsecs;
  static int[] d2lsecs = E.a2lsecs;
  static final int[] I2ASECS = E.a2lsecs;
  static int[] d25 = {2, 3, 4, 5};
  static final int[] IA25 = d25;
  static final int[] IA03 = {0, 1, 2, 3};
  static final int[] IA4 = IA03;
  static final double NZERO = E.NZERO;
  static final double PZERO = E.PZERO;
  static final int[] ms = {0, 1};
  static final int[] mr = {0, E.lsecs};
  static final int aDl = 9;

  // Assets  range 0. - 1.
  static final double decrMostGapMult[] = {1 / (14 * 16), 1 / (14 * 15), 1 / (14 * 14), 1 / (14 * 13), 1 / 14 * 1 / 12, 1 / 14 * 1 / 11, 1 / 14 * 1 / 10, 1 / 14 * 1 / 9};
  boolean iCF = false; // did initCashFlow;
  static final boolean subAssetsIsStaff[] = {false, false, true, true};
  static final boolean subAssetsIsReserve[] = {false, true, false, true};
  boolean assetsInitialized = false;
  // The following Ix are 2 to 18 are starting indexs for the 4 SubAsset Ix
  // A6Row[] needsArray;
  ARow aSectorPriority;
  ARow difficulty;
  double iwealth;
  double wealth;
  double colonists;
  double cash = 0.;
  double res;
  double aknowledge;
  double percentDifficulty;
  // int[] sLoops = new int[E.hcnt];  //count of swap loops (health only);
  static int maxn = 40;
  int i = -4, j = -4, k = -4, l = -4, m = -4, n = -4, splus = -4,term = -4;
  double clanRisk;

  //Assets forward fund, zero at start of end, stat at end of end
  double resEmergencyFutureFundAssigned = 0.;
  double staffEmergencyFutureFundAssigned = 0.;
  double resEmergencyFutureFundRequired = 0.;
  double staffEmergencyFutureFundRequired = 0.;
  double yearsFutureFund=0.;
  int yearsFutureFundTimes=0;
  String resTypeName = "anot";
  Double rsval = 0.;
  
  double resFutureFundRequired = 0.;
  double staffFutureFundRequired = 0.;
  double resRorwardFundAssigned = 0.;
  double staffFutureFundAssigned = 0.;
  double totalFutureFundAssignedj = 0;
  double econsCnt;


  static double[][][] maintRequired = E.maintRequired;
  static double[][][] mxCosts = E.maintCost;
  static double[][][] tCosts = E.shipTravelLightyearCostsBySourcePerConsumer;
  static double[][][] gReqs = E.resourceGrowthRequirementBySourcePerConsumer;
  static double[][][] gCosts = E.resourceGrowthCostBySourcePerConsumer;
  double[] trand;   // reference to random numbers in Econ
  History h1, h2, h3, h4, h5, h6, h7, h8; // pointers to history lines
 
// Assets last trade value if multiple trades this year
 // int term = -4;  // term or level of trade
  double preTradeSum4 = 0., preTradeAvail = 0., postTradeSum4 = 0., postTradeAvail = 0.;
  int tradedSuccessTrades; // successful trades this year
  double tradedStrategicWorths; // positive strategic worths
  double tradedStrategicRealWorths; // real worths of successful trades
  double tradedStrategicCosts;// 2-3 least strategic value traded
  double tradedStrategicRealCosts; // 2-3 real costs of trades
  double tradedManualsWorths;  // worth of manuals received in trades
  double tradingOfferWorth; // valid if didGoods;
  // if multiple ships trade in a year, this is for the last ship
  int tradedShipOrdinal; // count of ships traded this year
  int tradedShipsTried; // count of ships trying trade this year
  String tradingShipName= "none";
  int prevBarterYear = -20;  // set by Assets.barter
  boolean newTradeYear1 = false; // set by Assets.barter
  boolean newTradeYear2 = false; // set after maint  & travel saved
  int yrTradesStarted;  // -1 if no trade this year, set at newTradeYear
  // int[] tradedShipAccepted = new int[E.hcnt];
  int oClan = -5, oPors = -6;
  int lTradedEcons = 20;
  Econ[] oTradedEcons = new Econ[lTradedEcons];
  int oTradedEconsNext = 0;
  double fav = -5, oFav = -5;  //in assets
  double tradedFav = -4;
  double tradedOFav = -4;
  double tradedFirstStrategicReceipts;
  double tradedFirstReceipts;
  double tradedFirstSends;
  double tradedFinalStrategicReceipts;
  double tradedFinalReceipts;
  double tradedFinalSends;
  // double[] tradedGoodBal = new double[E.hcnt];
  // double[] tradedGoodWorth = new double[E.hcnt];
  boolean tradeAccepted = false;
  boolean tradeRejected = false;
  boolean tradeLost = false;
  // int acceptedTrade = -5;  // barter number of tradeOK
  // int rejectedTrade = -6; // barter number of rejected trad
  A2Row tradedBid;
  double tradedStrategicValue;
  double tradedStrategicFrac;
  ARow tradedMoreManuals;
  double lightYearsTraveled = 0.;
  String tradedShipNames[][] = {{"A", "B", "C", "D", "E"}, {"A", "B", "C", "D", "E"}, {"A", "B", "C", "D", "E"}, {"A", "B", "C", "D", "E"}, {"A", "B", "C", "D", "E"}};
  double strategicGoal = 0., rGoal0 = 0., strategicValue = 0., goodFrac = 0.;
  double endTradeWorth = -200.;
  /**    in trade.Assets
   * bids positive are offers, negative are requests, positive are what I can
   * give, negative what I can send
   */
  A2Row bids;
  int lev = History.loopMinorConditionals5;
  
  // Assets permanent values, flags and unit values
  ABalRows bals;
  boolean didGoods = false;
  //Assets do decay in calcGrowth only first time each year, set false in endYear
  boolean didStart = false, didDecay = false, didCashFlowInit = false;
  A6Row balances;   //Assets a subset of ABalRows
  A6Row cashFlowSubAssetBalances; // assume no recreate of each ARow
  A6Row growths;  // Subset of ABalRows
  A6Row cashFlowSubAssetsGrowths;
  A6Row mtgNeeds6;  //needs are positive, avails negative  Assets
  A6Row cashFlowSubAssetUnitsNeededToSurvive;
  A6Row mtgAvails6;// the negative of mtgNeeds6
  A6Row cashFlowSubAssetUnitsAvailableToSwap;
  A2Row rawFertilities2;
  A2Row rawProspects2;
 
  
  ARow commonKnowledge;
  ARow newKnowledge;
  ARow knowledge;
  ARow manuals;
  ARow moreK; // in doGrow incr knowledge
  ARow lessM; // in doGrow The manual made commonKnowledge
  ARow ydifficulty;
  double initialSumWorth = -200.;
  double startYrSumWorth = -200.;
  double prevYrSumWorth = -300.;
  double initialSumKnowledge, prevYrSumKnowledge, initialSumKnowledgeWorth;
  double startYrSumKnowledge, startYrSumKnowledgeWorth, prevYrSumKnowledgeWorth;
  
  double poorKnowledgeAveEffect = 4., poorHealthAveEffect = 1.5;
  int ixWRSrc = -2;  // 0,1 source balances, bals Working and Reserved, rc sg
  int ixWSrc; // 2,4 source Working ARows index ixWRSrc *2 + 2
  int ixRSrc; // 3,5 source Reserved ARows index, ixWRSrc*2 + 3
  // Assets these are indexes for rows in bal
  static final int TCOST = A6Rowa.tcost;
  static final int TBAL = A6Rowa.tbal;
  static final int RIX = ABalRows.RIX, CIX = ABalRows.CIX, SIX = ABalRows.SIX;
  static final int GIX = ABalRows.GIX, SGIX = ABalRows.SGIX, RCIX = ABalRows.RCIX;
  // warning the following duplicates ABalRows and must not be changed
  // static int rcIx = -2;
  static final int RCIX2 = ABalRows.RCIX2;
//  static int sgIx = -1;
  static int BALANCESIX = ABalRows.BALANCESIX;
  // static final int balancesIx=2;
  // static int GROWTHSIX = BALANCESIX + 4; //6
  static final int GROWTHSIX = ABalRows.GROWTHSIX;
  //static int bonusYearsIx = GROWTHSIX + 4; //10
  static final int BONUSYEARSIX = ABalRows.BONUSYEARSIX;
  //static int bonusUnitsIx = bonusYearsIx + 4;//14
  static final int BONUSUNITSIX = ABalRows.BONUSUNITSIX;
  //static int cumulativeDecayIx = bonusUnitsIx + 4; //18
  //static int balsLength = cumulativeDecayIx + 4; //22
  static final int CUMULATIVEDECAYIX = ABalRows.CUMULATIVEDECAYIX;
  static final int BALSLENGTH = ABalRows.BALSLENGTH;
  static final int balancesSums[] = {BALANCESIX + RCIX, BALANCESIX + SGIX};
  static final int balancesSubSum1[] = {BALANCESIX + RIX, BALANCESIX + CIX};
  static final int balancesSubSum2[] = {BALANCESIX + SIX, BALANCESIX + GIX};
  static final int balancesSubSums[][] = {balancesSubSum1, balancesSubSum2};
  Assets.CashFlow.DoTotalWorths syW, iyW;

  /**
   * The history versions of CashFlow are always copied to involving a new
   * HAssets(), than copy of the cur values that are of interest. The history
   * HCashFlow is a limited copy of CashFlow, only containing declarations of
   * members that will be used in some historical result or decision. the
   * SubAsset within HCashFlow is likewise limited
   */
  //CashFlow initial, started, noTrade, noTrade2;
  // CashFlow ysgLooped, ysgCosts, ysGrowed, ysEndyr, traded, growLooped, growCosts, growed, endyr;
  // ARow sectorPriority = new ARow();
  // ARow priorityYr = new ARow();
  enum yrphase {

    PRESEARCH, SEARCH, TRADE, SWAPING, GROW, PAY, HEALTH, END;

    int n() {
      return ordinal();
    }
  } // end yrphase
  
  yrphase yphase = yrphase.GROW;
  boolean em;  // print error message

  double tmin;  // temporary min

  NumberFormat dFrac;
  NumberFormat whole;
  NumberFormat dfo;

  /**
   * constructor for Assets
   *
   */
  public Assets() {
  }

  /**
   * obsolete constructor not used
   *
   * @param ec
   * @param stx
   * @param name
   * @param clan
   * @param pors
   * @param hist
   * @param wealth
   * @param sectorPri
   * @param res
   * @param colonists
   * @param knowledge
   * @param percentDifficulty
   * @param tranda
   */
  public Assets(Econ ec, StarTrader stx, String name, int clan, int pors, ArrayList<History> hist, double wealth, ARow sectorPri, double res, double colonists, double knowledge, double percentDifficulty, double[] tranda) {
    System.out.println("Assets() 288 start" + name);
    this.ec = ec;
    this.st = stx;
    this.name = name;
    this.yrStart = eM.year;
    this.clan = clan;
    this.pors = pors;
    this.hist = hist;
    this.wealth = wealth;
    trand = tranda;
    aSectorPriority = sectorPri;
    this.res = res;
    this.colonists = colonists;
    this.aknowledge = knowledge;
    this.percentDifficulty = percentDifficulty;
    hist.add(new History("aa", 5, "Initial assets yr" + year, "wealth=", df(wealth), "colonists=", df(colonists), "Knowledge=", df(knowledge), "difficulty=", df(percentDifficulty)));
    StackTraceElement a1 = Thread.currentThread().getStackTrace()[1];
    StackTraceElement a2 = Thread.currentThread().getStackTrace()[2];
    StackTraceElement a3 = Thread.currentThread().getStackTrace()[3];
    //   StackTraceElement a4 = Thread.currentThread().getStackTrace()[4];
    System.out.println("as construct " + name + " at " + a3.getMethodName() + ", " + a2.getMethodName() + " wealth=" + df(wealth));
    //   this.cur = new CashFlow(this);
  } //end constructor

  /**
   * initiator for Assets
   *
   * @param aeconCnt econ count
   * @param aaec hold Econ instance
   * @param stx holds StarTrader instance
   * @param eem holds EM instance
   * @param aaname name of economy
   * @param aaclan clan of economy
   * @param aapors 0==planet, 1==Ship
   * @param aahist history pointer
   * @param iwealth initial wealth before distribution
   * @param aawealth value of wealth
   * @param aasectorPri ARow of sector Priorities
   * @param aares number of resource units
   * @param aacolonists number of colonist units
   * @param aaknowledge units of common knowledge
   * @param aapercentDifficulty difficult percent
   * @param aatranda points to an array of random values between 0. - 2.
   */
  public void assetsInit(int aeconCnt, Econ aaec, StarTrader stx, EM aeM, String aaname, int aaclan, int aapors, ArrayList<History> aahist,double iwealth, double aawealth, ARow aasectorPri, double aares, double aacolonists, double aaknowledge, double aapercentDifficulty, double[] aatranda) {
    System.out.println("AssetsInit 228 start" + name);
    dFrac = NumberFormat.getNumberInstance();
    whole = NumberFormat.getNumberInstance();
    dfo = dFrac;
    difficulty = new ARow();
    //   startYrs = new HCashFlow[7]; // might use instead of name 1,2 ...
    //   prevns = new HCashFlow[7];
    double sumPri = 0.;
    econCnt = aeconCnt;
    ec = aaec;
    st = stx;
    eM = aeM;
    name = aaname;
    yrStart = eM.year;
    clan = aaclan;
    pors = aapors;
    hist = aahist;
    this.iwealth = iwealth;
    wealth = aawealth;
    trand = aatranda;
    aSectorPriority = aasectorPri;
    res = aares;
    colonists = aacolonists;
    aknowledge = aaknowledge;
    percentDifficulty = aapercentDifficulty;
    // move the definitions here so they can reference a defined Assets
    bals = new ABalRows(ABalRows.balsLength, ABalRows.tbal, History.valuesMajor6, "bals");
     balances = new A6Row(History.valuesMajor6, "balances");
  cashFlowSubAssetBalances = balances; // assume no recreate of each ARow
  growths = new A6Row(History.valuesMajor6, "growths");
  cashFlowSubAssetsGrowths = growths;
  mtgNeeds6 = new A6Row(lev, "mtgNeeds6");
  cashFlowSubAssetUnitsNeededToSurvive = mtgNeeds6;
  mtgAvails6 = new A6Row(lev, "mtgAvails6");
  cashFlowSubAssetUnitsAvailableToSwap = mtgAvails6;
  bids = new A2Row(History.valuesMajor6, "bids");
  commonKnowledge = new ARow();
  newKnowledge = new ARow();
  knowledge = new ARow();
  manuals = new ARow();
  moreK = new ARow(); // in doGrow incr knowledge
  lessM = new ARow(); // in doGrow The manual made commonKnowledge
  ydifficulty = new ARow();
    double sumWealth = res * eM.nominalWealthPerResource[pors] + colonists * eM.nominalWealthPerStaff[pors] + aknowledge*eM.nominalWealthPerCommonKnowledge[pors] + wealth;
    hist.add(new History("aa", History.valuesMajor6, "Init" + year + " i$" + df(iwealth),"r" + df(res),"r$" + df(res * eM.nominalWealthPerResource[pors]), "s" + df(colonists),"s$" + df(colonists * eM.nominalWealthPerStaff[pors]), "K" + df(aknowledge),"K$" + df(aknowledge*eM.nominalWealthPerCommonKnowledge[pors]),"$" + df(wealth),"i$" + df(iwealth), "difficulty=", df(percentDifficulty)));
    //  System.out.println("Assets() 623 end constructor");
    System.out.println("Assets.assetsInit 393 more" + name);
    //  needsArray = new A6Row[5];

    cur = new CashFlow(this);
    cur.cashFlowInit(this);
    assetsInitialized = true;
  } // end Assets.assetsInit

  /**
   * generate string of r or s source and index scrIx
   *
   * @param ixSrc 0,1 index of source r or s
   * @param srcIx index of sector
   * @return rNs[ixSrc] + srcIx
   */
  String rNsIx(int ixSrc, int srcIx) {
    return rNs[ixSrc] + srcIx;
  }

  /**
   * generate String of r or s source and index
   *
   * @param n
   * @return
   */
  String rNsIx(int n) {
    return rNs[(int) n / LSECS] + n % LSECS;
  }

  /**
   * get the raw value 0,1 value of the source, depends on ixWRSrc
   *
   * @return ixWRSrc
   */
  int getSrcWRix() {
    return ixWRSrc;
  }

  /**
   * get the index for Working ARows in Bals or Balances
   *
   * @return ixWRSrc*2+2
   */
  int getSrcWix() {
    return ixWRSrc * 2 + 2;
  }
  /**
   * get the index for Reserved ARows in Bals or Balances
   *
   * int getSrcRix() { return ixWRSrc*2+3; }
   *
   *
   * /**
   * set hist Title line
   *
   * @param lTitle
   */
  int prevTitleLine = 0;

  void histTitles(String lTitle) {
    histTitles(aPre, lTitle);
  }

  /**
   * set Titles for the following lines Don't set a title if the previous line
   * was a title
   *
   * Titles have the special level 20
   *
   * @param aPre Prefix for the titles
   * @param lTitle content of the title column
   */
  void histTitles(String aPre, String lTitle) {
    // eliminate duplicates, remove previous title
    if (prevTitleLine == hist.size()) {
      hist.remove(hist.size() - 1);
    }
    if (prevTitleLine != hist.size()) {
      if (History.dl > History.valuesMajor6) {
        hist.add(new History(aPre, History.headers20, lTitle, "0Lifeneed", "1Structs", "2Energy", "3Propel", "4Defense", "5Gov", "6Colinize", "Min", "Sum", "Ave"));
        prevTitleLine = hist.size();
      }
    }
  }

  /**
   * format the value
   *
   * @param v input value
   * @return value as a string
   */
  protected String df2(double v) {
    return eM.mf(v);
  }

  /**
   * format the value
   *
   * @param v input value
   * @return value as a string
   */
  protected String df(double v) {
    return ec.mf(v);
  }

  /**
   * format the value
   *
   * @param v input value
   * @param n ignored
   * @return value as a string
   */
  protected String df(double v, int n) {
    return ec.mf(v);
  }

  /**
   * format the value
   *
   * @param v input value
   * @return value as a string
   */
  String mf(double v) {
    return ec.mf(v);
  }

  /**
   * format the value
   *
   * @param v input value
   * @return value as a string
   */
  protected String df7(double v) {
    return ec.mf(v);
  }

  String whole(double n) {
    whole.setMaximumFractionDigits(0);
    return whole.format(n);
  }

  String wh(double n) {
    whole.setMaximumFractionDigits(0);
    return whole.format(n);
  }
  double doubleTrouble(Double trouble) {return E.doubleTrouble(trouble,"");}
  double doubleTrouble(Double trouble,String vs){
    return E.doubleTrouble(trouble,vs);
  }
  double doubleTroubled(Double trouble){
    if(trouble.isNaN()){
      if(E.debugDouble){
        throw new MyErr(String.format("Not a number found, term%d, i%d, j%d, m%d, n%d",term,i,j,m,n)); 
      } else {
        return 0.0;
      }
    }
    if(trouble.isInfinite()){
      if(E.debugDouble){
      throw new MyErr(String.format("Infinite number found, term%d,i%d,j%d,m%d,n%d",term,i,j,m,n));
      } else {
        return 100.0;
      }
    }
      return (double)trouble;
    }
  /**
   * use ec.cRand(randx,mRand) in Econ get Random number by index modified by
   * fraction an array of random numbers is generated at the start of each
   * financial year this array is constant for that economy until the next year.
   * Each economy has it own array
   *
   * @param randx random index, converted to positive value
   * @param mRand modifying fraction
   * @return ec.cRand(randx,mRand);
   */
  protected double cRand(int randx, double mRand) { //Assets.cRand
    return ec.cRand(randx, mRand);
  }

  /**
   * return a random number constant for this year, this index use
   * Econ.cRand(randIx,1. if eM.randFrac[pors] == 0 than a 1.0 is always the
   * returned random number
   *
   *
   * @param trand ignored , but ec.trand is the array of random numbers
   * @param randIx index into ec.trand
   * @return a random number around 1.0
   */
  protected double cRand(double[] trand, int randIx) { // Assets.cRand
    return ec.cRand(randIx, 1.0);
  }

  /**
   * return a random number constant for this year, this index get random number
   * for randIx use cRand(randIx,1.)
   *
   * @param randIx index into years random numbers
   * @return random number always the same if year, randIx are the same
   */
  double cRand(int randIx) {
    return ec.cRand(randIx, 1.);
  }

  /**
   * add to Assets.cash
   *
   * @param aCash amount to add
   * @return total of cash + aCash
   */
  double addCash(double aCash) {
    return cash += aCash;
  }

  /**
   * get the count of number of trades with ships this year
   *
   * @return tradedShipOrdinal
   */
  int getShipOrdinal() {
    return tradedShipOrdinal;
  }

  /**
   * get the count of trades started this year
   *
   * @return yrTradesStarted
   */
  int getYrTradesStarted() {
    return yrTradesStarted;
  }

  /**
   * get tradingGoods Assets
   *
   * @return bids for Trading
   */
  A2Row getTradingGoods() {
    if (cur == null) {
      yphase = yrphase.PRESEARCH;
      cur = new CashFlow(this);
      cur.cashFlowInit(this);
    }
    A2Row ret = cur.getTradingGoods();
    cur = null;
    return bids;
  }

  /**
   * get Worth this Assets critical bids to trade force calculation of bids if
   * needed
   *
   * @return worth to trade
   */
  double getTradingWorth() {  //Assets
    getTradingGoods();
    return tradingOfferWorth;
  }

  /**
   * get the number of trades tried this year
   *
   * @return trades tried this year
   */
  int getTradedShipsTried() {
    return tradedShipsTried;
  }

  /**
   * get the number of successful trades this year
   *
   * @return the number of successful trades
   */
  int getTradedSuccessTrades() {
    return tradedSuccessTrades;
  }

  /**
   * get knowledge
   *
   * @return knowledge
   */
  ARow getKnowledge() {
    return knowledge;
  }

  /**
   * get common knowledge
   *
   * @return common knowledge
   */
  ARow getCommonKnowledge() {
    return commonKnowledge;
  }

  /**
   * get new knowledge
   *
   * @return new knowledge
   */
  ARow getNewKnowledge() {
    return newKnowledge;
  }

  /**
   * get manuals
   *
   * @return manuals
   */
  ARow getManuals() {
    return manuals;
  }

  /**
   * eliminated 1/15/2016, use only make(ARow A) new an ARow if it was not
   * instantiated save space in auxiliary CashFlow only using a few fields
   *
   * @param a ARow if it already exists
   * @return a or a new ARow
   */
  ARow makeNewNot(ARow a) {   //Assets.makeNew
    if (a != null) {
      return a;
    }
    ARow AA = new ARow();
    return AA;
  }

  /**
   * return a set ARow from old using anew if it exists save space in auxiliary
   * CashFlow only using a few fields
   *
   * @param anew possible existing ARow
   * @param old source ARow to set
   * @return this set to old
   */
  ARow makeSetNot(ARow anew, ARow old) {   //Assets.makeSet
    if (anew != null && old != null) {
      return anew.set(old);
    }
    else {
      if (old == null) {
        old = makeZero(old);
      }
      if (anew == null || anew.values == null) {
        anew = makeZero(anew);
      }
      anew.set(old);
      return anew;
    }
  }

  /**
   * make a new A2Row and make the 2 ARows from the invoking A2Row
   *
   * @param a the input A2Row
   * @return the copy of rowsin including the 2 copied ARow
   */
  A2Row copy(A2Row a) {
    A2Row tem = new A2Row();
    if (a == null) {
      a = new A2Row();
    }
    for (int m : IA01) {
      tem.getRow(m).set(a.getRow(m));
    }
    tem.lev = a.lev;
    tem.titl = "cp" + a.titl;
    return tem;
  }

  /**
   * make a copy of the old ARow or a zero ARow if old is null
   *
   * @param old
   * @return copy of ARow old or a zero ARow if old is null
   */
  ARow copy(ARow old) {
    if (old == null) {
      ARow tem = new ARow().zero();
      tem.get(0);
      return tem;
    }
    else {
      return new ARow().set(old);
    }
  }

  /**
   * make a copy of an A6Row instance
   *
   * @param a the old A6Row
   * @return new A6Row with all the values of A
   */
  A6Row copy(A6Row a) {
    A6Row tem = new A6Row();
    if (a == null) {
      a = new A6Row();
    }
    int[] d15 = {0, 1, 2, 3, 4, 5};
    for (int m : d15) {
      tem.getRow(m).set(a.getRow(m));
    }
    tem.lev = a.lev;
    tem.balances = a.balances;
    tem.costs = a.costs;
    tem.titl = "cp" + a.titl;
    return tem;
  }

  /**
   * make a copy of an A10Row instance
   *
   * @param a A10Row original
   * @return A10Row copy
   */
  A10Row copy(A10Row a) {
    A10Row tem = new A10Row();
    if (a == null) {
      a = new A10Row();
    }
    int[] d19 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    for (int m : d19) {
      tem.getRow(m).set(a.getRow(m));
    }
    tem.lev = a.lev;
    tem.balances = a.balances;
    tem.costs = a.costs;
    tem.titl = "cp" + a.titl;
    return tem;
  }

  /**
   * 1/15/2016 Eliminate this in favor of make, set means something different
   * return the old ARow or a new zero ARow if old is null ARow.set... generally
   * means modify and existing ARow ARow.mult ARow.divby ... does not modify
   * ARow but creates a new one for a stacked set of methods
   *
   * @param old
   * @return old or a new zero ARow if old is null
   */
  ARow setNot(ARow old) {   // Assets.set
    if (old == null) {
      return new ARow().zero();
    }
    else if (old.values == null) {
      old.fill();
      return old;
    }
    else {
      return old;
    }
  }
  
  /** get the rN for the stats name
   * 
   * @param dname  string stats name to be found
   * @return the integer related to that name
   */
   int getStatrN(String dname){
    EM.addlErr = "setStat dname=" + dname;
    Object o1 = eM.resMap.get(dname);
    if (o1 == null) {
      System.out.printf("setStat a cannot find \"%s\" \n", dname);
      o1 = eM.resMap.get("missing name");
    } 
     return (int)o1;
   }
   
   
  /** get a units sum from the stats database, it could be for the current year or for the cunulative sum of all the years
   * 
   * @param rN           the index into the stats database
   * @param curCum       either ICUM or ICUR0 from EM
   * @param porsStart    0:start with planets, 1 start with ships
   * @param porsEnd      1. 0:1 sum just planets, 2. 0:2 sum planets & ships
   * @param clanStart    0-4 sum of the clan to start with
   * @param clanEnd      1-5 end of clan sum, 0:1,1:2 etc. 1 clan<br>
   *                     0:5 sum all of the clans
   * @return the sum of units as filtered by the selectors
   */
   int getCurCumPorsClanUnitSum(int rN,int curCum,int porsStart,int porsEnd,int clanStart,int clanEnd){
    return eM.getCurCumPorsClanUnitSum(rN,curCum,porsStart,porsEnd,clanStart,clanEnd);
  }

  /**
   * set a statistical value for later viewing assume a count of 1
   *
   * @param dname name of the statistic
   * @param pors 0=planet, 1=ship
   * @param clan clan 0-4
   * @param val the value of the statistic
   */
  void setStat(String dname, int pors, int clan, double val) {
    EM.addlErr = "setStat dname=" + dname;
    Object o1 = eM.resMap.get(dname);
    if (o1 == null) {
      System.out.printf("setStat a cannot find \"%s\" \n", dname);
      o1 = eM.resMap.get("missing name");
    }
    eM.setStat((int) o1, pors, clan, val, 1, ec.age);
    EM.addlErr = "";
  }

  /**
   * set a statistical value for later viewing
   *
   * @param dname name of the statistic
   * @param pors 0=planet, 1=ship
   * @param clan clan 0-4
   * @param val the value of the statistic
   * @param cnt the count of units 1 or 0 in certain comditions
   */
  void setStat(String dname, int pors, int clan, double val, int cnt) {
    EM.addlErr = "setStat dname=" + dname;
    int o1 = eM.resMap.get(dname);
    eM.setStat(o1, pors, clan, val, cnt, ec.age);
    EM.addlErr = "";
  }

  /**
   * set a result statistical value for later viewing
   *
   * @param rN number of the stat
   * @param pors planet or ship
   * @param clan clan of the stat
   * @param val value of the stat
   * @param cnt count 1 or 0 if another setStat will set cnt
   * @param age age of the economy Econ
   */
  void setStat(int rN, int pors, int clan, double val, int cnt, int age) {
    if (rN < NZERO) {
      eM.bErr("unknown result Name");
      return;
    }
    eM.setStat(rN, pors, clan, val, cnt, age);
  }

  /**
   * set a result statistical value for later viewing
   *
   * @param rN number of the stat
   * @param pors planet or ship
   * @param clan clan of the stat
   * @param val value of the stat
   * @param cnt count 1 or 0 if another setStat will set cnt
   * @param age age of the economy Econ
   */
  void setStat(int rN, int pors, int clan, double val, int cnt) {
    if (rN < NZERO) {
      eM.bErr("unknown result Name");
      return;
    }
    eM.setStat(rN, pors, clan, val, cnt, ec.age);
  }

  /**
   * return the old ARow or a new zero ARow if old is null This allows declaring
   * many ARow'sas that are used for only some SubAssets most of the use is in
   * CashFlow calculating costs, swaps, remnants etc.
   *
   * @param old
   * @return old or a new zero ARow if old is null
   */
  ARow make(ARow old) {   //Assets.make
    if (old == null) {
      return new ARow().zero();
    }
    else if (old.values == null) {
      old.fill();
      return old;
    }
    else {
      return old;
    }
  }

  /**
   * return old if it exists, otherwise create a new one with a level of
   * History.valuesMinor7
   *
   * @param old reference to a possibly existing A6Row
   * @param tit title for a new A6Row
   * @return
   */
  A6Row make6(A6Row old, String tit) {
    if (old == null) {
      return new A6Row(History.valuesMinor7, tit);
    }
    else {
      return old;
    }
  }

  /**
   * return old if it exists, otherwise create a new one with a level of
   * History.valuesMinor7
   *
   * @param old reference to a possibly existing A10Row
   * @param tit title of a created A10Row
   * @return
   */
  A10Row make10(A10Row old, String tit) {
    if (old == null) {
      return new A10Row(History.valuesMinor7, tit);
    }
    else {
      return old;
    }
  }

  /**
   * return a zero ARow, make one only if it is null
   *
   * @param a
   * @return this zerod
   */
  ARow makeZero(ARow a) {
    if (a != null) {
      return a.zero();
    }
    ARow aa = new ARow().zero();
    return aa;
  }

  /**
   * make and existing a zero, else create a zero valued one
   *
   * @param a
   * @return
   */
  A2Row makeZero(A2Row a) {
    if (a != null) {
      a.getARow(E.lsecs).zero();
      a.getARow(0).zero();
      return a;
    }
    A2Row aa = new A2Row(new ARow().zero(), new ARow().zero());
    return aa;
  }

  /**
   * make and existing a zero, else create a zero valued one
   *
   * @param a
   * @param titl title of the object
   * @return
   */
  A2Row makeZero(A2Row a, String titl) {
    if (a != null) {
      a.getARow(E.lsecs).zero();
      a.getARow(0).zero();
      return a;
    }
    A2Row aa = new A2Row(new ARow().zero(), new ARow().zero());
    aa.titl = titl;
    return aa;
  }

  A4Row makeZero(A4Row a) {
    if (a == null) {
      return new A4Row();
    }
    int[] d4 = {0, 1, 2, 3};
    for (int m : d4) {
      a.A[m] = make(a.A[m]).zero();
    }
    return a;
  }

  /**
   * zero or make and zero a and set titl
   *
   * @param a reference to A10Row object, if null create new object
   * @param titl title of object
   * @return A10Row object with all values zero
   */
  A10Row makeZero(A10Row a, String titl) {
    if (a == null) {
      return new A10Row(History.valuesMinor7, titl);
    }
    a.zero();
    return a;
  }

  /**
   * zero or make and zero a and set titl
   *
   * @param a reference to A6Row object, if null create new object
   * @param titl title of object
   * @return A6Row object with all values zero
   */
  A6Row makeZero(A6Row a, String titl) {
    if (a == null) {
      return new A6Row(History.valuesMinor7, titl);
    }
    a.zero();
    return a;
  }

  /**
   * flip the rc and sg row signs in a new A2Row
   *
   * @param B
   * @return flipped values of rc and sg
   */
  A2Row flip(A6Row B) {
    A2Row aa = new A2Row();
    for (int m : E.d2) {
      for (int n : ASECS) {
        aa.set(m, n, -B.get(m, n));
      }
    }
    return aa;
  }

  /**
   * receive yearStart from trade.Econ, set trand for cRand
   *
   * @param atrand the array of random numbers set in Econ by\
   * @param ahist the hist for the new year
   * @param lyears = light years set from StarTrader to Econ.yearStart
   */
  void yearStart(double[] atrand, ArrayList<History> ahist, double lYears) { // trade.Assets.yearStart
    year = eM.year;
    //   CashFlow the = cur;
    //  age++;  // initially -1, first year is 0;
    otherName = "";
    trand = atrand;   // set history array
    hist = ahist;   // set hist
    lightYearsTraveled = lYears;
    if (cur == null) {
      cur = new CashFlow(this);
      cur.cashFlowInit(this);
      cur = null;
    }

  }

  double getHealth() {
    return health;
  }

  double getWorth() {
    return sumTotWorth;
  }

  boolean getDie() {
    return died;
  }

  int getAge() {    // Assets.getAge
    return ec.age;
  }

  /**
   * list bids
   *
   * @param lev level of listing
   * @param pre prefix
   */
  void listBids(int lev, String pre
  ) {  // Assets.CashFlow.Trades
    hist.add(h1 = new History(pre, lev, "T" + term + " " + name + " bidC", bids.getARow(0)));
    hist.add(h2 = new History(pre, lev, "T" + term + " " + name + " bidG", bids.getARow(E.lsecs)));
  }

  /**
   * list bids was goods
   *
   * @param lev level of listing
   * @param pre prefix
   */
  void listGoods(int lev, String pre) {
    listBids(lev, pre);
  }

  /**
   * year end, for end of year from Econ from StarTrader
   *
   */
  void yearEnd() {  //trade.Assets
    if (cur == null) {
      cur = new CashFlow(this);
      cur.cashFlowInit(this);
    }
    cur.yearEnd();
    cur = null; // release all CashFlow storage
    // decrement cumulativeDecay, bonus years and bonus units
    for (int m : balsIxA) {
      for (int n : ASECS) {
        // decrement bonusRawUnits
        bals.getRow(ABalRows.bonusUnitsIx + m).add(n, -(bals.getRow(ABalRows.bonusYearsIx + m).get(n) < PZERO ? 0. : bals.getRow(ABalRows.bonusUnitsIx + m).get(n) / bals.getRow(ABalRows.bonusYearsIx + m).get(n)));
        // decrement bonusYears
        bals.getRow(ABalRows.bonusYearsIx + m).set(n, (bals.getRow(ABalRows.bonusYearsIx + m).get(n) > PZERO ? bals.getRow(ABalRows.bonusYearsIx + m).get(n) - 1. : 0.));
        // now increment cumulativeDecay
        bals.getRow(ABalRows.cumulativeDecayIx + m).add(n, bals.getRow(ABalRows.GROWTHSIX + m).get(n) * eM.decay[m][pors]);
      }
    }
    // Assets.yearEnd, zero yearly counters before yearStart
    tradedSuccessTrades = 0; // successful trades this year
    tradedStrategicWorths = 0.; // positive strategic worths
    tradedStrategicRealWorths = 0.; // real worths of successful trades
    tradedStrategicCosts = 0.;// 2-3 least strategic value traded
    tradedStrategicRealCosts = 0.; // 2-3 real costs of trades
    tradedManualsWorths = 0.;  // worth of manuals received in trades
    tradingOfferWorth = 0;
    // if multiple ships trade in a year, this is for the last ship
    tradedShipOrdinal = 0;
    tradedShipsTried = 0;
    // yrTradesStarted = -1;  // -1 if no trade this year
    // int[] tradedShipAccepted = new int[E.hcnt];
    tradedFav = 0.;
    tradedOFav = 0.;
    tradedFirstStrategicReceipts = 0.;
    tradedFirstReceipts = 0.;
    tradedFirstSends = 0.;
    tradedFinalStrategicReceipts = 0.;
    tradedFinalReceipts = 0.;
    tradedFinalSends = 0.;
    // double[] tradedGoodBal = new double[E.hcnt];
    // double[] tradedGoodWorth = new double[E.hcnt];
    tradeAccepted = false;
    tradeRejected = false;
    tradeLost = false;
    // int acceptedTrade = -5;  // barter number of tradeOK
    // int rejectedTrade = -6; // barter number of rejected trad
    tradedBid = null;
    tradedStrategicValue = 0.;
    tradedStrategicFrac = 0.;
    tradedMoreManuals = null;
  //  String tradedShipNames1[][] = {{"A", "B", "C", "D", "E"}, {"A", "B", "C", "D", "E"}, {"A", "B", "C", "D", "E"}, {"A", "B", "C", "D", "E"}, {"A", "B", "C", "D", "E"}};
//    tradedShipNames = tradedShipNames1;
  }

  /**
   * get the SOS flag
   *
   * @return Assets.sos
   */
  boolean getSOS() {
    return sos;
  }

  String getOtherName() {
    return otherName;
  }

  /**
   * return the guests balance ARow
   *
   * @return guests balance ARow
   */
  ARow getGuests() {
    return bals.getRow(ABalRows.BALANCESIX + ABalRows.GIX);
  }
  /** get the guest grades reference from bals
   * 
   * @return reference to guest grades
   */
  double [][] getGuestGrades(){
    return bals.getGuestGrades();
  }

  /**
   * return the cargo balances ARow reference
   *
   * @return cargo balance ARow
   */
  ARow getCargo() {
    return bals.getRow(ABalRows.BALANCESIX + ABalRows.CIX);
  }

  /** select a planet to do the trade with
   * 
   * @param wilda list of tradable planets
   * @return Econ of the selected planet
   */
  Econ SelectPlanet(Econ[] wilda){
     if (cur == null) {
      cur = new CashFlow(this);
      cur.cashFlowInit(this);
    }
    return cur.selectPlanet(wilda);
  }
  /**
   * pass on request to barter to CashFlow to Assets.CashFlow.Trades instantiate
   * CashFlow if needed
   *
   * @param inOffer the input offer
   * @return the output offer after processed by Assets.CashFlow.Trades
   */
  Offer barter(Offer inOffer) {  // Assets.barter
  
    newTradeYear1 = prevBarterYear == eM.year ? false : true;
    if (prevBarterYear != eM.year) { //a new year barter
      yrTradesStarted = eM.year;
      tradedShipOrdinal = 0;
      tradedShipsTried = 0;
      prevBarterYear = eM.year;
    }
    if (cur == null) {
      cur = new CashFlow(this);
      cur.cashFlowInit(this);
    }
    Offer myIn = cur.barter(inOffer);
    // if exit trade exit cur
    if (cur.myTrade == null) {
      cur = null;
      if (ec.clearHist()) {
        hist.clear();
      }
    }
    prevBarterYear = eM.year;
//    otherName = myIn.getOtherName();
    return myIn;
  }

  /**
   * return the current loop n
   *
   * @return
   */
  int getN() {
    if (cur == null) {
      return 0;
    }
    return cur.getN();
  }

  // end of Assets only methods
  // start subclasses
  /**
   * Assets.CashFlow This object holds one year of an economies values and the
   * processing that occurs in a year. The values are cumulative and hopefully
   * increase each year. import random values from Econ, and use them for the
   * entire year.
   *
   * Ships, select the next planet to travel to and trade. They may try to
   * caculate several years ahead, but of course planets change, so plans may
   * need revision
   *
   * At startYear The efficiencies for this year is calculated based on the
   * difficulty, priorities and amount of knowledge for each sector of the
   * economy, Minimal files are created to pass to Trades or yearEnd
   *
   * StarTrader initiates the barters (Trades) cycling through ships, which
   * selecting eligible planets. The trades trade some of the required resources
   * and staff for planets and ships, if Trades.accepted than both planet and
   * ship are strategically better with resources and staff.
   *
   * Then the Grow values are calculated for this year. With nominal grow
   * values, Raw costs are now calculated.
   *
   * The health and raw fertility are calculated. The health is the least healt
   * for any subsection of the economy Now a health penalty is applied to the
   * costs, increasing costs for health &lt; 1. decarasing costs for health &gt;
   * 1..
   *
   * Now swapping may occur, first move working resource or staff to or from
   * reserves. in emergency (health too low or fertility too low) staff can be
   * moved between sectors. Moved staff require some training to be back to
   * their former productivity repurposing resources is much more costly,
   * because repurposing is always inefficient, and costs both resources and
   * staff.
   *
   * The purpose of the growth stage is to try to end up with more value than
   * what you started with in this year. Usually, each economy has some very
   * limited (low priority) sectors with limited staff and resources, and some
   * very plentiful resources. Trades are supposed to be a way to even some of
   * this out, but that depends on where the ship was previously, and whether it
   * has a trade that the planet wants. Ships arrive with a random set of
   * resources and staff, widely varying in usefulness of Trade
   *
   * Maintenance travel and growth always cost both resource and staff. They
   * cost more when difficulty is high or knowledge is low. In addition, random
   * factors change costs from year to year.
   *
   * @author albert Steiner
   */
  class CashFlow {  // Assets.CashFlow

    // Cashflow relate to subassets
    SubAsset resource = new SubAsset(0);
    SubAsset staff = new SubAsset(2);
    SubAsset r = resource, s = staff;
    SubAsset guests = new SubAsset(3, true, staff); //reserves set partner.partner to themself
    SubAsset cargo = new SubAsset(1, true, resource);
    SubAsset[] sys = {resource, cargo, staff, guests};
    SubAsset[] partners = {cargo, resource, guests, staff};
    SubAsset[] others = {staff, guests, resource, cargo};
    SubAsset c = cargo, g = guests;
    SubAsset[] workers = {resource, staff};

    double sumNewKnowledgeWorth, sumCommonKnowledgeWorth, sumManualsWorth;
    ARow ypriorityYr = new ARow();

    // start CashFlow Swap loop variables
    boolean debugSumGrades2 = false;
    boolean flagg, flagh, flagf, flagm, prevFlagg, prevFlagh, prevFlagf, prevFlagm;
    boolean emergHs, emergHr, emergency, preveHs, preveHr, notDone;
    boolean doFailed, doLoop, done;
    boolean hFlag, gFlag, gfFlag, fFlag, geFlag, nheFlag, heFlag, gmFlag, hmFlag;
    boolean emergFs, emergFr, failed = false, hTrue = false, gTrue = false;
    boolean hEmerg = false, gEmerg = false;
    boolean incrMinProsperity, incrMinFertility, incrWorth;
    double prevMinProsperity = -100., prevMinFertility = -100, prevTotWorth = -100.;
    //0-2 inc, 3-5 decr, 6-8 xfer
    double maxMult[] = {1., .8, .5, 1., .8, .5, 1., .8, .6};
    int swapLoopMax=3;
    int unDo = 0, nn = 0, reDo = 0;
    A10Row doNot = new A10Row(lev, "doNot");
    int stopped[] = {0, 0, 0, 0, 0}, stopX[] = {5, 5, 0, 0, 0};
    boolean useMTCosts = false;
    // end loop variables
    boolean tEmerg = false;

    String yrName = "startYr";
    String lTitle = "Init";
    //  String[] sysSs = {"r", "c", "s", "g", "null"};
    //  String[] sysS = {"resource", "cargo", "staff", "guests", "null"};
    int rlev, blev; // flags for level of History and if statements for it
    int swapAlt = 0; // swapLoops%2  0,1
    int costsComp = -10;
    int costsUse = -10;  // recompute costs of costsUse > costsComp;
    double yearStartHealth = 2.0;
    double fertility = 2., minH = -.5, minFert = -.3;
    int ixArow;
    int srcIx=-2, destIx=-2, forIx=-2, ixWRFor=-2, chrgIx=-2, needIx=-2,need4Ix=-2, need3Ix=-2,sourceIx=-2;
    int rChrgIx=-2, sChrgIx=-2;
    double rChrg, sChrg;
    int swapLoops=-2, swap4Step=-2, swap7Step=-2;
    double[] catastrophyBalIncr = new double[E.hcnt];
    double[] catastrophyPBalIncr = new double[E.hcnt];
    double[] catastrophyDecayBalDecr = new double[E.hcnt];
    double[] catastrophyDecayPBalDecr = new double[E.hcnt];
    /**
     * now define ARows to carry history, see swapResults
     */
    final double doNotDays2 = 2;
    final double doNotDays3 = 3.;
    final double doNotDays5 = 5.;
    final double doNotDays100 = 100.;
    ARow yprevGrowth = new ARow();   // last years actual growth
    ARow yprevUnitGrowth = new ARow();

    ARow ylimLowSGbyR = new ARow();
    ARow ylimMidRCbyR = new ARow();
    ARow ylimMidSGbyR = new ARow();
    ARow ylimHiRCbyR = new ARow();
    ARow ylimHiSGbyR = new ARow();
    /**
     * the following used by Assets.CashFlow.swaps
     */
    final int lPrevns = 9;// in trade.Assets.CashFlow
    HSwaps[] prevprevns; // previous number swap values
    HSwaps[] prevns = new HSwaps[lPrevns];
    HSwaps[] prevgood = new HSwaps[lPrevns];
    boolean negProspects = false, negOutlook = false, negNeeds = false;
    boolean wrongIxSrc = false, doneIncr = false;
    ARow swapRtoC = new ARow();
    ARow swapCtoR = new ARow();
    ARow swapStoG = new ARow();
    ARow swapGtoS = new ARow();
    ARow xferRCtoR = new ARow();
    ARow xferSGtoS = new ARow();
    A2Row xferRCSG = new A2Row(xferRCtoR, xferSGtoS);
    ARow tneed = new ARow();
    ARow tMove = new ARow();
    ARow tRneed = new ARow();
    ARow tRMove = new ARow();
    ARow tSneed = new ARow();
    ARow tSMove = new ARow();
    ARow tRcost = new ARow();
    ARow tScost = new ARow();
    double fmov = 0, smov = 0, rmov = 0, movMin = 0, mkeep = 0;
    double rmov1 = 0., smov1 = 0.;
    double xferMovMin;

    double swapMins[] = new double[2];
    double swapMaxs[] = new double[2];
    boolean tests = true, testr = true, doxfer = false;
    History errHistory;
    SubAsset dest = staff;
    SubAsset source = guests;
    SubAsset osource = cargo;
    SubAsset rchrg = resource;
    SubAsset schrg = staff;
    SubAsset prevrchrg = resource;
    SubAsset prevschrg = staff;
    SubAsset forRes;
    /*   double fFrac = E.usrFutureMaxn[pors][clan];
     double gFrac = E.usrGrowthMaxn[pors][clan];
     double gfFrac = E.usrGrowFirstMaxn[pors][clan];
     double hFrac = E.usrHealthMaxn[pors][clan];
     double hgFrac = E.usrHealthGrowMaxn[pors][clan];
     */

    // Assets holds iyW,syW
    // in Assets.CashFlow
    DoTotalWorths btW, tW, rawCW, gSwapW, gGrowW, gCostW, fyW;
    double iyWTotWorth, syWTotWorth, btWTotWorth, tWTotWorth, rawCWTotWorth, gSwapWTotWorth, gGrowWTotWorth, gCostWTotWorth, fyWTotWorth;
    double NeedsPlusSum, NeedsNegSum, rawProspectsMin, rawProspectsMin2, rawProspectsNegSum;
    double curSum, needsSum;

    double rFutureFundDue = 0., sFutureFundDue = 0., rEmergFutFund = 0., sEmergFutFund = 0.;
    double movd = 0., rcost = 0., scost = 0., need = 0., tmin = 0.;
    double prevsrc = 0., prevdest = 0., prevosrc = 0., prevodest = 0.;
    double fracN = n / eM.maxn[pors];
    int prevn;
    double prevNextN;
    double nextN;
    double yHealthPenalty;
    double ysumPriority;

    double sumRCWorth = 0.;
    double sumSGWorth = 0.;
    double preTradeWorth = 0.;
    double postNHealth;
    double postNHealthWorth;
    double additionToKnowledgeBiasForSumKnowledge
           = eM.additionalKnowledgeGrowthForBonus[0] / 7;
    double multiplierForEfficiencyFromRequirements
           = eM.additionalKnowledgeGrowthForBonus[0] / 6;

    // required for  Assets.CashFlow.getNeeds
    int bLev = History.dl;
    double poorHealthEffect = 0., PHE = 0., totNeeds = -999999.;
    int swapType = 7;
    double[] redoFrac = {1., 1., .85, .75, .5, .3, .1, .05562};
    double mov = -1., mov1 = -1., mov2 = -2., mov3 = -1., mov4 = -1.;
    int econCnt = 0;

    double minRH, minRF;
    int minRHIx = -1, minRFIx = -1;

    A6Row worths = new A6Row(lev, "worths");
    A6Row yrStrtWorths = new A6Row(lev, "yrStrtWorths");
    // swapCosts 0,1 incr, 2,3 decr, 4,5 exchange
    int rxfers = 0, sxfers = 0; //count of continuous r or s xfers
    A6Row swapCosts = new A6Row(lev, "swapCosts");
    A2Row stratVarsHG = new A2Row();
    A2Row stratVars = new A2Row();
    A6Row rawGrowths = new A6Row(lev, "rawGrowths");
    A6Row reqMaintCosts = new A6Row(lev, "reqMaintCosts");

    //   A6Row reqMaintRemnants = new A6Row(lev, "reqMaintRemnants");
    //   A6Row reqMaintEmergNeeds = new A6Row(lev, "reqMaintEmergNeeds");
//    A6Row reqMaintNeeds = new A6Row(lev, "reqMaintNeeds");
    A6Row invMEff = new A6Row(History.valuesMinor7, "invMEff");
    A6Row invGEff = new A6Row(History.valuesMinor7, "invGEff");
    //   A6Row reqMaintFractions = new A6Row(lev, "reqMaintFractions");
    //   A6Row reqMaintLimitedFractions = new A6Row(lev, "reqMaintLimitedFractions");

    int decrCnt = 0;
    double decrGain = 0;
    double decrCost = 0;
    int typeGrow = 2;
    int typeTrade = 0;
    int typeHealth = 1;
    int flowType = 0;
    double curGrowGoal, adjGrowGoal, adjVal;
    double curMaintGoal, adjMaintGoal;
    /**
     * now cashflow variables for yCalcCosts
     */

    A6Row maintCosts = new A6Row(lev, "maintCosts");
    A6Row aYrTravelCosts = new A6Row();
    A6Row travelCosts = new A6Row(lev, "travelCosts");
    A6Row healths = new A6Row(lev, "healths");
    A6Row reqGrowthCosts = new A6Row(lev, "reqGrowthCosts");
    A6Row rawGrowthCosts = new A6Row(lev, "rawGrowthCosts");
    A6Row mtgCosts = new A6Row(lev, "mtgCosts");
    A6Row goalmtg1Needs6 = new A6Row(lev, "goalmtg1Needs6");
    A10Row goalmtg1Neg10 = new A10Row(lev, "goalmtg1Neg10");
    A6Row mNeeds = new A6Row(History.valuesMajor6, "mNeeds");
    A10Row mtggCosts10 = new A10Row(lev, "mtggCosts10");    //   A6Row mtggRemnants = new A6Row();
    //   A6Row mtggEmergNeeds = new A6Row(lev, "mtggEmergNeeds");
    A6Row mtgFertilities = new A6Row(lev, "mtgFertilities");
    //  A6Row rawGoalFertilities = new A6Row(lev, "rawGoalFertilities");
    //  A6Row rawGoalHealths = new A6Row(lev, "rawGoalHealths");
    A6Row mtggRawHealths = new A6Row(lev, "rawMTGGHealths");
    A6Row mtggRawFertilities = new A6Row(lev, "rawMTGGFertilities");
    A2Row fertilities = new A2Row(lev, "fertilities");  // mtggRemnants/reqGrowthCosts
    A6Row growthCosts = new A6Row(lev, "growthCosts");  // rawGrowthCost*fertilities
    A6Row mtggGrowthCosts = new A6Row(lev, "mtggGrowth");  // rawGrowthCost*fertilities
    A6Row mtgReqFertilities = new A6Row();
    A10Row reqMaintCosts10;
    A10Row reqGrowthCosts10;
    A10Row rawGrowthCosts10;
    A10Row maintCosts10;
    A10Row travelCosts10, mtgCosts10, mtCosts10, growthCosts10;
    A10Row consumerHealthMTGCosts10, consumerTrav1YrCosts10, consumerMaintCosts10;
    A10Row consumerReqGrowthCosts10, consumerReqMaintCosts10, consumerTravelCosts10, consumerFertilityMTGCosts10;
    A10Row consumerHealthEMTGCosts10, consumerFertilityEMTGCosts10;
    A10Row consumerRawGrowthCosts10;
    // A2Row rawHealths2;
    A2Row fertilities2;
    A2Row mtggRawProspects2;
    A2Row mtggRawFertilities2;
    A2Row mtggRawHealths2;
    A6Row mtggGrowths6 = new A6Row(lev, "mtggGrowths6");
    A6Row mtNeeds6 = new A6Row(lev, "mtNeeds6");
    //int lResults = 40;
    // double[] mtgResults = new double[lResults];
    //double[] mtggResults = new double[lResults];
    //double[] hgResults = new double[lResults];
    //double[] heResults = new double[lResults];
    //double mtggSumRemnant = 0;
    // double mtgSumRemnant = 0;
    // A6Row reqMaintRawFractions = new A6Row();
    // double mtgResults10[] = new double[lResults];
    //double mtggResults10[] = new double[lResults];
    A6Row growths10;
    A6Row mtggNeeds6, mtGNeeds6;
    A6Row invMEfficiency = new A6Row(lev, "invMEfficiency");
    A6Row invGEfficiency = new A6Row(lev, "invMEfficiency");
    double maxAvail = 0.; // max available for a given swap
    double maxavail1, maxavail2, maxavail3, maxavail4;

    /**
     * Assets.CashFlow start to add costs that apply to the R and S balances
     * first the maintenance and travel costs with the health penalty applied
     * yRMTNPCost etc Than the growth cost with RawFertility for this sector
     * applied then the healthPenalty give yRMTLimitedGNoPenaltyCost etc
     * remnants are these costs subtracted from R and S, note that costs for C
     * apply to yR... and costs for G apply to yS... finally r.tRemnant and
     * s.tRemnant is the balance left after the costs
     */
    ARow yR2MTNPRemnant = new ARow();
    ARow yS2MTNPRemnant = new ARow();
    ARow yRMTLimitedGNoPenaltyCost = new ARow();
    ARow ySMTLimitedGNoPenaltyCost = new ARow();
    ARow yRMTCostsHPenRemnant = new ARow();
    ARow ySMTCostsHPenRemnant = new ARow();
    ARow ySReqGrowthMTNPCost = new ARow();
    ARow yWReqGrowthMTNPCost = new ARow();
    ARow yRReqGrowthMTNPRemnant = new ARow();
    ARow yWReqGrowthMTNPRemnant = new ARow();
    ARow yWtoSFrac = new ARow();  // balance divby work
    ARow yRRawGrowthMTNPRemnant = new ARow();
    ARow ySRawGrowthMTNPRemnant = new ARow();
    ARow yRRawGrowthHPenRemnant = new ARow();
    ARow ySRawGrowthHPenRemnant = new ARow();
    ARow yIovrJRRawGMTCosts = new ARow();
    ARow yIovrJSRawGMTCosts = new ARow();
    ARow yRRawMTGHPenRemnant = new ARow(); // full growth remnant
    ARow ySRawMTGHPenRemnant = new ARow();
    ARow yRMTFRemnant = new ARow(); // fertility growth remnant
    ARow ySMTFRemnant = new ARow();
    ARow ySLimitedGHPenCosts = new ARow();
    ARow yRMTLimitedGHPenRemnant = new ARow();
    ARow yS2MTLimitedGHPenRemnant = new ARow();
    ARow yR2MTLimitedGHPenRemnant = new ARow();
    ARow yRReqGrowthMTHPenCost = new ARow();
    ARow ySReqGrowthMTHPenCost = new ARow();
    ARow yRReqGrowthMTHPenRemnant = new ARow();
    ARow ySReqGrowthMTHPenRemnant = new ARow();
    ARow yWReqMaintHealth = new ARow();
    ARow yCCostsRemnant = new ARow();
    ARow ySVCostsRemnant = new ARow();
    ARow yRFuture1 = new ARow();   // future 1 more year
    ARow ySFuture1 = new ARow();
    ARow yRFuture2 = new ARow();
    ARow ySFuture2 = new ARow();

    E.SwpCmd cmd = E.SwpCmd.NONE;
    boolean swapped = false;
    ARow rneed = new ARow(); // temp value for a given swap
    ARow sneed = new ARow();  // temp value for a given swap
    ARow yrneed = new ARow();
    ARow ysneed = new ARow();
    A2Row yNeed = new A2Row(yrneed, ysneed);
    ARow ymove = new ARow();

    // Assets.CashFlow
    boolean donext = false, sEmerg = false, rEmerg = false;
    double rresmult, sresmult, rresmult2, sresmult2;
    ARow wtdRtoC = new ARow();
    ARow wtdStoG = new ARow();
    A2Row wtdRS = new A2Row(wtdRtoC, wtdStoG);
    A6Row rawUnitGrowths;
    // HCashFlow prev1n, prev2n, prev3n, prev4n, prev5n, prev6n, prev7n;
    // HCashFlow[] startYrs; // might use instead of name 1,2 ...

    // in Assets.CashFlow 
    HSwaps xitCalcCosts;
//    A2Row yRemnant = new A2Row(r.tRemnant, ySRemnant);

    Offer myOffer;
    Offer oOffer;
    Offer newOffer;

    ARow yRAvail = new ARow();
    ARow ySAvail = new ARow();
    //Assets.CashFlow
    Assets as;
    NumberFormat dFrac = NumberFormat.getNumberInstance();
    NumberFormat whole = NumberFormat.getNumberInstance();
    NumberFormat dfo = dFrac;

    CashFlow(Assets aas) {
      System.out.flush();
      System.out.flush();
      System.err.flush();
      StackTraceElement a1 = Thread.currentThread().getStackTrace()[1];
      StackTraceElement a2 = Thread.currentThread().getStackTrace()[2];
      StackTraceElement a3 = Thread.currentThread().getStackTrace()[3];
      StackTraceElement a4 = Thread.currentThread().getStackTrace()[4];
      System.out.println("CF construct " + name + " at " + a4.getMethodName() + ", " + a3.getMethodName() + ", " + a2.getMethodName() + " wealth=" + df(wealth));
      //    System.out.println("CashFlow(Assets) " + name + " constructor");
      as = aas;

    } // end constructor of CashFlow
    
    String df(double v){return ec.mf(v);}

    /**
     * an object which derives many total sums at the time of instantiation
     *
     * @note sumTotWorth
     * @note sumRCWorth
     * @note sumSGWorth
     * @note sumCommonKnowledgeBal
     * @note sumCommonKnowledgeWorth
     * @note sumeNewKnowledgeWorth
     * @note sumKnowledgeWorth
     * @note sumManualsWorth
     * @note sumRBal;
     * @note sumCBal;
     * @note sumRCBal;
     * @note sumSBal;
     * @note sumGBal;
     * @note sumSGBal; Wnote sumRCSGBal;
     */
    class DoTotalWorths { // Assets.CashFlow.DoTotalWorths

      DoTotalWorths now, prev;
      // process to remember the previous 
      double sumSBal = staff.sumGrades();
      double sumGBal = guests.sumGrades();
      double sumRCBal = bals.getRow(ABalRows.BALANCESIX + ABalRows.RCIX).sum();
      double sumSGBal = bals.getRow(ABalRows.BALANCESIX + ABalRows.SGIX).sum();
      double minSGBal = bals.getRow(ABalRows.BALANCESIX + ABalRows.SGIX).min();
      double minRCBal = bals.getRow(ABalRows.BALANCESIX + ABalRows.RCIX).min();
      double sumRBal = bals.getRow(ABalRows.BALANCESIX + ABalRows.RIX).sum();
      double sumCBal = bals.getRow(ABalRows.BALANCESIX + ABalRows.CIX).sum();
      ;
      double minGBal = bals.getRow(ABalRows.BALANCESIX + ABalRows.GIX).min();
      double minCBal = bals.getRow(ABalRows.BALANCESIX + ABalRows.CIX).min();
      double minSBal = bals.getRow(ABalRows.BALANCESIX + ABalRows.SIX).min();
      double minRBal = bals.getRow(ABalRows.BALANCESIX + ABalRows.RIX).min();
      double[] sumBals = {sumRCBal, sumSGBal, sumRBal, sumCBal, sumSBal, sumGBal};
      double[] sumMins = {minRCBal, minSGBal, minRBal, minCBal, minSBal, minGBal};
      double sumRCSGBal = sumRCBal + sumSGBal;
      double sumTotBalances = sumRCSGBal;
      double sumSWorth = staff.worth.sum();
      double sumGWorth = guests.worth.sum();
      double sumRWorth = sumRBal * eM.nominalWealthPerResource[pors];
      double sumCWorth = sumCBal * eM.nominalWealthPerResource[pors] * eM.cargoWorthBias[0];
      double sumRCWorth = sumRWorth + sumCWorth;
      double sumSGWorth = sumSWorth + sumGWorth;
      double[] sumWorths = {sumRCWorth, sumSGWorth, sumRWorth, sumCWorth, sumSWorth, sumGWorth};
      A6Row totBalances = bals.getBalances(History.valuesMinor7, "balances");
      //  double[] worthPerSubBal = {eM.nominalWealthPerResource[pors], eM.nominalWealthPerResource[pors] * eM.cargoWorthBias[0], .3, .3}; //finish at instantiation
      double cash;
      double sumCommonKnowledgeBal = make(commonKnowledge).sum();
      double sumCommonKnowledgeWorth = sumCommonKnowledgeBal * eM.nominalWealthPerCommonKnowledge[pors];
      double sumNewKnowledgeBal = make(newKnowledge).sum();
      double sumNewKnowledgeWorth = sumNewKnowledgeBal * eM.fracNewKnowledge[0] * eM.nominalWealthPerCommonKnowledge[pors];
      double sumKnowledgeBal = sumCommonKnowledgeBal + sumNewKnowledgeBal;
      double sumKnowledgeWorth = sumCommonKnowledgeWorth + sumNewKnowledgeWorth;
      double sumManualsBal = manuals.sum();
      double sumManualsWorth = sumManualsBal * eM.manualFracKnowledge[pors] * eM.nominalWealthPerCommonKnowledge[pors];
      ;
      double sumTotWorth = sumRCWorth + sumSGWorth + cash + sumKnowledgeWorth + sumManualsWorth;
      ;
      A6Row totGrowths = bals.getGrowths(History.valuesMinor7, "totGrowths");
      double sumTotGrowths = totGrowths.curSum();
      A6Row totRawGrowths; // = A6Rowa.copy6(ABalRows.rawGrowths, "rawGrowths").copy(History.valuesMinor7);
      //    double difGrowthsWorth;
      double sumTotGrowthCosts = make10(growthCosts10, "growthCosts10").curSum();
      double difWorth;
      A6Row totInvMEff = invMEfficiency;
      double sumTotInvMEff = totInvMEff.curSum();
      A6Row totInvGEff = invGEfficiency;
      double sumTotInvGEff = totInvGEff.curSum();
      A6Row totCumDecay = bals.getCumDecay(History.valuesMinor7, "totCumDecay");
      double sumTotCumDecay = totCumDecay.curSum();
      double[] sumTotBonusValues = {r.bonusUnitGrowth.sum(), s.bonusUnitGrowth.sum()};
      int sumTotBonusYears = (int) (r.bonusYears.sum() + s.bonusYears.sum());
//DoTotalWorths iyW, syW, tW, rawCW,gSwapW, gGrowW, gCostW, fyW;

      DoTotalWorths() {
        /*
        staff.sumGrades();  // calc staff.worth
        sumSBal = staff.balance.sum();
        guests.sumGrades();  // calc guests.worth
        sumGBal = guests.balance.sum();
        //    sumGrades calculates worth based on grade worths, fraction varies
    //    worthPerSubBal[0] = eM.nominalWealthPerResource[pors];
    //    worthPerSubBal[1] = eM.nominalWealthPerResource[pors] * eM.cargoWorthBias[0];
     //   worthPerSubBal[2] = s.worth.sum() / s.balance.sum();
     //   worthPerSubBal[3] = guests.worth.sum() / guests.balance.sum();
        sumSGWorth = staff.worth.sum() + guests.worth.sum();
        resource.worth.setAmultV(resource.balance, eM.nominalWealthPerResource[pors]);
        sumRBal = r.balance.sum();
        sumCBal = c.balance.sum();
        sumRCBal = sumRBal + sumCBal;
        sumSGBal = sumSBal + sumGBal;
        sumRCSGBal = sumRCBal + sumSGBal;
        cargo.worth.setAmultV(cargo.balance, eM.nominalWealthPerResource[pors] * eM.cargoWorthBias[0]);
        sumRCWorth = resource.worth.sum() + cargo.worth.sum();
        sumCommonKnowledgeWorth = eM.nominalWealthPerCommonKnowledge[pors] * (sumCommonKnowledgeBal = commonKnowledge.sum());
        sumManualsWorth = (sumManualsBal = manuals.sum()) * eM.manualFracKnowledge[pors] * eM.nominalWealthPerCommonKnowledge[pors];
        sumNewKnowledgeWorth = eM.fracNewKnowledge[0] * eM.nominalWealthPerCommonKnowledge[pors] * (sumNewKnowledgeBal = newKnowledge.sum());
        sumKnowledgeWorth = sumCommonKnowledgeWorth + sumNewKnowledgeWorth;
        sumKnowledgeBal = sumCommonKnowledgeBal + sumNewKnowledgeBal;
        sumTotWorth = sumRCWorth + sumSGWorth + cash + sumKnowledgeWorth + sumManualsWorth;
        if (growths == null) {
          totGrowths = new A6Row();
        } else { totGrowths = growths.copy();}
        sumTotGrowths = totGrowths.curSum();
        if (totRawGrowths == null) {
          totRawGrowths = new A6Row();
        }
        sumTotRawGrowths = totRawGrowths.curSum();
        if (invMEff == null) {
          invMEff = new A6Row();
        }
        sumInvMEff = invMEff.curSum();
        if (invGEff == null) {
          invGEff = new A6Row();
        }
        sumInvGEff = invGEff.curSum();
        if (totCumDecay == null) {
          totCumDecay = new A6Row();
        }
        sumTotCumDecay = totCumDecay.curSum();
         */
        // set prev to the previous now = this; if null prev = this;
        if (now != null) {
          prev = now;
        } // set up prev pointer
        else {
          prev = this;
        }
        now = this;
      } // end DoTotalWorth()

      /**
       * get fraction of sum of SG balances divided by sum of RC balances
       *
       * @return sumSGBal/sumRCBal
       */
      double getSGFracRCSum() {
        return sumSGBal / sumRCBal;
      }

      /**
       * get the measure of SG min against SG ave
       *
       * @return ((sumSGBal*E.invL2secs - minSGBal)/sumSGBal)*E.invL2secs;
       */
      double getSGMinFracAve() {
        return ((sumSGBal * E.invL2secs - minSGBal) / sumSGBal) * E.invL2secs;
      }

      /**
       * get the fraction of the difference of a balance since difPrev by
       * fracPrev
       *
       * @param iX index of element: RCIX,SGIX,RIX,CIX,SIX,GIX
       * @param difPrev the previous DoTotalWorths
       * @param fracPrev the standard divisor
       * @return (sumBals[2+iX] - difPrev.sumBals[2+iX])/
       * fracPrev.sumBals[2+iX];
       */
      double getSumIXDifPrevFracPrev(int iX, DoTotalWorths difPrev, DoTotalWorths fracPrev) {
        return (sumBals[2 + iX] - difPrev.sumBals[2 + iX]) / fracPrev.sumBals[2 + iX];
      }

      /**
       * get the fraction of the difference of a minimum since difPrev by
       * fracPrev
       *
       * @param iX index of element: RCIX,SGIX,RIX,CIX,SIX,GIX
       * @param difPrev the previous DoTotalWorths
       * @param fracPrev the standard divisor
       * @return (sumMins[2+iX] - difPrev.sumMins[2+iX])/ fracPrev.sumMins[2+iX]
       */
      double getMinIXDifPrevFracPrev(int iX, DoTotalWorths difPrev, DoTotalWorths fracPrev) {
        return (sumMins[2 + iX] - difPrev.sumMins[2 + iX]) / fracPrev.sumMins[2 + iX];
      }

      /**
       * get the fraction of the difference of a worth since difPrev by fracPrev
       *
       * @param iX index of element: RCIX,SGIX,RIX,CIX,SIX,GIX
       * @param difPrev the previous DoTotalWorths
       * @param fracPrev the standard divisor
       * @return sumWorths[2+iX] - difPrev.sumWorths[2+iX])/
       * fracPrev.sumWorths[2+iX]
       */
      double getWorthIXDifPrevFracPrev(int iX, DoTotalWorths difPrev, DoTotalWorths fracPrev) {
        return (sumWorths[2 + iX] - difPrev.sumWorths[2 + iX]) / fracPrev.sumWorths[2 + iX];
      }

      /**
       * get this instance of sumTotWorth
       *
       * @return sumTotWorth
       */
      double getTotWorth() {
        return sumTotWorth;
      }

      /**
       * get this instance of Knowledge Balance
       *
       * @return knowledge sum
       */
      double getKnowledgeBal() {
        return sumKnowledgeBal;
      }

      /**
       * get this instance of commonKnowledge balance
       *
       * @return commonKnowledge sum
       */
      double getCommonKnowledgeBal() {
        return sumCommonKnowledgeBal;
      }

      /**
       * get this instance of newKnowledge Balance
       *
       * @return newKnowledge sum
       */
      double getNewKnowledgeBal() {
        return sumNewKnowledgeBal;
      }

      /**
       * get this instance of manuals Balance
       *
       * @return manuals sum
       */
      double getManualsBal() {
        return sumManualsBal;
      }

      /**
       * get this instance of manuals Worth
       *
       * @return sumManualsWorth sum
       */
      double getManualsWorth() {
        return sumManualsWorth;
      }

      /**
       * get this instance of newKnowledge Worth
       *
       * @return sumNewKnowledgeWorth sum
       */
      double getNewKnowledgeWorth() {
        return sumNewKnowledgeWorth;
      }

      /**
       * get this instance of KnowledgeWorth Balance
       *
       * @return sumNewKnowledgeWorth sum
       */
      double getKnowledgeWorth() {
        return sumKnowledgeWorth;
      }

      /**
       * get this instance of commonKnowledge Worth
       *
       * @return sumCommonKnowledgeWorth sum
       */
      double getCommonKnowledgeWorth() {
        return sumCommonKnowledgeWorth;
      }

      /**
       * get this instance of SG Balance Sum
       *
       * @return SG Balance Sum
       */
      double getSGBal() {
        return totBalances.getRow(1).sum();
      }

      /**
       * get this instance of S Balance Sum
       *
       * @return S Balance Sum
       */
      double getSBal() {
        return totBalances.getRow(4).sum();
      }

      /**
       * get this instance of G Balance Sum
       *
       * @return G Balance Sum
       */
      double getGBal() {
        return totBalances.getRow(5).sum();
      }

      /**
       * get this instance of RC Balance sum
       *
       * @return RC Balance Sum
       */
      double getRCBal() {
        return totBalances.getRow(0).sum();
      }

      /**
       * get this instance of R Balance sum
       *
       * @return R Balance Sum
       */
      double getRBal() {
        return totBalances.getRow(2).sum();
      }

      /**
       * get this instance of C Balance sum
       *
       * @return C Balance Sum
       */
      double getCBal() {
        return totBalances.getRow(3).sum();
      }

      /**
       * get RC sum difference from the prev instance
       *
       * @return (rc - prev rc) / prev rc
       */
      double getRCDif() {
        return (getRCBal() - prev.getRCBal()) / prev.getRCBal();
      }

      /**
       * get R sum difference from the prev instance
       *
       * @return (r - prev r) / prev r
       */
      double getRDif() {
        return (getRBal() - prev.getRBal()) / prev.getRBal();
      }

      /**
       * get C sum difference from the prev instance
       *
       * @return (c - prev c) / prev c
       */
      double getCDif() {
        return (getCBal() - prev.getCBal()) / prev.getCBal();
      }

      /**
       * get SG sum difference from the prev instance
       *
       * @return (sg - prev sg) / prev sg
       */
      double getSGDif() {
        int n = 1;
        return (getSGBal() - prev.getSGBal()) / prev.getSGBal();
      }

      /**
       * get S sum difference from the prev instance
       *
       * @return (s - prev s) / prev s
       */
      double getSDif() {
        int n = 1;
        return (getSBal() - prev.getSBal()) / prev.getSBal();
      }

      /**
       * get G sum difference from the prev instance
       *
       * @return (g - prev g) / prev g
       */
      double getGDif() {
        int n = 1;
        return (getGBal() - prev.getGBal()) / prev.getGBal();
      }

      /**
       * get a reference to Balances
       *
       * @return internal Balances
       */
      A6Row getTBalances() {
        return totBalances;
      }

      DoTotalWorths setPrev(DoTotalWorths aPrev) {
        return prev = aPrev;
      }

    }  // end class DoTotalWorth

    /**
     * start declaration of subclass SubAsset the subclass has access to all
     * methods and objects in Assets and Assets.CashFlow, but does not contain them in each instance.
     * as if it were and extension of Assets.
     * <p>The SubAssets are contained by Assets.CashFlow both class instances are 
     * short lived so that all of their variables are instanciated for the whole game.
     * Enduring values are held in the Assets class and are then referenced in instances of Assets.CashFlow and Assets.CashFlow.SubAsset.
     * <P>Four instances of SubAssets are created in each instance of CashFlow.  They are <d><dt>resource or r</dt><dd>working resources like steel, corn,oxygen,coal etc.  Annual depreciation of resource occurs primarily on working resources, and working resources may be increased each year if growth is possible. Only working resources can be used to pay costs.</dd>
     * <dt>cargo or c</dt><dd>reserved resource, not part of a building or actively part of commerce.  However, it is the resource that can is traded. Only a smaller amount of annual depreciation and growth.  The cargo unit worth is  less than the working resource, and unit costs are less than working.</dd>
     * <dt>staff or s</dt><dd>Working colonists, nothing is done without staff, Staff are in 16 different grades. 
* The grades represent stages of development of staff, usually advancing 1 stage a year.  There are 4 groups of grades with 4 grades in each group.
* <ul><li>Colonists start as babies which do no work, up to intern learning how to work.</li>
* <li>Engineer: able to do increasingly complex work</li>
* <li>Faculty: can work, but mainly teach enabling staff to advance one or more grades each year</li>
* <li>Researcher: some work, but their primary job is to increase knowledge.  Increased knowledge reduces depreciation each year and increases growth<li>
* <ul>Blame this process on my 50 years working as IT engineer at universities</dd>
* <dt>guests or g</dt><dd>reserved staff with a parallel set of grades which do not contribute to any SubAsset value except for unit worth, but only working staff can pay costs.  As a reserve the associated unit costs are less.  Trades are done with guests not staff.</dd>
* <dt>knowledge common, new and manuals</dt><dd>The goal of the game is to increase units of resource and staff, and also increase the forms of knowledge for each financial sector.  Common knowledge is essentially the knowledge most planets have, new knowledge is developed by faculty and researchers and becomes common knowlege in the next year, manuals have worth but must be transformed into common knowledge by faculty and researchers.  Knowledge is the sum of common knowledge and new knowledge, increases in knowlege make years more efficient reducing the cost of travel, maintenencce and growth.  Knowledge cannot be traded to travel between stars, only knowledge as manuals can be transported, and must then be learned before it becomes common knowledge.</dd>
* <dt>random activity</dt><dd>A level of random activity is set the the game master, but each clan master can also set a clan random activity level which is added to that of the game.  The two levels influence the size of random activities, and remains constant throughout a given year.  Random activity changes costs, growth, evaluation of trades an catastrophies.  A catastrophy involves some size of loss of resources and staff</dl>
     */
    class SubAsset { // Assets.CashFlow.SubAsset

      Assets as1;
      boolean sstaff = false;
      boolean reserve = false;
      int sIx, subIx; // the index number for some of the tables
      String aschar;
      String aName = "";
      SubAsset partner; // the other reserve or working subyr with this year
      SubAsset other; // sas <-> partner, sac <-> sag
      SubAsset oPartner;

      ARow balance = new ARow();
      ARow nnbalanceWithPartner = new ARow();
      ARow bonusUnitGrowth = new ARow();
      ARow yearlyUnitGrowth = new ARow();
      ARow rawUnitGrowth = new ARow();
      ARow rawUnitGrowthAfterDecay = new ARow();
      ARow cumulativeDecay = new ARow();
      ARow rawGrowth = new ARow();
      ARow fGrowth; // based on fertility function
      ARow growth = new ARow();  // based on balance -hptMTGGCostsLG
      //    ARow nominalGrowth;
      //     ARow emergencyGrowth;
      //     ARow growFull;
      ARow avail = new ARow();   // only for working SubAsset
      ARow availWithPartner = new ARow();
      ARow reserved = new ARow();
      
      

      /**
       * now calculate the SubAsset possible Fertility and Growth The
       * calculation in Asset includes balance - R and C growthRequirement costs
       * / R + C growth Requirements the S Fertility is calculated similarly / R
       * Growth Requirement Variables are declared not defined, because most
       * uses of the class use only a few of the variables, only the CashFlow
       * Cur uses all variable variable are defined with the "make" and
       * "makeZero" methods of Assets Already defined variables are simply used
       * without a new. Assets "copy" is used to new variables than copy their
       * values
       */
      ARow health;
      //(balance-tReqGrowthCosts)/tReqGrowthCosts
      ARow reqRawFertility;
      // E.minFertility <= reqRawFertility <= E.maxFertility
      ARow reqFertility;
      ARow fertility;

      ARow gFertility;  // fertility with the goal
      ARow wFertility;  // fertility with 1 whole goal
      ARow eFertility;  // fertility with emergency goal
      double goalFertility; // E.goalFertility[pors][clan]
      ARow tRawFertility;
      ARow health1;
      ARow fertility1;
      ARow fertility2;
      //     ARow jGrowth;
      // ARow tRawGrowth;
      //   ARow hptcosts;// total costs with health penalty hpt...
      // Prefix hp = healthPenalty, np=no penalty
      ARow nptgrowthCosts;// total grow costs without health penalty npt
      ARow nptgrowthCosts1;
      //    ARow nptgrowthCosts2;
      //   ARow nptgrowthCosts3;
      ARow tReqGrowthCosts;
      ARow tReqGrowthCosts1;
      ARow gReqGrowthCosts;  //Growth Costs with the Coal Applied
      ARow wReqGrowthCosts;  //Growth Costs with the Coal Applied
      ARow eReqGrowthCosts;  //Growth Costs with emergency Applied
      ARow tReqMaintCosts;
      ARow tReqRawMaintCosts;
      ARow gReqMaintCosts;
      ARow gReqRawMaintCosts;
      ARow wReqMaintCosts;
      ARow wReqRawMaintCosts;
      ARow eReqMaintCosts;
      //    ARow tReqMaintCosts2;
      //  ARow tReqMaintCosts3;
      //    ARow tReqMaintCosts4;
      ARow nptT1yrCosts;
      ARow nptT1yrCosts1;
      //   ARow nptT1yrCosts2;
      //  ARow nptT1yrCosts3;

      ARow nptRawGrowthCosts;
      ARow nptRawGrowthCosts1;
      ARow nptMTRawGrowthCosts;
      // Prefix hp = healthPenalty, np=no penalty
      ARow hptRawGrowthCosts;
      ARow hpgRawGrowthCosts;
      ARow hpwRawGrowthCosts;
      ARow hpeRawGrowthCosts;
      //   ARow hptRawGrowthCosts1;

      //    ARow nptRawGrowthCosts2;
      //    ARow nptRawGrowthCosts3;
      /**
       * goal and emergency costs are used in swapping and trading in swapping,
       * transmute/repurposed can be done below emergency or goal values
       * swapping may try to reach whole 100% health/growth values if resources
       * and time allow. in trading, attempt to trade goals, but if some
       * resource/staff is in emergency, allow major resources (not staff) to
       * trade down to emergency level.
       */
      ARow hptGrowthCosts;  // Prefix hp = healthPenalty, np=no penalty
      ARow hpgGrowthCosts;  // Prefix hp = healthPenalty, g=Goal cost
      ARow hpwGrowthCosts;  // Prefix hp = healthPenalty, w=whole(1) cost
      ARow hpeGrowthCosts;  // Prefix hp = healthPenalty, emergency cost
      ARow hptMaintCosts;
      ARow hpgMaintCosts;
      ARow hpwMaintCosts;
      ARow hpeMaintCosts;
      ARow hptTravCosts;
      ARow hptMTRawGrowthCosts;
      ARow hpgMTRawGrowthCosts;
      ARow hpwMTRawGrowthCosts;
      ARow hpeMTRawGrowthCosts;
      ARow hptMTReqGrowthCosts;
      ARow hpgMTReqGrowthCosts;
      ARow hpwMTReqGrowthCosts;
      ARow hpeMTReqGrowthCosts;
      ARow hptMTCosts;
      ARow hpgMTCosts;
      ARow hpwMTCosts;
      ARow hptGCosts;
      ARow hpgGCosts;
      ARow hpwGCosts;
      ARow hpeGCosts;
      ARow hptMTGCosts;
      ARow hpgMTGCosts;  // costs with Growth Goal
      ARow hpgMTGCostsLG;// Goal Costs less Growth
      ARow hpwMTGCosts;  // costs with Growth Goal
      ARow hpwMTGCostsLG;// Goal Costs less Growth
      ARow hpeMTGCosts;  // costs with Growth Goal
      ARow hpeMTGCostsLG;// Goal Costs less Growth
      ARow hptMTGCosts2;
      ARow tFuthpMTGCosts;
      //   ARow nptReqGrowthRemnant;
      ARow tReqGrowthFertility;
      ARow tReqGrowthRemnant;
      ARow gReqGrowthRemnant;
      ARow wReqGrowthRemnant;
      ARow eReqGrowthRemnant;
      ARow hptMTReqGrowthFertility;
      ARow hptRawGrowthRemnant;
      ARow hpwRawGrowthRemnant;
      ARow hpgRawGrowthRemnant;
      ARow hpeRawGrowthRemnant;
      ARow tReqMaintRemnant;
      ARow eReqMaintRemnant;
      ARow wReqMaintRemnant;
      ARow gReqMaintRemnant;
      ARow tReqMaintHealth;
      ARow wReqMaintHealth;
      ARow gReqMaintHealth;
      ARow eReqMaintHealth;
      ARow nptRawMaintCosts;
      ARow nptMaintCosts;
      ARow npgMaintCosts;
      ARow npwMaintCosts;
      ARow npeMaintCosts;
      ARow nptMaintRemnant;
      ARow hptMaintRemnant;
      ARow nptT1yrRemnant;
      ARow hptT1yrCosts;
      ARow hptT1yrRemnant;
      ARow nptTravCosts;
      ARow nptTrav1yrRemnant;
      ARow nptRawGrowthRemnant;
      //     ARow nptMTRawGrowthRemnant;
      ARow hptMTRawGrowthRemnant;
      //      ARow hptMTRawGrowthRawFertility;
      ARow hptMTRawGrowthFertility;
      ARow hptRawGrowthFertility;
      //    ARow hptTravRemnant;
      ARow nptMTCosts;
      ARow nptMTRemnant;
      //    ARow tMTHealth;
      //    ARow hptMTRemnant;
      //  ARow tMTFertility = new ARow();
      ARow hptMTGRemnant;
      ARow hptMTGRemnant2;
      ARow tMTGFertility = new ARow();

      ARow hptMTGGRemnant;
      ARow posRemnantWithPartner = new ARow();
      ARow remnantWithPartner = new ARow();
      ARow lowResWithPartner = new ARow();
      ARow highResWithPartner = new ARow();
      ARow lowReservedWithPartner = new ARow();  // subtract costs
      ARow highReservedWithPartner = new ARow();
      ARow lowReserved;
      ARow highReserved;
      ARow tHealthRemnant = new ARow();
      ARow tWellnessRemnant = new ARow();
      ARow tFertilityRemnant = new ARow();
      ARow tRemnant = new ARow();
      ARow AvailRemnant;

      ARow tFutRemnant;

      ARow tFutWithPartnerRemnant;
      ARow tFutLowReservedWithPartner;
      ARow tFutHighReservedWithPartner;

      // ARow yrScost = new ARow();
      // ARow cumXcost = new ARow();
      // ARow cumScost = new ARow();
      //  ARow cumtcosts = new ARow();
      //    ARow hpmtCosts;
      //   ARow npmtCosts;
       // Assets.CashFlow.SubAsset
      int lgrades2 = E.lgrades;
      double[][] grades = new double[E.lsecs][lgrades2];
      ARow work;
      // Costs to staff are in terms of work, not balance
      //  do value * balance / work to convert or
      //  do value * workToBalance  =  balance / work
      ARow workToBalance; // convert work based remnant to balance remnant
      ARow facultyEquiv = new ARow();
      ARow researcherEquiv = new ARow();
      ARow manualsToKnowledgeEquiv = new ARow();
      ARow colonists = new ARow();
      ARow engineers = new ARow();
      ARow faculty = new ARow();
      ARow researchers = new ARow();
      ARow maintCost = new ARow();
      ARow travelCost = new ARow();
      ARow requiredForMaint = new ARow();
      ARow requiredForGrowth = new ARow();
      ARow maintEfficiency = new ARow();
      ARow groEfficiency = new ARow();
      ARow invMaintEfficiency = new ARow();
      ARow invGroEfficiency = new ARow();
      // ARow cumulativeCost = new ARow();
      ARow worth = new ARow();

      /**
       * Temp Values, don't copy for year
       *
       */
      /**
       * availTMRemnant available - poorHealthEffect adjustments on T & M
       */
      ARow rAvailTMRemnant; // r available - T M costs adjusted by poorHealthEffect
      ARow sAvailTMRemnant; // s
      ARow maxLeft;
      ARow need;

      ARow yYrStrtWorth;
      ARow prevGrowth = new ARow();   // last years actual growth
      ARow prevUnitGrowth;
      ARow prevWorth;

      ARow prevHealth;
      ARow prevFertility;
      ARow prevNeed;
      ARow prevMaxLeft;
      ARow bonusYears = new ARow();
      
      /** data format using the method in Econ
       * 
       * @param dd  value to be formatted
       * @return string value
       */
      String df(double dd) { return ec.df(dd);}

      /**
       * constructor Assets.CashFlow.SubAsset
       *
       * @param asIx an index counter used for fchar and other arrays
       * @param areserve boolean true of a reserve not working SubAsset
       * @param apartner partner of this SubAsset
       *
       * requires a paired call by method setPartner
       */
      public SubAsset(int asIx) {   // Assets.CashFlow.SubAsset
        reserve = false;
        sIx = asIx;
        as1 = as;
      }

      public SubAsset(int asIx, boolean areserve, SubAsset apartner) {
        // Assets.CashFlow.SubAsset
        reserve = areserve;
        sIx = sIx = asIx;
        partner = apartner;
        partner.partner = this;
        as1 = as;
      }

      void initResource(int sIx, boolean reserve, SubAsset partner, double resources) {
        sstaff = false;
        aName = "resource";
        other = staff;
        oPartner = guests;
         
        initSubAsset(sIx, reserve, partner);
        if(!assetsInitialized)for (int m = 0; m < E.lsecs; m++) {
          balance.set(m, resources * ypriorityYr.get(m) / ypriorityYr.sum(), "initial balance for each sector");
        }
        hist.add(new History("&&", 5, "Init Res Bal", resource.balance));
      }

      void initCargo(int sIx, boolean reserve, SubAsset partner, double resources) {
        aName = "cargo";
        initSubAsset(sIx, reserve, partner);
        sstaff = false;
        other = guests;
        oPartner = staff;
        if(!assetsInitialized)for (int m = 0; m < E.lsecs; m++) {
          balance.set(m, resources * ypriorityYr.get(m) / ypriorityYr.sum(), "initial balance for each sector");
        }
        hist.add(new History("&&", 6, "Init Cargo Bal", cargo.balance));
      }

      void initStaff(int asIx, boolean reserve, SubAsset partner, double initCol) {
        aName = "staff";
        initSubAsset(asIx, reserve, partner);
        sstaff = true;
        other = resource;
        oPartner = cargo;
        // ARow resourceStaff = new ARow();
        // for the grades sum to math the staff for that sector

        grades = bals.getStaffGrades(); // reset ref grades from bals
        boolean initedGrades = didCashFlowInit || grades[0][0] > 0. || grades[0][1] > 0 || grades[0][5] > 0.;
        if (!initedGrades) {
          double sumAssignments = 0.;
          for (int j = 0; j < E.lgrades; j++) {
            sumAssignments += E.initStaffAssignmentPerEcon[pors][j];
          }
          
          if(!assetsInitialized){
          for (int i = 0;  i< E.lsecs; i++) {
            balance.set(i, (initCol * ypriorityYr.get(i) / ypriorityYr.sum()), "total value for staff per sector");

            for (int j = 0; j < E.lgrades; j++) {
              staff.grades[i][j] = balance.get(i) * E.initStaffAssignmentPerEcon[pors][j] / sumAssignments;
            }
          }

          } //!assetsInitialized
          hist.add(new History("&&", 6, "staff PriYr", ypriorityYr));
        }
        staff.sumGrades();
        hist.add(new History("&&", 6, "initstaff=" + df(initCol), balance));
        
        hist.add(new History("&&", 6, " IStaffBal", staff.balance));
      }

      // Assets.CashFlow.SubAsset
      void initGuests(int sIx, boolean reserve, SubAsset partner, double acolonists) {
        aschar = "guests";
        initSubAsset(sIx, reserve, partner);
        sstaff = true;
        other = cargo;
        oPartner = resource;
        // ARow resourceStaff = new ARow();
        grades = bals.getGuestGrades(); // reset ref grades into bals
        if(!assetsInitialized){
        boolean initedGrades = grades[0][0] > 0. || grades[0][1] > 0 || grades[0][5] > 0.;
        if (!initedGrades) {
          double sumAssignments = 0.;
          for (int j = 0; j < lgrades2; j++) {
            sumAssignments += E.initStaffAssignmentPerEcon[pors][j];
          }
          for (int i = 0; i < E.lsecs; i++) {
            balance.set(i, (acolonists * ypriorityYr.get(i) / ypriorityYr.sum()), "total value for staff per sector");

            for (int j = 0; j < lgrades2; j++) {
              guests.grades[i][j] = balance.get(i) * E.initStaffAssignmentPerEcon[pors][j] / sumAssignments;
            }
          }

        }
      } 
        guests.sumGrades();
        hist.add(new History("&&", 6, " GBalance", guests.balance));
      }

      /**
       * common init code for each init subAsset
       * Assets.CashFlow.SubAsset.initSubAsset called by each initResource,
       * initCargo,initStaff,initGuests
       *
       * @param asIx the index number for this subAsset
       * @param areserve true if this is a reserve not a working subAsset
       * @param apartner The partner working for reserve, reserve for working
       */
      // Assets.CashFlow.SubAsset
      // called by each subassit init process
      void initSubAsset(int asIx, boolean areserve, SubAsset apartner) {
        reserve = areserve;
        sIx = asIx;
        aschar = aChar[asIx];
        this.partner = apartner;
        // make sure balance
        balance = bals.getRow(ABalRows.BALANCESIX + asIx);
        growths.A[asIx + 2] = sys[asIx].growth = growths.A[2 + asIx] = bals.getRow(GROWTHSIX + asIx);
        growths.aCnt[2 + asIx]++;
        //  growth = bals.getRow(ABalRows.GROWTHSIX + asIx);
        //         growth = growths.A[2 + asIx];
        fertility = makeZero(fertility);
        cumulativeDecay = bals.getRow(ABalRows.cumulativeDecayIx + asIx);
        bonusUnitGrowth = bals.getRow(ABalRows.BONUSUNITSIX + asIx);
        bonusYears = bals.getRow(ABalRows.BONUSYEARSIX + asIx);
        prevUnitGrowth = makeZero(prevUnitGrowth);
        prevWorth = makeZero(prevWorth);
        prevHealth = makeZero(prevHealth);
        prevFertility = makeZero(prevFertility);
        prevNeed = makeZero(prevNeed);
        prevMaxLeft = makeZero(prevMaxLeft);
        bonusYears = bals.getRow(ABalRows.bonusYearsIx + asIx);
        bonusUnitGrowth = bals.getRow(ABalRows.bonusUnitsIx + asIx);
        grades = bals.getGrades()[sIx]; // of mpt staff
        yearlyUnitGrowth = new ARow();
        rawUnitGrowth = new ARow();
        rawUnitGrowthAfterDecay = new ARow();
        rawGrowth = new ARow();
        //      nominalGrowth = new ARow();
        //     growFull = new ARow();
        invMaintEfficiency = new ARow();
        invGroEfficiency = new ARow();
        groEfficiency = new ARow();
        maintEfficiency = new ARow();
      }

      /**
       * copy AssetYr.SubAsset instance to a new copy of the sa copy variables
       * need for a year instance SubAsset variables that are not copied are
       * left with initial values
       *
       * @param sa AssetYr.SubAsset instance being copied
       * @return
       */
      SubAsset copyy(SubAsset sa) {// Assets.CashFlow.SubAsset
        setny(sa);
        rawUnitGrowth = copy(sa.rawUnitGrowth);
        worth = copy(sa.worth);
        health = copy(sa.health);
        fertility = copy(sa.fertility);
        need = copy(sa.need);
        growth = copy(sa.growth);
        rawGrowth = copy(sa.rawGrowth);
        maxLeft = copy(sa.maxLeft);
        cumulativeDecay = copy(sa.cumulativeDecay);
        bonusUnitGrowth = copy(sa.bonusUnitGrowth);
        bonusYears = copy(sa.bonusYears);
        return this;
      }

      /**
       * set AssetYr.SubAsset instance to a new copy of the sa copy variables
       * need during the n interations uncopied variables are left as
       * initialized
       *
       * @param sa
       * @return
       */
      SubAsset copyn(SubAsset sa) {// Assets.CashFlow.SubAsset
        setny(sa);
        if (!reserve) {
          tReqGrowthFertility = copy(sa.tReqGrowthFertility);
          remnantWithPartner = copy(sa.remnantWithPartner);
          highReservedWithPartner = copy(sa.highReservedWithPartner);
          lowReservedWithPartner = copy(sa.lowReservedWithPartner);

          if (sstaff) {
            work = copy(sa.work);
          }
          if (sstaff) {
            grades = sa.copyGrades(sa.grades);
          }
        }
        return this;
      }

      /**
       * common method to copy HCashFlow.SubAsset members from CashFlow.SubAsset
       *
       * @param sa variables out of cur.Subassets
       * @return a HCashFlow.SubAsset with common copied members
       */
      SubAsset setny(SubAsset sa) { // Assets.CashFlow.SubAsset
        as1 = sa.as1;
        sIx = sa.sIx;
        sstaff = sa.sstaff;
        reserve = sa.reserve;
        aschar = sa.aschar;
        aschar = sa.aschar;
        partner = sys[sa.partner.sIx];
        balance = copy(sa.balance);
        tRemnant = copy(sa.tRemnant);
        //      balanceWithPartner = copy(sa.balanceWithPartner);
        availWithPartner = copy(sa.availWithPartner);
        if (!reserve) {
          hptMTGCosts = copy(sa.hptMTGCosts);
          fertility = copy(sa.fertility);
          fertility1 = copy(sa.fertility1);
          health = copy(sa.health);
          remnantWithPartner = copy(sa.remnantWithPartner);
          highReservedWithPartner = copy(sa.highReservedWithPartner);
          lowReservedWithPartner = copy(sa.lowReservedWithPartner);
        }
        if (sstaff) {  // both staff and guests
          work = copy(sa.work);
          grades = copyGrades(sa.grades);
          sumGrades();
        }

        return this;
      }

      /**
       * calculate efficiency only for SubAsset resource and SubAsset Staff
       * staff
       *
       */
      protected void calcEfficiency() {  // Assets.CashFlow.SubAsset
        // working Efficiency Bias is a function of E.effBias and percentDifficulty
        // never less than .5 E.effBias[pors] or more tnan 1.5 E.effBias
        double workEffBias = eM.effBias[pors] * .5 + eM.effBias[pors] * (100 - percentDifficulty) * .01;
// define temporary internal variables
        ARow GroReqSum = new ARow();
        ARow GroReqMultiplier = new ARow();
        ARow MaintReqSum = new ARow();
        ARow MaintReqMultiplier = new ARow();
        ARow KnowledgeGroMultiplier = new ARow();
        ARow KnowledgeMaintMultiplier = new ARow();
        ARow meff = new ARow();
        ARow geff = new ARow();
        GroReqSum = makeZero(GroReqSum);
        MaintReqSum = makeZero(MaintReqSum);
        GroReqMultiplier = makeZero(GroReqMultiplier);
        MaintReqMultiplier = makeZero(MaintReqMultiplier);
        KnowledgeGroMultiplier = makeZero(KnowledgeGroMultiplier);
        KnowledgeMaintMultiplier = makeZero(KnowledgeMaintMultiplier);
        maintEfficiency = makeZero(maintEfficiency);
        groEfficiency = makeZero(groEfficiency);
        aschar = aChar[sIx];
        splus = spluss[sIx];
        // one factor in efficiency, is a sectors importance to other sectors
        // use the grow Requirements table and Maint Requirements table to determin
        // sector importance.  Sum them in the following loops
        for (int i = 0; i < E.lsecs; i++) {
          for (int j = 0; j < E.lsecs; j++) {
            GroReqSum.add(i, gReqs[pors][i][j + splus] * E.gReqEffMult * E.gReqMult[pors][0] * knowledge.get(j));
            MaintReqSum.add(i, maintRequired[pors][i][j + splus] * E.mReqEffMult * E.mReqMult[pors][0] * knowledge.get(j));
          } // go through j to get the sums
          GroReqMultiplier.add(i, GroReqSum.get(i) * multiplierForEfficiencyFromRequirements);
          MaintReqMultiplier.add(i, MaintReqSum.get(i) * multiplierForEfficiencyFromRequirements);
          KnowledgeGroMultiplier.add(i, Math.sqrt((GroReqMultiplier.get(i) + knowledge.sum() * additionToKnowledgeBiasForSumKnowledge + (pors == E.S ? manuals.sum() : 0.) * eM.manualEfficiencyMult[pors][0] + knowledge.get(i)) / eM.nominalKnowledgeForBonus[0]) + eM.additionToKnowledgeBiasSqrt[0]);
          KnowledgeMaintMultiplier.add(i, Math.sqrt((MaintReqMultiplier.get(i) + knowledge.sum()
                  * additionToKnowledgeBiasForSumKnowledge + (pors == E.S ? manuals.sum() : 0.) * eM.manualEfficiencyMult[pors][0]
                  + knowledge.get(i)) / eM.nominalKnowledgeForBonus[0]) + eM.additionToKnowledgeBiasSqrt[0]);
          // the higher difficulty the lower the efficiency
          maintEfficiency.add(i, Math.sqrt(workEffBias + (1. - workEffBias) * (ydifficulty.get(1) < PZERO ? 0. : KnowledgeMaintMultiplier.get(i)) / ydifficulty.get(i)));
          groEfficiency.add(i, Math.sqrt(eM.effBias[pors] + (1. - eM.effBias[pors]) * (ydifficulty.get(1) < PZERO ? 0. : KnowledgeGroMultiplier.get(i)) / ydifficulty.get(i)));

        }// end loop on i
        meff = copy(maintEfficiency);
        geff = copy(groEfficiency);

        if (History.dl > History.debuggingMajor10) {
          StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
          StackTraceElement aa = Thread.currentThread().getStackTrace()[2];
          StackTraceElement ab = Thread.currentThread().getStackTrace()[3];
          hist.add(new History(History.debuggingMajor10, ">>>", a0.getMethodName(), "at", a0.getFileName(), wh(a0.getLineNumber()), "from=", aa.getFileName(), wh(aa.getLineNumber()), "ffrom", ab.getFileName(), wh(ab.getLineNumber())));
        }
        hist.add(new History(History.headers20, aschar + " efficiency", "0LifeSup", "1Struct", "2Energy", "3Propel", "4Defense", "5Gov", "6Col", "Min", "Sum", "Ave"));
        invMaintEfficiency = make(invMaintEfficiency);
        invMaintEfficiency.invertA(maintEfficiency.limVal(maintEfficiency, eM.rsefficiencyMMin[pors][0], eM.rsefficiencyMMax[pors][0]));
        poorKnowledgeAveEffect = invMaintEfficiency.ave();
        ARow tt1 = invMEfficiency.getRow(sIx + 2);
        invMEfficiency.getRow(sIx + 2).set(invMaintEfficiency);
        invGroEfficiency = make(invGroEfficiency);
        invGroEfficiency.invertA(groEfficiency.setLimVal(groEfficiency, eM.rsefficiencyGMin[pors][0], eM.rsefficiencyGMax[pors][0]));
        //  invGroEfficiency = invGroEfficiency.set(invMaintEfficiency);
        invGEfficiency.getRow(sIx + 2).set(invGroEfficiency);

        partner.invGroEfficiency.set(invGroEfficiency);
        partner.invMaintEfficiency.set(invMaintEfficiency);
        partner.groEfficiency.set(groEfficiency);
        partner.maintEfficiency.set(maintEfficiency);

        if (History.dl > 9) {
          hist.add(new History("&&", 9, aschar + " GroReqSum", GroReqSum));
          hist.add(new History("&&", 9, aschar + " GroReqMultiplier", GroReqMultiplier));
          hist.add(new History("&&", 9, aschar + " MaintReqSum", MaintReqSum));
          hist.add(new History("&&", 9, aschar + " MaintReqMultiplier", MaintReqMultiplier));
          hist.add(new History("&&", 9, aschar + " KnowledgeMaintMultiplier", KnowledgeMaintMultiplier));
          hist.add(new History("&&", 9, aschar + " KnGroMultiplier", KnowledgeGroMultiplier));

          hist.add(new History("&&", 9, "knowledge", knowledge));
          hist.add(new History("&&", 9, aschar + " o meff", meff));
          hist.add(new History("&&", 9, aschar + " o geff", geff));
          hist.add(new History("&&", 9, aschar + " difficulty", ydifficulty));
          hist.add(new History("&&", 9, aschar + " mEfficiency", maintEfficiency));
          hist.add(new History("&&", 9, aschar + " gEfficiency", groEfficiency));
          hist.add(new History("&&", 9, aschar + " invMEfficiency", invMaintEfficiency));
          hist.add(new History("&&", 9, aschar + " invGEfficiency", invGroEfficiency));

        }
      } // end calcEfficiency  in SubAsset

      // double decayIncSum = 0., cumDecaySum = 0., bonusUnitGrowthSum;
      /**
       * initYr init Start of Year process for SubAsset s
       *
       */
      void initYr() {// Assets.CashFlow.SubAsset.initYr
        calcGrowth();
      }

      /**
       * calculate staff and guest unit growth per staff  work unit
       *
       */
      void calcGrowth() { // Assets.CashFlow.SubAsset.calcGrowth
        splus = spluss[sIx];
        //  aschar = aChar[sIx];  alllready set
        //   decayIncSum = cumDecaySum = bonusUnitGrowthSum = 0.;

        prevUnitGrowth = make(rawUnitGrowth);
        prevWorth = make(worth);
        prevHealth = make(health);
        prevFertility = make(fertility);
        prevNeed = make(need);
        if (sIx == 2) {
          aChar[sIx] = "s";
        }
        if (didDecay) {
          prevGrowth.zero();
        }
        else {
          prevGrowth.set(bals.getGrowthsRow(sIx));
          // later      didDecay = true;
        }
        // only do decay once a year
        ARow newDecay = new ARow().setAmultV(prevGrowth, eM.decay[sIx][pors]);
        cumulativeDecay.add(newDecay);
        prevMaxLeft = make(maxLeft);
        bonusYears = bals.getBonusYearsRow(sIx);
        bonusUnitGrowth = bals.getBonusUnitsRow(sIx);
        rawUnitGrowth = make(rawUnitGrowth);
        ARow rawBiasedUnitGrowth = new ARow();
        ARow rawPriorityUnitGrowth = new ARow();
        ARow rg1 = new ARow();
        ARow rg2 = new ARow();
        ARow rg3 = new ARow();
        ARow rg4 = new ARow();
        ARow uG1 = new ARow();
        double bonusLeft = 0;
        if (sstaff) {
          sumGrades();// get recompute for work
        }
        // calculate raw growth for this year.  A function of game growth value,
        // economy priorities, and groEfficiency

        double rawValue = 0., rawUValue = 0.;
        double growthFrac = (eM.maxGrowth[pors] - bals.curSum()) / eM.maxGrowth[pors];
    //    double yuGrow[] = {0.,0.,0.,0.,0.,0.,0.,0.};
        for (int n = 0; n < LSECS; n++) {
          // double a1 = balance.get(n);
          // double a2 = partner.balance.get(n);
         // yuGrow[n] = yearlyUnitGrowth.set(n, eM.growth[sIx][pors] * growthFrac - cumulativeDecay.get(n) + (bonusLeft = (bonusYears.get(n) > PZERO ? bonusUnitGrowth.get(n) : 0.)));

          //get unitGrowth1, step one in final unit growth
          double rawUnitGrowthd = uG1.set(n, rawBiasedUnitGrowth.set(n, (1.1 * yearlyUnitGrowth.get(n) * (eM.fracBiasInGrowth[pors])) + rawPriorityUnitGrowth.set(n, (yearlyUnitGrowth.get(n) * eM.fracPriorityInGrowth[pors] * ypriorityYr.get(n)) * groEfficiency.get(n) * cRand(3 * sIx + n + 30))));
          /**
           * raw growth in ships, is dependent on lightYearsTraveled raw growth
           * for planets dependent on staff work
           */

          rawUValue = rawUnitGrowth.set(n, (rg1.set(n, (pors == E.S) ? rg3.set(n, lightYearsTraveled * eM.travelGrowth[E.S]) : 1.) * rg2.set(n, rg4.set(n, cRand(n + 10)) * uG1.get(n))));
          rawValue = rawGrowth.set(n, s.work.get(n) * rawUnitGrowth.get(n) * cRand(n + 4));
          if (!didDecay) {
            // now count down the bonus units & years
            bonusYears.set(n, bonusYears.get(n) - 1);
            if (bonusYears.values[n] > 0
                    && bonusUnitGrowth.values[n] > 0.) {
              bonusUnitGrowth.set(n, bonusUnitGrowth.get(n) - bonusUnitGrowth.get(n) / (bonusYears.get(n) + eM.catastrophyBonusYearsBias[0][pors]));
            }
            didDecay = true;
          }
          //   E.myTest(rawValue < eM.mRCSGGrowth[sIx][pors][0], ">>>>ERROR rawGrowth %14.10f too small,eM.Growth=%14.10f,yearlyUnitGrowth=%14.10f,rawUG1=%14.10f, rawUnitGrowth %14.10f, min %14.10f,n=%d,%s,lightYearsTraveled=%10.7f", rawValue, eM.growth[sIx][pors], yug, rawUnitGrowthd, rawUValue, eM.mRCSGGrowth[sIx][pors][0], n, name, lightYearsTraveled);
        }//end for on n

        ec.aPre = aPre = "#G";
        if (History.dl > 9) {
          hist.add(new History(aPre, 9, aschar + " growth[sIx][pors]", df(eM.growth[sIx][pors]), "sIx", wh(sIx), "pors", wh(pors), "fracBiax..", df(eM.fracBiasInGrowth[pors]), "fracPriority", df(eM.fracPriorityInGrowth[pors])));
          hist.add(new History(aPre, 9, aschar + " balance", balance));
          hist.add(new History(aPre, 9, aschar + " prevGrowth", prevGrowth));
          hist.add(new History(aPre, 9, aschar + " cumulativeDecay", cumulativeDecay));
          hist.add(new History(aPre, 9, " knowledge", knowledge));
          hist.add(new History(aPre, 9, aschar + " bonusUnitGrowth", bonusUnitGrowth));
          hist.add(new History(aPre, 9, " bonusYears", bonusYears));
          hist.add(new History(aPre, 9, aschar + " groEfficiency", groEfficiency));
          hist.add(new History(aPre, 9, aschar + " yearlyUnitGrowth", yearlyUnitGrowth));
          hist.add(new History(aPre, 9, aschar + " rawUG1", uG1));
          hist.add(new History(aPre, 9, aschar + " rawBiasedUnitGrowth", rawBiasedUnitGrowth));
          hist.add(new History(aPre, 9, aschar + " rawPriorityUnitGrowth", rawPriorityUnitGrowth));
          hist.add(new History(aPre, 9, aschar + " rawUnitGrowth", rawUnitGrowth));
          hist.add(new History(aPre, 9, aschar + " rg1", rg1));
          hist.add(new History(aPre, 9, aschar + " rg2", rg2));
          hist.add(new History(aPre, 9, aschar + " rg3", rg3));
          hist.add(new History(aPre, 9, aschar + " rg4", rg4));
          hist.add(new History(aPre, 9, aschar + " rawGrowth", rawGrowth));
          hist.add(new History(aPre, 9, aschar + "sIx=" + sIx + " Trav=", df(lightYearsTraveled), "pors+" + pors));
          hist.add(new History(aPre, 9, " ypriorityYr=", ypriorityYr));
          hist.add(new History(aPre, 9, "staffWork", s.work));

          //        hist.add(new History("&&", 9, aschar + " balWPartner", balanceWithPartner));
        }
      }// end Assets.CashFlow.SubAsset.calcGrowth

      /**
       * OBSOLETE calculate costs for each SubAsset
       *
       * @param balances
       * @param rawGrowths
       * @param invMEfficiency
       * @param invGEfficiency
       * @param RIX
       * @param sIx
       * @param tIx
       * @param iReqMaint
       * @param jReqMaint
       * @param ix
       * @param consumerReqMaintCosts10
       * @param nReqMaint
       * @param iReqGrowth
       * @param jReqGrowth
       * @param consumerReqGrowthCosts10
       * @param nReqGrowth
       * @param iMaint
       * @param jMaint
       * @param consumerMaintCosts10
       * @param nMaint
       * @param iTravel1Yr
       * @param jTravel1Yr
       * @param mTravel1Yr
       * @param nTravel1Yr
       * @param iGrowth
       * @param jGrowth
       * @param consumerGrowthCosts10
       * @param nGrowth
       * @param swork
       * @param lightYearsTraveled
       */
      void calcRawCostsNot(A6Row balances, A6Row rawGrowths, A6Row invMEfficiency, A6Row invGEfficiency, int RIX, int sIx, int tIx, A6Row iReqMaint, A6Row jReqMaint, int ix, A10Row consumerReqMaintCosts10, A10Row nReqMaint, A6Row iReqGrowth, A6Row jReqGrowth, A10Row consumerReqGrowthCosts10, A10Row nReqGrowth, A6Row iMaint, A6Row jMaint, A10Row consumerMaintCosts10, A10Row nMaint, A6Row iTravel1Yr, A6Row jTravel1Yr, A10Row mTravel1Yr, A10Row nTravel1Yr, A6Row iGrowth, A6Row jGrowth, A10Row consumerGrowthCosts10, A10Row nGrowth, ARow swork, double lightYearsTraveled) {  // Assets.CashFlow.SubAsset
        double t1, t2, t3, t4 = -999., t5, t6;
        int rcorsg = ix / 2;
        /**
         * now loop through i = consumer aspect of financial sectors j is the
         * supplier section of the financial sectors In general we measure the
         * demands on the financial sector, against the resource availability of
         * the financial sector. This process also gathers year totals yj... of
         * service requirements. The health of an economy is how close the
         * weakest sector is to supplying the demands from the consumers. Random
         * factors change relationships each year. The fertility of an economy
         * is measured by how close the service financial sectors, meet the
         * demands from all consumer aspects.
         */
        if (n < 999 && (ix == 0 || ix == 2)) {
          hist.add(new History("#a", History.valuesMajor6, "s balance", balances.A[4]));
          hist.add(new History("#a", History.valuesMajor6, "nMaint i=" + i, nMaint.A[6]));
          hist.add(new History("#a", History.valuesMajor6, "consumerMaintCosts10 i=" + i, consumerMaintCosts10.A[6]));
        }
        for (i = 0; i < E.lsecs; i++) {
          ARow kMaint = new ARow();
          for (j = 0; j < E.lsecs; j++) {
            // !crand          iRConsumerRequiredForMaintenance.add(i, t1 = balance.get(i) *maintRequired[pors][i][j] * E.maintReqAdj[pors][sIx] * (E.maintReqTabRow[sIx] == 0 ? 1. :maintRequired[pors][E.maintReqTabRow[sIx]][j]) / maintEfficiency.get(i));
            //  t1 = balances.get(RIX, i) * cRand(i * E.lsecs + j) * E.maintRequired[pors][i][j] * E.maintReqAdj[0][pors][ix] * (tIx == 0 ? 1. : E.maintRequired[pors][tIx][j]) * invMaintEfficiency.get(i);
            // these values are all staff counts, converted from work counts by bal/swork
            //     t2 = balance.get(i) * cRand(i * E.lsecs + 8 + j) * E.maintRequired[pors][i][j + E.lsecs] * E.maintReqAdj[1][pors][ix] * (tIx == 0 ? 1. : E.maintRequired[pors][tIx][j + E.lsecs]) * invMaintEfficiency.get(i) * balances.get(4, j) / swork.get(j);

            t1 = bals.get(BALANCESIX + ix, i) * cRand(i * E.lsecs + j) * E.maintRequired[pors][i][j] * eM.rs[0][0][pors][rcorsg] * eM.ps[pors][ix] * (tIx == 0 ? 1. : E.maintRequired[pors][tIx][i]) * invMaintEfficiency.get(i);
            // these values are all staff counts, converted from work counts by bal/swork
            t2 = bals.get(BALANCESIX + ix, i) * cRand(i * E.lsecs + 8 + j) * E.maintRequired[pors][i][j + E.lsecs] * eM.rs[0][1][pors][rcorsg] * eM.ps[pors][ix] * (tIx == 0 ? 1. : E.maintRequired[pors][tIx][i + E.lsecs]) * invMaintEfficiency.get(i) * bals.get(BALANCESIX + SIX, j) / swork.get(j);
            // gather 7 service requests to i  (7 j values, service by i
            iReqMaint.add(0, i, t1);
            iReqMaint.add(RIX, i, t1);
            // gather 7 requests for service for j (7 i values, service by i
            jReqMaint.add(0, j, t1);
            jReqMaint.add(RIX, j, t1);
            iReqMaint.add(1, i, t2);
            iReqMaint.add(sIx, i, t2);
            jReqMaint.add(1, j, t2);
            jReqMaint.add(sIx, j, t2);
            consumerReqMaintCosts10.add(2 + ix, i, t1);
            consumerReqMaintCosts10.add(0, i, t1);
            consumerReqMaintCosts10.add(6 + ix, i, t2);
            consumerReqMaintCosts10.add(1, i, t2);
            nReqMaint.add(2 + ix, j, t1);
            nReqMaint.add(6 + ix, j, t2);
            nReqMaint.add(0, j, t1);
            nReqMaint.add(1, j, t2);

            if (sIx == 1) {
              double aa = balance.get(i);
              double ab = groEfficiency.get(i);
            }

            //         t1 = balances.get(RIX, i) * cRand(i * E.lsecs + j) * E.maintRequired[pors][i][j] * eM.maintReqAdj[0][pors][ix] * (tIx == 0 ? 1. : E.maintRequired[pors][tIx][j]) * invMaintEfficiency.get(i);
            // these values are all staff counts, converted from work counts by bal/swork
            //         t2 = balance.get(i) * cRand(i * E.lsecs + 8 + j) * E.maintRequired[pors][i][j + E.lsecs] * eM.maintReqAdj[1][pors][ix] * (tIx == 0 ? 1. : E.maintRequired[pors][tIx][j + E.lsecs]) * invMaintEfficiency.get(i) * balances.get(4, j) / swork.get(j);
            t1 = balances.get(2 + ix, i) * cRand(i * E.lsecs + j) * E.resourceGrowthRequirementBySourcePerConsumer[pors][i][j] * eM.rs[1][0][pors][rcorsg] * eM.ps[pors][ix] * (tIx == 0 ? 1. : E.resourceGrowthRequirementBySourcePerConsumer[pors][tIx][i]) * invMaintEfficiency.get(i);
            // these values are all staff costs, converted from work counts by bal/swork
            t2 = balances.get(2 + ix, i) * cRand(i * E.lsecs + 8 + j) * E.resourceGrowthRequirementBySourcePerConsumer[pors][i][j + E.lsecs] * eM.rs[1][1][pors][rcorsg] * eM.ps[pors][ix] * (tIx == 0 ? 1. : E.resourceGrowthRequirementBySourcePerConsumer[pors][tIx][i + E.lsecs]) * invMaintEfficiency.get(i) * balances.get(4, j) / swork.get(j);
            iReqGrowth.add(0, i, t1);
            iReqGrowth.add(RIX, i, t1);
            jReqGrowth.add(0, j, t1);
            jReqGrowth.add(RIX, j, t1);
            iReqGrowth.add(1, i, t2);
            iReqGrowth.add(sIx, i, t2);
            jReqGrowth.add(1, j, t2);
            jReqGrowth.add(sIx, j, t2);
            consumerReqGrowthCosts10.add(ix + 2, i, t1);
            consumerReqGrowthCosts10.add(ix + 6, i, t2);
            nReqGrowth.add(ix + 2, j, t1);
            nReqGrowth.add(ix + 6, j, t2);
            consumerReqGrowthCosts10.add(0, i, t1);
            consumerReqGrowthCosts10.add(1, i, t2);
            nReqGrowth.add(0, j, t1);
            nReqGrowth.add(1, j, t2);

            if (n < 5 && i == 6 && j == 6) {
              hist.add(new History("#b", History.valuesMajor6, "s balance", balances.A[4]));
              hist.add(new History("#b", History.valuesMajor6, "mRGrowthC6 i=" + i + " ix" + ix + " n" + n, consumerReqGrowthCosts10.A[6]));
              hist.add(new History("#b", History.valuesMajor6, "nRGrowth6 i=" + i, nReqGrowth.A[6]));
              hist.add(new History("#b", History.valuesMajor6, "rawUnitGrowth", rawUnitGrowth));
              hist.add(new History("#b", History.valuesMajor6, "rawGrowth", rawGrowth));
            }

            t1 = balances.get(2 + ix, i) * cRand(i * E.lsecs + j + 31) * E.maintCost[pors][i][j] * eM.rs[2][0][pors][rcorsg] * eM.ps[pors][ix] * (tIx == 0 ? 1. : E.maintCost[pors][tIx][i]) * invMEfficiency.get(ix, i);
            t4 = t2 = balances.get(2 + ix, i) * cRand(i * E.lsecs + j + 41) * E.maintCost[pors][i][j + E.lsecs] * eM.rs[2][1][pors][rcorsg] * eM.ps[pors][ix] * (tIx == 0 ? 1. : E.maintCost[pors][tIx][i + E.lsecs]) * invMEfficiency.get(ix, i) * balances.get(4, j) / swork.get(j);
            iMaint.add(0, i, t1);
            iMaint.add(RIX, i, t1);
            jMaint.add(0, j, t1);
            jMaint.add(RIX, j, t1);
            iMaint.add(1, i, t2);
            iMaint.add(sIx, i, t2);
            jMaint.add(1, j, t2);
            jMaint.add(sIx, j, t2);
            consumerMaintCosts10.add(ix + 2, i, t1);
            consumerMaintCosts10.add(ix + 6, i, t2);
            nMaint.add(ix + 2, j, t1);
            nMaint.add(ix + 6, j, t2);
            consumerMaintCosts10.add(0, i, t1);
            consumerMaintCosts10.add(1, i, t2);
            nMaint.add(0, j, t1);
            nMaint.add(1, j, t2);
            kMaint.add(j, t2);
            if (n < 5 && i == 6 && j == 6) {
              hist.add(new History("#c", History.valuesMajor6, "nM i=" + i + " ix" + ix + " n" + n, nMaint.A[6]));
              hist.add(new History("#c", History.valuesMajor6, "mM v=" + df(t2), consumerMaintCosts10.A[6]));
              hist.add(new History("#c", History.valuesMajor6, "kM i=" + i + " j=" + j, kMaint));
            }

            t1 = balances.get(2 + ix, i) * cRand(i * E.lsecs + j + 46) * tCosts[pors][i][j] * eM.rs[3][0][pors][rcorsg] * eM.ps[pors][ix] * (tIx == 0 ? 1. : tCosts[pors][tIx][i]) * invMEfficiency.get(ix, i);
            t2 = balances.get(2 + ix, i) * cRand(i * E.lsecs + j + 55) * tCosts[pors][i][j + E.lsecs] * eM.rs[3][1][pors][rcorsg] * eM.ps[pors][ix] * (tIx == 0 ? 1. : tCosts[pors][tIx][i + E.lsecs]) * invMEfficiency.get(ix, i) * balances.get(4, j) / swork.get(j);
            iTravel1Yr.add(0, i, t1);
            iTravel1Yr.add(RIX, i, t1);
            jTravel1Yr.add(0, j, t1);
            jTravel1Yr.add(RIX, j, t1);
            iTravel1Yr.add(1, i, t2);
            iTravel1Yr.add(sIx, i, t2);
            jTravel1Yr.add(1, j, t2);
            jTravel1Yr.add(sIx, j, t2);
            mTravel1Yr.add(ix + 2, i, t1);
            mTravel1Yr.add(ix + 6, i, t2);
            nTravel1Yr.add(ix + 2, j, t1);
            nTravel1Yr.add(ix + 6, j, t2);
            mTravel1Yr.add(0, i, t1);
            mTravel1Yr.add(1, i, t2);
            nTravel1Yr.add(0, j, t1);
            nTravel1Yr.add(1, j, t2);

            t1 = swork.get(i) * rawUnitGrowths.get(2 + ix, i) * cRand(i * E.lsecs + j + 65) * gCosts[pors][i][j] * eM.rs[4][0][pors][rcorsg] * eM.ps[pors][ix] * (tIx == 0 ? 1. : gCosts[pors][tIx][i]) * invGEfficiency.get(ix, i);
            t2 = swork.get(i) * cRand(i * E.lsecs + j + 70) * gCosts[pors][i][j + E.lsecs] * eM.rs[4][1][pors][rcorsg] * eM.ps[pors][ix] * (tIx == 0 ? 1. : gCosts[pors][tIx][i + E.lsecs]) * invGEfficiency.get(ix, i) * balances.get(4, j) / swork.get(j);
            iGrowth.add(0, i, t1);
            iGrowth.add(RIX, i, t1);
            jGrowth.add(0, j, t1);
            jGrowth.add(RIX, j, t1);
            iGrowth.add(1, i, t2);
            iGrowth.add(sIx, i, t2);
            jGrowth.add(1, j, t2);
            jGrowth.add(sIx, j, t2);
            consumerGrowthCosts10.add(ix + 2, i, t1);
            consumerGrowthCosts10.add(ix + 6, i, t2);
            nGrowth.add(ix + 2, j, t1);
            nGrowth.add(ix + 6, j, t2);
            consumerGrowthCosts10.add(0, i, t1);
            consumerGrowthCosts10.add(1, i, t2);
            nGrowth.add(0, j, t1);
            nGrowth.add(1, j, t2);

          } // end j
          if (n < -999 && ix == 0) {
            hist.add(new History("#d", History.valuesMajor6, "nM i=" + i + " " + df(t4), nMaint.A[6]));
            hist.add(new History("#d", History.valuesMajor6, "mM i=" + i, consumerMaintCosts10.A[6]));
          }
        } // end i  in SubAsset
      }  // Assets.CashFlow.SubAsset.calcRawCosts (2) end

    
      protected double setGrade(int sector, int grade, double value) {
        // Assets.CashFlow.SubAsset
        grades[sector][grade] = value;
        return grades[sector][grade];

      }

      /**
       * make a copy of the grades array
       *
       * @param gr old grades array
       * @return new copy of gr;
       */
      double[][] copyGrades(double[][] gr) {// Assets.CashFlow.SubAsset
        double[][] ret = new double[E.lsecs][];
        for (int m : ASECS) {
          ret[m] = new double[lgrades2];
          for (int n = 0; n < lgrades2; n++) {
            ret[m][n] = gr[m][n];
          }
        }
        return ret;
      }

      protected double getGrade(int sector, int grade) {// Assets.CashFlow.SubAsset
        return grades[sector][grade];
      }
      
 /** debug test an array of increases for multiple ARow s from the grades of a SubAsset
     
       * the ARow ending in Equiv are used in SubAsset.calcGrowth()
       * knowledge is reAdded from commonKnowledge, newKnowledge
       * @return the balance.sum() by grades not ARow units sum which should match
        * @param sourceIx 0 to 6 sum only sourceIx sectors, 
        * <br>&lt; 0 && &gt; -5 ?sum all sectors
        * <br>&lt; -10 no debuging of any kind
        * @param moves  an array ot the changes for each sector
        * @return sum of units values from Assets.CashFlow.SubAsset.sumGrades()
 */
       void checkGrades(){       
        double preValues[] = {0.,0.,0.,0.,0.,0.,0.,0.,0.};
        double sGSums[] = {0.,0.,0.,0.,0.,0.,0.,0.,0.}; 
        double preGSums[]  = {0.,0.,0.,0.,0.,0.,0.,0.,0.}; 
        double resGSums[] = {0.,0.,0.,0.,0.,0.,0.,0.,0.};
         debugSumGrades2 = E.debugSumGrades && assetsInitialized;
         int sourceMax = sourceIx > -1?sourceIx:LSECS; // debug 7 financialSectors
         if(debugSumGrades2){ // check grades against units
           for(int mm = 0; mm < LSECS; mm++){
             for(int nn=0; nn < lgrades2; nn++){
               sGSums[mm] += doubleTrouble(grades[mm][nn],"mm=" + mm + "nn=" + nn + ",");
             }
             sGSums[8] += sGSums[mm];
           }
           double sumDif=0.,dif=0,sumg=0., sumu=0.,difFracSum=.00001,difFrac=.001;
          //Prevalidate the existing grades and balance if debugSumGrades2
          for(int sourceIx2 = 0;sourceIx2<LSECS;sourceIx2++){
            preValues[sourceIx2] = balance.get(sourceIx2);
              sumg=0.;
              for(int nn=0;nn<lgrades2; nn++){
                if(grades[sourceIx2][nn] < -0.0){
                 throw(new MyErr(String.format("Illegal negative grade %7.3g at   ix%d, grade%d, term%d, i%d, j%d, m%d,n%d",grades[sourceIx2][nn], sourceIx2, nn,as.term,as.i,as.j,as.m,as.n)));
                }
                preGSums[sourceIx2] += grades[sourceIx2][nn];
                  } //for nn
                preGSums[8] += preGSums[sourceIx2];
            
              double sectU=balance.get(sourceIx2);
              double difMax = sectU*0.0001 + .0001;
              double sumBal = balance.sum();
              if(((dif=sectU - preGSums[sourceIx2]) < -difMax || dif > difMax)){
                throw(new MyErr("difference  too large" + "=" + df(dif) + " difMax " + df(difMax) + ", balance.get(" +  sourceIx2 + ")" + df(balance.get(sourceIx2))  + " for pre grades " +aschar + sourceIx2 + " "+ df(preGSums[sourceIx2]) + " pregrades2[sourceIx2]=" + df(sGSums[sourceIx2]) + "\n less units" + df(sectU) + " units sumBal=" + df(sumBal) + " grades sGSums[8]=" + df(sGSums[8]) + ", grades preGSums[8]=" + df(preGSums[8]) + ", sourceIx2=" + sourceIx2 + ", term" + as.term + ", i" + as.i + ", j" + as.j +", m" + as.m + ", n" + as.n));
                
           //   throw(new MyErr(String.format("difference[%d] %7.3g is greater than difMax %7.3g for pre balance %7.3g  less pre grade units %7.3g   sourceIx%d, term%d, i%d, j%d, m%d,n%d",sourceIx2, dif,difMax,sectU,preGSums[sourceIx2],sourceIx,as.term,as.i,as.j,as.m,as.n)));
              }//dif
          }// for sourceIx2
         }// if debugSumGrades2
      
      } 

      /** sum multiple ARow s from the grades of a SubAsset
       *
       * Uses SubAsset variables grade, debugSumGrades, balance
       * 
       *  output SubAsset ARow are: worth, work, facultyEquiv, researcherEquiv, manualsToKnowledgeEquiv, colonists, engineers, faculty, researchers,
       * the ARow ending in Equiv are used in SubAsset.calcGrowth()
       * knowledge is reAdded from commonKnowledge, newKnowledge
       * @return sum of both balance and grades
       */
      protected double sumGrades() {// Assets.CashFlow.SubAsset.sumGrades
        if(debugSumGrades2 && sIx < 2){
         throw new MyErr(String.format("Can't sumGrades for " +  aName));
        }

        balance = makeZero(balance);
        worth = makeZero(worth);
        work = makeZero(work);
        facultyEquiv = makeZero(facultyEquiv);
        researcherEquiv = makeZero(researcherEquiv);
        manualsToKnowledgeEquiv = makeZero(manualsToKnowledgeEquiv);
        colonists = makeZero(colonists);
        engineers = makeZero(engineers);
        faculty = makeZero(faculty);
        researchers = makeZero(researchers);
        knowledge.set(commonKnowledge, newKnowledge);
        double[] sgWork = {0, 0, 1., 0.};
        double sumG = 0;
        for (int i = 0; i < E.lsecs; i++) {
          for (int j = 0; j < lgrades2; j++) {
            if(debugSumGrades2 && grades[i][j] < NZERO){
            throw new MyErr(String.format( "Neg grade=grades[" + i + "][" + j + "]=" + df(grades[i][j])));
            }
            balance.add(i, doubleTrouble(grades[i][j]));
            sumG += grades[i][j];
            worth.add(i, grades[i][j] * (.5 + .5 * E.sumWorkerMults[j]) * eM.nominalWealthPerStaff[pors] * E.staffWorthBias[j] * eM.wBias[sIx]);
            work.add(i, grades[i][j] * E.sumWorkerMults[j] * sgWork[sIx]);
            facultyEquiv.add(i, grades[i][j] * E.sumFacultyMults[j]);
            researcherEquiv.add(i, grades[i][j] * E.sumResearchMults[j]);
            manualsToKnowledgeEquiv.add(i, grades[i][j] * E.sumManualToKnowledgeByStaff[j]);
          }

          // now sum the subgrades of staff / guests
          for (int j = 0; j < 4; j++) {
            colonists.add(i, grades[i][j]);
            engineers.add(i, grades[i][j + 4]);
            faculty.add(i, grades[i][j + 8]);
            researchers.add(i, grades[i][j + 12]);
          }
        }
        return sumG;
      } // Assets.CashFlow.SubAsset.sumGrades
      
      /** check that the balance matches the sum of the grades for each sector
       * do not check if Assets have not be initialized
       * @return sum of both balance and grades
       */
      double checkSumGrades(){
        debugSumGrades2 = E.debugSumGrades && assetsInitialized;
        if(debugSumGrades2)checkGrades();
        double ret = sumGrades();
        if(debugSumGrades2)checkGrades();
        return ret;
      }
     
      /**
       * doGrow do end of year growth for each SubAsset growth is done for the
       * SubAsset which calls the method both resource/cargo and staff/guests
       * are handled staff/guests move colonists up grades, as determined the
       * sumFacultyMults and sumResearchMults than knowledge upgrade is done for
       * the new grades of staff new knowledge growth is done only for staff
       *
       */
      void doGrow(String aPre) { // Assets.CashFlow.SubAsset.doGrow(...)
        hist.add(new History(aPre, History.valuesMajor6, aschar + " balance", balance));
        hist.add(new History(aPre, History.valuesMajor6, aschar + " worth", worth));
        hist.add(new History(aPre, 3, "grow " + aschar, growth));
        if (!sstaff) {
          balance.add(growth);  // ARow adds all sectors
        }
        else {
          /* facultyEquiv = makeZero(facultyEquiv);
           researcherEquiv = makeZero(researcherEquiv);
           manualToKnowledgeEquiv = makeZero(manualToKnowledgeEquiv);*/

          moreK = makeZero(moreK);
          lessM = makeZero(lessM);

          double orig1s, skipGrades, yesSkipGrades, sUp, kIncr, mDecr;
          int gradesUp;
          for (int ix = 0; ix < E.lsecs; ix++) {;
            for (int k = 14; k > 0; k--) {
              E.myTest(grades[ix][k] < NZERO, "doGrowa neg grade %7.3f=grades[%1.0f][%2.0f]  ", grades[ix][k], ix + 0., k + 0.);
              orig1s = grades[ix][k];
              skipGrades = Math.min(staff.facultyEquiv.get(ix), knowledge.get(ix) / E.knowledgeRequiredPerFacultyForJumping[k]);
              // only allow skiping grades as a function of
              // facultyEquiv
              double yesSkipGrades2 = E.fractionStaffUpgrade[k] > 1. ? (skipGrades * E.staffPromotePerFaculty[k] * staff.facultyEquiv.get(ix)) : 0;
              // limit to count in that grade
              yesSkipGrades = Math.min(yesSkipGrades2, grades[ix][k]);
              yesSkipGrades = Math.max(0., yesSkipGrades);
              /**
               * calculate if moving up 2 or 3 grades
               */
              gradesUp = (int) Math.ceil(E.fractionStaffUpgrade[k]);
              /* increasse higher grade, reduce min1s */
              if (gradesUp > 0) {
                // add jumped staff to higher grades
                double AMval = grades[ix][k + gradesUp] += yesSkipGrades;
                // remove jumped staff if any from grades[ix][k]
                grades[ix][k] -= yesSkipGrades;

              }
              E.myTest(grades[ix][k] < NZERO, "doGrow1 neg grade %7.3f=grades[%1.0f][%2.0f] %1.0f=gradesUp, %7.3f=yesSkipGrades, %7.3f=grades[%1.0f][%2.0f]", grades[ix][k], ix + 0., k + 0., gradesUp + 0., yesSkipGrades, grades[ix][k + gradesUp], ix + 0., k + gradesUp + 0.);
              /**
               * constrain fraction of upgrades for Full Staff by the fraction
               * in E.fractionStaffUpgrade[k] or if less than 1 allow all of
               * min1s the members of that grade to move up 1 grade.
               */
              sUp = E.fractionStaffUpgrade[k] % 1. > PZERO ? (E.fractionStaffUpgrade[k] % 1. * grades[ix][k]) : grades[ix][k];
              sUp = Math.min(sUp, grades[ix][k]);
              sUp = Math.max(sUp, 0.);
              grades[ix][k] -= sUp;
              grades[ix][k + 1] += sUp;
              E.myTest(grades[ix][k] < NZERO, "doGrow2 neg grade %7.3f=grades[%1.0f][%2.0f],%7.3f=sUp, %1.0f=gradesUp, %7.3f=yesSkipGrades, %7.3f=grades[%1.0f][%2.0f]", grades[ix][k], ix + 0., k + 0., sUp, gradesUp + 0., yesSkipGrades, grades[ix][k + 1], ix, k + 1);

              E.myTest(grades[ix][k + 1] < NZERO, "doGrow3 neg grade %7.3f=grades[%1.0f][%2.0f],%7.3f=sUp, %1.0f=gradesUp, %7.3f=yesSkipGrades, %7.3f=grades[%1.0f][%2.0f]", grades[ix][k + 1], ix + 0., k + 1., sUp, gradesUp + 0., yesSkipGrades, grades[ix][k], ix, k + 0.);

              if (grades[ix][k] < NZERO) {
                E.myTest(true, "negative %9.5f = grades[%1.0f=ix][%2.0f=k] %9.5f=yesSkipGrades,%1.0f=gradesUp,%9.5f=sUp,%9.5f=orig value", grades[ix][k], ix + 0., k + 0., yesSkipGrades, gradesUp + 0., sUp, orig1s);
              }
            } // end loop on k
            // now add in growth at the lowest grade
            grades[ix][0] += growth.get(ix);
            E.myTest(grades[ix][0] < NZERO, "doGrow4 neg grade %7.3f=grades[%1.0f][0] %7.3f=growth.get[%1.0f]", grades[ix][0], ix + 0., growth.get(ix), ix + 0.);

            sumGrades();  // sum researcher, manualsToKnowledge equiv
            if (!reserve) { // skip knowledge for guests
              // now upgrade the knowledge for sector ix.
              kIncr = moreK.set(ix, knowledge.get(ix) + researcherEquiv.get(ix) * eM.knowledgeGrowthPerResearcher[0] + knowledge.sum() * additionToKnowledgeBiasForSumKnowledge + (knowledge.get(ix) > eM.nominalKnowledgeForBonus[0] ? knowledge.get(ix) * eM.additionalKnowledgeGrowthForBonus[0] : 0.) * staff.groEfficiency.get(ix));
              newKnowledge.add(ix, kIncr);
              // now move manuals to commonKnowledge
              mDecr = lessM.set(ix, kIncr * eM.KLearnManuals * manualsToKnowledgeEquiv.get(ix));
              mDecr = lessM.set(ix, Math.min(manuals.get(ix), mDecr));
              commonKnowledge.add(ix, mDecr);
              manuals.add(ix, -mDecr);  // move manuals to knowledge
            }
          } // end loop on ix
          checkSumGrades(); // now  check sum all grades and related values
// now sum all grades and related values
          if (grades[0][2] < NZERO) {
            E.myTest(true, "doGrow grades neg1 staff.grades[0][2]=" + df(staff.grades[0][2]));
          }
        }

        hist.add(new History(aPre, History.valuesMajor6, aschar + " endBalance", balance));
        hist.add(new History(aPre, History.valuesMajor6, aschar + " endWorth", worth));
      }// end doGrow

      /**
       * move staff/resources value between sectors move from balance of the
       * owning SubSector, to the destination. The owning SubAsset is the
       * source, its partner may also be used to satisfy the move. Only the
       * availFrac of the working SubAsset may be used. move staff to staff,
       * resource to resource, do a fatal error E.myTest if cannot do move
       *
       * @param move amount of resource or staff to move instance(source) to
       * @param sourceIx int sector of source to be moved
       * @param destIx int destination sector for move
       * @param myDest SubAsset destination for move, may be another econ
       * @param downgrade int Staff may be downgraded in a move
       * @param availFrac 1. value %lt; PZERO no avail test done 2. value %lt;
       * 1.-PZERO WR frac available 3. value %lt; 1.+PZERO no avail test 4.
       * value %ge; 1.+PZERO src units available
       * @return amount of move that couldn't be done must be 0
       */
      double putValue(double move, int sourceIx, int destIx, SubAsset myDest, int downgrade, double availFrac) {
        return putValue(balances, move, sourceIx, destIx, myDest, downgrade, availFrac);
      }

      /**
       * move staff/resources value between sectors move from balance of the
       * owning SubSector, to the destination. The owning SubAsset is the
       * source, its partner may also be used to satisfy the move. Only the
       * availFrac of the working SubAsset may be used. move staff to staff,
       * resource to resource, do a fatal error E.myTest if cannot do move
       *
       * @param avails6 the mtgAvails6 from CashFlows.getNeeds...
       * @param move amount of resource or staff to move instance(source) to
       * @param sourceIx int sector of source to be moved
       * @param destIx int destination sector for move
       * @param myDest SubAsset destination for move, may be another econ
       * @param downgrade int Staff may be downgraded in a move
       * @param availFrac <ul><li>value %lt; PZERO: no avail kept
       * required after move
       * <li> value %lt;1.-PZERO: WR frac to be kept available on source after
       * move
       * <li> value %lt; 1.+PZERO no avail kept required after the move
       * <li>value %ge; 1.+PZERO :src units to be kept availables after the move
       * </ol>
       * @return amount of move that couldn't be done must be 0
       */
      double putValue(A6Row avails6, double move, int sourceIx, int destIx, SubAsset myDest, int downgrade, double availFrac) { // Assets.CashFlow.SubAsset

        double remMov = move, spMov = 0., opMov = 0.;
        SubAsset sp = this;  // source partner
        SubAsset op = this.partner; // the other partner
        SubAsset dp = myDest; // destination partner 
        SubAsset wp = (sp.reserve) ? op : sp;  // working partner
        SubAsset rp = (sp.reserve) ? sp : op; // reserve partner

        int sixsp = sp.sIx;
        int sixdp = dp.sIx;
        int sixop = op.sIx;
        int sixwp = wp.sIx;
        
        double availSp = avails6.get(sixsp+BALANCESIX,sourceIx);
        double availOp = avails6.get(sixop+BALANCESIX,sourceIx);
        double balSp = balances.get(sixsp + BALANCESIX, sourceIx);
        double balOp = balances.get(sixop + BALANCESIX, sourceIx);
        // both balance and avail must have enough units for a move
        double balSp1 = balSp < availSp?balSp:availSp;
        double balOp1 = balOp < availOp?balOp:availOp;
        double bal1SO = balSp + balOp;  // sum of source and partner balances
        double bavailSO = balSp1 + balOp1; // least sum avail,bal
        double balWp = sp.reserve?balOp1:balSp1;
        double balRp = op.reserve?balOp1:balSp1;
        double balDp = bals.get(sixdp + BALANCESIX, destIx);
        
        double resv = 5.;
        if(balSp1 > sp.balance.get(sourceIx)){
          E.myTest(true,"Error bal%s%d %10.5g > balance%s%d %10.5g dif %10.5g n=%d",sp.aschar,sourceIx,balSp,sp.aschar,sourceIx,balSp -  sp.balance.get(sourceIx));
        }
          if(E.debugPutValue && balOp1 > op.balance.get(sourceIx)){
          E.myTest(true,"Error bal%s%d %10.5g > balance%s%d %10.5g dif %10.5g n=%d",sp.aschar,sourceIx,balSp,sp.aschar,sourceIx,balOp - op.balance.get(sourceIx),n);
        }

        if (availFrac < PZERO) {
          resv = 0.;
        }
        else if (availFrac < 1. - PZERO) {
          resv = bal1SO * availFrac;  // frac of sum not just source
        }
        else if (availFrac < 1. + PZERO) {
          resv = 0.;
        }
        else {
          resv = availFrac;
        }
        // resv is the working  reserved that must remain with wp
        // resv = 0 if availtype==1 or availType == 0, else see below
        double canMovSp = balSp1 - (sp.reserve?0.0:resv); // remainder available to move
        double canMovOp = balOp1 - (op.reserve?0.0:resv);
        double canMovSO = canMovSp + canMovOp;
        // only availSp if incr or decr, for xfer avail == both with W reserved
        // this allows trade to also use availSP + availOp
        double canMov = (dp == sp.partner && sourceIx == destIx) ? canMovSp : canMovSO;

        if (E.debugPutValue && move < -0.0) {
          E.myTest(true, "ERROR negative move = %7.2f, n=" + n + ",term=" + term + ", i=" + i + ", j=" + j, move, n, term, i, j);
        }
        if (E.debugPutValue && balSp1 < NZERO) {
          E.myTest(true, "ERROR negative %s%d = %7.4G, n=" + n + ",term=" + term + ", i=" + i + ", j=" + j, sp.aschar, sourceIx, balSp, n, term, i, j);
        }
        //This covers incr, decr, xfer and trade because of avail processing
        if (E.debugPutValue && canMov - move <  -0.0) {
          throw new MyErr(String.format("ERROR move=%2.2g is more than canMov %5.5g, canMov%s%d = %5.5g,canMovOp%s%d=%5.5g rem=%5.5g, n=" + n + ",term=" + term + ", i=" + i + ", j=" + j, move,  canMov, sp.aschar, sourceIx, canMovSp, op.aschar, sourceIx, canMovOp, canMov - move, n, term, i, j));
        }

        // if incr or decr, only use avail sp
        // if xfer or trade(as1's differ 
        if (sourceIx != destIx || sp.as1 != dp.as1) {
          opMov = Math.min(move, canMovOp);  // use all availOp
          if (E.debugPutValue && canMovOp - opMov < -0.0) {
            throw new MyErr(String.format("ERROR canMovOp" + df(canMovOp) + " too small, opMov=" + df(opMov) + " =" + df(canMovOp - opMov) + ", canMov " + df(canMov) + " - move" + df(move) + " =" + df(canMov - move) + ", n=" + n + ",term=" + term + ", i=" + i + ", j=" + j));
          }
          op.move2(opMov, sourceIx, destIx, myDest, downgrade);
          spMov = move - opMov;
        } else {
          //source must do the whole move
          spMov = move;
        }
        // either xfer or trade, use availsp and availop
        if (spMov > 0.0) {
          if (E.debugPutValue && canMovSp - spMov < -0.0) {
            throw new MyErr(String.format("ERROR %s%d->%s%d canMovSp%s%d %10.5g - spMov %10.5g=%10.5g, balSp %10.5g, balSp1 %10.5g, availSp %10.5g canMov %10.5g - move %10.5g=%10.5g, canMovOp%s%d %10.5g - opMov %10.5g = %10.5g, n=%d,  i=%d, j=%d, term=%d",sp.aschar,sourceIx,dp.aschar,destIx,sp.aschar,sourceIx,canMovSp, spMov,canMovSp-spMov,balSp,balSp1,availSp,canMov,move,canMov-move,op.aschar,sourceIx,canMovOp,opMov,canMovOp-opMov,n,i,j,term));
         //   E.myTest(true, "ERROR canMovSp" + sp.aschar + sourceIx + " " + df(canMovSp) + " - spMov  " + df(spMov) + "=" + df(canMovSp - spMov) + ", n=" + n + ",term=" + term + ", i=" + i + ", j=" + j);
          }
          remMov = sp.move2(spMov, sourceIx, destIx, myDest, downgrade);
          if (sp.balance.get(sourceIx) < NZERO) {
            throw new MyErr(String.format("ERROR negative sp%s%d balance %10.5g,canMovSp%7.3g, n=%d, term=%d, i=%d, j=%d",sp.aschar,sourceIx,sp.balance.get(sourceIx),canMovSp,n,term,i,j));
          }
          
        }
        hist.add(new History("@m", 7, name + " from " + aschar + sourceIx, "movVal=", df(move), "S" + sp.aschar + sourceIx + "=" + df(balSp), "O" + op.aschar + sourceIx + "=" + df(balOp), dp.aschar + destIx + "=" + df(balDp), "now", "S" + sp.aschar + sourceIx + "=" + df(bals.get(sixsp + BALANCESIX, sourceIx)), "O" + op.aschar + sourceIx + "=" + df(bals.get(sixop + BALANCESIX, sourceIx)), "D" + dp.aschar + sourceIx + "=" + df(bals.get(sixdp + BALANCESIX, sourceIx))));
        return remMov;  // possible error if a leftover
      } // Assets.CashFlow.SubAsset.putValue

      /**
       * move sub-operation for putValue
       *
       * @param move move from this instance
       * @param sourceIx sector to move of the calling source
       * @param destIx sector to receive move
       * @param myDest destination sector (may be in a different Asset)
       * @param downgrade grades down for a staff moved
       *
       * @return any unfinished move
       */
      double move2(double move, int sourceIx, int destIx, SubAsset myDest, int downgrade) {//Assets.CashFlow.SubAsset
        double remMov = move;
        double prevsbal = balance.get(sourceIx);
        double[] spPreVals = new double[10];
        double[] dpPreVals = new double[10];
        for(m=0;m<LSECS;m++){
          spPreVals[m] = balance.values[m];
          dpPreVals[m] = myDest.balance.values[m];
        }
         this.balance.add(sourceIx,-move); //decrement for all SubAssets
         myDest.balance.add(destIx, move);
        if (!sstaff) {
          // see if source is enough for the move
          remMov -= move;
          if(E.debugPutValue && this.balance.get(sourceIx) < NZERO)throw new MyErr(String.format("Error " + aschar + sourceIx + " negative=" + df(this.balance.get(sourceIx))));
        }
        else {
          double mov1 = 0., mov2 = 0;
          if (E.debugPutValue && move < -0.0 || spPreVals[sourceIx] - move < -0.0 || bals.get(this.sIx + 2,sourceIx) <-0.0 || this.balance.get(sourceIx) < -0.0) {
            throw new MyErr(String.format( "ERROR negative source%s%d %7.3g - mov %7.3g rem=%7.3g,%7.3g",  this.aschar, sourceIx, spPreVals[sourceIx],move, this.balance.get(sourceIx),bals.get(this.sIx + 2, sourceIx)));
          }
           
          double tmov = move * lgrades2 / (balance.get(sourceIx) * (lgrades2 - 2)); // fraction of augmented move per staff
          double avmov = move * lgrades2 / (balance.get(sourceIx) * (lgrades2 - 5)); // augmented a mov
          Double amov = 0., oldSGrade = 0., oldDGrade = 0.;

          int k = 0, kt = 0, kmax = 18;
          for (int ii = 0; ii < kmax && (remMov > PZERO); ii++) {
            k = ii % lgrades2;
            oldSGrade = grades[sourceIx][k];

            if (E.debugPutValue && grades[sourceIx][k] < NZERO) {
              throw new MyErr(String.format(" neg grades[" + sourceIx + "][" + k + "]=" + df(grades[sourceIx][k]) + " loop index ii=" + ii + " move=" + df(move) + " remMov=" + df(remMov) + " prevsbal=" + df(prevsbal)));
            }
            amov = tmov * grades[sourceIx][k];
            amov = Math.max(avmov, amov); // increase a small tail
            amov = Math.min(amov, grades[sourceIx][k]); // prevent neg
            amov = Math.min(amov, remMov);
            amov = Math.max(amov, 0.); // force not negative
            amov = amov.isInfinite() || amov.isNaN() ? 0. : amov;
            if (amov > remMov) {
              amov = remMov;
            }
            if (E.debugPutValue && grades[sourceIx][k] - amov < -0.0) {
              throw new MyErr(String.format(" moveValue grades neg2 grades[" + sourceIx + "][" + k + "]=" + df(grades[sourceIx][k]) + " - amov=" + df(amov) + " =" + df(grades[sourceIx][k] - amov) + " ii=" + ii + " move=" + df(move) + " avmov=" + df(avmov) + "tmov=" + df(tmov) + " remMov=" + df(remMov) + " prevsbal=" + df(prevsbal)));
            }
            grades[sourceIx][k] -= amov;
            kt = k - downgrade > 0 ? k - downgrade : k;
            if (E.debugPutValue && myDest.grades[destIx][kt] < NZERO) {
              E.myTest(true, " moveValue grades neg myDest.grades[" + sourceIx + "][" + k + "]=" + df(grades[sourceIx][k]) + " - " + "amov=" + df(amov) + " =" + df(myDest.grades[sourceIx][k]) + " ii=" + ii + " move=" + df(move) + " avmov=" + df(avmov) + "tmov=" + df(tmov) + " remMov=" + df(remMov) + " prevsbal=" + df(prevsbal));
            }
            oldDGrade = myDest.grades[destIx][kt];
            myDest.grades[destIx][kt] += amov;
            if (E.debugPutValue && grades[destIx][kt] < -0.0) {
              E.myTest(true, " moveValue grades neg2 grades[" + destIx + "][" + kt + "]" + " ii=" + ii + " move=" + df(move) + " avmov=" + df(avmov) + "tmov=" + df(tmov) + " remMov=" + df(remMov) + " prevsbal=" + df(prevsbal));
            }
            remMov -= amov;
          }
          myDest.checkSumGrades();  // myDest add the move
          checkSumGrades();// subtracted mov
          if(E.debugPutValue && this.balance.get(sourceIx) < NZERO)E.myTest(true,"Error " + aschar + sourceIx + " = negative " + df(this.balance.get(sourceIx)));
          if(E.debugPutValue && myDest.balance.get(destIx) < NZERO)E.myTest(true,"Error " + myDest.aschar + destIx + " = negative " + df(myDest.balance.get(destIx)));
        }
        return remMov;
      }// Assets.CashFlow.SubAsset.move2

      /**
       * apply the cost to sp then to op if needed
       *
       * @param cost assume caller has tested for value in sp or sp+op
       * @param sourceIx
       * @param availFrac<ul><li>value %lt; PZERO: no avail kept
       * required after cost
       * <li> value %lt;1.-PZERO: Source+Other frac to be kept available on source after
       * move
       * <li> value %lt; 1.+PZERO no avail kept required after the move
       * <li>value %ge; 1.+PZERO :src units to be kept availables after the move
       * </ol>
       *
       * @return
       */
      double cost3(double cost, int sourceIx, double availFrac) {
        // Assets.CashFlow.SubAsset.cost3
        if(sstaff){checkGrades();}
        double costRem = cost,myRem = 0.;
        SubAsset sp = this;  // source partner
        SubAsset op = this.partner; // the other partner
        SubAsset wp = (reserve) ? op : sp;

        int sixsp = sp.sIx;
        int sixop = op.sIx;
        int sixwp = wp.sIx;

        double balSp = bals.get(sixsp + BALANCESIX, sourceIx);
        double balOp = bals.get(sixop + BALANCESIX, sourceIx);
        double balWp = bals.get(sixwp + BALANCESIX, sourceIx);
        double balSO = balSp + balOp;  // sum of source and partner

        int availType = 0;
        double resv = 0.0;
        if (availFrac < PZERO) {
          availType = 1;
          resv = 0.0;
        }
        else if (availFrac < 1. - PZERO) {
          availType = 2;
          resv = balSp*availFrac;
        }
        else if (availFrac < 1. + PZERO) {
          availType = 1; // around 1
          resv = 0.0;
        }
        else {
          availType = 3; // over 1.+PZERO
          resv = availFrac;
        }
        // resv is the reserved units cannot be spent
        double availSp,availOp, availSO, avail;
        if(E.debugDouble){
        availSp = doubleTrouble(doubleTrouble(balSp) - (op.reserve ? doubleTrouble(resv) : 0.)); // remainder available to move
        availOp = doubleTrouble(doubleTrouble(balOp) - (sp.reserve ? resv : 0.));
        availSO = doubleTrouble(availSp + availOp);
        // only availSp if incr or decr, for xfer avail == both with W reserved
        // this allows trade to also use availSP + availOp
        avail = availSO;
      } else {
        availSp = balSp - (op.reserve ? resv : 0.); // remainder available to move
        availOp = balOp - (sp.reserve ? resv : 0.);
        availSO = availSp + availOp;
        // only availSp if incr or decr, for xfer avail == both with W reserved
        // this allows trade to also use availSP + availOp
        avail = availSO;
        }
        boolean isShip = pors == E.S;
        double costSp = 0.;
        aPre = "$c";
        // ensure there is enough balance to cover the cost

        //   hist.add(new History(aPre, History.valuesMinor7, n + "cost3 A " + aschar + sourceIx, "costExcd Avail", "kF=" + df(availFrac), "aW=" + df(availW), "aR=" + df(availR), "-Cst=" + df(cost), "=>" + df(availWR - cost)));
        if(E.debugCosts && cost < +0.0)E.myTest(true,"Error cost negative = %10.5g",cost);
        if (E.debugCosts && avail - cost < -0.0) {
          E.myTest(true, "cost=" + df(cost) + " exceeds available=" + df(avail) + ", " + sp.aschar + sourceIx + "=" + df(avail) + ", O" + op.aschar + sourceIx + "=" + df(availOp) + ", n=" + n + ", reDo" + reDo + ", i=" + i + ", j=" + j);
        }

        if (E.debugCosts && avail - cost > +0.0) {
          costSp = Math.min(cost, availSp);
          sp.cost1(costSp, sourceIx);
          if(E.debugCosts && sp.balance.get(sourceIx) < NZERO) E.myTest(true,"source%s%d negative %7.4g, n=%d,swapType=%d",sp.aschar,sourceIx,sp.balance.get(sourceIx),n,swapType);

        }
        costRem = cost - costSp;
        if (costRem > 0.0) {
          if (E.debugCosts && availOp - costRem < -0.0) {
            E.myTest(true, "costRem=%10.5 exceeds availOp%s%d=%10.5g" + ", n=" + n + ", reDo" + reDo + ", i=" + i + ", j=" + j,costRem,op.aschar,sourceIx,availOp);
          }
          myRem =  op.cost1(costRem, sourceIx);
        }
         if(op.balance.get(sourceIx) < NZERO)E.myTest(true,"Error " + op.aschar + sourceIx + " is less than 0.0 = " + df(op.balance.get(sourceIx)));
        // raise W cost as needed, the test above shows there is enough balance
        // double costsW = costsW1;
        //double costsR = costsR1;
        hist.add(new History(aPre, History.informationMinor9, n + "cost3 " + aschar + sourceIx ,"sP" + df(balSp),  "sAF" + df(availFrac), "sA=" + df(availSp),"sC" + df(costSp),"sB" + df(bals.get(sixsp + BALANCESIX, sourceIx)),"oP" + df(balOp),  "oA" + df(availOp), "oC" + df(costRem), "=>" + df(availOp - costRem), "oB"  + df(bals.get(sixop + BALANCESIX, sourceIx)), "<<<<<<<<<"));

        return myRem;
      }  // Assets.CashFlow.SubAsset.cost3

      /**
       * docost charge the total costs for a year of a SubAsset docost is called
       * as a method of the SubAsset resource and staff
       */
      void doCost(String aPre, ARow pays) {// Assets.CashFlow.SubAsset.doCost
        if (History.dl > 4) {
          StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
          hist.add(new History(aPre, 5, ">>> n" + n + "doCost " + name, a0.getMethodName(), "at", a0.getFileName(), wh(a0.getLineNumber()), "pays=", df(pays.sum())));
        }
        for (m = 0; m < E.lsecs; m++) {
          cost3(pays.get(m), m, eM.availFrac[pors][clan]);
        }

      }

      /**
       * docost charge the total costs for a year of a SubAsset docost is called
       * as a method of the SubAsset resource and staff
       */
      void doCost(String aPre, String costName, ARow costRow) {
        // Assets.CashFlow.SubAsset
        if (History.dl > 4) {
          StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
          hist.add(new History(aPre, 5, ">>> n" + n + "doCost " + name, a0.getMethodName(), "at", a0.getFileName(), wh(a0.getLineNumber()), costName + "=", df(costRow.sum())));
        }
        for (m = 0; m < E.lsecs; m++) {
          cost3(costRow.get(m), m, .00001);
        }

      }

      /**
       * a lazy conversion a moveStaff to dieStaff where move is now die
       *
       * @param cost number of staff to die
       * @param sourceIx
       * @return
       */
      double cost1(double cost, int sourceIx) {// Assets.CashFlow.SubAsset
        double[] spPreVals = new double[10];
        double spPreSum=0;
        for(m=0;m<LSECS;m++){
          spPreSum += spPreVals[m] = balance.values[m];
        }
        spPreVals[8] = spPreSum;
        if(sstaff)checkGrades();
        double prevbal = balance.get(sourceIx);
        double remMov = doubleTrouble(cost);
        double cost2 = cost;
        if(cost < E.NNZERO) {
          throw new MyErr(String.format("Negative cost%7.3g, term%d, i%d, j%d, m%d, n%d",as.term, as.i,as.j,as.m,as.n));
        }
        //9/9/15 skip almost 0  cost, avoid infinite or NaN results
        //     hist.add(new History("cst1a", 7, n + " preCost", balance));
        if ( cost > E.PPZERO) {
          int ii = 0;
          //  double mvd = 0;
          if (balance.get(sourceIx) - cost < NZERO && E.debugCosts) {
            throw new MyErr(String.format(" " + aschar + sourceIx + " cost=" + df(cost) + " exceeds balance=" + df(balance.get(sourceIx)) + " remainder=" + df(cost - balance.get(sourceIx))+ ", i" +i + ", j" + j + ", m" + m + ", n" + n));
          }
          
          if(!sstaff){
           if(E.debugDouble){
             double v = doubleTrouble(
                     doubleTrouble(balance.get(sourceIx)) - 
                             doubleTrouble(cost));
             balance.set(sourceIx,v);
           } else {
             balance.add(sourceIx, -cost);// for all SubAssets
           }
            remMov -= cost;
            hist.add(new History("$P", 5, n + "cost1 ", "cost" + aschar + sourceIx + "=", df(cost2), "prevbal=", df(prevbal), "->" + df(balance.get(sourceIx)), "rem=", df(remMov)));
          }
          else { // staff process
            // cost/(balance *(lgrades2-2) = costFrac per grade
            // lpgrades2 versus lpgrades2-5 increases the frac
            // normal cost This will probably prevent costing the 2 top grades
            checkGrades(); // should still add up right
            double bbb = balance.get(sourceIx);
            // avoid infinite tmov if balance.get(sourceIx) == 0.
            boolean bbbb = bbb > +0.0;
            double tmov = bbbb? cost * E.lgrades / (balance.get(sourceIx) * (E.lgrades - 2)):.01;
            // to be safe put in a somewhat larger limit frac
            double avmov = bbbb?cost  / (balance.get(sourceIx) * (lgrades2 - 5)):0.0;
            double amov = 0.,bmov=0.,cmov=0.,dmov=0.,emov=0., fgrad=0.,grem=0.;
            int k = 0, kt = 0;
            int iiMax = lgrades2*6;
            double lMult=0.0;
            for (ii = 0; ii < iiMax && (remMov > +0.0); ii++) {
              k = ii % lgrades2;
              if (grades[sourceIx][k] < NZERO) {
                if (History.dl > 4) {
                  StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];

                  hist.add(new History(aPre, 7, "n" + n + ">>>> zero error", a0.getMethodName(), "at", a0.getFileName(), wh(a0.getLineNumber()), "cost=" + df(cost), "prebal " + aschar + sourceIx, "[" + k + "]=" + df(grades[sourceIx][k])));
                }
                throw new MyErr(" grade lt zero " + aschar + sourceIx + "  grades[" + sourceIx + "][" + k + "]=" + df(grades[sourceIx][k]) + " ii=" + ii + " avmov=" + df(avmov) + "tmov=" + df(tmov) + " remMov=" + df(remMov) + " prevBal=" + df(prevbal) + ", term" + as.term + ", i" + as.i + ", j" + as.j +", m" + as.m + ", n" + as.n);
              }
             // increase amov at the count moves up
              //amov = tmov * grades[sourceIx][k]* (ii+ iiMax -10)/iiMax;
              if(E.debugDouble){
                 amov = doubleTrouble(
                         doubleTrouble(tmov) 
                                 * doubleTrouble(grades[sourceIx][k]) 
                                 * doubleTrouble(0. + (ii+ iiMax -10)/iiMax));
              bmov = doubleTrouble(Math.max(
                      doubleTrouble(avmov), 
                      doubleTrouble(amov))); // increase a small tail
              cmov = doubleTrouble(Math.min(
                      doubleTrouble(bmov), 
                      doubleTrouble(grades[sourceIx][k]))); // prevent neg grade result
              dmov = doubleTrouble(Math.min(
                      doubleTrouble(cmov), 
                      doubleTrouble(remMov)));  //don't take more than needed
              emov = doubleTrouble(Math.max(dmov, 0.)); // keep amov positive
                
              } else {
              amov = tmov * grades[sourceIx][k] * (ii+ iiMax -10)/iiMax;
              bmov = Math.max(avmov, amov); // increase a small tail
              cmov = Math.min(bmov, grades[sourceIx][k]); // prevent neg grade result
              dmov = Math.min(cmov, remMov);  //don't take more than needed
              emov = Math.max(dmov, 0.); // keep amov positive
              }
              if (grades[sourceIx][k] - emov < NZERO) {
                if (History.dl > 4) {
                  StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];

                  hist.add(new History("cst1E", 7, "n" + n + ">>>> error", "neg grade", a0.getMethodName(), "at", a0.getFileName(), ec.df(a0.getLineNumber()), "cost=" + df(cost), "bal " + aschar + sourceIx, "[" + k + "] - emov=" + df(grades[sourceIx][k] - emov)));
                }
                throw new MyErr(" cost1 grades neg2 grades[" + sourceIx + "][" + k + "]=" + df(grades[sourceIx][k]) + " - " + "emov=" +ec.df(emov) + " =" + df(grades[sourceIx][k] - emov) + " ii=" + ii + " cost=" + df(cost) + " avmov=" + df(avmov) + "tmov=" + df(tmov) + " remMov=" +ec.df(remMov) + " prevBal=" + df(prevbal) + ", term" + as.term + ", i" + as.i + ", j" + as.j +", m" + as.m + ", n" + as.n);
              }
              if(E.debugDouble){
               fgrad = grades[sourceIx][k] = 
                       doubleTrouble(
                               doubleTrouble(grades[sourceIx][k]) 
                                        - doubleTrouble(emov)); 
              } else {
              fgrad=grades[sourceIx][k] -= emov;
              }
              //    mvd += emov;
              grem = remMov -= emov;
              if(grades[sourceIx][k] < NZERO){
              throw new MyErr( " cost1 grades neg3 grades[" + sourceIx + "][" + k + "]=" + df(grades[sourceIx][k]) + "emov=" + ec.df(emov) + " ii=" + ii + " cost=" + df(cost) + " avmov=" + df(avmov) + "tmov=" + ec.df(tmov) + " remMov=" + df(remMov) + " prevBal=" + ec.df(prevbal) + ", term" + as.term + ", i" + as.i + ", j" + as.j +", m" + as.m + ", n" + as.n);
            }
            
            //    destination.sumGrades();
          }
        if(E.debugCosts && remMov > .0001*prevbal) {
            throw new MyErr("cost1 " + aschar + sourceIx + " ii=" + ii + " excessive cost=" + ec.df(cost) + " remainder=" + ec.df(remMov) + " balance=" + ec.df(balance.get(sourceIx)) + " prev balance=" + ec.df(prevbal) + ", tmov" + ec.df(tmov) + ", amov" + ec.df(amov) + ", bmov" + ec.df(bmov) + ", cmov" + ec.df(cmov) + ", dmov" + ec.df(dmov) + ", emov" + ec.df(emov) + ", fgrad" + ec.df(fgrad) + ", grem" + ec.df(grem) + ", term" + as.term + ", i" + as.i + ", j" + as.j +", m" + as.m + ", n" + as.n);
          }
         if(E.debugDouble){
             double v = doubleTrouble(
                     doubleTrouble(balance.get(sourceIx)) - 
                             doubleTrouble(cost));
             balance.set(sourceIx,v);
           } else {
             balance.add(sourceIx, -cost);// for all SubAssets
           }
        checkSumGrades();
        }//sstaff
        }//cost
        //     hist.add(new History("cst1", 7, "postBal", balance));
        return remMov;  // return only if no error
      }// Assets.CashFlow.SubAsset.cost1

      /**
       * Apply ARow of costs to the calling SubAsset
       *
       * @param theCosts
       * @return
       */
      double costs(ARow theCosts) {// Assets.CashFlow.SubAsset
        double reMove = 0.;
        double mov = 0.;
        double bal = 0.;
        for (m = 0; m < E.lsecs; m++) {
          reMove = theCosts.get(m);
          bal = balance.get(m);
          mov = cost1(reMove, m);
          if (mov > PZERO) {
            E.myTest(true, "cost exceeds prev balance =" + df(bal) + " cost=" + df(reMove) + " Ix=" + m + " remainder=" + df(mov) + " balance=" + df(balance.get(m)));
          }
        }
        return mov;
      }// Assets.CashFlow.SubAsset.costs
    } // end Assets.AssetYr.SubAsset

    // Assets.CashFlow
    /**
     * Trades offers are part of the trading process between ships and planets.
     * Trade is a subclass of AssetYr enables its methods to access needed
     * objects in CashFlow, yet only 2 trade classes exist at the same time,
     * there is one thread with a binary trade session. Trade is instantiated
     * and closed in the CashFlow.barter
     */
    class Trades { // Assets.CashFlow.Trades

      //  int term; raised to Assets
      int changes, bartersNoChange = 0;

      String aPre = "*T", aPre0 = "*M", aPre1 = "*N", aPre2 = "*O", aPre3 = "*P";
      int lRes = History.loopIncrements3; // listing flag for summary
      int mRes = History.valuesMajor6; // listing for other 
      boolean doHistOther = eM.trade2HistOutputs; // dup all hist to other
      boolean newBarter = true;
      int oBlev = eM.trade2HistOutputs ? History.dl : -1; //blev of other lines

      int histStart = -100;
      double rGoalT, rGoalFrac;
      double requestsAddedValue;
      int chgCnt = 0;
      //  double cashBeforeTrade;
      //    ARow sCargo = new ARow();
      //    ARow sGuests = new ARow();
      double[][] gGrades = new double[E.lsecs][E.lgrades];
      double sf1 = 0., sv1 = 0., ts1 = 0., tr1 = 0., isf1 = 1., isv1 = 1., sf = 0., sv = 0.;
      Offer myOffer;
      int primaryStaffNeed, secondStaffNeed;
      A2Row myFirstBid = new A2Row(History.valuesMinor7, "myFirstBid");
      // value of the other at exit, entrance before flip
      A2Row oFirstBid = new A2Row(History.valuesMinor7, "oFirstBid");
      A2Row oprevGoods;
      //  A2Row oprev1Goods, oprev2Goods, oprev3Goods, oprev4Goods, initGoods;
      A2Row myxitGoods, myxit1Goods, myxit2Goods, myxit3Goods;
      // A2Row stratV,stratCV,stratF,nominalV,nominalCV;
      // use curPlusSum
      A2Row stratV = new A2Row(13, "stratV");
      A2Row stratCV = new A2Row(13, "stratCV");
      A2Row stratF = new A2Row(13, "stratF"); // stratFraction*stratWeight
      A2Row nominalV = new A2Row(13, "nominalV");
      A2Row nominalCV = new A2Row(13, "nominalCV");
      A2Row nominalCF = new A2Row(13, "nominalCF");
      A2Row goodC = new A2Row(13, "goodC");
      A2Row stratCF = new A2Row(13, "StratCF");// sF * critW
      A2Row nominalF = new A2Row(13, "nominalF"); // nF * normW
      A2Row multF = new A2Row(13, "multF"); //sum of stratF,normF, ?stratCF
      A2Row multV = new A2Row(13, "multV"); //sum of stratV,normV ?stratCV
      double nbOffers;
      double nbRequests, nbStrategicValue, nbExcessOffers;
      A2Row nbStratF, nbStratV, nbStratCV;

      //  ARow cLimTrade = new ARow();
//      ARow gLimTrade = new ARow();
      double realChanges = 0.; // per turn
      int[] valueChangesTried = new int[E.L2SECS];
      int[] oraisedBid = new int[E.L2SECS];
      int[] myRaisedBids = new int[E.L2SECS];
      int[] myTakeReq = new int[E.L2SECS]; // change offer(oReq)->myReq

      //   A2Row limTrade = new A2Row(cLimTrade, gLimTrade);
      //    A2Row myLim = limTrade; // a second reference to limTrade;
      A2Row availOfrs = new A2Row(History.informationMinor9, "availOfrs");
      A2Row maxReqs = new A2Row(History.loopMinorConditionals5, "maxReqs");
      A2Row needReq = new A2Row(History.informationMinor9, "needReq");
      A2Row fneedReq = new A2Row(History.informationMinor9, "fneedReq");
//      ARow cpyCMaxTrade = new ARow();
      //    ARow cpyGMaxTrade = new ARow();
      //     A2Row cpyMaxTrade = new A2Row(cpyCMaxTrade, cpyGMaxTrade);

      A2Row emergOfrs = new A2Row(History.informationMinor9, "emergOfrs");
      ARow cMovedTrade = new ARow();
      ARow gMovedTrade = new ARow();
      A2Row movedTrades = new A2Row(cMovedTrade, gMovedTrade);
      A2Row futRemnants = new A2Row(r.tFutRemnant, s.tFutRemnant);
      A2Row didGood = new A2Row(); // seet flags done
      int[] did = new int[E.l2secs];
      ARow pr = copy(r.balance);
      ARow pc = copy(c.balance);
      ARow ps = copy(s.balance);
      ARow pg = copy(g.balance);
      A6Row pbal = new A6Row(History.valuesMajor6, "pbal").set(balances);
      

      Trades() {// Assets.CashFlow.Trades
      }

      void initTrade(Offer inOffer, CashFlow ar) { // Assets.CashFlow.Trades
        histTitles("initTrade");
        ohist = inOffer.getOtherHist();
        oEcon = inOffer.getOEcon();
        inOffer.setC(ar.c.balance);  // check c == c
        if (oTradedEconsNext < lTradedEcons - 1) {
          oTradedEcons[oTradedEconsNext++] = oEcon;
        }
        ec.blev1 = oBlev = eM.trade2HistOutputs ? -1 : History.dl; //blev of other lines
        lightYearsTraveled = ((lightYearsTraveled < .2)) ? eM.initTravelYears[pors][0] : lightYearsTraveled;
        for (k = 0; k < 4; k++) {
          sys[k].calcEfficiency();
          sys[k].calcGrowth();
        }
        if (pors == E.P) {
          //      tradedShipOrdinal++;  // 1st trade this year=1, second=2 etc
          //    inOffer.setShipOrdinal(tradedShipOrdinal);
          //   traders[tradersX++] = inOffer.cnName[1];

        }
        for (int i = 0; i < E.l2secs; i++) {
          valueChangesTried[i] = 0;
          oraisedBid[i] = 0;
        }
        // always recalculate costs
        String ttype = yphase.toString() + " ";
        lTitle = ttype + inOffer.cnName[1] + " " + inOffer.cnName[0];
        histTitles(lTitle);
        hist.add(new History("**", History.loopIncrements3, lTitle, " >>>>>>> initiate a new trade <<<<<<"));
        swapped = true;
        n = 0;
        prevn = n;
        inOffer.setCash(cash);
        myOffer = inOffer; // give reference an additional name
        fracN = n / eM.maxn[pors];
        nextN = 2.;
        blev = History.dl;
        aPre = "Ta";
        bals.unzero("bals", BALANCESIX, 4);
        bals.sendHist(blev, aPre);
        //    hist.add(new History(aPre, 7, name + "r balance", r.balance));
        //   hist.add(new History(aPre, 7, name + " s balance", s.balance));

        // set reserve to the enter trade value, before recalc costs
        // set large values higher and small least reserve
        if (true) {
          histTitles("setReserves " + name);
          ARow rcRow = bals.getRow(BALANCESIX + RCIX);
          ARow sgRow = bals.getRow(BALANCESIX + SGIX);
          ARow rcOld = rcRow;
          ARow sgOld = sgRow;
          double rcAve = rcRow.ave();
          double sgAve = sgRow.ave();
          ARow cOld = bals.getRow(BALANCESIX + CIX);
          ARow gOld = bals.getRow(BALANCESIX + GIX);
          ARow rOld = bals.getRow(BALANCESIX + RIX);
          ARow sOld = bals.getRow(BALANCESIX + SIX);
          double cPre, gPre, rPre, sPre, sgPre, rcPre;

          // the frac values in EM represent the fracs to be reserved
          // after costs are calculated, they we be evaluated again
          double cFrac, cFrac1, gFrac, gFrac1, tifrac = eM.tradeReserveIncFrac[pors][clan];
          double rFrac = 1. - (cFrac = eM.startTradeCFrac[searchTrade][pors][clan]);
          double sFrac = 1. - (gFrac = eM.startTradeGFrac[searchTrade][pors][clan]);

          //     double cGoal = rcRow.sum() * eM.startTradeCFrac[0][pors][clan];
          //     double gGoal = sgRow.sum() * eM.startTradeGFrac[0][pors][clan];
          int maxN = E.LSECS;
          double nFrac = 1. / (6 + maxN); // 1/13 =.076923
          double dif;
          inOffer.setC(c.balance); // check cargo again
          for (int n = 0; n < E.LSECS; n++) {
            int nn = rcRow.maxIx(n);//rcMaxIx(0,1,2,3,4,5,6)
            rcPre = rcOld.get(nn);
            sgPre = sgOld.get(nn);
            cPre = cOld.get(nn);
            rPre = rOld.get(nn);
            gPre = gOld.get(nn);
            sPre = sOld.get(nn);
            tifrac = eM.tradeReserveIncFrac[pors][clan];

            // calculate the desired units in r less for larger rc
            //                              .6    + .14  * (7-0 - 1=6)=.86
            //   E.sysmsg("initTrade start loop1 %s%d=%7.2f, %s%d=%7.2f,%s%d=%7.2f,%s%d=%7.2f,rcsg=%b,%b,%b,%b",r.aschar,n,r.balance.get(n),c.aschar,n,c.balance.get(n),s.aschar,n,s.balance.get(n),g.aschar,n,g.balance.get(n),bals.A[2] == r.balance,bals.A[3] == c.balance,bals.A[4] == s.balance, bals.A[5] == g.balance);
            // (1-.076923 *n) (n=0,1,2,3...), 
            // cFrac1 = cFra*1,.92,.84.... cVal decrement by n
            double cVal = rcPre * (cFrac1 = cFrac * (1. - nFrac * n));
            double gVal = sgPre * (gFrac1 = gFrac * (1. - nFrac * n));
            // cval = desired value of c
            // calculate move for the desired units in r
            mov = cPre - cVal;
            double wReservFrac = 1.0 - cFrac;
            double cReservFrac = cFrac;
            E.myTest((dif = rcPre - rPre - cPre) > E.PZERO || dif < E.NZERO, "in initTrade resum Failure dif=%E,rcPre=%7.3f != rPre = %7.3f - cPre=%7.3f = %7.3f n=%2d, nn=%2d", dif, rcPre, rPre, cPre, rPre + cPre, n, nn);
            E.myTest((dif = sgPre - sPre - gPre) > E.PZERO || dif < E.NZERO, "in initTrade resum Failure dif=%E  sgPre=%7.3f - sPre= %7.3f - gPre=%7.3f = %7.3f, n=%2d,  nn=%2d", dif, sgPre, sPre, gPre, sPre + gPre, n, nn);
            if (mov < PZERO && false) { // c too small, SKIP move some r to c
              mov = Math.min(-mov, Math.min(rcPre * tifrac, Math.min(rcPre * wReservFrac, rPre)));
              hist.add(new History(aPre, 7, " r->c" + nn + "=" + df(mov), "preC=" + df(cPre), "=>c " + df(cPre + mov), "preR=" + df(rPre), "=>R " + df(rPre - mov), "cFrac=" + df(cFrac), "cFr1=" + df(cFrac1)));
              EM.addlErr = "r->c" + nn + "=" + df(mov) + ", preC=" + df(cPre) + "->c " + df(cPre + mov) + ", preR=" + df(rPre) + ", ->R " + df(rPre - mov);
              r.putValue(balances,mov, nn, nn, c, 0, .0001);
              inOffer.setC(c.balance);
            }
            else if (mov > PZERO) {  // c too large put some to r
              mov = Math.min(mov, cPre);
              hist.add(new History(aPre, 7, " c=>r" + nn + "=" + df(mov), "preC=" + df(cPre), "=>C " + df(cPre + mov), "preR" + df(rPre), "=>R " + df(rPre - mov), "cVal=" + df(cVal), "cFrac=" + df(cFrac), "rFr1=" + df(cFrac1)));
              EM.addlErr = "c->r" + nn + "=" + df(mov) + ", preC=" + df(cPre) + "->c " + df(cPre + mov) + ", preR=" + df(rPre) + ", ->R " + df(rPre - mov);
              c.putValue(balances,mov, nn, nn, r, 0, .0001);
              inOffer.setC(c.balance);
            }

            // now process the g moves
            mov = gPre - gVal;
            wReservFrac = 1. - gFrac;
            double gReservFrac = gFrac;
            if (mov < 0.0 && false) {  // g too small SKIP move  s=>g
              mov = Math.min(-mov, Math.min(sgPre * tifrac, Math.min(sgPre * wReservFrac, sPre)));
              hist.add(new History(aPre, 7, "s=>g" + nn + "=" + df(mov), "preG=" + df(gPre), "=>C " + df(gPre + mov), "preS=" + df(sPre), "=>S " + df(sPre - mov)));
              EM.addlErr = "s->g" + nn + "=" + df(mov) + ", m2=" + df(mov2) + ", m1=" + df(mov1) + ", preG=" + df(gPre) + "->g " + df(gPre + mov) + ", preS=" + df(sPre) + ", ->S " + df(sPre - mov);
              s.putValue(balances,mov, nn, nn, g, 0, wReservFrac);
              inOffer.setG(g.balance,as.getGuestGrades());

            }
            else if (mov > PZERO) {  //g too large move g=>s
              mov = Math.min(mov, gPre);
              hist.add(new History(aPre, 7, "g=>s" + nn + "=" + df(mov), "preG=" + df(gPre), "=>G " + df(gPre - mov), "preS=" + df(sPre), "=>S " + df(sPre + mov)));
              EM.addlErr = "g->s" + nn + "=" + df(mov) + ", preG=" + df(gPre) + "->g " + df(gPre - mov) + ", preS=" + df(sPre) + ", ->S " + df(sPre + mov);
              g.putValue(balances,mov, nn, nn, s, 0, .0001);
              inOffer.setG(g.balance,as.getGuestGrades());
            }
            //       E.sysmsg("initTrade  end first loop %s%d=%7.2f, %s%d=%7.2f,%s%d=%7.2f,%s%d=%7.2f,rcsg=%b,%b,%b,%b",r.aschar,n,r.balance.get(n),c.aschar,n,c.balance.get(n),s.aschar,n,s.balance.get(n),g.aschar,n,g.balance.get(n),bals.A[2] == r.balance,bals.A[3] == c.balance,bals.A[4] == s.balance, bals.A[5] == g.balance);
          }
        } // end first move loop
        EM.addlErr = ""; // wipe error info
        aPre = "#b";
        bals.sendHist(hist, aPre);
        inOffer.setC(c.balance);
        //       hist.add(new History(aPre, 7, name + "r balance", r.balance));
//        hist.add(new History(aPre, 7, name + " s balance", s.balance));
        // prepare for trade
        //   CashFlow.AssetsFlow asf = new CashFlow.AssetsFlow(as);
        //      asf.init(as, as.cur);
        aPre = "#c";
        //     balances.sendHist4(hist, History.aux2Info, aPre, 7, "iniT r", "iniT c", "iniT S", "iniT G");
        // recalc costs with the new r s values
        n = 0;
        // only use the m + t costs here to represent the travel
        histTitles("calcTravel");
        yCalcCosts(aPre, lightYearsTraveled, eM.tradeHealth[pors][clan], eM.tradeGrowth[pors][clan]);
        // in Assets.CashFlow.Trade.initTrade
        // Save the maint & travel for when lightYearsTraveled was used in yCalcCosts
        if (pors == E.S && newTradeYear1) {
          bals.set2(ABalRows.TCOSTSIX, travelCosts10.getRow(0));
          bals.set2(ABalRows.TCOSTSIX + 1, travelCosts10.getRow(1));
          bals.set2(ABalRows.MCOSTSIX, maintCosts10.getRow(0));
          bals.set2(ABalRows.MCOSTSIX + 1, maintCosts10.getRow(1));
          // don't change travel
          //    lightYearsTraveled = 0.2;
          newTradeYear1 = false;
          newTradeYear2 = true; // use Maint&travel in yrawCalcCosts
        }
        preTradeAvail = -mtgNeeds6.curSum();
        preTradeSum4 = bals.sum4();
        hEmerg = rawProspects2.curMin() < .1;
        //   histStart = hist.size();  // for rehist to start here
        btW = new DoTotalWorths();
        btWTotWorth = btW.getTotWorth();
        aPre = "#d";
        //pbal.setTitle("preInitBal");
        pbal.sendHist(hist, aPre);
        bals.sendHist2(History.loopMinorConditionals5, aPre);
        emergOfrs.titl = "emergOfrs";
        availOfrs.titl = "availOfrs";
        needReq.titl = "needReq";
        fneedReq.titl = "fneedReq";
        
        inOffer.setC(c.balance);
        calcTrades();
        inOffer.setC(c.balance);

        int gix = strategicValues.maxIx();
        ARow cmov = new ARow().zero();
        ARow gmov = new ARow().zero();
        aPre = "$b";
        if (History.dl > History.valuesMajor6) {
          balances.sendHist4(hist, History.aux2Info, aPre, 7, " r bal3", " c bal3", " s bal3", " g bal3");
        }
        if (History.dl > History.valuesMinor7) {
          emergOfrs.sendHist(hist, aPre);
          availOfrs.sendHist(hist, aPre);
          needReq.sendHist(hist, aPre);
          fneedReq.sendHist(hist, aPre);
          maxReqs.sendHist(hist, aPre);
          strategicValues.sendHist(hist, aPre);
        }
        // now set C and G to the emergOfrs amounts
        // the idea is to have a significant set of units to trade
        // loop financial sectors 0 - 6
        inOffer.setMyIx(ec);
        for (int n : ASECS) {
          double cbal = bals.get(BALANCESIX + CIX, n);
          double gbal = bals.get(BALANCESIX + GIX, n);
          double rbal = bals.get(BALANCESIX + RIX, n);
          double sbal = bals.get(BALANCESIX + SIX, n);
          double tifrac = eM.eM.tradeReserveIncFrac[pors][clan];
          double rcbal = bals.get(BALANCESIX + RCIX, n);
          double sgbal = bals.get(BALANCESIX + SGIX, n);
          // prevent subtracting more than cbal or gbal have
          double cet = emergOfrs.get(0, n);
          double get = emergOfrs.get(1, n);
          double cFrac = eM.startTradeCFrac[doingSearchOrTrade][pors][clan];
          double gFrac = eM.startTradeGFrac[doingSearchOrTrade][pors][clan];
          // calc amount to decrease C, neg means increase
          double cdif = cet > PZERO ? cet - cbal : 0.;// add to c from r
          // limit by amount of r available after reserve is kept
          // 1.-reserve is the amount available
          cdif = Math.min(cdif, rcbal * (1. - eM.tradeReservFrac[pors]));
          // the avail frac here is only .3 of the previous reserve
          double rReservFrac = (1. - cFrac) * .3;  //even smailer
          double rAvail = rbal - rcbal * rReservFrac;
          cdif = Math.min(cdif, rAvail);
          double gdif = get > PZERO ? get - gbal : 0; // to g from s
          gdif = Math.min(gdif, sbal * (1. - eM.tradeReservFrac[pors]));
          double sReservFrac = (1. - gFrac) * .3;
          double sAvail = sbal - sgbal * sReservFrac;
          gdif = Math.min(gdif, sAvail);  // may be negative

          //  limit moves by Trade Reserve Increase Frac
          double maxRmov = rbal > rcbal * tifrac ? rcbal * tifrac : rbal;
          double maxSmov = sbal > sgbal * tifrac ? sgbal * tifrac : sbal;
          // move balance from resource to cargo
          cdif = Math.min(cdif, maxRmov);
          if (cdif > PZERO) {  // cargo is short, needs some from resource
            // limit by reserve against rc
            E.myTest(rbal < cdif, "emergOfrs err n=%d rbal %5.2f < cdif %5.2f, emergOfrs %5.2f, cbal %5.2f, rcbal=%5.2f", n, rbal, cdif, cet, cbal, rcbal);
            //       cMovedTrade.set(m, mov)
            r.putValue(balances,cdif, n, n, cargo, 0, 0.);
            E.myTest((rbal = bals.get(BALANCESIX + RIX, n)) < NZERO, "ERROR: r=%4.2f less than zero", rbal);
            emergOfrs.set(0, n, bals.get(3, n)); // set to cargo bal
            hist.add(new History(aPre, 7, "r=>c" + n + "=" + df(cdif), "r=" + df(rbal), "=>" + df(bals.get(2, n)), "c=" + df(cbal), "=>" + df(bals.get(3, n)), "cet=" + df(cet), "cdif=" + df(cdif)));
            inOffer.setC(c.balance);
            // else cargo is -cdif more than is available to trade
          }
          else if (cdif < NZERO && false) { // don't put r back
            cdif = Math.min(-cdif, cbal);
            c.putValue(balances,cdif, n, n, r, 0, .0000);
            E.myTest((rbal) < NZERO, "ERROR r=%4.2f less than zero", rbal);
            emergOfrs.set(0, n, bals.get(BALANCESIX + CIX, n)); // set to cargo bal
            hist.add(new History(aPre, 7, "c=>r" + n + "=" + df(cdif), "c=" + df(cbal), "=>" + df(bals.get(3, n)), "r=" + df(rbal), "=>" + df(bals.get(2, n)), "cet=" + df(cet), "cdif=" + df(cdif)));
            E.sysmsg("initTrade loop2 end %s%d=%7.2f, %s%d=%7.2f,%s%d=%7.2f,%s%d=%7.2f,rcsg=%b,%b,%b,%b", r.aschar, n, r.balance.get(n), c.aschar, n, c.balance.get(n), s.aschar, n, s.balance.get(n), g.aschar, n, g.balance.get(n), bals.A[2] == r.balance, bals.A[3] == c.balance, bals.A[4] == s.balance, bals.A[5] == g.balance);
          }
          if (gdif > PZERO) {  // guests is short, needs some from resource
            gdif = Math.min(gdif, Math.min(sgbal * tifrac, sbal));
            emergOfrs.set(1, n, bals.get(5, n)); // set to guests bal
            E.myTest(sbal < gdif, "emerg Ofrs %5.2f err n=%d, sbal %5.2f < gdif %5.2f gbal %5.2f, sgbal %5.2f", get, n, sbal, gdif, gbal, sgbal);
            //       cMovedTrade.set(m, mov);
            // move balance from resource to cargo
            s.putValue(balances,gdif, n, n, g, 0,0.);
            E.myTest((sbal = bals.get(BALANCESIX + SIX, n)) < NZERO, "ERROR: sbal=%4.2f less than zero", sbal);
            hist.add(new History(aPre, 7, "s=>g" + n + "=" + df(gdif), "s=" + df(sbal), "=>" + df(bals.get(4, n)), "g=" + df(gbal), "=>" + df(bals.get(5, n)), "get=" + df(get), "gdif=" + df(gdif), "<<<<<<<<<<<<<<<<"));
            // else cargo is -cdif more than is available to trade
          }
          else if (gdif < NZERO && false) { // nothing back to s
            gdif = Math.min(-gdif, gbal);;
            g.putValue(balances,gdif, n, n, s, 0, .000);
            E.myTest((sbal = bals.get(4, n)) < NZERO, "sbal=%7.2f less than zero", sbal);
            hist.add(new History(aPre, 7, "s!=>g" + n, "s=" + df(sbal), "g=" + df(gbal), "=>" + df(g.balance.get(n)), "get=" + df(get), "gdif=" + df(gdif), "****"));
          }

          // now revise the trades to abide by the cbal and gbal
          emergOfrs.set(0, n, Math.min(emergOfrs.get(0, n), cargo.balance.get(n)));
          availOfrs.set(0, n, Math.min(availOfrs.get(0, n), cargo.balance.get(n)));
          emergOfrs.set(1, n, Math.min(emergOfrs.get(1, n), guests.balance.get(n)));
          availOfrs.set(1, n, Math.min(availOfrs.get(1, n), guests.balance.get(n)));
        }

        aPre = "T#";
        if (History.dl > 5) {
          hist.add(h1 = new History(aPre, 5, name + " initTrade R", resource.balance));
          hist.add(h2 = new History(aPre, 5, name + " initTrade S", staff.balance));
          hist.add(h3 = new History(aPre, 5, name + " initTrade C", c.balance));
          hist.add(h4 = new History(aPre, 5, name + " initTrade G", g.balance));
          emergOfrs.sendHist(hist, aPre);
          availOfrs.sendHist(hist, aPre);
        }
        myIx = inOffer.setMyIx(ec); // set myIx in Offer
        oIx = (myIx + 1) % 2;

        g.checkSumGrades();
        A2Row cg = new A2Row(c.balance, g.balance);

        inOffer.setC(c.balance);
        inOffer.setG(g.balance, g.grades);
        double pz = PZERO;
        double rNeed = PZERO;
        double aOffer = PZERO;
        double fNeed = PZERO;
        double need = fNeed > rNeed ? fNeed : rNeed;
        double eOffer = PZERO;
        double offer = aOffer > PZERO ? aOffer : 0.;
        int sv = 0;
        int ifSearch = yphase == yrphase.SEARCH ? 0 : 1; // search or barter
        double criticalNumber = (int) eM.criticalNumbers[ifSearch][pors][clan];
        bids = makeZero(bids, "bids");
        for (m = 0; m < E.L2SECS; m++) {
          // go from the most needed to least needed
          n = strategicValues.maxIx(m);//highest sv need, lowest bal
          rNeed = needReq.get(n);
          aOffer = availOfrs.get(n);
          fNeed = fneedReq.get(n);
          need = fNeed > rNeed ? fNeed : rNeed;
          eOffer = emergOfrs.get(n);  // used only in barter
          offer = aOffer > PZERO ? aOffer : 0.;
          // sv <  6 least strategic value, so most to give away, but fneed>0, so need not offer
          // if hot need and aOffer (avail) > 0, than save as an offer but not tEmerg offer
          bids.set(n, m < criticalNumber ? need > PZERO ? -need : offer : offer > PZERO ? offer : need > PZERO ? -need : 0.);
        }
        //  initGoods = E.copy(bids);
        myxitGoods = E.copy(bids);
        myxit1Goods = myxit2Goods = myxit3Goods = myxitGoods;
        hist.add(new History("ti", History.valuesMinor7, name + "initTrade" + ">>>>>>>>>>", "myIx" + inOffer.myIx, "c " + (inOffer.cargos[inOffer.myIx] == c.balance ? "c == cargos" : " c not cargos"), "<<<<<<<<<<<<<"));
        //       inOffer.resetIx();
        listBids(aPre, 3);
      }  // Assets.CashFlow.Trades.initTrade

      /**
       * exit Trade and move cargo and guests back to resource and staff however
       * if guests were redistributed for trade, put them back where they were
       * originally needed. term= 0 mytrade,-1 my reject,-2 other traded,-3o
       * reject
       */
      void xitTrade() {// Assets.CashFlow.Trades

        A2Row cg = new A2Row(c.balance, g.balance);
        A2Row cg2 = new A2Row().set(cg);
        // following stats

        i = 0;

        gGrades = g.copyGrades(g.grades);

        //    ARow ic = initGoods.getARow(0);
        //     ARow ig = initGoods.getARow(E.lsecs);
        //   hist.add(h1 = new History(aPre, 5, name + " xit0Good C", ic));
        //    hist.add(h2 = new History(aPre, 5, name + " xit0Good g", ig));
        //    hist.add(h3 = new History(aPre, 5, name + " xit0T C", pc));
        //  hist.add(h4 = new History(aPre, 5, name + " xit0T G", pg));
        //  hist.add(h5 = new History(aPre, 5, name + " xit1 R", resource.balance));
        //  hist.add(h6 = new History(aPre, 5, name + " xit1 S", staff.balance));
        //  hist.add(h7 = new History(aPre, 5, name + " xit1 C", c.balance));
        //    hist.add(h8 = new History(aPre, 5, name + " xit1 G", g.balance));
        if (myOffer.getTerm() == -1) {
          cash = pCash;  // no Trade done
        }
        yCalcCosts(aPre, lightYearsTraveled, eM.tradeHealth[pors][clan], eM.tradeGrowth[pors][clan]);
        postTradeAvail = -mtgNeeds6.curSum();
        postTradeSum4 = bals.sum4();
        hist.add(h1 = new History(aPre, 5, name + " xitf R", resource.balance));
        hist.add(h2 = new History(aPre, 5, name + " xitf S", staff.balance));
        hist.add(h3 = new History(aPre, 5, name + " xitf C", c.balance));
        hist.add(h4 = new History(aPre, 5, name + " xitf G", g.balance));
        if (pors == E.s) {
          myOffer.clean();
      //    ec.buildPlanetOffers(myOffer.cn[0].myPlanetOffers, myOffer);
        }

      }// Assets.CashFlow.Trades.xitTrade

      /**
       * disabled exit Barter, do clean up, in particular if ohist is set copy
       * my history to other history
       */
      void xitBarter() { // Assets.CashFlow.Trades
        // test whether to copy the last section of hist to ohist
        if (ohist != null && histStart > 100 && false) {
          int endHist = hist.size();
          History hh;
          for (int m = histStart; m < endHist; m++) {
            hh = hist.get(m);
            if (hh != null) {
              ec.addOHist(ohist, hh);
            }
          }
        }
        histStart = -100;
      } // Assets.CashFlow.Trades.xitBarter

      /**
       * Consider the new offer, and make a return offer
       *
       * for my unfavored clans, the strategicGoal increases for their unfavor
       * myclan the strategicGoal increases
       *
       * First convert of their offer to my offer. Their requests become my
       * offers (positive values of cargo or guests) Ensure that my offers do
       * not exceed values of maxTrades, or at most emergOfrs if reasserting a
       * high priority request, reduce my offers to 0 or the needed ..Trades.
       *
       * Their offers become my requests, these may become less than maxTrades
       *
       * then process requests/(offers + manualsValue) = strategicValue if
       * strategicValue .le strategicGoal
       *
       * strategicGoal = tradeFrac[pors][clan] *
       * ((tm1=cRand(15,clanMult[pors][clan])< 1./randMax?1./randMax:tm1>randMax:randMax:tm1)
       * *(1. - ((3. - fav[myclan][oClan])/3.)
       * *((3.-oclanFavMult[myclan]*fav[oClan][myclan])/3.)
       * *((fav[myclan][pclan]>3. && fav[oClan][myclan] .gt 3.?sosFrac:1.)) *(1
       * - barterTimes*barterMult)
       *
       * Each offer is processed by its strategic value and several rules
       *
       * 18) Initial offer: by ship, request up to 5 highest strategic requests
       * (lowest tradeMax values), requests are set to tradeMax.
       *
       * Offer everything that has a positive tradeMax value regardless of
       * stategicValue ratio
       *
       *
       * 17) Planet make request and offers equivalent to the ship method,
       * ignoring the offer from the ship.
       *
       * 16) Ship: Start processing using the strategicValue of the
       * request/offer. Attempt the reassert the top 4 of the initial ship
       * requests removed by planet. request only the 4 top request with values
       * no higher than previous values, only request 5 if it is offered. Repeat
       * ship offers up to ratio. Offers are created starting from lowest
       * strategic value, offering the positive tradeMax amount. Offer only
       * enough to equal the strategic value of the requests including requested
       * cash, divided by this turns strategicRatio minus the value of manuals
       * earned in the trade. If health is &lt 40% an include available cash in
       * the offer.<br>
       * 15) check for accept, keep track of raises rejected, either ruduce
       * later requests, increase offers but not above rejection, or offer cash
       * if urgent and cash available. Planet request only the 4 top requests,
       * 5'th only if offered, repeat planet offers<br>
       * 14) Ship request only 2 top requests no higher than previous requests 3
       * - 5 only if offered, raise only those a previous raise was not refused,
       * and only the amount offered, repeat ship offers to offer fraction.<br>
       * 13) planet request only 2 top requests no higher than previous
       * requests. 3 - 5 only if offered and amount of offer. repeat planet
       * offer to offer faction<br>
       * 12) ship request 1 top request no higher than previous, 2 -5 only to
       * amount of planet offer, repeat ship offers to offer fraction<br>
       * 11) planet request top 1 request no higher than previous, 2 - 5 only to
       * amount of ship offer, repeat planet offers to offer fraction<br>
       * 10,9,8,7,6,5,4,3,2 ship planet alternate, only trim what is offered to
       * offer fraction<br>
       * 1) final offer, no change allowed, either ship or planet may jump to
       * this value. May jumped to at any offer after 15<br>
       * 0) offer accepted as offered. may be issued at any offer after 15.
       * <br>
       * -1) offer rejected as offered, may be issued at any offer after 15.
       *
       * @param otherOffer
       *
       * @return
       */
      Offer barter(Offer prevOffer
      ) { // Assets.CashFlow.Trades.barter
        if (histStart < 100) {
          // initialize for xitBarter copy of hist to ohist
          histStart = hist.size();
        }
        hist.add(new History("**", 5, name + " ntr tBartr t=" + prevOffer.getTerm(), "before", "any test"));
        changes = 0; // restart count each barter
        newBarter = true;
        myOffer = prevOffer; // move Offer reference to xit name
        term = prevOffer.getTerm();
        ohist = prevOffer.getOHist();
        oname = prevOffer.getOName();
        oClan = prevOffer.getOClan();
        myIx = prevOffer.setMyIx(ec);;
        myIx = prevOffer.getMyIx();
        aPre0 = "#A";
        aPre = "#B";
        aPre2 = "#C";
        aPre3 = "#D";
        ec.blev = History.dl;
        ec.lev = mRes;
        fav = eM.fav[clan][oClan];
        oFav = eM.fav[oClan][clan];
        //    E.sysmsg(name + "Enter Trades.barter term=" + term);
        hist.add(new History(aPre0, mRes, name + "ntr barter" + ">>>>>>>>>>", "myIx" + prevOffer.myIx, "c" + (prevOffer.cargos[prevOffer.myIx] == c.balance ? "c == cargos" : " c != cargos"), "<<<<<<<<<<<<<"));

        // ===================== t18 =======================================
        if (term == eM.barterStart) {  // first Planet offer
          tradedFirstStrategicReceipts = totalStrategicRequests;
          tradedFirstReceipts = totalRequests;
          tradedFirstSends = totalOffers;
          // listneedReq(mRes, aPre0);
          //  listavailOfrs(mRes, aPre0);
          // listemergOfrs(mRes, aPre0);
          // listGoods(mRes, aPre0);

          if (History.dl > History.valuesMinor7 && false) {
            emergOfrs.sendHist(hist, aPre);
            availOfrs.sendHist(hist, aPre);
            needReq.sendHist(hist, aPre);
            fneedReq.sendHist(hist, aPre);
            strategicValues.sendHist(hist, aPre);
          }

          //   listDifBid(History.valuesMajor6, "xit", oprevGoods);
          enforceStrategicGoal();
          myOffer.set2Goods(bids);  // set at this point
       //   myOffer.set2InitialPlanetGoods(bids); // save for selectplanet
          hist.add(new History(aPre, lRes, "T" + term + " " + name + " vals" + changes, "sv=" + ec.mf(sv1), "->" + ec.mf(sv), "sf=" + df(sf1), "->" + df(sf), "ofr=" + df(offers), df(bids.curPlusSum()), "rqst=" + df(requests), df(bids.curNegSum()), "exOf" + df(excessOffers), "x/of" + df(excessOffers / offers), "<<<<<<<"));
          ec.addOHist(ohist, new History(aPre, lRes, "T" + term + " " + name + " vals" + changes, "sv=" + ec.mf(sv1), "->" + ec.mf(sv), "sf=" + df(sf1), "->" + df(sf), "ofr=" + df(offers), df(bids.curPlusSum()), "rqst=" + df(requests), df(bids.curNegSum()), "exOf" + df(excessOffers), "x/of" + df(excessOffers / offers), "<<<<<<<"));
          myOffer.setTerm(term - 1);  
          xitBarter();
          myxit3Goods = myxit2Goods = myxit1Goods = myxitGoods = bids.copy();
          //        E.sysmsg(" xit Trades.barter term=" + myOffer.getTerm());
          // listCG(balances, 4, "bsx", myOffer);
          return myOffer;
// Assets.CashFlow.Trades.barter
        }

        // ====================17,16,15,14 =================================
        else if (term > eM.barterStart - 5) { // ship > 13
          int myIx1 = prevOffer.myIx;
          ARow cc1 = prevOffer.cargos[0];       

          bids = prevOffer.getGoods();
          if (term > eM.barterStart - 3) { // 17 ship,16 planet only 
            oFirstBid = bids.copy(); // save copy of first planet and ship at exit 
          }
          bids.sendHistt("ntr bid c", "ntr bid g");
          hist.add(new History(aPre, mRes, name + "ntr barter" + ">>>>>>>>>>", "myIx" + prevOffer.myIx, "c" + (prevOffer.cargos[prevOffer.myIx] == c.balance ? "c == cargos" : " c != cargos"), "<<<<<<<<<<<<<"));
          myOffer = prevOffer.flipOffer(ec); // set up for our process

          int myIx = myOffer.myIx;
          ARow ccc = myOffer.cargos[myIx];
          hist.add(new History(aPre, mRes, name + "ntr barter" + term + ">>>>>>>>>>", "myIx=" + myOffer.myIx, "c " + (myOffer.cargos[myOffer.myIx] == c.balance ? "== cargos" : "not cargos"), (ccc == c.balance ? "ccc == c.balance" : "ccc != c.balance"), "<<<<<<<<<<<<<"));
          // E.myTest(myOffer.cargos[myIx] != cargo.balance, "ccc != cargo.balance myIx=" + myIx);
          //        bids = myOffer.getGoods();
          if (History.dl > History.valuesMinor7 && false) {
            bids.sendHistt("flpd bid c", "flpd bid g");

            emergOfrs.sendHistcg();
            availOfrs.sendHistcg();
            needReq.sendHistcg();
            fneedReq.sendHistcg();
            strategicValues.sendHistcg();
          }
          enforceStrategicGoal(); // sf1, sv1, sf,sv,excessOffers
          hist.add(new History(aPre, lRes, "T" + term + " " + name + " CONTc" + changes, "sv=" + ec.mf(sv1), "->" + ec.mf(sv), "sf=" + df(sf1), "->" + df(sf), "ofr=" + df(offers), df(bids.curPlusSum()), "rqst=" + df(requests), df(bids.curNegSum()), "exOf" + df(excessOffers), "x/of" + df(excessOffers / offers), "<<<<<<<"));
          ec.addOHist(ohist, new History(aPre, 3, "T" + term + " " + name + " CONTc" + changes, "sv=" + ec.mf(sv1), "->" + ec.mf(sv), "sf=" + df(sf1), "->" + df(sf), "ofr=" + df(offers), df(bids.curPlusSum()), "rqst=" + df(requests), df(bids.curNegSum()), "exOf" + df(excessOffers), "x/of" + df(excessOffers / offers), "<<<<<<<"));

          myxit3Goods = myxit2Goods = myxit1Goods = myxitGoods = bids.copy();
          //     listDifBid(History.valuesMajor6, "xit17", oprevGoods);
          myOffer.setTerm(term - 1);
          //       E.sysmsg(" xit Trades.barter term=" + myOffer.getTerm());
          xitBarter(); // list messages to other
          //      listCG(balances, 4, "bsx", myOffer);
          return myOffer;

          // Assets.CashFlow.Trades.barter
        }
        //============================== 1 ==================================
        else if (term == 1) {
          bids = prevOffer.getGoods();
          listGoods(mRes, "ba");
          hist.add(new History(aPre, mRes, name + "ntr barter" + ">>>>>>>>>>", "myIx" + prevOffer.myIx, "c" + (prevOffer.cargos[prevOffer.myIx] == c.balance ? "c == cargos" : " c not cargos"), "<<<<<<<<<<<<<"));
          myOffer = prevOffer.flipOffer(ec); // set up for our process

          int myIx = myOffer.myIx;
          ARow ccc = myOffer.cargos[myIx];
          hist.add(new History(aPre, mRes, name + "ntr barter" + term + ">>>>>>>>>>", "myIx=" + myOffer.myIx, "c" + (myOffer.cargos[myOffer.myIx] == c.balance ? "c == cargos" : " c != cargos"), (ccc == c.balance ? "ccc == c.balance" : "ccc != c.balance"), "<<<<<<<<<<<<<"));
          //     E.myTest(ccc != cargo.balance, "ccc != cargo.balance myIx=" + myIx);
          bids = myOffer.getGoods();  // get the reference
          oprevGoods = bids.copy();
          // possibly reduce offers to fit requests
          enforceStrategicGoal(); // sf1,sv1,sf,sv,excessOffers
          aPre = "#y";
          listGoods(mRes, "$#");
          hist.add(new History(aPre, lRes, "done=" + term + " " + name, "barter", "ended", "barter", "ended", "<<<<<<<"));
          ec.addOHist(ohist, new History(aPre, lRes, "done=" + term + " " + name, "barter", "ended", "barter", "ended", "<<<<<<<"));
          // if I had to make changes, I d0 not accept final offer
          if (term == 1 && testNoTrade(myOffer) || myOffer.getTerm() < 1) {  // Assets.CashFlow.Trades.barter
            tradeRejected = true;

            listDifBid(History.valuesMajor6, "rej" + term, oprevGoods);
            myOffer.setTerm(-1);  // rejected
            term = -1;  // rejected
            listGoods(5, "!R");
            // strategicValue = calcStrategicSums();
            // strategicGoal = calcStrategicGoal();

            hist.add(new History(aPre, lRes, "T" + term + " " + name + " REJa" + changes, "sv=" + ec.mf(sv1), "->" + ec.mf(sv), "sf=" + df(sf1), "->" + df(sf), "ofr=" + df(offers), df(bids.curPlusSum()), "rqst=" + df(requests), df(bids.curNegSum()), "exOf" + df(excessOffers), "x/of" + df(excessOffers / offers), "<<<<<<<"));
            ec.addOHist(ohist, new History(aPre, lRes, "T" + term + " " + name + " REJa" + changes, "sv=" + ec.mf(sv1), "->" + ec.mf(sv), "sf=" + df(sf1), "->" + df(sf), "ofr=" + df(offers), df(bids.curPlusSum()), "rqst=" + df(requests), df(bids.curNegSum()), "exOf" + df(excessOffers), "x/of" + df(excessOffers / offers), "<<<<<<<"));
            xitBarter();
            //         listCG(balances, 4, "bsx", myOffer);
            return myOffer;
          }
          // if trade was not rejected than it was accepted
          myOffer.setTerm(0);  //accept trade

          E.sysmsg(" Trades.barter acceped");
          listGoods(mRes, "B+");
          //    listCG(balances, 4, "byes", myOffer);
          // stats are already saved in cur = Assets.CashFlow by calcStrategicSums
          //         sendStats(tSend, tReceipts, tStratValue, tBid, (int) E.fav[clan][myOffer.getOClan()]);
          //     strategicValue = calcStrategicSums();
          //   strategicGoal = calcStrategicGoal();
          hist.add(new History(aPre, lRes, "T" + term + " " + name + " ACCa" + changes, "sv=" + ec.mf(sv1), "->" + ec.mf(sv), "sf=" + df(sf1), "->" + df(sf), "ofr=" + df(offers), df(bids.curPlusSum()), "rqst=" + df(requests), df(bids.curNegSum()), "exOf" + df(excessOffers), "x/of" + df(excessOffers / offers), "<<<<<<<"));
          ec.addOHist(ohist, new History(aPre, 3, "T" + term + " " + name + " ACCa" + changes, "sv=" + ec.mf(sv1), "->" + ec.mf(sv), "sf=" + df(sf1), "->" + df(sf), "ofr=" + df(offers), df(bids.curPlusSum()), "rqst=" + df(requests), df(bids.curNegSum()), "exOf" + df(excessOffers), "x/of" + df(excessOffers / offers), "<<<<<<<"));
          myOffer.accepted(ec); // the bid becomes a move
          didTrade = true;
          rejectTrade = false;
          listDifBid(History.valuesMajor6, "#X" + term, oprevGoods);
          xitBarter();
          E.sysmsg(" xit Trades.barter accepted");
          //      listCG(balances, 4, "bsx", myOffer);
          return myOffer;

          // Assets.CashFlow.Trades.barter
        }
        else if (term < 1) {
          E.myTest(true, "Error: barter error term < 1");

        }
        //========================= 13 -> 2 ===============================
        else if (term < eM.barterStart - 3) {  // Now the rest
          bids = prevOffer.getGoods();
          listGoods(mRes, "@a");
          hist.add(new History(aPre, mRes, name + "ntr barter" + ">>>>>>>>>>", "myIx" + prevOffer.myIx, "c" + (prevOffer.cargos[prevOffer.myIx] == c.balance ? "c == cargos" : " c not cargos"), "<<<<<<<<<<<<<"));
          myOffer = prevOffer.flipOffer(ec); // set up for our process

          //    oprev1Goods = oprevGoods;
          hist.add(new History(aPre, mRes, name + "ntr barter" + ">>>>>>>>>>", "myIx" + myOffer.myIx, "c" + (myOffer.cargos[myOffer.myIx] == c.balance ? "c == cargos" : " c not cargos"), "<<<<<<<<<<<<<"));
          int myIx = myOffer.myIx;
          ARow ccc = myOffer.cargos[myIx];
          //     E.myTest(ccc != cargo.balance, "ccc != cargo.balance myIx=" + myIx);
          //      bids = myOffer.getGoods();  // get the reference
          oprevGoods = bids.copy();
          //  sv1 = strategicValue = calcStrategicSums();
          //  sf1 = strategicGoal = calcStrategicGoal();
          if (History.dl > History.valuesMinor7) {
            emergOfrs.sendHist(hist, aPre);
            availOfrs.sendHist(hist, aPre);
            needReq.sendHist(hist, aPre);
            fneedReq.sendHist(hist, aPre);
            strategicValues.sendHist(hist, aPre);
          }

          enforceStrategicGoal(); // sf1,sv1,sf,sv,excessOffers
          myOffer.set2Values(bids, offers, requests, totalSend, totalReceipts, strategicGoal, strategicValue); // save for selectPlanet
          if (testTrade(myOffer)) {
            // already fixed  strategicValue = calcStrategicSums(); 
            //   strategicGoal = calcStrategicGoal();
            hist.add(new History(aPre, lRes, "T" + term + " " + name + " ACCc" + changes, "sv=" + ec.mf(sv1), "->" + ec.mf(sv), "sf=" + df(sf1), "->" + df(sf), "ofr=" + df(offers), df(bids.curPlusSum()), "rqst=" + df(requests), df(bids.curNegSum()), "exOf" + df(excessOffers), "x/of" + df(excessOffers / offers), "<<<<<<<"));
            ec.addOHist(ohist, new History(aPre, lRes, "T" + term + " " + name + " ACCc" + changes, "sv=" + ec.mf(sv1), "->" + ec.mf(sv), "sf=" + df(sf1), "->" + df(sf), "ofr=" + df(offers), df(bids.curPlusSum()), "rqst=" + df(requests), df(bids.curNegSum()), "exOf" + df(excessOffers), "x/of" + df(excessOffers / offers), "<<<<<<<"));
            myOffer.setTerm(1);
            term = 1;

            E.sysmsg(" Trades.barter acceped term=" + myOffer.getTerm());
            listGoods(mRes, " acpt t=");
            //        sendStats(tSend, tReceipts, tStratValue, tBid, (int) E.fav[clan][myOffer.getOClan()]);
            listDifBid(History.valuesMajor6, "acpt" + term, oprevGoods);
            //     myOffer.accepted(ec);
            //    didTrade = true;
            //    rejectTrade = false;
            hist.add(new History("B+", History.informationMinor9, "aftr accpt term=" + term, "abcde fgh ijk"));
            xitBarter();
            //     hist.add(new History("B+", History.informationMinor9, "aftr xBartr term=" + term, "abcde fgh ijk"));
            //     listCG(balances, 4, "bsx", myOffer);
            E.sysmsg(" xit Trades.barter acceped term=" + myOffer.getTerm());
            return myOffer;
          }
          if (testNoTrade(myOffer)) { // Assets.CashFlow.Trades.barter
            //         tBid = bids.copy();
            //        tStratValue = sv1;
            //        tStratFrac = sf1;
            //        offeredTrade = eM.year;
            //tSend = totalSend;
            //        tReceipts = totalReceipts;
            //       tMoreManuals = myOffer.getMoreManuals();
            myOffer.setTerm(-1);  //rejected
            term = -1;
            listDifBid(History.valuesMajor6, "rej", oprevGoods);
            // myOffer.setTerm(-1);  //rejected
            // term = -1;
            didTrade = false;
            rejectTrade = true;
            listGoods(mRes, "B-");
            //     strategicValue = calcStrategicSums();
            //     strategicGoal = calcStrategicGoal();
            hist.add(new History(aPre, lRes, "T" + term + " " + name + " REJ chgs" + changes, "sv=" + ec.mf(sv1), "->" + ec.mf(sv), "sf=" + df(sf1), "->" + df(sf), "ofr=" + df(offers), df(bids.curPlusSum()), "rqst=" + df(requests), df(bids.curNegSum()), "exOf" + df(excessOffers), "x/of" + df(excessOffers / offers), "<<<<<<<"));
            ec.addOHist(ohist, new History(aPre, lRes, "T" + term + " " + name + " REJchgs" + changes, "sv=" + ec.mf(sv1), "->" + ec.mf(sv), "sf=" + df(sf1), "->" + df(sf), "ofr=" + df(offers), df(bids.curPlusSum()), "rqst=" + df(requests), df(bids.curNegSum()), "exOf" + df(excessOffers), "x/of" + df(excessOffers / offers), "<<<<<<<"));
            xitBarter();
            //     listCG(balances, 4, "bsx", myOffer);
            return myOffer;
          }
          //     E.myTest(true,"Error: entered dead end of barter");
          // give a little extra offer initially
          //     strategicGoal = calcStrategicGoal(term) - .2;  // use next barter term
          //      calcStrategicSums();
          //      renewStrategicRequests(3);
          //     limitOffers();
          //        enforceStrategicGoal(); //sf1,sv1,sf,sv,excessOffers
          //    previously set  strategicValue = calcStrategicSums();
          //     strategicGoal = calcStrategicGoal();
          double ts2 = totalSend;
          double tr2 = totalReceipts;
          listGoods(mRes, "brtrx");
          hist.add(new History(aPre, lRes, "T" + term + " " + name + " CONTc" + changes, "sv=" + ec.mf(sv1), "->" + ec.mf(sv), "sf=" + df(sf1), "->" + df(sf), "ofr=" + df(offers), df(bids.curPlusSum()), "rqst=" + df(requests), df(bids.curNegSum()), "exOf" + df(excessOffers), "x/of" + df(excessOffers / offers), "<<<<<<<"));
          ec.addOHist(ohist, new History(aPre, lRes, "T" + term + " " + name + " CONTc" + changes, "sv=" + ec.mf(sv1), "->" + ec.mf(sv), "sf=" + df(sf1), "->" + df(sf), "ofr=" + df(offers), df(bids.curPlusSum()), "rqst=" + df(requests), df(bids.curNegSum()), "exOf" + df(excessOffers), "x/of" + df(excessOffers / offers), "<<<<<<<"));
          // remember bids is a pointer to bids in the offer
          myOffer.setTerm(term - 1);
          //         myxit3Goods = myxit2Goods;
          //         myxit2Goods = myxit1Goods;
          myxit1Goods = myxitGoods;
          myxitGoods = bids.copy();
          listDifBid(History.valuesMajor6, "xit" + term, oprevGoods);
          //    listCG(balances, 4, "bsx", myOffer);
          xitBarter();
          E.sysmsg("xit Trades.barter term=" + myOffer.getTerm());
          return myOffer;
        }
// should not be reached
        E.myTest(true, "Error: entered dead end of barter");
        bids = prevOffer.getGoods();
        listGoods(mRes, "bstrtaa");
        hist.add(new History(aPre, mRes, name + "ntr barter" + ">>>>>>>>>>", "myIx" + prevOffer.myIx, "c" + (prevOffer.cargos[prevOffer.myIx] == c.balance ? "c == cargos" : " c not cargos"), "<<<<<<<<<<<<<"));
        myOffer = prevOffer.flipOffer(ec); // set up for our process

        hist.add(new History(aPre2, mRes, name + "ntr barter" + ">>>>>>>>>>", "myIx" + myOffer.myIx, "c" + (myOffer.cargos[myOffer.myIx] == c.balance ? "c == cargos" : " c not cargos"), "<<<<<<<<<<<<<"));
        int myIx = myOffer.myIx;
        ARow ccc = myOffer.cargos[myIx];
        E.myTest(ccc != cargo.balance, "ccc != cargo.balance myIx=" + myIx);
        bids = myOffer.getGoods();  // get the reference
        oprevGoods = bids.copy();
        double sv1 = strategicValue = calcStrategicSums();
        double sf1 = strategicGoal = calcStrategicGoal();
        double ts1 = totalSend;
        double tr1 = totalReceipts;
        listGoods(5, aPre2);

        if (testTrade(myOffer)) {
          myOffer.setTerm(0);
          term = 0;
          strategicValue = calcStrategicSums();
          strategicGoal = calcStrategicGoal();
          double ts2 = totalSend;
          double tr2 = totalReceipts;
          listGoods(3, "TT");
          hist.add(new History(aPre, lRes, "T" + term + " " + name + " CONTINUE", "sv=" + ec.mf(sv1), "->" + ec.mf(sv), df(strategicValue / isv1), "sf=" + df(sf1), "->" + df(sf), df(sf / isf1), "ofr=" + df(offers), "rqst=" + df(requests), "exOf" + df(excessOffers), "x/of" + df(excessOffers / offers), "<<<<<<<"));
          ec.addOHist(ohist, new History(aPre, lRes, "T" + term + " " + name + " CONTINUE", "sv=" + ec.mf(sv1), "->" + ec.mf(sv), df(strategicValue / isv1), "sf=" + df(sf1), "->" + df(sf), df(sf / isf1), "ofr=" + df(offers), "rqst=" + df(requests), "exOf" + df(excessOffers), "x/of" + df(excessOffers / offers), "<<<<<<<"));
          //         sendStats(tSend, tReceipts, tStratValue, tBid, (int) E.fav[clan][myOffer.getOClan()]);
          myOffer.accepted(ec);
          didTrade = true;
          rejectTrade = false;
          xitBarter();
          return myOffer;
        }
        if (testNoTrade(myOffer)) {
          myOffer.setTerm(-1);
          term = -1;
          strategicValue = calcStrategicSums();
          strategicGoal = calcStrategicGoal();
          double ts2 = totalSend;
          double tr2 = totalReceipts;
          listGoods(3, "xx");
          didTrade = false;
          rejectTrade = true;
          hist.add(new History(aPre, lRes, "T" + term + " " + name + " vals", "sv=" + df(sv1), "->" + df(strategicValue), df(strategicValue / isv1), "sf=" + df(sf1), "->" + df(strategicGoal), df(strategicGoal / isf1), "ofr=" + df(offers), "rqst=" + df(requests), "<<<<<<<"));
          ec.addOHist(ohist, new History(aPre, lRes, "T" + term + " " + name + " vals", "sv=" + df(sv1), "->" + df(strategicValue), df(strategicValue / isv1), "sf=" + df(sf1), "->" + df(strategicGoal), df(strategicGoal / isf1), "ofr=" + df(offers), "rqst=" + df(requests), "<<<<<<<"));
          xitBarter();
          return myOffer;
        }

        // give a little extra offer initially
        //     strategicGoal = calcStrategicGoal(term) - .2;  // use next barter term
        enforceStrategicGoal();
        myOffer.setTerm(term - 1);
        double ts2 = totalSend;
        double tr2 = totalReceipts;
        listBids("x", History.loopIncrements3);
        hist.add(new History(aPre, lRes, "T" + term + " " + name + " vals", "sv=" + df(sv1), "->" + df(strategicValue), df(strategicValue / isv1), "sf=" + df(sf1), "->" + df(strategicGoal), df(strategicGoal / isf1), "ofr=" + df(offers), "rqst=" + df(requests), "<<<<<<<"));
        ec.addOHist(ohist, new History(aPre, lRes, "T" + term + " " + name + " vals", "sv=" + df(sv1), "->" + df(strategicValue), df(strategicValue / isv1), "sf=" + df(sf1), "->" + df(strategicGoal), df(strategicGoal / isf1), "ofr=" + df(offers), "rqst=" + df(requests), "<<<<<<<"));
        // remember bids is a pointer to bids in the offer
        myxit3Goods = myxit2Goods;
        myxit2Goods = myxit1Goods;
        myxit1Goods = myxitGoods;
        myxitGoods = bids.copy();
        //    E.sysmsg(" xit Trades.barter term=" + myOffer.getTerm());
        xitBarter();
        return myOffer;
      } // Assets.CashFlow.Trades.Barter

      // Assets.CashFlow.Trades.listG
      void listBids(String pre, int lev
      ) {
        hist.add(h1 = new History(pre, lev, term + " " + name + " bidC" + myOffer.getMyIx(), bids.getARow(0)));
        hist.add(h2 = new History(pre, lev, term + " " + name + " bidG" + myOffer.getPrevMyIx(), bids.getARow(E.lsecs)));
      }

      /**
       * list the values of c and g
       *
       * @param bals balances etc
       * @param pre hist prefix of listing
       * @param lev level at which to list
       * @param myOffer
       */
      void listCG(ABalRows bals, String pre, int lev,
              Offer myOffer
      ) {
        hist.add(h1 = new History(pre, lev, term + " " + name + " balC.3 " + myOffer.getMyIx(), bals.getRow(3)));
        hist.add(h2 = new History(pre, lev, term + " " + name + " balG.5 " + myOffer.getPrevMyIx(), bals.getRow(5)));
        hist.add(h1 = new History(pre, lev, term + " " + name + " c.bal", c.balance));
        hist.add(h2 = new History(pre, lev, term + " " + name + " g.bal", g.balance));
        E.myTest(myOffer.getC() != c.balance, "myOffer cargo not c.balance");
        E.myTest(myOffer.getG() != guests.balance, "myOffer guest not g.balance");
        E.myTest(c.balance != EM.curEcon.as.cur.c.balance, "c.balance is bad");
      }

      void listStrategicValues(String pre, int lev) {
        listStrategicValues(lev, pre);
      }

      // Assets.CashFlow.Trades
      /**
       * list strategicValues
       *
       * @param lev level of listing
       * @param desc descriptor of listing
       */
      void listStrategicValues(int lev, String desc
      ) {
        //  A2Row bids = strategicValues;
        hist.add(h1 = new History(desc, lev, term + " " + name + " straC", strategicValues.getARow(0)));
        hist.add(h2 = new History(desc, lev, term + " " + name + " straG", strategicValues.getARow(E.lsecs)));
      }

      void listNeedreq(String pre, int lev) {
        listneedReq(lev, pre);
      }

      // Assets.CashFlow.Trades
      /**
       * list the need requirement
       *
       * @param lev level to list
       * @param desc descriptor
       */
      void listneedReq(int lev, String desc
      ) {
        //  A2Row bids = maxTrades;
        hist.add(h1 = new History(desc, lev, term + " " + name + " needC", needReq.getARow(0)));
        hist.add(h2 = new History(desc, lev, term + " " + name + " needG", needReq.getARow(E.lsecs)));
      }

      void listFneedReq(String pre, int lev) {
        listFneedReq(lev, pre);
      }

      // Assets.CashFlow.Trades
      /**
       * list the future need requirement
       *
       * @param lev level of listing
       * @param desc descriptor
       */
      void listFneedReq(int lev, String desc
      ) {
        //  A2Row bids = maxTrades;
        hist.add(h1 = new History(desc, lev, term + " " + name + " fneedC", fneedReq.getARow(0)));
        hist.add(h2 = new History(desc, lev, term + " " + name + " fneedG", fneedReq.getARow(E.lsecs)));
      }

      void listEmergOfrs(String pre, int lev) {
        listemergOfrs(lev, pre);
      }

      // Assets.CashFlow.Trades
      /**
       * list the emergency offor
       *
       * @param lev level of listing
       * @param desc descriptor
       */
      void listemergOfrs(int lev, String desc
      ) {

        hist.add(h1 = new History(desc, lev, term + " " + name + " emrgC", emergOfrs.getARow(0)));
        hist.add(h2 = new History(desc, lev, term + " " + name + " emrgG", emergOfrs.getARow(E.lsecs)));
      }

      void listAvailOfrs(String pre, int lev) {
        listavailOfrs(lev, pre);
      }

      // Assets.CashFlow.Trades
      /**
       * list available offers
       *
       * @param lev level of listing
       * @param desc descriptor
       */
      void listavailOfrs(int lev, String desc
      ) {
        hist.add(h1 = new History(desc, lev, term + " " + name + " limC", availOfrs.getARow(0)));
        hist.add(h2 = new History(desc, lev, term + " " + name + " limG", availOfrs.getARow(E.lsecs)));
      }

      /**
       * list the changes to the current bids from the previous bids
       *
       * @param lev level of hist output
       * @param desc description of changer
       * @param prevBid previous good (often entry)
       */
      void listDifBid(int lev, String desc, A2Row prevBid) {
        hist.add(h1 = new Difhist(lev, desc + term + " " + name + " difC", bids.getARow(0), prevBid.getARow(0)));
        hist.add(h2 = new Difhist(lev, desc + term + " " + name + " difG", bids.getARow(E.lsecs), prevBid.getARow(E.lsecs)));
      }

      // Assets.CashFlow.Trades
      void histOther(History... hargs
      ) {
        if (doHistOther && false) {
          int len = hargs.length;
          for (int k = 0; k < len; k++) {
            if (hargs[k] != null) {
              ohist.add(hargs[k]);
            }
          }
        }
      }

      /**
       * caculate various trading sums.<br>
       * goods or bids sector values %gt; 0 are offers.<br>
       * bids sector values &lt; 0 are requests and finally receipts.<br>
       * if excessOffers%gt;0 increase requests, or reduce offers.<br>
       * if excessOffers&lt;0 increase offers or reduce requests.<br>
       * sv =
       * requests/offers or Iget/Ipaid, <br>
       * sf = goal get for what I paid. <br>Calculate
       * strategic requests and offers, set totalReceipts and totalSend. <br>Calculate
       * value sums:
       * <br>Critical values are a few top and bottom goods values
       *
       * @return strategicValue requests/offers or what frac I get for what I give
       * @note A2Row stratV complex strategic values of bids traded negative
       //* values are requests/, positive offers, sends
       * @note A2Row stratF complex strategic fraction to be applied to bids
       * value it is bids values which are negative or positive
       * @note offers  is the value of bids we offer to the other
       * @note requests the value of bids we are requesting to receive or get
       * @note bids positive offers, negatives requests
       */
      
      double calcStrategicSums() {// Assets.CashFlow.Trades
        sf = calcStrategicGoal();  // reduced after each barteer
        totalStrategicRequests = totalStrategicOffers = totalStrategicFrac = criticalStrategicRequests = criticalStrategicOffers = criticalStrategicFrac = lowStrategicOffers = strategicReceipts = strategicOffers = 0.;
        nominalRequests = nominalOffers = nominalFrac = 0.;
        criticalNominalRequests = criticalNominalOffers = criticalNominalFrac = 0.;
        requests = offers = 0.;
        multF = makeZero(multF); // all A2Row 
        stratV = makeZero(stratV);
        stratCV = makeZero(stratCV);
        stratF = makeZero(stratF);
        stratCF = makeZero(stratCF);
        nominalF = makeZero(nominalF);
        nominalV = makeZero(nominalV);
        nominalCV = makeZero(nominalCV);
        nominalCF = makeZero(nominalCF);
        goodC = makeZero(goodC);
        int ifSearch = yphase == yrphase.SEARCH ? 0 : 1; // search or barter
        int n, p, iXrors;
        // high sector indexes > hcntr most important high values
        int hcntr = (int) eM.criticalNumbers[ifSearch][pors][clan];
        // low sector indexes < lcntr most important low values
        int lcntr = (int) eM.criticalNumbers[ifSearch][pors][clan];
        int criticalHighSectors = strategicValues.minIx(hcntr);
        int criticalLowSectors = strategicValues.maxIx(lcntr);
        // loop max to min StrategicValues
        for (int m : E.A2SECS) {
          n = strategicValues.maxIx(m);//max to min iX stratV
          double gg = bids.get(n);
          double stv = strategicValues.get(n);
          iXrors = (int) n / E.lsecs;// 0 or 1
          double nv = eM.nominalRSWealth[iXrors][pors];

          // requests  negative bids
          if (gg < NZERO) {
            totalStrategicRequests -= gg * stv;
            nominalRequests -= gg * nv;
            nominalV.set(n, gg * nominalF.set(n, nv * eM.tradeNominalFrac[pors][clan]));
            stratV.set(n, gg * stratF.set(n, stv * eM.tradeStrategicFrac[pors][clan]));
            multV.set(n, gg * multF.set(n, stv * eM.tradeStrategicFrac[pors][clan] + nv * eM.tradeNominalFrac[pors][clan]));
            // only count entries 
            if (n > criticalHighSectors) {
              criticalStrategicRequests -= gg * stratCF.add(n, stv);
              criticalNominalRequests -= gg * nv;
              nominalCV.set(n, gg * nominalCF.set(n, nv * eM.tradeNominalFrac[pors][clan]));
              stratCV.set(n, gg * stratCF.set(n, stv * eM.tradeCriticalFrac[pors][clan]));
              goodC.set(n, gg);
              multV.set(n, gg * multF.set(n, stv * eM.tradeStrategicFrac[pors][clan] + nv * eM.tradeNominalFrac[pors][clan] + stv * eM.tradeCriticalFrac[pors][clan]));
            }
          }
          //   totalStrategicRequests -= (gg < NZERO ? gg*stv : 0.);
          //    criticalStrategicRequests -= (gg < NZERO && n > criticalHighSectors) ? gg * stv : 0.;
          //    requests -= gg < NZERO ? gg:0.;
          //   nominalRequests -= (gg < NZERO) ? gg * nv : 0.;
          //  criticalNominalRequests -= (gg < NZERO && n > criticalHighSectors) ? gg * nv : 0.;
          // offers
          if (gg >= NZERO) {
            nominalV.set(n, gg * nv * eM.tradeNominalFrac[pors][clan]);
            stratV.set(n, gg * stratF.set(n, stv * eM.tradeStrategicFrac[pors][clan]
                    + nv * eM.tradeNominalFrac[pors][clan]));
            multV.set(n, gg * multF.set(n, stv * eM.tradeStrategicFrac[pors][clan] + nv * eM.tradeNominalFrac[pors][clan]));
            // only count entries with a higher strategic value than LowSectors
            if (n > criticalLowSectors) {
              criticalStrategicOffers += gg * stv;
              criticalNominalOffers += gg * nv;
              nominalCV.set(n, gg * nv * eM.tradeNominalFrac[pors][clan]);
              stratCV.set(n, stratV.set(m, gg * stv * eM.tradeCriticalFrac[pors][clan]));
              goodC.set(n, gg);
              multV.set(n, gg * multF.set(n, stv * eM.tradeStrategicFrac[pors][clan] + nv * eM.tradeNominalFrac[pors][clan] + stv * eM.tradeCriticalFrac[pors][clan]));
            }
          }
        } // end m , n
        tradingOfferWorth = criticalNominalOffers;
        //excessOffers = multV.curSum();
        nominalOffers = nominalV.curPlusSum();
        nominalRequests = nominalV.curNegSum();
        sumStrategicOffers = stratV.curPlusSum();
        sumStrategicRequests = stratV.curNegSum();
        double myKnowledge = myOffer.commonKnowledge[myIx].sum();
        double oKnowledge = myOffer.commonKnowledge[oIx].sum();
        Double bCash, plusCash, negCash;
        tradedCash = bCash = myOffer.getCash();
        plusCash = bCash > PZERO ? bCash : 0.;
        negCash = bCash < NZERO ? bCash : 0.;
        double offeredManuals, requestedManuals;
        tradedMoreManuals = offeredManuals = myOffer.getValueMoreManuals(myIx).sum();
        requestedManuals = myOffer.getValueMoreManuals(oIx).sum();
        // calculate the mult against both request and some based on requests
        // if 0 bids.negSum() set 1;
        goodFrac = bids.negSum() > NZERO || -goodC.negSum() * 2 > -bids.negSum() ? 1. : -goodC.negSum() * 2 / -bids.negSum();
        offers = totalStrategicOffers = multV.plusSum() + offeredManuals * eM.tradeManualsFrac[pors][clan] + plusCash;
        totalSend = unitOffers = multV.plusSum() + plusCash;
        requests = totalStrategicReceipts = -multV.negSum() + requestedManuals * eM.tradeManualsFrac[pors][clan] + negCash;
        unitRequests = -multV.negSum() + negCash;
        // requests = totalStrategicReceipts = -multV.negSum() * goodFrac + requestedManuals + negCash;
        E.myTestDouble(goodC.negSum(), "goodC.negSum()");
        E.myTestDouble(goodC.plusSum(), "goodC.plusSum()");
        E.myTestDouble(multV.negSum(), "multV.negSum");
        E.myTestDouble(multV.plusSum(), "multV.plusSum");
        E.myTestDouble(requestedManuals, "requestedManuals");
        E.myTestDouble(bCash, "bCash");
        E.myTestDouble(negCash, "negCash");

        //sv = requests / offers; // fraction strategicValue get/give
        // do not include any traded manuals
        // see what I get for what I paid = get/paid
        sv = strategicValue = offers < PZERO ? 0. : requests / offers;
        // goal strategicFraction sf 
        // desiredOffer = requests/sf,
        excessOffers = offers - requests / sf; // 

        hist.add(new History(aPre, History.valuesMinor7, name + " calcSum fracs", "S=" + ec.mf(eM.strategicFracs[ifSearch][pors][clan]), "C=" + ec.mf(eM.tradeCriticalFrac[pors][clan]), "N=" + ec.mf(eM.nominalFracs[ifSearch][pors][clan]), "rS=" + ec.mf(requests), "rC=" + ec.mf(criticalStrategicRequests), "rN=" + ec.mf(nominalRequests), "oS=" + ec.mf(offers), "oC=" + ec.mf(criticalStrategicOffers), "oN=" + ec.mf(nominalOffers), "<<<<<<<"));
        hist.add(new History(aPre, History.valuesMinor7, name + " multSums", "rS=" + ec.mf(totalStrategicRequests * eM.strategicFracs[ifSearch][pors][clan]), "rC=" + ec.mf(criticalStrategicRequests * eM.tradeCriticalFrac[pors][clan]), "rN=" + ec.mf(nominalRequests * eM.nominalFracs[ifSearch][pors][clan]), "oS=" + ec.mf(totalStrategicOffers * eM.strategicFracs[ifSearch][pors][clan]), "oC=" + ec.mf(criticalStrategicOffers * eM.tradeCriticalFrac[pors][clan]), "oN=" + ec.mf(nominalOffers * eM.nominalFracs[ifSearch][pors][clan]), "sS=" + ec.mf((totalStrategicOffers - totalStrategicRequests) * eM.strategicFracs[ifSearch][pors][clan]), "sC=" + ec.mf((criticalStrategicOffers - criticalStrategicRequests) * eM.tradeCriticalFrac[pors][clan]), "sN=" + ec.mf((nominalOffers - nominalRequests) * eM.nominalFracs[ifSearch][pors][clan]), "<<<<<<<"));
        hist.add(new History(aPre, History.valuesMinor7, name + " Sums ", "req=" + ec.mf(requests), "Ofrs=" + ec.mf(offers), "cash=" + ec.mf(cash), "bC" + ec.mf(bCash), "sval=" + ec.mf(strategicValue), "xcofr" + ec.mf(excessOffers), "<<<<<"));
        hist.add(new History(aPre, History.valuesMinor7, name + "from Offers", "cK" + ec.mf(myOffer.commonKnowledge[myIx].sum()), ec.mf(myOffer.commonKnowledge[oIx].sum()), "manls" + ec.mf(offeredManuals), ec.mf(requestedManuals), "total", "o=" + ec.mf(totalStrategicOffers), "r=" + ec.mf(totalStrategicRequests), "<<<<<<<<<<"));

        E.myTestDouble(offers, "offers");
        E.myTestDouble(requests, "requests");
        E.myTestDouble(sv, "sv");
        if (newBarter) {
          newBarter = false;
          nbOffers = offers;
          nbRequests = requests;
          sv1 = nbStrategicValue = strategicValue;
          nbExcessOffers = excessOffers;

          nbStratF = stratF.copy();
          nbStratV = stratV.copy();
          nbStratCV = stratCV.copy();
        }
        if (term > eM.barterStart - 2) {
          totalStrategicRequestsFirst = totalStrategicRequests;
          totalStrategicOffersFirst = totalStrategicOffers;
          totalStrategicFracFirst = totalStrategicFrac;
          criticalStrategicRequestsFirst = criticalStrategicRequests;
          criticalStrategicOffersFirst = criticalStrategicOffers;
          criticalStrategicFracFirst = criticalStrategicFrac;

          nominalRequestsFirst = nominalRequests;
          nominalOffersFirst = nominalOffers;
          nominalFracFirst = 0.;
          criticalNominalRequestsFirst = criticalNominalRequests;
          criticalNominalOffersFirst = criticalNominalOffers;
          criticalNominalFracFirst = criticalNominalFrac;
          requestsFirst = requests;
          sumNominalRequestsFirst = sumNominalRequests;
          offersFirst = offers;
          bidsFirst = bids.copy();
          strategicValuesFirst = strategicValues.copy();
        }
        int bLev = ec.blev = History.dl;

        didGoods = true;
        return strategicValue;
      } // Assets.CashFlow.Trades.calStrategicSums

      boolean testTrade(Offer myOffer
      ) { // Assets.CashFlow.Trades
        if (changes > 0) {
          hist.add(new History("@i", 5, term + " CHANGES", "changes=" + changes, "sv=" + ec.mf(strategicValue), "sf=" + ec.mf(strategicGoal), "ts=" + ec.mf(offers), "tr=" + ec.mf(requests)));
          return false; // no trade if changes
        }
        if (sv > sf || (sv > rGoal0 && term < eM.barterStart * .5)) {
          hist.add(new History("@g", 3, "T" + term + " " + name + " doTRADE", "sv" + ec.mf(strategicValue), "sf" + ec.mf(strategicGoal), "ofrs" + ec.mf(offers), "rqst" + ec.mf(requests)));
          //   myOffer.accepted(ec);
          return true;
        }
        else {
          hist.add(new History("@h", History.loopMinorConditionals5, "T" + term + " " + name + " no trade ", "sv" + ec.mf(strategicValue), "< sf" + ec.mf(strategicGoal), "ofrs" + ec.mf(offers), "rqst" + ec.mf(requests)));
          return false;
        }
      } // Assets.CashFlow.Trades

      void sendStats(double tSend, double tReceipts, double tStratValue, A2Row tBid,
              int favr
      ) {
        //percent tReceipts/strtYearTotWorth per favr, per ship#,tot, sumtot list1?
        switch (favr) {
          case 5:
            // gameRes.TRADEDRCDF5.wet(pors, clan, tReceipts / strtYearTotWorth, 1);
            setStat("TRADEDRCDF5", pors, clan, tReceipts / startYrSumWorth, 1);
            break;
          case 4:
            // gameRes.TRADEDRCDF4.wet(pors, clan, tReceipts / strtYearTotWorth, 1);
            setStat("TRADEDRCDF4", pors, clan, tReceipts / startYrSumWorth, 1);
            break;
          case 3:
            // gameRes.TRADEDRCDF3.wet(pors, clan, tReceipts / strtYearTotWorth, 1);
            setStat("TRADEDRCDF3", pors, clan, tReceipts / startYrSumWorth, 1);
            break;
          case 2:
            // gameRes.TRADEDRCDF2.wet(pors, clan, tReceipts / strtYearTotWorth, 1);
            setStat("TRADEDRCDF2", pors, clan, tReceipts / startYrSumWorth, 1);
            break;
          case 1:
            // gameRes.TRADEDRCDF1.wet(pors, clan, tReceipts / strtYearTotWorth, 1);
            setStat("TRADEDRCDF1", pors, clan, tReceipts / startYrSumWorth, 1);
            break;
          case 0:
            // gameRes.TRADEDRCDF0.wet(pors, clan, tReceipts / strtYearTotWorth, 1);
            setStat("TRADEDRCDF0", pors, clan, tReceipts / startYrSumWorth, 1);
            break;
        }
      }

      /**
       * test whether the barter can continue, or should be terminated
       *
       * @param myOffer
       * @return true if barter is to terminate
       */
      boolean testNoTrade(Offer myOffer
      ) { // Assets.CashFlow.Trades
        aPre = "t";
        if (changes > 0 && myOffer.getTerm() < 2) {// no trade if changes were required
          hist.add(new History(aPre, History.loopMinorConditionals5, "T" + term + " " + name + " rej&change", "changes=" + changes, "sv=" + ec.mf(strategicValue), "sf=" + ec.mf(strategicGoal), "ofrs=" + ec.mf(offers), "rqst=" + ec.mf(requests)));
          return true;
        }
        // early no trade if it would never work
        if (sv < sf && sv < rGoal0 && (term < eM.barterStart * .4)) {
          hist.add(new History(aPre, History.loopMinorConditionals5, "T" + term + " " + name + " REJECT ", "sv" + ec.mf(strategicValue), "<sf" + ec.mf(strategicGoal), "ofrs=" + ec.mf(offers), "rqst=" + ec.mf(requests)));
          return true;
        }
        if (requests < PZERO) {
          hist.add(new History(aPre, History.loopMinorConditionals5, "T" + term + " " + name + " REJECT 0.0", "sv" + ec.mf(strategicValue), "<sf" + ec.mf(strategicGoal), "ofrs=" + ec.mf(offers), "rqst=" + ec.mf(requests)));
          return true;
        }
        else {
          hist.add(new History(aPre, History.loopMinorConditionals5, "T" + term + " " + name + " yet more", "sv" + ec.mf(strategicValue), "sf" + ec.mf(strategicGoal), "ofrs=" + ec.mf(offers), "rqst=" + ec.mf(requests)));
          return false;
        }
      }  // Assets.CashFlow.Trade.testNoTrade

      /**
       * override the first num strategic bids (planet offers) to ship requests
       * do not override planet requests save sum as strat1Sum, norm1Sum Now add
       * any remaining planet offers sums from this operation strat2Sum,
       * norm2Sum, total requests strat3Sum, norm3Sum use availOfrs as limit of
       * ship offers unless emergency
       *
       * @param num number of bids ti override
       */
      void shipOverridePlanetsRequests(int num
      ) {// Assets.CashFlow.Trades
        double good = 0., fneed, need, avail, emerg, ig;
        for (int p : I2ASECS) {  // force did clear
          did[p] = 0;
        }
        listFneedReq(5, "ovr" + num + " ");
        listneedReq(5, "ovr" + num + " ");
        listavailOfrs(5, "ovr" + num + " ");
        listemergOfrs(5, "ovr" + num + " ");
        listGoods(5, "ovr" + num + " ");

        A2Row prevGood = bids.copy();
        double sv = 0., nv = 0.;
        int ix;
        double strat1Sum = 0., strat2Sum = 0., strat3Sum = 0.;
        double nom1Sum = 0., nom2Sum = 0., nom3Sum = 0.;
        // limit all values, but set ours if possible
        for (int ii = 0; ii < num; ii++) {
          ix = strategicValues.maxIx(ii); // start with highest strategic values
          good = bids.get(ix);
          fneed = fneedReq.get(ix);
          need = needReq.get(ix);
          avail = availOfrs.get(ix);
          emerg = emergOfrs.get(ix);
          sv = strategicValues.get(ix);
          nv = eM.nominalRSWealth[(int) (ix / E.lsecs)][pors];
          double needsum = 0;
          //other request=> my request  dont do
          if (good > PZERO && need > PZERO && false) {
            hist.add(new History(aPre, 5, term + " " + name + "oOfr=>myOfr", (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "ix=" + ix, "ii=" + ii, ec.mf(good), "to", ec.mf(-need)));
            bids.set(ix, -need);  //  o offer force to my request
            strat1Sum += -need * sv;
            nom1Sum += -need * nv;
            // valueChangesTried[ix]++;
            changes++;
          }
          // O Req => my ofr(-need) new different O Req
          else if (good > PZERO && -need < NZERO) {
            hist.add(new History(aPre, 5, term + " " + name + " oOfr => myReqest", (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "ix=" + ix, "ii=" + ii, ec.mf(good), "to", ec.mf(-need), "<<<<"));
            bids.set(ix, -need);  // force a new offer (o request)
            did[ix] = 1;
            strat1Sum += -need * sv;
            nom1Sum += -need * nv;
            //   valueChangesTried[ix]++; // doesn't count
            changes++;
          }  // o Ofr(my Req) => larger o Ofr(my Req)
          else if (good < NZERO && need > PZERO && -need < good) {
            hist.add(new History(aPre, 5, term + " " + name + " ovr decrment", "good", (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "ix=" + ix, "ii=" + ii, ec.mf(good), "to", ec.mf(-need), "<<<<"));
            bids.set(ix, -need);  // force a larger offer
            did[ix] = 1;
            strat1Sum += -need * sv;
            nom1Sum += -need * nv;
            //   valueChangesTried[ix]++; // doesn't count
            changes++;
          }
          else // o Ofr(my Req) => same oOfr(myReq)
          if (good < NZERO && need > PZERO) {
            strat1Sum += -need * sv;
            nom1Sum += -need * nv;
            did[ix] = 1;
            hist.add(new History(aPre, 13, term + " " + name + " ovr keep", (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "ix=" + ix, "ii=" + ii, ec.mf(good), "<<<", "<<<"));
          }
          else {
            hist.add(new History(aPre, 13, term + " " + name + " ovr keep", (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "ix=" + ix, "ii=" + ii, ec.mf(good), "<<", "<<"));
          }
        }
        // now do the rest of the requests
        // override offers starting from least strategic
        // skip for now
        for (int ii = 0; false && ii < num; ii++) {
          ix = strategicValues.minIx(ii); // start with lowestt strategic offer
          good = bids.get(ix);
          fneed = fneedReq.get(ix);
          need = needReq.get(ix);
          avail = availOfrs.get(ix);
          emerg = emergOfrs.get(ix);
          if (good < NZERO && avail > PZERO) {
            hist.add(new History(aPre, 5, term + " " + name + " override", (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "ix=" + ix, "ii=" + ii, ec.mf(good), "to", ec.mf(avail)));
            bids.set(ix, ig);  // force an offer
            //       valueChangesTried[ix]++;
            changes++;
          }
          else if (good > NZERO && avail > good) {
            hist.add(new History(aPre, 5, term + " " + name + " ovr incr", (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "ix=" + ix, "ii=" + ii, ec.mf(good), "to", ec.mf(avail)));
            bids.set(ix, ig);  // force an offer
            //       valueChangesTried[ix]++;
            changes++;

          }
          else {
            hist.add(new History(aPre, 13, term + " " + name + " ovr keep", (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "ix=" + ix, "ii=" + ii, ec.mf(good), "not", ec.mf(ig), "", ""));
          }

        }
        listDifBid(5, "ovr", prevGood);
      }// Assets.CashFlow.Trades.overrideOthersRequests

      /**
       * reset some of the bids to reflect the planet requests only reset the
       * first num requests
       *
       * @param num number of bids ti override
       */
      void overrideOthersRequests(int num
      ) {// Assets.CashFlow.Trades
        double good = 0., fneed, need, avail, emerg, ig;
        listFneedReq(5, "ovr" + num + " ");
        listneedReq(5, "ovr" + num + " ");
        listavailOfrs(5, "ovr" + num + " ");
        listemergOfrs(5, "ovr" + num + " ");
        listGoods(5, "ovr" + num + " ");
        A2Row prevGood = bids.copy();
        int ix;
        // limit all values, but set ours if possible
        for (int ii = 0; ii < num; ii++) {
          ix = strategicValues.maxIx(ii); // start with highest strategic value first
          good = bids.get(ix);
          fneed = fneedReq.get(ix);
          need = needReq.get(ix);
          avail = availOfrs.get(ix);
          emerg = emergOfrs.get(ix);
          double needsum = 0;
          //my requests can override make ours requests
          if (good > PZERO && need > PZERO) {
            hist.add(new History(aPre, 5, term + " " + name + " override", (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "ix=" + ix, "ii=" + ii, ec.mf(good), "to", ec.mf(-need)));
            bids.set(ix, -need);  // force a request
            //   valueChangesTried[ix]++; // doesn't count
            changes++;
          }
          else if (good > PZERO && need < NZERO) {
            hist.add(new History(aPre, 5, term + " " + name + " override", (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "ix=" + ix, "ii=" + ii, ec.mf(good), "to", ec.mf(-need)));
            bids.set(ix, -need);  // force a request
            //   valueChangesTried[ix]++; // doesn't count
            changes++;
          }
          else if (good < NZERO && need > PZERO && -need < good) {
            hist.add(new History(aPre, 5, term + " " + name + " ovr decrment", "good", (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "ix=" + ix, "ii=" + ii, ec.mf(good), "to", ec.mf(-need)));
            bids.set(ix, -need);  // force a offer
            //   valueChangesTried[ix]++; // doesn't count
            changes++;

          }
          else {
            hist.add(new History(aPre, 13, term + " " + name + " ovr keep", (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "ix=" + ix, "ii=" + ii, ec.mf(good), "", ""));
          }
        }
        // override offers starting from least strategic
        for (int ii = 0; ii < num; ii++) {
          ix = strategicValues.minIx(ii); // start with lowestt strategic offer
          good = bids.get(ix);
          fneed = fneedReq.get(ix);
          need = needReq.get(ix);
          avail = availOfrs.get(ix);
          emerg = emergOfrs.get(ix);
          if (good < NZERO && avail > PZERO) {
            hist.add(new History(aPre, 5, term + " " + name + " override", (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "ix=" + ix, "ii=" + ii, ec.mf(good), "to", ec.mf(avail)));
            //          bids.set(ix, ig);  // force an offer
            //       valueChangesTried[ix]++;
            changes++;
          }
          else if (good > NZERO && avail > good) {
            hist.add(new History(aPre, 5, term + " " + name + " ovr incr", (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "ix=" + ix, "ii=" + ii, ec.mf(good), "to", ec.mf(avail)));
            //      bids.set(ix, ig);  // force an offer
            //       valueChangesTried[ix]++;
            changes++;

          }
          else {
            //     hist.add(new History(aPre, 13, term + " " + name + " ovr keep", (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "ix=" + ix, "ii=" + ii, ec.mf(good), "not", ec.mf(ig), "", ""));
          }

        }
        listDifBid(5, "ovr", prevGood);
      }// Assets.CashFlow.Trades.overrideOthersRequests

      /**
       * get the bid SubAasset name and sector number
       *
       * @param ix
       * @return
       */
      String gbsnn(int ix
      ) {
        String ret = (ix < E.lsecs ? "c." : "g.") + ix % E.lsecs;
        return ret;
      }

      /**
       * get the bid SubAasset name and sector number
       *
       * @param ix
       * @return
       */
      String getBidSubAssetNameNumber(int ix
      ) {
        return gbsnn(ix);
      }

      /**
       * return tag string without ! if true, with ! if false
       *
       * @param tag boolean variable to be checked
       * @param sss visual symbol for variable, may be shorter
       * @return blank + ? ! + sss
       */
      String ifTag(boolean tag, String sss) {
        return " " + (tag ? sss : "!" + sss);
      }

      /**
       * Enforce the strategic goal for your clan and planet or ship each term
       * use calcStrategicGoal tnen calcStrategicValue the strategicValue is the
       * requests you will receive for the offers you give strategocValue,sv,
       * sv1 = strategic requests/offers strategicGoal,sg,sg1 = desired
       * strategic requests/offers excessOffers = currentOffers - sg *
       * currentRequests = goal requests/offers * currentRequests = goalOffers
       * excessOffers = current offers-- goalOffers Assume traders make their
       * interest in specific sectors known by re-raising the request for a
       * specific sector after the other trader decreased the request. But after
       * re-raising a request E.maxTries = {@value E#maxTries} assume the
       * specified sector is not available.<br>
       * if excessOffers then increase requests and finally decrease offers if
       * excessRequests (-excessOffers) increase offers then decr requests A
       * planets offers become ship requests in the next term and a planets
       * requests become ship offers in the next term each economy limit offers
       * to the max available except in emergencies limit offers to the max
       * emergency available A trade only takes place when both ship and planet
       * agree on amounts. that is one term must take place with no changes and
       * be an acceptable strategicValue When a change limit is reached,
       * requests and offers cannot be increased they can only be decreased or
       * left unchanged.
       * Enter with current value of fliped bids and cash set for developing
       * this Econ's bid
       */
      void enforceStrategicGoal() {// Assets.CashFlow.Trades.enforceStrategicGoal
        myIx = myOffer.setMyIx(ec);
        double bCash = myOffer.getCash();
        ec.blev2 = History.dl;
        hist.add(new History(aPre, 5, term + " " + name + " enfStVal", "req=" + ec.mf(requests), "ofr" + ec.mf(offers), "sf=" + ec.mf(sf), "xcsOfrs=", ec.mf(excessOffers), "bCash=" + wh(bCash), (hEmerg ? "hEmerg" : "!!!hEmerg"), "<<<<<<<<<<<"));
        A2Row entrGoods = bids.copy();
        // calculate strategicGoal the desired value of strategicValue
        // allow a little extra
        // do the first calculation
        calcStrategicSums(); //  sv,excessOffers
        sv1 = sv;  // sf,requests,offers,excessOffers
        sf1 = sf;  // save the first sf as sf1, sv as sv1

        // define variables for use in m forloops, save garbage collection
        // sv = reqs/offrs; sf *ofrs = reqs; ofrs = reqs/sf
        double maxDif, dif;

        // if excessOffers &lt; 0, need to increase offers or reduce requests
        int ix = E.l2secs - 1;
        aPre = "^e";
        double svg = 0, bid = 0, emerg = 0, avail, fneed, need, needo, fuzz, more, mf, dif2;
        double first = 0.;
        double xou = 0;  //excessOfferUnits for these bids
        double xof = 0; // excessOffers
        int ifSearch = yphase == yrphase.SEARCH ? 0 : 1; // search or barter
        int hcntr = (int) eM.criticalNumbers[ifSearch][pors][clan];
        int lcntr = (int) eM.criticalNumbers[ifSearch][pors][clan];
        boolean highCritical = false;
        boolean isExcessOffers = false, isExcessRequests = false, isAvail, isEmerg, isNeed, isFneed;
        boolean triesOK, isRequest, isOffer, isChanged, doLimit, isMoreOffers;
        boolean hiCrit, didChange, doChg;
        boolean rnd1, rnd2, rnd3, rnd23, notHiCrit;
        //    double xov = 0;  //excessOffer (value in strategic values)
        A2Row changed = new A2Row(13, "changed"); // zero's elements
        A2Row prevBids = new A2Row(History.loopMinorConditionals5, "prevBids").set(bids);
        fuzz = (offers + requests) * .01;
        double subFuzz = fuzz * -1.01;
        double limit = PZERO;
        //   double incTerm = eM.incTriesPTerm[pors];
        //force alternation of sectors to override
        double incTerm = Math.max(eM.maxTries[pors], (.8 + eM.barterStart - term) * eM.incTriesPTerm[pors]);
        double incYr = 1. / eM.incTriesPTerm[pors]; //count of max tries
        int maxCnt = (int) (eM.cntMult[pors] * (eM.maxTries[pors] + (eM.barterStart - 2 - term) * incTerm));
        double incTerm1 = eM.maxTries[pors] + (eM.barterStart - 2 - term) * incTerm;
        double incTerm2 = eM.maxTries[pors] - .3 + (eM.barterStart - 2 - term) * incTerm;
        double incTerm3 = eM.maxTries[pors] - .6 + (eM.barterStart - 2 - term) * incTerm;
        double ofrac = 200. / (198. + term);
        double realChangesMax = 10.;
        hist.add(new History(aPre, 5, term + " " + name + " enfStVal", "req" + ec.mf(requests), "ofr" + ec.mf(offers), "sf=" + ec.mf(sf), "xof=" + ec.mf(excessOffers), "bCash=" + wh(bCash), (hEmerg ? "hEmerg" : "!!!hEmerg"), "fuzz" + ec.mf(fuzz), "ofrac" + ec.mf(ofrac), "maxCnt=" + maxCnt, "<<<<<<<<<<<"));
        ec.addOHist(ohist, new History(aPre, 5, term + " " + name + " enfStVal", "req" + df(requests), "ofr" + df(offers), "sf=" + df(sf), "xcsOfrs=", df(excessOffers), "bCash=" + wh(bCash), (hEmerg ? "hEmerg" : "!!!hEmerg"), "fuzz" + df(fuzz), "subFuzz" + df(subFuzz), "<<<<<<<<<<<"));

        ec.lev = History.valuesMinor7;
        ec.blev = History.dl;
        oFirstBid.setTitle("oFirstBid");
        oFirstBid.sendHist(hist, aPre);
        bids.setTitle("bids");
        bids.sendHist(hist, aPre);
        multV.sendHist(hist, aPre);
        emergOfrs.sendHist(hist, aPre);
        availOfrs.sendHist(hist, aPre);
        needReq.sendHist(hist, aPre);
        fneedReq.sendHist(hist, aPre);
        strategicValues.sendHist(hist, aPre);

        // starting means set all avail > 0 to emerg, do not count as 
        // value changes tried
        boolean starting = term > eM.barterStart - 2; // 18,17
        // allow inc values 
        boolean incValueOK = term > eM.barterStart - 9;//18-10
        int mmMax = starting ? 2 : 4;
        //1 at start and limits then 2 and 3 rounds
        for (int mm = 1; mm < mmMax; mm++) {
          rnd1 = mm == 1;
          rnd2 = mm == 2;
          rnd3 = mm == 3;
          rnd23 = mm > 1;
          realChangesMax = eM.maxTries[pors] * (eM.barterStart - term) * eM.incTriesPTerm[pors] * 3 + mm;

          excessOffers += subFuzz;
          for (int m = 0; m < E.L2SECS; m++) {
            // go from the most strategicValues (m = index), we have least of them
            // request things we have least of.
            ix = strategicValues.maxIx(m); // requests <0, start least amount

            String sT = "T." + term + "." + mm;
            String gcN = sT + " " + ix + "->" + (ix / LSECS > 0. ? "G" : "C") + (int) (ix % E.LSECS);

            didChange = false;
            bid = bids.get(ix);
            first = oFirstBid.get(ix); // unflipped orr > => req in mybid
            emerg = emergOfrs.get(ix); // eofr > 0
            avail = availOfrs.get(ix);
            double need1 = needReq.get(ix); // > 0
            double fneed1 = fneedReq.get(ix); // > 0
            isRequest = bid < E.NZERO;
            // String sreq = gcN + " " + (bid < E.NZERO ? "bReq" : bid > E.PZERO ? "bOfr" : "bfuzz");
            String sreq = gcN + " " + (starting ? bid < E.NZERO ? "sReq" : bid > E.PZERO ? "sOfr" : "sfuzz" : bid < E.NZERO ? "bReq" : bid > E.PZERO ? "bOfr" : "bfuzz");
            isOffer = bid > PZERO;
            // approach emerg if the first mm round did not gain enough offer
            //only use emerg in emergency
            // on rounds %gt; 1 limit = emerg or (avail + 2*emerg)/3
            limit = hEmerg ? emerg : mm > 1 ? (avail + emerg + emerg) * .3333 : avail;
            //' String sLimit = (limit < bid?"limit":"!limit");
            mf = multF.get(ix); //composit strategic Fraction
            //  starting = mm < 1 ? starting : false; // starting only round 0
            // limit need and fneed to all the other was offering
            double fneed2 = need = fneed = need1 > fneed1 ? need1 : fneed1;  // it might be zero
            // ensure that need/req does not exceed a first offer by other;
            //  need = fneed = first > E.NZERO && term < eM.barterStart - 1 ? fneed2 > first ? first : fneed2 : first < E.PZERO ? 0. : fneed2; // need, fneed > 0
            // if fneed2 if higher than first, bFirst is false
            Boolean bFirst = term < eM.barterStart - 1 && first > E.NZERO && first < fneed2;
            // String sNeed = (bid - -need > fuzz? " limit": " bidOK";
            excessOffers += subFuzz;
            isExcessRequests = excessOffers < -fuzz;
            isExcessOffers = excessOffers > fuzz;
            String sXcReq = (isExcessRequests ? " xcReq" : isExcessOffers ? " xcOfr" : " fuzz");
            //     isMoreOffers = excessOffers + offers*eM.moreOfferBias[pors] > fuzz;
            isNeed = need > PZERO; // need exists
            //        highCritical = m < hcntr;
            isAvail = avail > PZERO; //avail exists
            isEmerg = emerg > PZERO;  // emergency value exists
            Boolean needy = -need - bid > E.PZERO;  // subtract dif
            doLimit = bid - limit > E.PZERO;
            Boolean greedy = avail - bid > E.PZERO;  // sff ,ptr
            Boolean pbid = bid > E.PZERO;
            Boolean nbid = bid < E.NZERO;
            //    notHiCrit = m < hcntr;
            hiCrit = m >= hcntr;

            doChg = chgCnt < maxCnt;
            isChanged = changed.get(ix) > 0.; // changed this term
            triesOK = valueChangesTried[ix] < incTerm && !isChanged && doChg;
            String sLimit = (bFirst ? "first" : !incValueOK ? "!inc" : !triesOK ? "!tries" : needy ? "needy" : nbid ? "bReq" : doLimit ? "limit" : greedy ? "greedy" : pbid ? "bOfr" : "bfuzz");
            String stries = "ct" + chgCnt + ":" + maxCnt + "tr" + ec.df(valueChangesTried[ix]) + ":" + ec.df(incTerm);
            ;
            svg = multV.get(ix);
            xou = mf > PZERO ? excessOffers / mf : 0.; //convert to bid units
            xof = excessOffers;
            double gtst = myOffer.getG().get(ix % LSECS);
            E.myTest(ix >= LSECS 
                    && (myOffer.getG().get(ix % LSECS) - emerg) < NZERO,
                    "Emerg %8e is greater than Guest[%2d] %8e,%8e, m=%2d,ix=%2d,myIx=%1d,%1d,term=%3d",
                    emerg, 
                    ix % LSECS, 
                    myOffer.getG().get(ix % LSECS),
                    g.balance.get(ix % LSECS), m, ix, 
                    myIx, myOffer.myIx, term);
            if(ix < LSECS && (myOffer.getC().get(ix % LSECS) - emerg) < NZERO)
            E.myTest(ix < LSECS && (myOffer.getC().get(ix % LSECS) - emerg) < NZERO, "Emerg %2.4g is greater than cargo[%2d] %2.4g,%4.4g, m=%2d,ix=%2d,myIx=%1d,%1d,term=%3d", emerg, ix % LSECS, myOffer.getC().get(ix % LSECS), c.balance.get(ix % LSECS), m, ix, myIx, myOffer.myIx, term);

            // when starting showing offerings takes priority, request
            // only if hiCrit
            if (starting && isNeed && bid > -need
                    && (((!isAvail) || hiCrit))) {
              dif = bid - -need;  // dif > 0, decr bid, excessOffers

              bids.add(ix, -dif); // decrease bid increase this request<0 add dif < 0
              excessOffers += dif2 = -dif * mf * ofrac;  // decrease excessOffers

              hist.add(new History(aPre, History.informationMinor9, sreq, stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + df(bid), "ned" + df(need), "avl" + df(avail), "xof=" + df(xof), "xou=" + df(xou), "mf=" + df(mf), "svg=" + df(svg), "<<<<<<<"));
              hist.add(new History(aPre, 5, "T" + term + " startingneed." + mm + ", " + gbsnn(ix), "Nd" + df(need), "B" + df(bid), "sub" + df(dif), "->" + df(bids.get(ix)), "ofrs" + df(xof), "sub" + df(dif2), "->" + df(excessOffers), "VCTried" + wh(valueChangesTried[ix]), "<<<<<"));
              ec.addOHist(ohist, new History(aPre, History.debuggingMinor11, sreq, stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + df(bid), "ned" + df(need), "avl" + df(avail), "xof=" + df(xof), "xou=" + df(xou), "mf=" + df(mf), "svg=" + df(svg), "<<<<<<<"));
              ec.addOHist(ohist, new History(aPre, 5, "OT" + term + " startingneed." + mm + ", " + gbsnn(ix), "Nd" + df(need), "B" + df(bid), "sub" + df(dif), "->" + df(bids.get(ix)), "ofrs" + df(xof), "sub" + df(dif2), "->" + df(excessOffers), "VCTried" + wh(valueChangesTried[ix]), "<<<<<"));
              if (!starting) {
                changed.set(ix, 1.);
                chgCnt += 1;
                valueChangesTried[ix]++;
              }
            }
            else // if not starting and round1 do more requests
            if (!starting && isNeed && bid > -need
                    && isExcessOffers
                    && incValueOK && doChg && !isChanged) {
              dif = bid - -need;  // dif > 0, decr bid, excessOffers
              bids.add(ix, -dif); // decrease bid increase this request<0 add dif < 0
              excessOffers += dif2 = -dif * mf * ofrac;  // decrease excessOffers

              hist.add(new History(aPre, History.informationMinor9, sreq, stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + df(bid), "ned" + df(need), "avl" + df(avail), "xof=" + df(xof), "xou=" + df(xou), "mf=" + df(mf), "svg=" + df(svg), "<<<<<<<"));
              hist.add(new History(aPre, 5, "T" + term + " need1." + mm + ", " + gbsnn(ix), "Nd" + df(need), "B" + df(bid), "sub" + df(dif), "->" + df(bids.get(ix)), "ofrs" + df(xof), "sub" + df(dif2), "->" + df(excessOffers), "VCTried" + wh(valueChangesTried[ix]), "<<<<<"));
              ec.addOHist(ohist, new History(aPre, History.debuggingMinor11, sreq, stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + df(bid), "ned" + df(need), "avl" + df(avail), "xof=" + df(xof), "xou=" + df(xou), "mf=" + df(mf), "svg=" + df(svg), "<<<<<<<"));
              ec.addOHist(ohist, new History(aPre, 5, "OT" + term + " need1." + mm + ", " + gbsnn(ix), "Nd" + df(need), "B" + df(bid), "sub" + df(dif), "->" + df(bids.get(ix)), "ofrs" + df(xof), "sub" + df(dif2), "->" + df(excessOffers), "VCTried" + wh(valueChangesTried[ix]), "<<<<<"));
              changed.add(ix, 1.);
              if (dif > 0.) { // only count if a change
                chgCnt += 1;
                valueChangesTried[ix]++;
              }
            }
            else // set request unless starting if bid > -need, counts ok
            if (bid > -need
                    && isNeed && !starting && rnd23 && isExcessOffers
                    && incValueOK && triesOK && doChg && !isChanged) {
              dif = bid - -need;  // dif > 0, decr bid, excessOffers
              bids.add(ix, -dif); // decrease bid increase this request<0 add dif < 0
              excessOffers += -dif * mf;  // decrease excessOffers
              hist.add(new History(aPre, History.debuggingMinor11, sreq, stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + df(bid), "ned" + df(need), "avl" + df(avail), "xof=" + df(xof), "xou=" + df(xou), "mf=" + df(mf), "svg=" + df(svg), "<<<<<<<"));
              hist.add(new History(aPre, 5, "T" + term + " needy." + mm + ", " + gbsnn(ix), "Nd" + df(need), "B" + df(bid), "sub" + df(dif), "->" + df(bids.get(ix)), "ofrs" + df(xof), "sub" + df(dif * mf), "->" + df(excessOffers), "VCTried" + wh(valueChangesTried[ix]), "<<<<<"));
              ec.addOHist(ohist, new History(aPre, History.debuggingMinor11, sreq, stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + df(bid), "ned" + df(need), "avl" + df(avail), "xof=" + df(xof), "xou=" + df(xou), "mf=" + df(mf), "svg=" + df(svg), "<<<<<<<"));
              ec.addOHist(ohist, new History(aPre, 5, "OT" + term + " need2." + mm + ", " + gbsnn(ix), "Nd" + df(need), "B" + df(bid), "sub" + df(dif), "->" + df(bids.get(ix)), "ofrs" + df(xof), "sub" + df(dif * mf), "->" + df(excessOffers), "VCTried" + wh(valueChangesTried[ix]), "<<<<<"));
              changed.add(ix, 1.);
              chgCnt += 1;
              valueChangesTried[ix]++;
            }
            //  decrease offers if excessOffers 
            else if (rnd23 && isExcessOffers && isOffer) {
              maxDif = bid; // bid maxDif>0 max decrease of bid units
              dif = (xou < maxDif) ? xou : maxDif; // dif>0,xou>0, maxDif > 0
              bids.add(ix, -dif); //decr bid to 0 bid or  0 excessOffers
              excessOffers -= dif * mf * ofrac;  // decrease excessOffers 
              hist.add(new History(aPre, History.debuggingMinor11, sreq, stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + df(bid), "ned" + df(need), "avl" + df(avail), "xof=" + df(xof), "xou=" + df(xou), "mf=" + df(mf), "svg=" + df(svg), "<<<<<<<"));
              ec.addOHist(ohist, new History(aPre, History.debuggingMinor11, sreq, stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + df(bid), "ned" + df(need), "avl" + df(avail), "xof=" + df(xof), "xou=" + df(xou), "mf=" + df(mf), "svg=" + df(svg), "<<<<<<<"));
              hist.add(new History(aPre, 5, "T" + term + " decrOfr." + mm + ", " + gbsnn(ix), "Xou" + df(xou), "B" + df(bid), "-" + df(dif), "=" + df(bids.get(ix)), "ofrs" + df(xof), "sub" + df(dif * mf), "->" + df(excessOffers), "VCTrid" + wh(valueChangesTried[ix]), "<<<<<"));

              ec.addOHist(ohist, new History(aPre, 5, "T" + term + " decrOfr." + mm + ", " + gbsnn(ix), "Xou" + df(xou), "B" + df(bid), "+" + df(-dif), "=" + df(bids.get(ix)), "ofrs" + df(xof), "sub" + df(dif * mf), "->" + df(excessOffers), "VCTrid" + wh(valueChangesTried[ix]), "<<<<<"));
              chgCnt += 1;
              changed.add(ix, 1.);
              valueChangesTried[ix]++;

            }
            else // now enforce the limits
            if (doLimit) {  // offer too large, reduce bid
              dif = bid - limit; // dif < 0 decrease bid
              bids.add(ix, -dif); //move bid back to limit
              excessOffers += -dif * mf;  // get strategic value of bids
              hist.add(new History(aPre, History.debuggingMinor11, sreq, stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + df(bid), "ned" + df(need), "avl" + df(avail), "xof=" + df(xof), "xou=" + df(xou), "mf=" + df(mf), "svg=" + df(svg), "<<<<<<<"));
              ec.addOHist(ohist, new History(aPre, History.debuggingMinor11, sreq, stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + df(bid), "ned" + df(need), "avl" + df(avail), "xof=" + df(xof), "xou=" + df(xou), "mf=" + df(mf), "svg=" + df(svg), "<<<<<<<"));
              hist.add(new History(aPre, 5, "T" + term + " limit." + mm + "," + gbsnn(ix), "L" + df(limit), "G" + df(bid), "sub" + df(-dif), "=" + df(bids.get(ix)), "ofrs" + df(xof), "sub" + df(-dif * mf), "->" + df(excessOffers), "VCTried" + wh(valueChangesTried[ix]), "<<<<<"));
              ec.addOHist(ohist, new History(aPre, 5, "OT" + term + " limit." + mm + "," + gbsnn(ix), "L" + df(limit), "G" + df(bid), "sub" + df(-dif), "=" + df(bids.get(ix)), "ofrs" + df(xof), "sub" + df(-dif * mf), "->" + df(excessOffers), "VCTried" + wh(valueChangesTried[ix]), "<<<<<"));
              changed.add(ix, 1.);
            }
            else {
              hist.add(new History(aPre, History.aux2Info, sreq, stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + df(bid), "ned" + df(need), "avl" + df(avail), "xof=" + df(xof), "xou=" + df(xou), "mf=" + df(mf), "svg=" + df(svg), "<<<<<<<"));
            }
          }// for m
          //        changes += (int)changed.curSum();
          calcStrategicSums(); //  sv,excessOffers
          bids.sendHist(hist, aPre);
          excessOffers += subFuzz;  //force sv to be a bit more than sf
          if (term < eM.barterStart - 1) {
            // if round 2 or 3, no changes and sum &gt; myGoal for trade.
            if (rnd23 && sv > sf && changed.sum() <= PZERO) {
              return;  // done if strategic value is more than the goal
            }
          }

          //     }// for mm
          // loop from strategic least to most, amount most to least
          // rount 1: ? still offering too little, increase offers
          // or finally rnd2 decrease requests
          for (int m = 0; m < E.L2SECS; m++) {
// low to high
            ix = strategicValues.minIx(m);
            String sT = "T." + term + "." + mm;
            String gcN = sT + " " + ix + "->" + (ix / LSECS > 0. ? "G" : "C") + (int) (ix % E.LSECS);
            didChange = false;
            bid = bids.get(ix);
            first = oFirstBid.get(ix);
            emerg = emergOfrs.get(ix);
            avail = availOfrs.get(ix);
            double need1 = needReq.get(ix); // > 0
            double fneed1 = fneedReq.get(ix); // > 0
            isRequest = bid < E.NZERO;
            String sreq = gcN + " " + (starting ? bid < E.NZERO ? "sReq" : bid > E.PZERO ? "sOfr" : "sfuzz" : bid < E.NZERO ? "bReq" : bid > E.PZERO ? "bOfr" : "bfuzz");
            isOffer = bid > PZERO;
            // approach emerg if the first mm round did not gain enough offer
            //only use emerg in emergency
            // on rounds %gt; 1 limit = emerg or (avail + 2*emerg)/3
            limit = hEmerg ? emerg : mm > 1 ? (avail + emerg + emerg) * .3333 : avail;
            //' String sLimit = (limit < bid?"limit":"!limit");
            mf = multF.get(ix); //composit strategic Fraction
            //  starting = mm < 1 ? starting : false; // starting only round 0
            // limit need and fneed to all the other was offering
            double fneed2 = need1 > fneed1 ? need1 : fneed1;  // it might be zero
            // ensure that need/req does not exceed a first offer by other;
            need = fneed = first > E.NZERO && term < eM.barterStart - 1 ? fneed2 > first ? first : fneed2 : first < E.PZERO ? 0. : fneed2; // need, fneed > 0
            Boolean bFirst = term < eM.barterStart - 1 && first > E.NZERO && first < fneed2;
            // String sNeed = (bid - -need > fuzz? " limit": " bidOK";
            isExcessRequests = excessOffers < -fuzz;
            isExcessOffers = excessOffers > fuzz;
            String sXcReq = (isExcessRequests ? " xcReq" : isExcessOffers ? " xcOfr" : " fuzz");
            //String stries = "trys=" + ec.df(valueChangesTried[ix]) + ec.df(incTerm);
            //     isMoreOffers = excessOffers + offers*eM.moreOfferBias[pors] > fuzz;
            isNeed = need > PZERO; // need exists
            //        highCritical = m < hcntr;
            isAvail = avail > PZERO; //avail exists
            isEmerg = emerg > PZERO;  // emergency value exists
            Boolean needy = -need - bid > E.PZERO;  // subtract dif
            doLimit = bid - limit > E.PZERO;
            Boolean greedy = avail - bid > E.PZERO;  // sff ,ptr
            Boolean pbid = bid > E.PZERO;
            Boolean nbid = bid < E.NZERO;
            // String sLimit = (needy ? "needy" : bFirst ? "first" : doLimit ? "limit" : greedy ? "greedy" : pbid ? "pbid" : nbid ? "nbid" : "bfuzz");

            //    notHiCrit = m < hcntr;
            hiCrit = m >= hcntr;

            doChg = chgCnt < maxCnt;
            isChanged = changed.get(ix) > 0.; // changed this term
            triesOK = valueChangesTried[ix] < incTerm && !isChanged && doChg;
            String sLimit = (bFirst ? "first" : !incValueOK ? "!inc" : !triesOK ? "!tries" : needy ? "needy" : nbid ? "bReq" : doLimit ? "limit" : greedy ? "greedy" : pbid ? "bOfr" : "bfuzz");
            String stries = "ct" + chgCnt + ":" + maxCnt + "tr" + ec.df(valueChangesTried[ix]) + ":" + ec.df(incTerm);
            svg = multV.get(ix);
            xou = mf > PZERO ? excessOffers / mf : 0.; //convert to bid units
            xof = excessOffers;
            double goodStrategicValue = bid * mf;

            // Starting terms process offers starting with least significant
            // if starting and excessRequests try to make offer
            // raise offers rnd23
            // can steal requests
            //   if ((starting || isExcessRequests) && isAvail && triesOK && bid < avail) {
            if (starting && isAvail && bid < avail) {
              dif = avail - bid;  // avail > 0 make bid <= avail dif>0
              bids.add(ix, dif); // increase bid for offer
              excessOffers += dif * mf;  // increase excessOffers
              hist.add(new History(aPre, History.debuggingMinor11, sreq, stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + df(bid), "ned" + df(need), "avl" + df(avail), "xof=" + df(xof), "xou=" + df(xou), "mf=" + df(mf), "svg=" + df(svg), "<<<<<<<"));
              ec.addOHist(ohist, new History(aPre, History.debuggingMinor11, sreq, stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + df(bid), "ned" + df(need), "avl" + df(avail), "xof=" + df(xof), "xou=" + df(xou), "mf=" + df(mf), "svg=" + df(svg), "<<<<<<<"));
              hist.add(new History(aPre, 5, "T" + term + " ofr." + mm + ", " + gbsnn(ix), "A" + df(avail), "B" + df(bid), "+" + df(-dif), "=" + df(bids.get(ix)), "ofrs" + df(xof), "add" + df(-dif * mf), "->" + df(excessOffers), "vChangesTried", wh(valueChangesTried[ix]), "<<<<<"));

              ec.addOHist(ohist, new History(aPre, 5, "OT" + term + " ofr." + mm + ", " + gbsnn(ix), "A" + df(avail), "B" + df(bid), "+" + df(dif), "=" + df(bids.get(ix)), "ofrs" + df(xof), "add" + df(-dif * mf), "->" + df(excessOffers), "vChangesTried", wh(valueChangesTried[ix]), "<<<<<"));
              changed.add(ix, 1.);
              if (!starting) {
                chgCnt += 1;
                valueChangesTried[ix]++;
              }
            }

            else // no may move request to zero
            // no no raise offers if excessRequests, no steal requests, can get zero
            // ignore triesOk
            if (!starting && false && isExcessRequests && isAvail && isOffer && bid < 0.) {
              dif = 0. - bid; // dif > 0.
              dif = xou > dif ? dif : xou + .5 * (dif - xou); // may make excessOffers > 0

              bids.add(ix, dif); // increase offer increase excessOffers
              excessOffers += dif * mf;  // decrease excessOffers
              changed.add(ix, 1.);
              hist.add(new History(aPre, History.debuggingMinor11, sreq, stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + df(bid), "ned" + df(need), "avl" + df(avail), "xof=" + df(xof), "xou=" + df(xou), "mf=" + df(mf), "svg=" + df(svg), "<<<<<<<"));
              ec.addOHist(ohist, new History(aPre, History.debuggingMinor11, sreq, stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + df(bid), "ned" + df(need), "avl" + df(avail), "xof=" + df(xof), "xou=" + df(xou), "mf=" + df(mf), "svg=" + df(svg), "<<<<<<<"));
              hist.add(new History(aPre, 5, "T" + term + (!triesOK ? " !tries." : !doChg ? " !chng." : isChanged ? " wasChgd." : " incOfrs.") + mm + ", " + gbsnn(ix), "A" + df(avail), "B" + df(bid), "+" + df(-dif), "=" + df(bids.get(ix)), "ofrs" + df(xof), "add" + df(dif * mf), "->" + df(excessOffers), "VCTrid" + wh(valueChangesTried[ix]), "<<<<<"));

              ec.addOHist(ohist, new History(aPre, 5, "T" + term + (!triesOK ? " !tries." : !doChg ? " !chng." : isChanged ? " wasChgd." : " incOfrs.") + mm + ", " + gbsnn(ix), "A" + df(avail), "B" + df(bid), "+" + df(-dif), "=" + df(bids.get(ix)), "ofrs" + df(xof), "add" + df(dif * mf), "->" + df(excessOffers), "VCTrid" + wh(valueChangesTried[ix]++), "<<<<<"));
              chgCnt += starting ? 0 : 1;
            } //no no  decrease requests if excessRequests and triesOK did above
            else if (false && rnd3 && isExcessRequests && !isChanged && doChg && triesOK && isRequest) {
              //  maxDif = bid; // bid<0, maxDif<0-max increase of bid units
              dif = (xou > bid) ? xou : bid; //dif <0, xou<0, bid< 0
              bids.add(ix, -dif); //incr bid, decr req, 0 bid or excessOffers
              excessOffers -= dif * mf;  // increase excessOffers 
              hist.add(new History(aPre, History.debuggingMinor11, sreq, stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + df(bid), "ned" + df(need), "avl" + df(avail), "xof=" + df(xof), "xou=" + df(xou), "mf=" + df(mf), "svg=" + df(svg), "<<<<<<<"));
              ec.addOHist(ohist, new History(aPre, History.debuggingMinor11, sreq, stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + df(bid), "ned" + df(need), "avl" + df(avail), "xof=" + df(xof), "xou=" + df(xou), "mf=" + df(mf), "svg=" + df(svg), "<<<<<<<"));
              hist.add(new History(aPre, 5, "T" + term + " decrReq." + mm + ", " + gbsnn(ix), "B" + df(bid), "+" + df(-dif), "=" + df(bids.get(ix)), "ofrs" + df(xof), "add" + df(dif * mf), "->" + df(excessOffers), "VCTrid" + wh(valueChangesTried[ix]), "<<<<<"));

              ec.addOHist(ohist, new History(aPre, 5, "T" + term + " decrReq." + mm + ", " + gbsnn(ix), "B" + df(bid), "+" + df(-dif), "=" + df(bids.get(ix)), "ofrs" + df(xof), "add" + df(dif * mf), "->" + df(excessOffers), "VCTrid" + wh(valueChangesTried[ix]++), "<<<<<"));
              changed.add(ix, 1.);
              chgCnt += 1;
              isExcessRequests = excessOffers < NZERO;
              isExcessOffers = excessOffers > PZERO;
            }
            else {
              hist.add(new History(aPre, History.aux2Info, sreq, stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + df(bid), "ned" + df(need), "avl" + df(avail), "xof=" + df(xof), "xou=" + df(xou), "mf=" + df(mf), "svg=" + df(svg), "<<<<<<<"));
            }

          }// m
          calcStrategicSums(); //reset excessOffers,sf,sv
          bids.sendHist(hist, aPre);
        } // mm

        changes += (int) changed.sum();
        // deal with availCash
        // offer:send less or request more
        // Assets.CashFlow.Trades.enforceStrategicGoal
        // keep min cash of 1000 unless rawhealth < .2 than spend more
        // its an emergency use cash
        //        double cash2 = cash - minCash; // cash we may trade
        xof = excessOffers; // excessOffers < 0 means excess req
        bCash = myOffer.getCash();

        // if bCash < 0, it increases aCash aCash the tradableCash
        double aCash = availCash - bCash; // remaining avail cash
        if (aCash > PZERO && xof < fuzz) {
          double bCash2 = bCash;  // result of change
          double aCash2 = aCash;
          if (aCash + xof < fuzz) { //aCash partial solution
            xof += aCash; // reduce excess requests
            // offer positive cash is cash I offer
            bCash2 = myOffer.setCash(bCash + aCash); // inc offered cash
            aCash2 = 0;   // reduce myCash
            hist.add(new History(aPre, 5, "T" + term + " " + name + " excessOfffers=", df(excessOffers), "=>" + df(xof), "$=" + wh(pCash - bCash), "=>" + df(pCash - bCash2), "myOffer.getCash=", df(bCash), "=>" + df(bCash2), "rawH=", df(rawProspects2.min())));
          }
          else { // aCash is enough or more
            aCash2 += excessOffers;  // reduce cash by  excessOffers<0
// add excessOffers to cash we are offering, excessOffers<0
            bCash2 = myOffer.setCash(-excessOffers + bCash);
            xof = 0.;
            hist.add(new History(aPre, 5, "b" + term + " " + name + " excessOfffers=", df(excessOffers), "=>" + df(xof), "$=" + wh(pCash - bCash), "=>" + df(cash - bCash2), "myOffer.getCash=", df(bCash), "=>" + df(bCash2), "rawH=", df(rawProspects2.min())));

          }
          changes++;
        } // end reduce cash

        // still need to reduce our offers?
        // reduce cash offered from the other (request here)
        // Assets.CashFlow.Trades.enforceStrategicGoal
        bCash = myOffer.getCash();
        double bCash2 = bCash;
        xof = excessOffers;
        if (bCash < NZERO) {  // can we reduce requested cash
          if (excessOffers > fuzz && excessOffers + bCash > fuzz) { //still need to reduce more than just cash
            //       cash -= myOffer.getCash(); //increase cash
            xof += bCash;
            bCash2 = myOffer.setCash(0.);
            hist.add(new History(aPre, 5, "b" + term + " " + name + " excessOffers", df(excessOffers), "=>", df(xof), "offerCash", wh(bCash), "=>", wh(bCash2), "cash", wh(pCash), "=>" + wh(pCash + bCash)));
            changes++;
          }
          else if (excessOffers > fuzz && (bCash + excessOffers) < fuzz) {
            // reduce requested cash
            bCash2 = myOffer.setCash(bCash + excessOffers);

            xof = 0.;
            if (History.dl > 4) {
              hist.add(new History(aPre, 5, "b" + term + " " + name + " excessOffers", df(excessOffers), "=>", df(xof), "offerCash", wh(bCash), "=>", wh(bCash2), "cash", wh(pCash), "=>" + wh(pCash + bCash)));
            }
            changes++;
          }

        } // end increase cash
        calcStrategicSums(); //reset excessOffers,sf,sv
        listDifBid(5, "sl2", entrGoods);
        changed.sendHist(History.loopMinorConditionals5, "#F");
      } // Assets.CashFlow.Trades.enforceStrategicGoal

      /**
       * decide whether to re-raise my request or not
       *
       * @param n a number of requests to be considered
       */
      void renewStrategicRequests(int n
      ) {// Assets.CashFlow.Trades.renewStrategicRequests
        double aTry, aDif, good, need, prevGood, fneed, avail, ig;
        double sv = 0;
        listGoods(9, "renw");
        //   listInitGoods(9, "renw");
        requestsAddedValue = 0; // start count here
        int ix, gCnt, n2 = n / 2;
        // try to renew the n top strategic values
        for (int m = 0; m < E.lsecs && n > 0; m++) {

          ix = strategicValues.maxIx(m);
          good = bids.get(ix);

          ig = 0;
          //    ig = initGoods.get(ix);
          gCnt = valueChangesTried[ix];
          prevGood = myxitGoods.get(ix);
          sv = strategicValues.get(ix);
          // renew a sector only maxTries times,
          // renew starting with high statistical value
          // hLim < 0  a request
          // good > hLim  good request could be higher
          // valueChangesTried  times for more request (decrease good)
          if (ig < NZERO && good > ig) {  //renew reduced request
            if (valueChangesTried[ix] < eM.maxTries[pors]) {
              bids.set(ix, ig); // reset to original value;
              valueChangesTried[ix]++;
              hist.add(new History(aPre, 5, "b" + term + " " + name, "renew" + m, (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "good=", df(good), "to", df(bids.get(ix)), "changes=" + valueChangesTried[ix], ""));
              changes++;

            }
          }
        }// end loop increasing my high strategic value bids
      } // Assets.CashFlow.Trades.renewStrategicRequests

      /**
       * calculate the strategic Goal < Requested/offered strategicGoal =
       * tradeFrac[pors][clan] *
       * ((tm1=cRand(15,favRand[pors][clan])< 1./randMax?1./randMax:tm1>randMax:randMax:tm1)
       * *(1. - ((3. - fav[myclan][oClan])/3.)
       * -(3.-oclanFavMult[myclan]*fav[oClan][myclan])/3.)
       * -(fav[myclan][pclan]>3. && fav[oClan][myclan]>3.?sosFrac:1.)) *(1 -
       * barterTimes*barterMult)
       *
       * @return strategicGoal = desired totalReceipts/totalSend
       * (strategicValue)
       */
      double calcStrategicGoal() {// Assets.CashFlow.Trades.calcStrategicGoal
        //       term = myOffer.getTerm();  // use class term
        aPre = "S$";
        int ttype = pors;
        ttype = oEcon.pors == E.S && pors == E.S ? 2 : pors;
        double frac;
        double tmpRand = cRand(15 + term, eM.randMult);
        tmpRand = cRand(15, eM.randMult);
        double randFrac = tmpRand < 1. / eM.randMax ? 1. / eM.randMax : tmpRand > eM.randMax ? eM.randMax : tmpRand;
        oClan = myOffer.getOClan();
        //reduce FavFrac as favor goes higher
        double myFavV = (eM.favMult * eM.fav[clan][oClan] - eM.favMult * 3.);
        double myFavFrac = 1. / (1. + myFavV); // mult decr or incr goal
        double oFavV = (eM.favMult * eM.fav[oClan][clan] - eM.favMult * 3.) * eM.oClanMult;
        double oFavFrac = 1. / (1. + oFavV);
        // no reduction in goal unless sos is registered as sosFrac = 1./(1+sosfrac[pors]
        double sosFrac = 1. / (1. + (sos ? (eM.fav[clan][oClan] > 3. && eM.fav[oClan][clan] > 3.) ? eM.sosfrac[pors] : 0. : 0.));
        double gtBias = eM.goalTermBias[pors];
        double termFrac2 = (eM.barterStart + gtBias) / ((gtBias + eM.barterStart - term) * (gtBias + eM.barterStart - term));
        double termFrac02 = (eM.barterStart + gtBias) / ((gtBias + eM.barterStart - 0.) * (gtBias + eM.barterStart - 0.));
        double termFrac3 = (eM.barterStart + gtBias) / ((gtBias + eM.barterStart - term));
        double termFrac03 = (eM.barterStart + gtBias) / ((gtBias + eM.barterStart - 0.));
        double termFrac = (eM.barterStart) / ((gtBias + eM.barterStart - term));
        double termFrac0 = (eM.barterStart) / ((gtBias + eM.barterStart - 0.));
        double tfrac = eM.tradeFrac[ttype][clan] * randFrac;
        rGoalFrac = eM.tradeFrac[pors][clan] * randFrac * myFavFrac * oFavFrac * sosFrac;
        sf = strategicGoal = frac = rGoalFrac * termFrac;
        rGoal0 = rGoalFrac * termFrac0;

        hist.add(new History(aPre, 5, "T" + term + name + " goal=" + df(frac), "rand" + df(tmpRand), "randF" + df(randFrac), "*myF" + df(eM.fav[clan][oClan]) + "=>" + df(myFavFrac), "*oFc" + df(eM.fav[oClan][clan]) + "=>" + df(oFavFrac), "*sosF=" + df(sosFrac), "trdF =" + df(eM.tradeFrac[pors][clan]), "*rand=" + df(tfrac), "*termF=" + df(termFrac), "goal=" + df(frac), "gtb" + df(gtBias), "<<<<<<<"));
        return strategicGoal = frac;
      }// Assets.CashFlow.Trades.calcStrategicGoal

      /**
       * calculate requ double calcStrategicGoal(int term){
       *
       * }
       * /**
       * Calculate stategic values of resource/cargo and staff/guests. Only
       * cargo and guests are traded, but some resources are moved to cargo, and
       * some staff are moved to guests. Trading of cargo and guests is done in
       * order to promote long term growth in resources and staff for planets,
       * and for ships, trades gain necessary resources and staff, and
       * accumulates tradable guests and cargo that can be traded with other
       * planets. In every case, the strategy is to attempt to optimize long
       * term growth. The application of different random costs each year
       * complicates the issue of calculating strategy. The input to strategy,
       * are the calculated needs, generally needs are seen as resource/cargo
       * and staff/guests,
       *
       * using the mtggNeeds6 (5 year future needs) and using mtgNeeds6 (current
       * needs) calculate results: &\n; fneedReq future need request &\n;
       * needReq current need request availOfrs current offers,&\n; emergOfrs
       * emergency offer (reserve eliminated (c, g will be allocated for
       * this)&\n;
       *
       */
      void calcTrades() {// Assets.CashFlow.Trades.calcTrades

        // Assets.CashFlow.Trades.calcTrades
        // Calc Strategic value resource nominal value 2
        ec.aPre = aPre = "&j";
        histTitles("calcTrades1");
        strategicValues = makeZero(strategicValues, "stratValues");
        strategicValues.getRow(BALANCESIX + RCIX).setAmultV(stratVars.getRow(BALANCESIX + RCIX), eM.nominalWealthPerTrade[pors]);
        // planets should not request staff;
        strategicValues.getRow(BALANCESIX + SGIX).setAmultV(stratVars.getRow(BALANCESIX + SGIX), eM.tradeWealthPerStaff[pors]);
        tEmerg = rawProspects2.curMin() < eM.tradeEmergFrac[pors][clan];
        //      strategicValues = strategicValues.mult(stratVars, E.nominalWealthPerResource);
        //   cStratVal = strategicValues.getRow(0);
        //    gStratVal = strategicValues.getRow(1);
        hist.add(new History(aPre, 9, " strategicValues", " is stratVars rc mult", df(eM.nominalWealthPerTrade[pors]), "PG" + df(eM.tradeWealthPerStaff[pors]), "SG" + df(eM.tradeWealthPerStaff[pors])));
        bLev = 99;
        stratVars.sendHist(hist, bLev, aPre, "c stratVars", "g stratVars");
        strategicValues.sendHist(hist, bLev, aPre, "c strategicValues", "g strategicValues");

        double aneed = 0., aavail = 0., avail2 = 0., eneed = 0., eeneed = 0.;
        //fneedReq, needReq,availOfrs,emergOfrs
        // fneedReq likely need x yrs in future biggest needs
        // fneedTrads also include the current goal needs
        //  fneedReq sets the largest need
        // needReq are the current needs, from the remnants, not goals
        // availOfrs amount available within the trade goal
        //  availOfrs is the amount offered first for trade
        // emergOfrs least need, most amount available in an emergency
        //       maxTrades = new A2Row(cMaxTrade.zero(), gMaxTrade.zero());

        // Planets need to offer ships about .5 of their resource & staff
        // offer the top resources&staff
        // Ships need to make available much of their resources, and 
        // swap/repurpose them to make availabe if needed by planet in emergency
        // This code tries to increase the offered values of least strategic values
        int startAvail = (int) eM.startAvail[pors][clan];
        int criticalNumber = (int) eM.tradeCriticalNumber[pors][clan];
        // double[] availMin = {eM.availMin[pors] * bals.getRow(BALANCESIX + RCIX).ave(), eM.availMin[pors] * bals.getRow(BALANCESIX + SGIX).ave()}; // min avail for rc&sg
        int endRequests = criticalNumber;
        double ReservFrac = eM.availFrac[pors][clan];
        double emergFrac = eM.emergFrac[pors][clan];
        double emergRequestFrac = emergFrac;
        int kk = 3;
        // add -need(surplus) max-kk to bals max-kk
        // use the increment to make offers larger than surplus
        double availIncrement[] = {-eM.tradeReserveIncFrac[pors][clan] * (mtgNeeds6.getRow(0).min() - mtgNeeds6.getRow(0).max(kk)), -eM.tradeReserveIncFrac[pors][clan] * (mtgNeeds6.getRow(1).min() - mtgNeeds6.getRow(1).max(kk))};
        double emergToRegular = ReservFrac / emergFrac;
        // double emergeOfferFrac = emergFrac;
        A2Row ened = new A2Row(History.loopMinorConditionals5, "ened");
        A2Row aval = new A2Row(History.loopMinorConditionals5, "aval");
        A2Row aval1 = new A2Row(5, "aval1");
        A2Row aval2 = new A2Row(5, "aval2");
        A2Row stratn = new A2Row(5, "stratn");
        // double sumAvails = 0;
        // balAvail is zeroed
        //A2Row balAvail = new A2Row(History.loopMinorConditionals5, "balAvail");
        //  A2Row balEmerg = new A2Row(History.loopMinorConditionals5, "balEmerg");
        // A2Row stratAvails = new A2Row(History.loopMinorConditionals5, "stratAvail");
        // most strat min are largest, more has the least strategic effect
        // min(0) is the least strat value, min(13) most strat value
        // max(0) is the most strategic value, most wanted, least balances
        // the least stratValues are best traded
        // max startAvail=>L2SECS covers the least strat, most available
        int n = 0, nn = 0, nmax = stratVars.maxIx(startAvail);
        int rIx = 0, nIx = 0, brIx = 0, bnIx = 0;
        double requestInit = bals.curSum() / 6.; // more than ave
        for (int j = 0; j < E.l2secs; j++) {
          // go to lowest straVars largest values
          n = bals.curMinIx(j); // go from least balances to most
//if(n < 7){ E.sysmsg("calcTrades %s%d=%7.2f, %s%d=%7.2f,%s%d=%7.2f,%s%d=%7.2f",r.aschar,n,r.balance.get(n),c.aschar,n,c.balance.get(n),s.aschar,n,s.balance.get(n),g.aschar,n,g.balance.get(n));}
          // now set up need and fneed for requests
          if (j < endRequests) { // process smaller bals, so make requests
            // set init request - sector balance
            eneed = ened.set(n, requestInit - bals.curGet(n));
            eeneed = eneed * emergRequestFrac; // >0 larger request
            aneed = eeneed * emergToRegular; // >0 normal request
            needReq.set(n, aneed);
            fneedReq.set(n, eeneed);
            availOfrs.set(n, 0.);
            emergOfrs.set(n, 0.);
            hist.add(new History(aPre, History.valuesMinor7, "j=" + j + ",n=" + n, " fn=" + ec.df(eeneed), "en=" + ec.df(eneed), "an=" + ec.df(aneed), "requestIni", df(requestInit), "b=" + df(bals.curGet(n)), "<<<<<"));
          }
          else {
            needReq.set(n, 0.);
            fneedReq.set(n, 0.);
          }

          // calculate offers except for critical number of only requests
          // limit offers by actual balance, but much higher than mtgNeeds6 available
          if (j > criticalNumber) { // now calculate offers
            nn = bals.curMinIx(j - 2); // try for a lesser balance
            bnIx = nn % E.LSECS;
            brIx = (nn / E.LSECS);
            rIx = n / E.LSECS;
            nIx = n % E.LSECS;
            aavail = Math.min(bals.curGet(n) * emergFrac, aval.set(n, (-mtgNeeds6.get(rIx, nIx) + availIncrement[rIx])));
            avail2 = aavail * emergToRegular;
            // availOfrs: current surpluses we can barter, keeping a reserve
            availOfrs.set(n, avail2 > PZERO ? avail2 : 0.);
            // emergOfrs are avail without the reserve, use of tEmerg is set
            emergOfrs.set(n, aavail > PZERO ? aavail : 0.);
            hist.add(new History(aPre, History.valuesMinor7, " j=" + j, "aav" + ec.df(aavail), "av2" + ec.df(avail2), "avInc" + ec.df(availIncrement[rIx]), "nn=" + nn + ", n=" + n, "rIx=" + rIx + ",nIx=" + nIx, "brIx" + brIx + ",bnIx" + bnIx, "mtg=" + ec.df(-mtgNeeds6.get(rIx, nIx)), "<<<<<<"));
          }
          else {
            availOfrs.set(n, 0.);
            emergOfrs.set(n, 0.);
          }
        }

        bLev = History.informationMinor9;
        lev = History.valuesMinor7;
        histTitles("calcTrades2");
        hist.add(new History(aPre, lev, name + " strtAvail" + startAvail, "rqInit=" + requestInit, "aFrac=" + df(ReservFrac), "eFrac=" + df(emergFrac), "avInc" + df(availIncrement[0]), df(availIncrement[1]), "<<<<<<<<<<<<"));
        mtggNeeds6.sendHist2(hist, bLev, aPre, lev, "RCmtggNeeds6", "SGmtggNeeds6");
        mtGNeeds6.sendHist2(hist, bLev, aPre, lev, "RCmtGNeeds6", "SGmtGNeeds6");
        mtgNeeds6.sendHist2(hist, bLev, aPre, lev, "RCmtgNeeds6", "SGmtgNeeds6");
        bals.sendHist2(lev, aPre);
        ened.sendHistcg();
        aval.sendHistcg();
        aval1.sendHistcg();
        aval2.sendHistcg();
        emergOfrs.sendHist(hist, bLev, aPre, lev, "rc emergOfrs", "sg emergOfrs");
        availOfrs.sendHist(hist, bLev, aPre, lev, "rc availOfrs", "sg availOfrs");
        fneedReq.sendHist(hist, bLev, aPre, lev, "rc fneedReq", "sg fneedReq");
        needReq.sendHist(hist, bLev, aPre, lev, "rc needReq", "sg needReq");
      }  // Assets.CashFlow.Trades.calcTrades
 
    
    /** subclass to enable the search for a good planet to do the next trade.
     * trade.Assets.CashFlow.SearchRecord
     */
    class SearchRecord{
       Econ cn; //0=planet or ship,1=primaryShip
  String cnName = "aPlanetOther";
  A2Row goodsFracs = new A2Row(); //only one instance of goods, for both cn's  
  int goodsCnt= 0; // count of goods found
 // int prevTerm = 60;
  int prevAge = 1;
  double firstTradeWorth = 0.;
  double lastTradeWorth = 0.;
  double curTradeWorth = 0.; 
  double averageYearWorthIncrease = 0.0;
  int prevYear = -10;  // year of the offer **
  int firstYear = 0;
  int lastYear = 0;
  double travelCost=0.; // travel, maintenence cost of travel to this planet
 double[] xyzs = {-40, -41, -42}; //planet location
 double startWorth = 0.;// worth before the trade.
 double endWorth = 0.; // worth after trade
 double strategicValue = 0.; // received/sent;
  int clan = 0;
  NumberFormat dFrac = NumberFormat.getNumberInstance();
  NumberFormat whole = NumberFormat.getNumberInstance();
  NumberFormat dfo = dFrac;
 // EM eM; see outer class
      
  /** constructor with no variables
   * 
   */
      SearchRecord(){
        prevYear = -10;
      } // end class SearchRecord constructor
      
      SearchRecord(Econ one){
        cn = one;
        clan = one.clan;
        pors = one.pors;
        prevAge = -12;
        cnName = one.name;
      }
      
      void updatePlanet(TradeRecord tRec){
        double goodsSum = tRec.goods.plusSum() - tRec.goods.negSum();
         if(prevYear < 0){ // first entry
          prevYear = eM.year;
          firstYear = eM.year;
          firstTradeWorth = tRec.endWorth;
        }
         lastTradeWorth = tRec.endWorth;
         lastYear = eM.year;
        for(int n=0;n < E.L2SECS;n++){
          // initial values of 0.0
          goodsFracs.add(n,tRec.goods.get(n)/goodsSum);
          goodsCnt++;
       } 
      
      }
      
      Econ searchForNextPlanet(Econ[] nearPlanetsList,ArrayList<TradeRecord>  knownPlanets){
        Econ ret = eM.planets.get(0);
        int nearPlanetsListLength =  nearPlanetsList.length;
        int knownPlanetsLength = knownPlanets.size();
        SearchRecord[] searchNearPlanets = new SearchRecord[nearPlanetsListLength];
        for(int m = 0; m < nearPlanetsListLength;m++){
          searchNearPlanets[m] = new SearchRecord(nearPlanetsList[m]);
        }
        
        
        for(int m=0;m < knownPlanetsLength;m++){
         TradeRecord nextKnownPlanet = knownPlanets.get(m); 
         Econ knownEcon = knownPlanets.get(m).cn;
         for(int n=0;n< nearPlanetsListLength;n++){
           if(knownEcon == nearPlanetsList[n]){
            searchNearPlanets[m].updatePlanet(knownPlanets.get(m));
           }
         }
        }
        
        return ret;
      } // end searchForNextPlanet
      
    } // end trade.Assets.CashFlows.Trades.SearchRecord
//
    
     Econ selectPlanet(Econ[] wilda) {
    String wildS = "in selectPlanet for:" + name + " names=";
    for (Econ ww : wilda) {
      wildS += " " + ww.name + " distance=" + ec.calcLY(ec, ww);
    }
    int r = (int) Math.floor(Math.random() * 5.3 % wilda.length);
    wildS += " selected:" + wilda[r].name;
    System.out.println(wildS);
    return wilda[r];
  }
 } // Assets.CashFlow.Trades
    
      int eeea = 0, eeeb = 0, eeec = 0, eeed = 0, eeee = 0, eeef = 0, eeeg = 0, eeeh = 0, eeei = 0, eeej = 0;

    /**
     * start the process to deal with cashFlow for the next year
     *
     * @param aas higher level Class Assets
     */
   
    void cashFlowInit(Assets aas) { //Assets.CashFlow.initCashFlow
      histTitles("initCashFlow");
      EM.wasHere = "CashFlow.init... before HSwaps eeea=" + ++eeea;
      prevns = new HSwaps[lPrevns];
      // set balances sub ARows to reference in bals
      balances.A[BALANCESIX + RCIX] = bals.A[BALANCESIX + RCIX];
      balances.A[BALANCESIX + SGIX] = bals.A[BALANCESIX + SGIX];
      //    System.out.println(name + " " + new Date().toString() + "start initCashFlow");
      calcPriority(percentDifficulty);// get yprorityYr
      // now initialize knowledge subs from bals references
      EM.wasHere = "CashFlow.init... before for loop eeeb=" + ++eeeb;
      for (int i = didCashFlowInit ? E.LSECS : 0; i < E.lsecs; i++) {
        commonKnowledge.set(i, E.knowledgeForPriority * aknowledge * ypriorityYr.get(i) / ypriorityYr.sum() + E.knowledgeByDefault * aknowledge, "set initial knowledge per econ sector");
      }
      //Assets.CashFlow.initCashFlow
      knowledge.set(commonKnowledge, newKnowledge);
      cash = wealth;
      term = -4;
      hist.add(new History("&&", 9, "knowledge", knowledge));
      //  System.out.println("5651 mid CashFlow.initCashFlow");
      lTitle = "initResource";
      histTitles(lTitle);
      resource.initResource(0, false, cargo, res);
      r = resource;
      //lTitle = "init Cargo";
      //histTitles(lTitle);
      EM.wasHere = "CashFlow.init... before initCargo eeec=" + ++eeec;
      cargo.initCargo(1, true, resource, res * eM.initialReserve[pors]);
      c = cargo;
      //lTitle = "init Staff";
      // histTitles(lTitle);
      staff.initStaff(2, false, guests, colonists);
      s = staff;
      //lTitle = "init Guests";
      //histTitles(lTitle);
      guests.initGuests(3, true, staff, colonists * eM.initialReserve[pors]);
      g = guests;
      dFrac.setMinimumFractionDigits(2);
      whole.setMaximumFractionDigits(0);
      EM.wasHere = "CashFlow.init... after initGuests eeed=" + ++eeed;
      E.myTest(!(r.balance == bals.getRow(BALANCESIX + RIX)), "r.balance.get(0)=%6.2f not equal bals.getRow(BALANCESIX+RIX).get(0)=%6.2f\n", r.balance.get(0), bals.getRow(BALANCESIX + RIX).get(0));
      //     E.myTest(!(r.growth == bals.getRow(GROWTHSIX + RIX)), "r.growth.get(0)=%6.2f not equal bals.getRow(GROWTHSIX+RIX).get(0)=%6.2f\n", r.growth.get(0), bals.getRow(GROWTHSIX + RIX).get(0));

      balances.setUseBalances(History.informationMinor9, "balances", r.balance, c.balance, s.balance, g.balance);

      // reset x.growth and growths to the entering bals.
      for (i = 0; i < 4; i++) {
        sys[i].growth = growths.A[2 + i] = bals.A[GROWTHSIX + i];
        growths.aCnt[2 + i]++;
      }
      EM.wasHere = "CashFlow.init... after growths for eeee=" + ++eeee;
      rawGrowths.setUseBalances(History.informationMinor9, "rawGrowth", r.rawGrowth, c.rawGrowth, s.rawGrowth, g.rawGrowth);
      invMEff.setUseBalances(History.valuesMinor7, "invMEff", r.invMaintEfficiency, c.invMaintEfficiency, s.invMaintEfficiency, g.invMaintEfficiency);
      invGEff.setUseBalances(History.valuesMinor7, "invGEff", r.invGroEfficiency, c.invGroEfficiency, s.invGroEfficiency, g.invGroEfficiency);
      //   calcPriority(percentDifficulty);
      clanRisk = eM.clanRisk[pors][clan];
      doFailed = false;
      EM.wasHere = "CashFlow.init... before calc Priority eeef=" + ++eeef;
      calcPriority(percentDifficulty); // calc this years piority into priorityYr and as.difficulty
      EM.wasHere = "CashFlow.init... before calcCatastrophy eeeg=" + ++eeeg;
      calcCatastrophy();
      EM.wasHere = "CashFlow.init... before calcEfficiency loop eeeh" + ++eeeh;
      for (k = 0; k < 4; k++) {
        sys[k].calcEfficiency();
        sys[k].calcGrowth();
      }
      EM.wasHere = "CashFlow.init... after calcGrowth loop eeei" + ++eeei;
      //  System.out.println("5631 near end CashFlow.initCashFlow");
      didStart = (ec.age < 1 ? false : didStart);// probably age = -1 before year
      if (!didStart) {
        lTitle = "strtCashFlow";
        histTitles(lTitle);
        hist.add(new History(aPre, 5, "initAsYr" + year, "wealth=", df(wealth), "colonists=", df(colonists), "Knowledge=", df(aknowledge), "difficulty=", df(percentDifficulty)));
        start();
      }
      //    started = traded = growed = endyr = copyy(cur);
      didCashFlowInit = true;
      EM.wasHere = "CashFlow.init... end eeej=" + ++eeej;
      //   System.out.println("5045 end CashFlow.initCashFlow");
    }  //Assets.CashFlow.cashFlowInit
    
    /** select a planet to trade
     * 
     * @param wilda  list of tradeable planets from StarTrader
     * @return the Econ of the selected planet
     */
     Econ selectPlanet(Econ[] wilda) {
      if(myTrade == null){
        Offer myOffer = new Offer();
        myTrade = new Trades();
        myTrade.initTrade(myOffer,this);
      }
    return myTrade.selectPlanet(wilda);
     }
    /**
     * return the current value of loop n
     *
     * @return
     */
    int getN() {
      return n;
    }

    SubAsset copyIf(CashFlow.SubAsset sa) { // only
      if (sa == null) {
        return null;
      }
      return sys[sa.sIx];
    }

    //  double addCash(double cash) {
    //     return this.cash += cash;
    //  }
    /**
     * Calculate composite priority with initial priority for an economy+user
     * priority which can be changed each year with that compute the difficulty,
     * this will be used to calculate efficiency
     *
     * @see calculate ypriorityYr the composite if Econ and user priorities
     */
    void calcPriority(double percentDifficulty) {  //CashFlow
      ARow uAdjPri = new ARow();
      ARow yPritmp = new ARow();
      for (int i = 0; i < E.lsecs; i++) {
        uAdjPri.set(i, eM.userPriorityAdjustment[i][pors][clan] * E.priorityAdjustmentMultiplierFrac[pors]);
        yPritmp.set(i, (aSectorPriority.get(i) + E.initPriorityBias[pors] + uAdjPri.get(i)));
      }
      for (int i = 0; i < E.lsecs; i++) {
        //adjust each value by the sectoryPriory sum/ yPritmp sum
        // gets a sum of values add up to 100
        ypriorityYr.set(i, (yPritmp.get(i)) * (yPritmp.sum() < PZERO ? 0. : aSectorPriority.sum() / yPritmp.sum()), "priority recalculated each year");
      }
      hist.add(new History("&&", 9, "uAdjPri", uAdjPri));
      hist.add(new History("&&", 9, "yPritmp", yPritmp));
      hist.add(new History("&&", 9, "asectorPriority", aSectorPriority));
      hist.add(new History("&&", 9, "apriorityYr", ypriorityYr));
      //     ypriorityYr.setReValuedA(10., 18., ypriorityYr);//  values 10 - 18
//      hist.add(new History("&&", 9, "re priorityYr", ypriorityYr));
      for (int i = 0; i < E.lsecs; i++) {
        // note that the following code is to increase difficulty as priority decreases
        // sector difficulty is a function of economy difficulty and priority
        ydifficulty.set(i, percentDifficulty * (eM.difficultyByPriorityMin[pors] + (ypriorityYr.get(i) < PZERO ? 0. : (eM.difficultyByPriorityMult[pors]) / ypriorityYr.get(i))), "difficulty for each sector");
        // or just ignore the ypriorityYr, just use percentDifficulty
        //  ydifficulty.set(i, percentDifficulty, "difficulty for each sector");
      }
      hist.add(new History("&&", 9, "userAdjPri", uAdjPri));
      hist.add(new History("&&", 9, "asectorPriority", aSectorPriority));
      hist.add(new History("&&", 9, "apriorityYr", ypriorityYr));
      hist.add(new History("&&", 9, "difficulty=" + df(percentDifficulty), ydifficulty));
      if (History.dl > 10) {
        StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
        StackTraceElement aa = Thread.currentThread().getStackTrace()[2];
        StackTraceElement ab = Thread.currentThread().getStackTrace()[3];
        hist.add(
                new History(11, ">>>", a0.getMethodName(), "at", a0.getFileName(),
                        wh(a0.getLineNumber()), "from=", aa.getFileName(), wh(aa.getLineNumber()),
                        "ffrom", ab.getFileName(), wh(ab.getLineNumber())));
      }
      hist.add(new History(20, "difficulty", "0Life", "1Struct", "2Energy", "3Propel", "4Defense", "5Gov", "6Col", "Min", "Sum", "Ave"));
      //     hist.add(new History("&&", 9, "userPriority", userPriority));

    }

    void calcCatastrophy() {
      double t1 = 0., cc = 1.;

      if (eM.randFrac[pors][0] > PZERO && ((t1 = cRand(31)) < eM.userCatastrophyFreq[pors][clan] * (cc = eM.gameUserCatastrophyMult[pors][clan])) && t1 > 0.) {

        int r1 = (int) (cRand(6) * balances.get(0, 0)) % 7;
        int r2 = (int) (cRand(7) * balances.get(0, 1)) % 7;
        int s1 = (int) (cRand(8) * balances.get(1, 0)) % 7;
        int s2 = (int) (cRand(9) * balances.get(1, 1)) % 7;
        int r3 = (int) (cRand(10) * balances.get(0, 2)) % 7;
        int r4 = (int) (cRand(11) * balances.get(0, 5)) % 7;
        int s3 = (int) (cRand(12) * balances.get(1, 2)) % 7;
        int s4 = (int) (cRand(13) * balances.get(1, 3)) % 7;
        int r5 = (int) (cRand(14) * balances.get(0, 5)) % 7;
        double rReduce1 = 1. / (cRand(14) * cc * eM.catastrophyUnitReduction[pors][0] * 1.5);
        double rReduce2 = 1. / (cRand(15) * cc * eM.catastrophyUnitReduction[pors][0] * .6);
        double sReduce1 = 1. / (cRand(16) * cc * eM.catastrophyUnitReduction[pors][0] * 1.5);
        double sReduce2 = 1. / (cRand(17) * cc * eM.catastrophyUnitReduction[pors][0] * .6);
        int rBonusYrs1 = (int) (cRand(16) * cc * eM.catastrophyBonusYears[pors][0] * 2);
        int rBonusYrs2 = (int) (cRand(17) * cc * eM.catastrophyBonusYears[pors][0] * .8);
        int sBonusYrs1 = (int) (cRand(18) * cc * eM.catastrophyBonusYears[pors][0] * 2);
        int sBonusYrs2 = (int) (cRand(19) * cc * eM.catastrophyBonusYears[pors][0] * .8);
        double rBonusVal1 = cRand(20) * cc * eM.catastrophyBonusGrowthValue[pors][0] * 1.9;
        double rBonusVal2 = cRand(21) * cc * eM.catastrophyBonusGrowthValue[pors][0] * .5;
        double sBonusVal1 = cRand(22) * cc * eM.catastrophyBonusGrowthValue[pors][0] * 1.9;
        double sBonusVal2 = cRand(23) * cc * eM.catastrophyBonusGrowthValue[pors][0] * .5;
        double rDecayReduce1 = cRand(24) * cc * eM.decay[0][pors] * .006 * balances.getRow(0).sum();
        double rDecayReduce2 = cRand(25) * cc * eM.decay[0][pors] * .002 * balances.getRow(0).sum();
        double sDecayReduce1 = cRand(26) * cc * eM.decay[2][pors] * .006 * balances.getRow(1).sum();
        double sDecayReduce2 = cRand(27) * cc * eM.decay[2][pors] * .002 * balances.getRow(1).sum();
        int rBonusX1 = (int) (3 + cRand(28) * 5.) % 7;
        int rBonusX2 = (int) (4 + cRand(29) * 4.) % 7;
        int sBonusX1 = (int) (3 + cRand(30) * 5.) % 7;
        int sBonusX2 = (int) (4 + cRand(31) * 4.) % 7;
        double sBonusManuals = cRand(32) * cc * eM.catastrophyManualsMultSumKnowledge[pors][0] * 1.6 * knowledge.sum();

        double c1, c2, c3;
        r.cost3((c1 = balances.get(0, r1) * rReduce1), r1, 0);  // apply costs to P and S
        setStat("rCatCosts", pors, clan, c1, 1);
        s.cost3((c2 = balances.get(0, s1) * sReduce1), s1, 0);
        setStat("sCatCosts", pors, clan, c2, 1);
        r.cost3((c3 = balances.get(0, r2) * rReduce2), r2, 0);
        setStat("rCatCosts", pors, clan, c3, 1);
        r.bonusYears.add(rBonusX1, rBonusYrs1);             // both P & S
        setStat("rCatBonusY", pors, clan, rBonusYrs1 + rBonusYrs2, 2);
        r.bonusUnitGrowth.add(rBonusX1, rBonusVal1);
        setStat("rCatBonusVal", pors, clan, rBonusVal1 + rBonusVal2, 2);
        r.bonusYears.add(rBonusX2, rBonusYrs2);
        r.bonusUnitGrowth.add(rBonusX2, rBonusVal2);
        s.bonusYears.add(sBonusX1, sBonusYrs1);
        setStat("sCatBonusY", pors, clan, sBonusYrs1, 1);
        s.bonusUnitGrowth.add(sBonusX1, sBonusVal1);
        setStat("sCatBonusVal", pors, clan, sBonusVal1, 1);
        r.cumulativeDecay.add(r4, -rDecayReduce2);
        setStat("rCatNegDecay", pors, clan, rDecayReduce1, 1);
        s.cumulativeDecay.add(s3, -sDecayReduce1);
        setStat("sCatNegDecay", pors, clan, sDecayReduce1, 1);
        if (pors == E.P) {
          r.cumulativeDecay.add(s4, -rDecayReduce2);
          setStat("rCatNegDecay", pors, clan, rDecayReduce2, 1);
        }
        else {  // ships
          manuals.add(sBonusX2, sBonusManuals);  // Adds into value of trades
          setStat("sCatBonusManuals", pors, clan, sBonusManuals, 1);
        }

      }
    }

    /**
     * Assets.CashFlow variables needed here
     */
    String resTypeName = "anot";
    double remainingFF = 0., excessForFF = 0.;

    /**
     * calculate and process any need to move reserves into the
     * eM.clanFutureFunds
     *
     * @param n The number of the swap loop;
     * @return true = something set to future, false did nothing, continue swap
     */
    boolean calcFutureFund() {
      boolean doing = true, xcess = false, didXcessFF = false;
      int nReDo = 3;
      double mDif = .5;
      Double remainingFF = 0., excessForFF = 0., frac1 = 0., frac2 = 0.;
      Double val = 0., dif1 = 0., val1 = 0., tmp1 = 0., max1 = 0., tmp3 = 0.;//REmergFF
      // finish processes before leaving this loop
      int mMax = 1; // try no loops
      for (m = 0; m < mMax && (doing || xcess); m++) {
        xcess = doing = false; // false unless section does doing
        // only continue sizeFF
        if (resTypeName.contains("SizeFF")) {
          resTypeName = "anot";
        }
        if (resTypeName.contains("EmergFF")) {
          resTypeName = "anot";
        }

        // finish a previously started dues operation
        if (remainingFF > PZERO) {
          val = remainingFF;
          // select the largest for each move to forward fnd
          sourceIx = bals.curMaxIx(0);
          ixWRSrc = sourceIx / E.LSECS; //0,1
          ixWSrc = ixWRSrc * 2 + 2;  // /working source
          ixRSrc = ixWRSrc * 2 + 3;  // reserve source
          srcIx = sourceIx % E.LSECS;// 0-6
          val = Math.min(remainingFF, Math.min(bals.get(ixWRSrc, srcIx) * eM.futureFundTransferFrac[pors][clan], eM.futureFundFrac[pors][clan] * bals.curSum()));
          E.myTest(val < NZERO, "Negative val=%7.4f, bals=%9.4f", val, bals.get(ixWRSrc, srcIx));
          remainingFF -= val;
          xcess = true;
          resTypeName = ixWRSrc > 0 ? "SizeFFs" : "SizeFFr";
          E.sysmsg("doing remainingFF m=%d", m);
          if(remainingFF > 0.0)mMax++; // increase allowed loops
        }
        else // now test for dues, only before first swap
        // only do after startFutureFundDues
        if (n == 0 && reDo == 0 && !didXcessFF && (excessForFF = bals.curSum()
                - eM.clanStartFutureFundDues[pors][clan]) * (1. + Math.sqrt(excessForFF)) > E.PZERO) {
          didXcessFF = true;
          // select the largest for each move to forward fnd
          sourceIx = bals.curMaxIx(0);
          ixWRSrc = sourceIx / E.LSECS;
          ixWSrc = ixWRSrc * 2 + 2;
          ixRSrc = ixWRSrc * 2 + 3;
          srcIx = sourceIx % E.LSECS;
          xcess = true;
          // amount to tranfer due to size
          val = Math.min(excessForFF, bals.get(ixWRSrc, srcIx) * eM.futureFundTransferFrac[pors][clan]);
          remainingFF = excessForFF - val; // get any leftover
          
          E.myTest(val < NZERO, "Negative val=%7.4f, bals=%9.4f", val, bals.get(ixWRSrc, srcIx));
          resTypeName = ixWRSrc > 0 ? "SizeFFs" : "SizeFFr";
          if(remainingFF > 0.0)mMax++; // increase allowed loops
        }
        // now check if resources balances too much bigger than staff, 
        // swaps cannot solve  this problem
        // swaps cannot do staff transfers to make rawProspects2.curMin() > PZERO
        // decrease needed diference as r xfers increase 
        // .1 * (rxfers * .1 +.7) > .1
        // max1 = rc sum, dif1 = rc sum - sg sum, frac1= (dif1/max1) 
        // tmp1 = .7 + r xfers * .1, frac2 = frac1 * tmp1
        else if (((frac2 = (frac1 = (dif1 = (max1 = bals.getRow(ixWRSrc = 0).sum()) - bals.getRow(ixArow = 1).sum()) / max1) ) > eM.clanFutureFundEmerg1[pors][clan]) && reDo > nReDo && max1 > PZERO) {
          //   || (rawProspects2.getRow(ixArow).min(4) < NZERO && rawProspects2.getRow(ixSrc).min(3) > PZERO )
          resTypeName = "REmergFF1";
          // val1 is the surpluss of dif1 over staff
          // ..TransferFrac limits fraction of surpluss to transfer
          val1 = (dif1 * mDif * eM.futureFundTransferFrac[pors][clan]);
          // limit the size of transferby largest balance* future fund trans limit
          val = Math.min(val1, bals.getRow(ixWRSrc).get(srcIx = bals.getRow(ixWRSrc).maxIx()) * eM.futureFundTransferFrac[pors][clan]);
          doing = true;
        }
        // now check available funds s too more r or r too more s
        // is s.sum() to greater r.sum()
        else if (((frac1 = (dif1 = (max1 = -mtgNeeds6.getRow(ixWRSrc = 1).sum()) - -mtgNeeds6.getRow(ixArow = 0).sum()) / max1)  > eM.clanFutureFundEmerg1[pors][clan]) && reDo > nReDo && max1 > PZERO) {
          resTypeName = "SEmergFF1";
          // val1 is the surpluss of dif1 over staff
          // ..TransferFrac limits fraction of surpluss to transfer
          val1 = (dif1 * mDif * eM.futureFundTransferFrac[pors][clan]);
          // limit the size of transfer
          val = Math.min(val1, bals.getRow(ixWRSrc).get(srcIx = bals.getRow(ixWRSrc).maxIx()) * eM.futureFundTransferFrac[pors][clan]);

          doing = true;
        }
        else if ((false && (frac1 = (dif1 = (max1 = -mtgNeeds6.getRow(ixWRSrc = 0).sum()) - -mtgNeeds6.getRow(ixArow = 1).sum()) / max1) 
                > eM.clanFutureFundEmerg2[pors][clan])  && reDo > nReDo && max1 > PZERO) {
          resTypeName = "REmergFF2";
          // val1 is the surpluss of dif1 over staff
          // ..TransferFrac limits fraction of surpluss to transfer
          val1 = (dif1 * .5 * eM.futureFundTransferFrac[pors][clan]);
          // limit the size of transfer
          val = Math.min(val1, bals.getRow(ixWRSrc).get(srcIx = bals.getRow(ixWRSrc).maxIx()) * eM.futureFundTransferFrac[pors][clan]);

          doing = true;
        }
        else if ((false && (frac1 = (dif1 = (max1 = -mtgNeeds6.getRow(ixWRSrc = 1).sum()) - -mtgNeeds6.getRow(ixArow = 0).sum()) / max1) > eM.clanFutureFundEmerg2[pors][clan]) && reDo > nReDo && max1 > PZERO) {
          resTypeName = "SEmergFF2";
          // val1 is the surpluss of dif1 over staff
          // ..TransferFrac limits fraction of surpluss to transfer
          val1 = (dif1 * mDif * eM.futureFundTransferFrac[pors][clan]);
          // limit the size of transfer
          val = Math.min(val1, bals.getRow(ixWRSrc).get(srcIx = bals.getRow(ixWRSrc).maxIx()) * eM.futureFundTransferFrac[pors][clan]);

          doing = true;
        }
        // now compare balances
        else if (((frac1 = (dif1 = (max1 = bals.getRow(ixWRSrc = 0).sum()) - bals.getRow(ixArow = 1).sum()) / max1) > eM.clanFutureFundEmerg2[pors][clan])  && swapLoops > swapLoopMax-1) {
          //   || (rawProspects2.getRow(ixArow).min(4) < NZERO && rawProspects2.getRow(ixWRSrc).min(3) > PZERO )
          resTypeName = "RcEmergFF2";
          // val1 is the surpluss of dif1 over staff
          // ..TransferFrac limits fraction of surpluss to transfer
          val1 = (dif1 * mDif * eM.futureFundTransferFrac[pors][clan]);
          // limit the size of transfer
          val = Math.min(val1, bals.getRow(ixWRSrc).get(srcIx = bals.getRow(ixWRSrc).maxIx()) * eM.futureFundTransferFrac[pors][clan]);

          doing = true;
        }
        // max1 = sg.sum
        //  dif1 =  nax1 - rc,sum
        //  frac = dif/max1 *.7
        // if(frac > clanFFE1[pors][clan]
        else if (((frac1 = (dif1 = (max1 = bals.getRow(ixWRSrc = 1).sum()) - bals.getRow(ixArow = 0).sum()) / max1) > eM.clanFutureFundEmerg2[pors][clan]) &&swapLoops > swapLoopMax-1) {
          resTypeName = "SgEmergFF2";
          // val1 is the surpluss of dif1 over staff
          // ..TransferFrac limits fraction of surpluss to transfer
          val1 = (dif1 * mDif * eM.futureFundTransferFrac[pors][clan]);
          // limit the size of transfer
          val = Math.min(val1, bals.getRow(ixWRSrc).get(srcIx = bals.getRow(ixWRSrc).maxIx()) * eM.futureFundTransferFrac[pors][clan]);

          doing = true;
        }
        else if ((false && (frac1 = (dif1 = (max1 = bals.getRow(ixWRSrc = 0).sum()) - bals.getRow(ixArow = 1).sum()) / max1) > eM.clanFutureFundEmerg2[pors][clan]) && reDo > nReDo && max1 > PZERO) {
          resTypeName = "RcEmergFF2";
          // val1 is the surpluss of dif1 over staff
          // ..TransferFrac limits fraction of surpluss to transfer
          val1 = (dif1 * tmp1 * eM.futureFundTransferFrac[pors][clan]);
          // limit the size of transfer
          val = Math.min(val1, bals.getRow(ixWRSrc).get(srcIx = bals.getRow(ixWRSrc).maxIx()) * eM.futureFundTransferFrac[pors][clan]);

          doing = true;
        }// (sg -rc /sg) * .7 > cFFE2 
        else if ((false && (frac1 = (dif1 = (max1 = bals.getRow(ixWRSrc = 1).sum()) - bals.getRow(ixArow = 0).sum()) / max1) > eM.clanFutureFundEmerg2[pors][clan])  && reDo > nReDo && max1 > PZERO) {
          resTypeName = "SgEmergFF2";
          // val1 is the surpluss of dif1 over staff
          // ..TransferFrac limits fraction of surpluss to transfer
          val1 = (dif1 * tmp1 * eM.futureFundTransferFrac[pors][clan]);
          // limit the size of transfer
          val = Math.min(val1, bals.getRow(ixWRSrc).get(srcIx = bals.getRow(ixWRSrc).maxIx()) * eM.futureFundTransferFrac[pors][clan]);

          doing = true;
        }
        else {
          // end if no more FF needed
          //   E.sysmsg("in calcFutureFund endb m=%d",m);
          destIx = srcIx;
          return m > 0;
        } // did nothing do rest of swap 

        // now test again whether the pevious code found something to process
        if (doing || xcess) {
          // find cashValue to transfer for size 
          rsval = val * eM.nominalRSWealth[ixWRSrc][pors];
          hist.add(new History("$b", History.loopIncrements3, "calcFF " + resTypeName,
                  "v=" + df(rsval),
                  rcNsq[ixWRSrc] + srcIx + "=" + df(bals.get(ixWRSrc, srcIx)),
                  "dif" + df(dif1), "f" + df(frac1), "FF=" + df(eM.clanFutureFunds[clan]), "rc" + df(bals.getRow(0).sum()), df(mtgNeeds6.getRow(0).sum()), "sg" + df(bals.getRow(1).sum()), df(mtgNeeds6.getRow(1).sum())));
          bals.sendHist(5, "$c");
          if(rsval.isNaN() || rsval.isInfinite()){
          E.myTestDouble(rsval, "val", "the value to move passed from previous tests, prevval bals %s%d =%7.2f", rcsg[2 * ixWRSrc], srcIx, bals.get(2 * ixWRSrc, srcIx));}
          if(rsval < NZERO){
          E.myTest(rsval < NZERO, "Error neg val=%9.4f, resTypeName=%s, ixWRSrc=%d, srcIx=%d", val, resTypeName, ixWRSrc, srcIx);}
          if(bals.get(2 + 2 * ixWRSrc, srcIx) + bals.get(3 + 2 * ixWRSrc, srcIx) - val < E.NZERO)
          E.myTest(bals.get(2 + 2 * ixWRSrc, srcIx) + bals.get(3 + 2 * ixWRSrc, srcIx) - val < E.NZERO, "calcFutureFund error name=%7s, %s%d = %7.2f, %s%d=%7.2f sum=%7.2f less than val=%7.2f *eM.futureFundTransferFrac[pors][clan]= %7.2f bals*eM=%7.2f", resTypeName, aChar[2 * ixWRSrc], srcIx, bals.get(2 + 2 * ixWRSrc, srcIx), aChar[1 + 2 * ixWRSrc], srcIx, bals.get(3 + 2 * ixWRSrc, srcIx), bals.get(ixWRSrc, srcIx), val, eM.futureFundTransferFrac[pors][clan], bals.get(ixWRSrc, srcIx) * eM.futureFundTransferFrac[pors][clan]);
           m++;
           hist.add(new History("$c", History.loopMinorConditionals5,"n" + n + "calcFF" +" m" + m + rcNsq[ixWRSrc] + srcIx + " " +  resTypeName, "v" + df(val),  "b" + df(bals.get(ixWRSrc, srcIx)), "df" + df(dif1), "f" + df(frac1), "FF=" + df(eM.clanFutureFunds[clan]), "r" + df(bals.getRow(0).sum()), df(mtgNeeds6.getRow(0).sum()), "s" + df(bals.getRow(1).sum()), df(mtgNeeds6.getRow(1).sum()),"<<<<<<<<"));
          setStat(resTypeName, pors, clan, rsval, 1);
          setStat(resTypeName.contains("Emerg")?"EmergFF":"SizeFF", pors, clan, rsval, 1);
          setStat("FutureFundSaved", pors, clan, rsval, 1);
          // transfer val to clanFutureFunds
     //.eM.clanFutureFunds[clan] += rsval;
     yearsFutureFund += rsval;
     yearsFutureFundTimes++;
     // cost is units no cashValue;
     sys[ixWRSrc * 2].cost3(val, srcIx, (E.emergReserve[ixWRSrc][pors][clan]));
          //   E.sysmsg("did transfer val=%5.0f, name=%5s, m=%d",val,resTypeName,m);
          
     
          yCalcCosts(aPre, lightYearsTraveled, eM.tradeHealth[pors][clan], eM.tradeGrowth[pors][clan]);
        }
        else {
          //      E.sysmsg("in calcFutureFund endc m=%d",m);
          // if m>0 we did something, so exit swaps
          destIx = srcIx;
          return m > 0;
        }
      } // end doing
      //  E.sysmsg("in calcFutureFund endd m=%d",m);
      destIx = srcIx;
      return m > 0;
    }

    /**
     * start body of CashFlow perform task for startYear, endYear initiated by
     * StarTrader->Envirn->Assets->CashFlow
     */
    void yinitCosts() {
      //  startYr.set(cur);
      histTitles("yhinitCosts");
      ycalcEfficiency();

      doFailed = false;
    } // end yinitCosts in CashFlow

    boolean notDoing() {  //Assets.CashFlow
      // add a line
      return failed = !swapped;  // remove doFailed
    }

  // String tradedShipNames[][] = as.tradedShipNames; //{{"A", "B", "C", "D", "E"}, {"A", "B", "C", "D", "E"}, {"A", "B", "C", "D", "E"}, {"A", "B", "C", "D", "E"}, {"A", "B", "C", "D", "E"}};
    /**
     * Assets.AssetYr variables used by Assets.AssetYr.Trades These are the
     * values of the last trade in an AssetYr, and are used to set statistics
     * about the last trade
     */
    Trades myTrade;   // in Assets.CashFlow
    //   A2Row bids = new A2Row(History.informationMinor9, "bids");
    A2Row strategicValues = new A2Row(History.informationMinor9, "strategicValues");
    double requests, offers, unitRequests, unitOffers, unitGets;
    double totalReceipts, totalSend, totalRequests, totalOffers, needs;
    double strategicReceipts = 0., strategicOffers = 0., totalStrategicReceipts = 0.;
    double sumNominalRequests = 0.;
    double criticalStrategicRequests = 0., criticalStrategicOffers = 0., criticalStrategicFrac = 0.;
    double totalStrategicRequests = 0;
    double totalStrategicOffers = 0;
    double totalStrategicFrac = 0;
    double nominalRequests = 0., nominalOffers = 0., nominalFrac = 0.;
    double criticalNominalRequests = 0., criticalNominalOffers = 0., criticalNominalFrac = 0.;
    boolean didTrade = false, rejectTrade = false, lostTrade = false;
// now for the firsts
    A2Row bidsFirst = new A2Row(History.informationMinor9, "bidsFirst");
    A2Row strategicValuesFirst = new A2Row(History.informationMinor9, "strategicValuesFirst");
    double totalStrategicRequestsFirst = totalStrategicRequests;
    double totalStrategicOffersFirst = totalStrategicOffers;
    double totalStrategicFracFirst = totalStrategicFrac;
    double strategicFracFirst, strategicValueFirst;
    double requestsFirst, offersFirst, sumRequestsFirst, totalSendFirst, totalRequestsFirst, totalOffersFirst, needsFirst;
    double strategicReceiptsFirst = 0., strategicOffersFirst = 0., totalStrategicReceiptsFirst = 0.;
    double sumNominalRequestsFirst = 0.;
    double criticalStrategicRequestsFirst = 0., criticalStrategicOffersFirst = 0., criticalStrategicFracFirst = 0.;

    double nominalRequestsFirst = 0., nominalOffersFirst = 0., nominalFracFirst = 0.;
    double criticalNominalRequestsFirst = 0., criticalNominalOffersFirst = 0., criticalNominalFracFirst = 0.;
    double pCash = cash;
      double availCash = rawProspects2 == null ? cash : rawProspects2.min() < .2 ? rawProspects2.min() < NZERO ? cash - 0 : cash - 500 : cash - 1000;
      double excessOffers = 0.;
      int bb; // set bb so that (barterStart+bb)%2 always = 1
      int myIx, oIx;
      ArrayList<History> ohist;

      int searchTrade = yphase == yrphase.SEARCH ? 0 : 1;
      String oname;
      Econ oEcon;
//      int oClan = -5; don't override Assets.oClan, Assets.oPors
      //for calcStrategicSums
      double sumStrategicRequests=0,sumStrategicOffers=0;
     
      double lowStrategicOffers = 0.;
      // static double maxTradeFrac=10.,minTradeFrac=.03;
      // EM.multTotalStaticFrac[][],EM.gameMultTotalStrategicFrac
      double tradedMoreManuals = 0., tradedCash = 0.;
      int doingSearchOrTrade = yphase == yrphase.SEARCH ? EM.DOINGSEARCH : EM.DOINGTRADE;
    double fav = -5, oFav = -5, computeFav = -5;

    /**
     * process the offer to barter in CashFlow create the trade class, pass on
     * each barter and finally process the termination type of the barter:
     * didTrade, rejectedTrade,lostTrade
     *
     * @param inOffer The offer from the other economy<br>
     * if entryTerm and newTerm<ol>
     * <li>entryTerm >1 newTerm=barter => entryTerm-1 </li>
     * <li>entryTerm >1 newTerm=barter => 1 force decision </li>
     * <li>entryTerm >1 newTerm=barter) => 0 traded xitTrade => 0</li>
     * <li>entryTerm >1 newTerm=barter => -1 rejected xitTrade => -1</li>
     * <li>entryTerm == 1 newTerm=barter == 0 traded xitTrade => 0</li>
     * <li>entryTerm == 1 newTerm=barter == -1 rejected xitTrade => -1</li>
     * <li>entryTerm == 0 traded other xitTrade => -2 ndLoop</li>
     * <li>entryTerm == -1 lost other xitTrade => -3 ndLoop</li>
     * </ol>
     * 
     * @return a new offer for the other economy
     */
    Offer barter(Offer inOffer) { //Assets.CashFlow.barter
      aPre = "b&";
      hist.add(new History(aPre, 5, name + " Y Barter R", resource.balance));
      hist.add(new History(aPre, 5, name + " B T=" + inOffer.getTerm() + " S", staff.balance));
      hist.add(new History(aPre, 5, name + " initT C", c.balance));
      hist.add(new History(aPre, 5, name + " initT G", g.balance));
      //   inOffer.setMyIx(ec); // done later screws up flipOffer
      Offer ret = inOffer; // ret replaced if barter
      Trades eTrad = myTrade;  //Assets.myTrade
      int entryTerm = inOffer.getTerm();
      int newTerm = entryTerm; // until barter runs, then post barter value
      int ehist = 0;

      hist.add(new History(aPre, 5, "entr CashFlow barter", (eTrad == null ? " !eTrad" : " eTrad"), "entryTerm=" , wh(entryTerm), "$=" + df(sumTotWorth), "l=" + hist.size() + "======================<<<<<<<<<<"));
      int lhist = hist.size();
      int lhista = lhist;
      ret = inOffer;
      // barter of a new trade, instantiate Trades, remember other hist for possible copy
      yphase = yrphase.TRADE;
      if (myTrade == null && entryTerm > 0) {
        //      E.sysmsg(name + " instantiate ASY Trades term=" + entryTerm);
        // start a new trade within this Econ->Assets->CashFlow->Trades
        aPre = "A&";
        hist.add(new History(aPre, 5, name + " ASYb R", resource.balance));
        hist.add(new History(aPre, 5, name + " initT S", staff.balance));
        hist.add(new History(aPre, 5, name + " initT C", c.balance));
        hist.add(new History(aPre, 5, name + " initT G", g.balance));
        preTradeSum4 = bals.sum4();
        hist.add(new History(aPre, 5, " " + name + " now instantiate", " a new", " trades"));
        myTrade = new Trades();
       } // end myTrade == null
      // test for a new visitor
      if(!inOffer.getOName().equals(tradingShipName) ){
        tradingShipName = inOffer.getOName();
        inOffer.setMyIx(ec);
        myTrade.initTrade(inOffer, this);
        hist.add(new History(aPre, 5, " " + name + " after init", " a new", " trades"));
        EM.porsClanVisited[pors][clan]++;
        EM.porsVisited[pors]++;
        EM.clanVisited[clan]++;
        EM.visitedCnt++;
        // new year barter in Assets.CashFlow.barter
        tradedShipsTried++;
        aPre = "c&";
        hist.add(new History(aPre, 5, name + " cur.Bar R", resource.balance));
        hist.add(new History(aPre, 5, name + " cur.Bar S", staff.balance));
        hist.add(new History(aPre, 5, name + " cur.Bar C", c.balance));
        hist.add(new History(aPre, 5, name + " cur.Bar G", g.balance));

      E.myTest(myTrade == null && entryTerm > 0, "xit ASY barter " + (eTrad == null ? " !eTrad" : " eTrad") + " entryTerm=" + entryTerm + (myTrade == null ? " !myTrade" : " myTrade"));
      }//end other name not equal
      // now set up for a barter by Trades.barter
      if (myTrade != null && entryTerm > 0) {
        hist.add(new History(aPre, 5, " " + name + "cashFlow barter", " term=" + inOffer.getTerm(), " trades"));
        inOffer.setMyIx(ec);  //Assets.CashFlow.barter

        // now barter =====================================
        ret = myTrade.barter(inOffer); // get entryTerm-1, 0, -1

        newTerm = ret.getTerm();
        hist.add(new History(aPre, 5, name + " inCF" + newTerm, "newTerm=" + newTerm, "entryTerm=" + entryTerm, "copy to other", "history"));
        ehist = hist.size();
        ArrayList<History> ohist = ret.getOtherHist();
        String oname = ret.getOtherName();
        E.myTest(myTrade == null, "xit CF.barter " + (eTrad == null ? " !eTrad" : " eTrad") + " entryTerm=" + entryTerm + (myTrade == null ? " !myTrade" : " myTrade"));
        // copy all of the history to ohist, if eM.trade2HistOutputs for all newTerms
        if (eM.trade2HistOutputs && !ec.clearHist()) {  ///Assets.CashFlow.barter
          hist.add(new History(aPre, 5, " " + name + ">>>>>", " term=" + inOffer.getTerm(), "start copy hist=" + hist.size(), "frm " + name, "to " + oname, " ===================================<<<<<<<"));
          ec.addOHist(ohist, new History(aPre, 5, " " + name + ">>>>>", " term=" + inOffer.getTerm(), "start copy hist=" + hist.size(), "frm " + name, "to " + oname, " ===================================<<<<<<<"));

          for (; lhist < ehist; lhist++) {
            History ahist = hist.get(lhist);
            if (ahist != null) {
              ec.addHist(ohist, ahist);
            }
          }
          hist.add(new History(aPre, 5, " " + name + " => " + oname, " term=" + inOffer.getTerm(), " end copy ===================================<<<<<<<"));
          ec.addOHist(ohist, new History(aPre, 5, " " + name + " => " + oname, " term=" + inOffer.getTerm(), " end copy ===================================<<<<<<<"));
        } // end printing other
      } // end entryTerm > 0
        // check for ending this trade //Assets.CashFlow.barter
        fav = (eM.fav[oClan][clan]);
        
        // may enter barter terminating process
        if (newTerm < 1) { 
          
          if (newTerm == 0) {  //trade accepted
            tradedShipOrdinal++; // set ordinal of the next ship if any
            tradedSuccessTrades++;
            tradeAccepted = true;
            tradeRejected = tradeLost = false;
            EM.tradedCnt++;
            EM.porsTraded[pors]++;
            EM.porsClanTraded[pors][clan]++;
            EM.clanTraded[clan]++;
            // leave fav set 5 to 0
            if(entryTerm == 0) ret.setTerm(-2); // other so no more return
            // else leave ret.term 0 for the other cn
          }
          else if (newTerm == -1) {
            tradeRejected = entryTerm == -1 ? false:true; // rejectd by this econ
            tradeLost = entryTerm == -1 ? true:false; // rejected by othre econ
            tradeAccepted  = false;
            fav = -2.;
            if(entryTerm == -1) ret.setTerm(-3);
            // else leave ret.ter -1 for the other cn
          }
          else if (newTerm < -1) { // should stop in econ
            tradeLost = false; // shouldn't get here
            tradeRejected = false;
            tradeAccepted = false;
            ret.setTerm(-5);
            fav = -3.;
          } //exitif   Assets.CashFlow.barter
          hist.add(new History("%v", 5, "inCF", " term was=" + entryTerm, "now=" + ret.getTerm(), "fav=" + df(fav)));
          double criticalStrategicRequestsPercentTWorth = criticalStrategicRequests*100 / startYrSumWorth;
          double criticalStrategicRequestsPercentFirst = (criticalStrategicRequests) / criticalStrategicRequestsFirst;
          double criticalNominalReceiptsFracWorth = sumNominalRequests / startYrSumWorth;
          double criticalNominalRequestsFracFirst = criticalNominalRequests / criticalNominalRequestsFirst;
          double criticalNominalRequestsFracStrategicRequests = criticalNominalRequests/sumStrategicRequests;
          // at 0 -1 -2 -3 -5 always xit
          myTrade.xitTrade(); // term= 0 mytrade,-1 my reject,-2 other traded,-3o reject
          //pretrade are initTrade values for this trade
          // frac of availiable units -mtgNeeds6.sum/bals.
          double fracPreTrade = preTradeAvail*100 / preTradeSum4;
          double fracPostTrade = postTradeAvail*100 / postTradeSum4;
          // see if/how much frac avail increases
          double tradeAvailIncrPercent = fracPostTrade*100 / fracPreTrade;
          tW = new DoTotalWorths();
          tWTotWorth = tW.getTotWorth();
          double worthincrPercent = (tW.sumTotWorth - btW.sumTotWorth)*100 / btW.sumTotWorth;
          ret.set2Values(ec,btW.sumTotWorth,tW.sumTotWorth); // needed in TradeRecord SearchRecord
          // Desired stats 
          if (fav >= 5.) {
            // gameRes.WTRADEDINCRF5.wet(pors, clan, worthincrPercent, 1);
            setStat("CRITICALRECEIPTSFRACSYFAV5", pors, clan, criticalStrategicRequestsPercentTWorth, 1);
            setStat("CRITICALRECEIPTSFRADROPT5", pors, clan, criticalStrategicRequestsPercentFirst, 1);
            setStat(EM.INCRAVAILFRAC5, pors, clan, tradeAvailIncrPercent, 1);
          }
          else if (fav >= 4.) {
            // gameRes.WTRADEDINCRF4.wet(pors, clan, criticalStrategicRequestsPercentTWorth, 1);
            setStat("CRITICALRECEIPTSFRACSYFAV4", pors, clan, criticalStrategicRequestsPercentTWorth, 1);
            setStat("CRITICALRECEIPTSFRADROPT4", pors, clan, criticalStrategicRequestsPercentFirst, 1);
            setStat(EM.INCRAVAILFRAC4, pors, clan, tradeAvailIncrPercent, 1);
          }
          else if (fav >= 3.) {
            // gameRes.WTRADEDINCRF3.wet(pors, clan, worthincrPercent, 1);
            setStat("CRITICALRECEIPTSFRACSYFAV3", pors, clan, criticalStrategicRequestsPercentTWorth, 1);
            setStat("CRITICALRECEIPTSFRADROPT3", pors, clan, criticalStrategicRequestsPercentFirst, 1);
            setStat(EM.INCRAVAILFRAC3, pors, clan, tradeAvailIncrPercent, 1);
          }
          else if (fav >= 2.) {
            // gameRes.WTRADEDINCRF2.wet(pors, clan, worthincrPercent, 1);
            setStat("CRITICALRECEIPTSFRACSYFAV2", pors, clan, criticalStrategicRequestsPercentTWorth, 1);
            setStat("CRITICALRECEIPTSFRADROPT2", pors, clan, criticalStrategicRequestsPercentFirst, 1);
            setStat(EM.INCRAVAILFRAC2, pors, clan, tradeAvailIncrPercent, 1);
          }
          else if (fav >= 1.) {
            // gameRes.WTRADEDINCRF1.wet(pors, clan, worthincrPercent, 1);
            setStat("CRITICALRECEIPTSFRACSYFAV1", pors, clan, criticalStrategicRequestsPercentTWorth, 1);
            setStat("CRITICALRECEIPTSFRADROPT1", pors, clan, criticalStrategicRequestsPercentFirst, 1);
            setStat(EM.INCRAVAILFRAC1, pors, clan, tradeAvailIncrPercent, 1);
          }
          else if (fav >= 0.) {
            // gameRes.WTRADEDINCRF0.wet(pors, clan, worthincrPercent, 1);
            setStat("CRITICALRECEIPTSFRACSYFAV0", pors, clan, criticalStrategicRequestsPercentTWorth, 1);
            setStat("CRITICALRECEIPTSFRADROPT0", pors, clan, criticalStrategicRequestsPercentFirst, 1);
            setStat(EM.INCRAVAILFRAC0, pors, clan, tradeAvailIncrPercent, 1);
          }
          else if (fav >= -1.) {
            // gameRes.WREJTRADEDPINCR.wet(pors, clan, worthincrPercent, 1);
            setStat("WREJTRADEDPINCR", pors, clan, worthincrPercent, 1);
            setStat(EM.INCRAVAILFRACa, pors, clan, tradeAvailIncrPercent, 1);
             eM.porsVisited[pors]++;
            eM.porsClanVisited[pors][clan]++;
          }
          else if (fav >= -2.) {
            // gameRes.WLOSTTRADEDINCR.wet(pors, clan, worthincrPercent, 1);
            setStat("WLOSTTRADEDINCR", pors, clan, worthincrPercent, 1);
            setStat(EM.INCRAVAILFRACb, pors, clan, tradeAvailIncrPercent, 1);
             eM.porsVisited[pors]++;
            eM.porsClanVisited[pors][clan]++;
          }
          else {
            // gameRes.UNTRADEDWINCR.wet(pors, clan, worthincrPercent, 1);
            setStat("UNTRADEDWINCR", pors, clan, worthincrPercent, 1);
            setStat(EM.INCRAVAILFRACc, pors, clan, tradeAvailIncrPercent, 1);
            eM.porsVisited[pors]++;
            eM.porsClanVisited[pors][clan]++;
          }
          if(fav >= 0){
             setStat("CRITICALRECEIPTSFRACSYFAVA", pors, clan, criticalStrategicRequestsPercentTWorth, 1);
             eM.clanTraded[clan]++;
             eM.porsClanTraded[pors][clan]++;
             eM.clanVisited[clan]++;
             eM.porsClanVisited[pors][clan]++;
             eM.porsTraded[pors]++;
             eM.porsVisited[pors]++;
          }
          hist.add(new History(aPre, 5, name + "CF.barter t=" + ret.getTerm(), "before", "xitTrade", "do null", "<<<<<<<<<", "<<<<<<<<<"));
          ec.addOHist(ohist, new History(aPre, 5, name + "CF.barter t=" + ret.getTerm(), "before", "xitTrade", "do null", "<<<<<<<<<", "<<<<<<<<<"));
          //  myTrade.xitTrade(); // term= 0 mytrade,-1 my reject,-2 other traded,-3o reject
          myTrade = null;  // terminate mytrade
          ret.setTerm(newTerm - 3);  //
          hist.add(new History(aPre, 5, name + "xit CF.barter t=" + ret.getTerm(), "after", "myTrade", "nulled", "<<<<<<<", "<<<<<<<<<"));
          ec.addOHist(ohist, new History(aPre, 5, name + "xit CFbarter t=" + ret.getTerm(), "after", "myTrade", "nulled", "<<<<<<<", "<<<<<<<<<"));
        }
        hist.add(new History(aPre, 5, "xit2 CF.barter", "o=" + oname, "newTerm=" + newTerm, "entryTerm=" + entryTerm,(myTrade == null?"!myTrade":"myTrade"), (eTrad == null ? "!eTrad" : "eTrad"), "lhist" + lhista, "ehist" + ehist, "$=" + df(sumTotWorth)));
        return ret;
    }

    /**
     * get tradingGoods and tradingOfferWorth assets.getTradingGoods nulls
     * CashFlow so myTrade is nulled
     *
     * @return the reference to bids
     */
    A2Row getTradingGoods() { //Assets.CashFlow.getTradingGoods
      if (!didGoods) {
        if (myTrade == null) {
          Offer inOffer = new Offer(eM.year, eM.barterStart, eM, ec, ec);
          myTrade = new Trades();
          aPre = "b#";
          hist.add(new History(aPre, 5, " " + name + "now init", " a new", " trades"));
          hist.add(new History(aPre, 5, name + " ASYb2 R", resource.balance));
          hist.add(new History(aPre, 5, name + " initT S", staff.balance));
          hist.add(new History(aPre, 5, name + " initT C", c.balance));
          hist.add(new History(aPre, 5, name + " initT G", g.balance));
          inOffer.setMyIx(ec);
          // set bids, set tradingOfferWorth
          yphase = yrphase.PRESEARCH;
          myTrade.initTrade(inOffer, this);
        }
        myTrade.calcStrategicSums();
        didGoods = true;
      }
      return bids;
    }

    ARow getManuals() {
      return manuals;
    }

    ARow getNewKnowledge() {
      return newKnowledge;
    }

    ARow getCommonKnowledge() {
      return commonKnowledge;
    }

    void yinitN() {  // Assets.CashFlow

      prevFlagg = flagg; // grow
      prevFlagh = flagh;  // health priority
      prevFlagf = flagf;   // grow xfer s if needed, ignore h
      prevFlagm = flagm; // xfer h
      int isDoNots = doNot.isDoNot();
      boolean isDoNotTrue = isDoNots > 0;
      histTitles("yinitN");
      hist.add(new History(aPre, 7, "n" + n + " yinitN", (swapped ? "swapped" : "!swapped"), "source=" + (source == null ? "null source" : source.aschar) + srcIx, "dest=" + (dest == null ? "null dest" : dest.aschar) + destIx, (failed ? "!doing" : "doing"), "rrg/rg=" + df(r.rawGrowth.ave() / r.balance.ave()), "srg/sb=" + df(s.rawGrowth.ave() / s.balance.ave()), (isDoNotTrue ? "stilldoNot" : "!doNot"), "$=" + df(sumTotWorth)));
      //     for (int k = 0; k < 4; k++) {
      //      sys[k].balanceWithPartner.set(sys[k].balance, sys[k].partner.balance);
      //     hist.add(new History(aPre, 5, "initN" + n + " " + aChar[k] + " balance", sys[k].balance));
      //    }

      gfFlag = fFlag = gFlag = geFlag = hFlag = nheFlag = heFlag = false;
      gmFlag = hmFlag = false;
      double nFrac = n / eM.maxn[pors];

      switch (yphase) {
        case TRADE:
          fFlag = nFrac <= eM.fFrac[pors];
          gFlag = nFrac <= eM.gFrac[pors];
          geFlag = nFrac > eM.geFrac[pors];
          gfFlag = nFrac <= eM.gfFrac[pors];
          hFlag = nFrac <= eM.hFrac[pors];
          heFlag = nFrac > eM.heFrac[pors];
          nheFlag = nFrac <= eM.nheFrac[pors];
          break;
        case GROW:
          /**
           * Growth and costs occur in the start yphase maintenance, travel, and
           * growth costs are increased if health < 1.0
           */

          gFlag = nFrac <= eM.gFrac[pors];
          gmFlag = nFrac <= eM.gmFrac[pors];
          geFlag = nFrac > eM.geFrac[pors];
          gfFlag = nFrac <= eM.gfFrac[pors];
          //hFlag = nFrac <= eM.hFrac[pors];
          // heFlag = nFrac > eM.heFrac[pors];
          //  nheFlag = nFrac <= eM.nheFrac[pors];
          break;
        case END:
          /**
           * Health must be ok
           */
          gFlag = nFrac <= eM.gFrac[pors];
          //    geFlag = nFrac <= eM.geFrac[pors];
          //   gfFlag = nFrac <= eM.gfFrac[pors];
          hFlag = nFrac <= eM.hFrac[pors];
          heFlag = nFrac > eM.heFrac[pors];
          hmFlag = nFrac <= eM.hmFrac[pors];
          nheFlag = nFrac <= eM.nheFrac[pors];

          break;
        case HEALTH:
          /**
           * end year, evaluate health again, if health < 0. die
           */
          hFlag = true;
          break;
        default:
          break;

      }
      emergHr = emergHs = false;
      doFailed = false;
    }

    /**
     * the efficiency is a function of knowledge and difficultyPercent The
     * relevance of each financial sector to the knowledge of a given sector is
     * in the requirement tables for growth and maintenance. This process logs
     * intermediate products on the way to efficiencies for resource and staff
     * during growth and maintenance/travel
     */
    protected void ycalcEfficiency() { // Assets.CashFlow.ycalcEfficiency
      resource.calcEfficiency();
      cargo.calcEfficiency();
      staff.calcEfficiency();
      guests.calcEfficiency();

    } // yCalcEfficiency   CashFlow

    int bbbb = 0, bbba = 0, bbbc = 0;

    void start() {  // Assets.CashFlow.start called from initCashFlow
      EM.wasHere = "CashFlow.start() just after entry bbba=" + ++bbba + " didStart=" + didStart;
      if (!didStart) {
        if (iyW == null) {
          iyW = new DoTotalWorths();
          //    iyWTotWorth = iyW.getTotWorth();
          startYrSumWorth = initialSumWorth = iyW.getTotWorth();
          startYrSumKnowledge = initialSumKnowledge = iyW.sumKnowledgeBal;
          startYrSumKnowledgeWorth = initialSumKnowledgeWorth = iyW.sumKnowledgeWorth;
          setStat("bothCreate",pors,clan,initialSumWorth,1); 
        }
        syW = new DoTotalWorths();
        // syWTotWorth = getTotWorth();
        prevYrSumWorth = startYrSumWorth;
        prevYrSumKnowledge = startYrSumKnowledge;
        prevYrSumKnowledgeWorth = startYrSumKnowledgeWorth;
        startYrSumWorth = syW.getTotWorth();
        startYrSumKnowledge = syW.sumKnowledgeBal;
        startYrSumKnowledgeWorth = syW.sumKnowledgeWorth;
      }
    }

    int swapsN = -10; // final end in the loop
    double fracLoopsCost = -10.; // difference between initial and final swaps worth
    //  dif/startYearTot
    double fractradeCost = -10.; // difference between initial and final trade worth
    // dif/startYearTot

    int cccaa = 0, cccab = 0, cccac = 0, cccad = 0, cccae = 0, cccaf = 0;
    int ccca = 0, cccb = 0, cccc = 0, cccd = 0, ccce = 0, cccf = 0, cccg = 0, ccch = 0, ccci = 0, cccj = 0;

    /**
     * yearEnd is the final routine in the cash flow. It is called after all
     * trades have happened, for ships it is invoked ater the one to five trades
     * have been finished, the didYearEnd is set in econ preventing a repeat
     * yearEnd. prepare then do swaps to get the best rawProspects2 a possible
     * trade, to first do any growth, and then costs payments. Costs payments
     * are based on the financial status after a full set of swaps have
     * optimized as much as possible the growth that can occur. The costs are
     * the costs of maintenance, travel and growth. The travel was set at year
     * start if this is a ship
     *
     */
    double yearEnd() {  // after trading done

      String aPre = "E@";
      curGrowGoal = eM.goalGrowth[pors][clan];
      curMaintGoal = eM.goalHealth[pors][clan];
      preveHr = preveHs = emergHr = emergHs = false;
      //   for (int i = 6; i < 4; i++) { // disabled this ran in initCashFlow
      //    balances.A[i+2] = sys[i].balance = bals.getRow(BALANCESIX + i);
      //    sys[i].bonusUnitGrowth = bals.getRow(BONUSUNITSIX + i);
      //    sys[i].bonusYears = bals.getRow(BONUSYEARSIX + i);
      //   sys[i].cumulativeDecay = bals.getRow(CUMULATIVEDECAYIX + i);
      //    growths.A[i+2] = sys[i].growth = growths.A[2+i] = bals.getRow(GROWTHSIX + i);
      //   growths.aCnt[2+i]++;
      //   }
      ec.saveHist = true;
      didStart = false;
      EM.wasHere = "CashFlow.yearEnd before start cccaa=" + ++cccaa;
      start();
      didStart = true;
      //   DoTotalWorths tW, rawCW, gSwapW, gGrowW, gCostW, fyW;
      iyWTotWorth = iyW.getTotWorth();
      syWTotWorth = syW.getTotWorth();
      //     traded = copyy(cur);
      //    double preGrowLoop = totalWorth(), prercGrowLoop = sumRCWorth, presgGrowLoop = sumSGWorth;
      EM.wasHere = "CashFlow.yearEnd at beginning ccca=" + ++ccca;
      swapCosts.zero();
      //    sumTotWorth = doTotalWorth(hist, "preGSwaps", startYearTotalWorths, difTotalWorths, preGSwapsTotalWorths);
      // set travel years for the case of at the initial planet
      if (pors == E.P) {
        lightYearsTraveled = ((lightYearsTraveled < .2)) ? eM.initTravelYears[pors][0] : lightYearsTraveled;
      }
      else {
        lightYearsTraveled = 0.;
        useMTCosts = true;
      }

      //  for (m = startYrs.length - 1; m > 0; m--) {
      //   startYrs[m] = hcopyy(cur);
      // }
      cmd = E.SwpCmd.NOT;

      rawProspects2 = makeZero(rawProspects2);
      rawFertilities2 = makeZero(rawFertilities2);
      // initialize prevns to cmd = not
      HSwaps abc = new HSwaps();
      prevns[0] = abc.copyn(cur);
      EM.wasHere = "CashFlow.yearEnd before setting prevns array cccab=" + cccab;
      for (m = prevns.length - 1; m > -1; m--) {
        prevns[m] = abc.copyn(cur);
      }
      EM.wasHere = "CashFlow.yearEnd after setting prevns array cccb=" + ++cccb;
      if ((pors == E.S) && newTradeYear2) {
        maintCosts10 = new A10Row(7, "maintCosts10").set(bals.getMrows());
        travelCosts10 = new A10Row(7, "travelCosts10").set(bals.getTrows());
        lightYearsTraveled = 0.;
      }
      EM.addlErr = ""; // clear it
      yphase = yrphase.PAY;
      doLoop("G@", yrphase.PAY, prevns[0]);
      EM.wasHere = "CashFlow.yearEnd just after doLoop cccc=" + ++cccc;

      // sLoops[0] = 0;
      gSwapW = new DoTotalWorths();
      sumTotWorth = gSwapWTotWorth = gSwapW.getTotWorth();
      fracLoopsCost = (sumTotWorth - startYrSumWorth) / startYrSumWorth;
      if (History.dl > 4) {
        StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
        hist.add(new History(aPre, 5, "n" + n + "xloop1", ">>>", a0.getMethodName(), "at", a0.getFileName(), wh(a0.getLineNumber()), (swapped ? "swapped" : "!swapped"), "prevN=" + prevn, "n=" + wh(n)));

      }
      hist.add(new History(aPre, 7, ">>>>end grow n" + prevn + "to" + n, (fFlag ? "f" : "!f") + whole(eM.maxn[pors] * eM.fFrac[pors]), (gfFlag ? "gf" : "!gf") + whole(eM.maxn[pors] * eM.gfFrac[pors]), (gFlag ? "g" : "!g") + whole(eM.maxn[pors] * eM.gFrac[pors]), (hFlag ? "h" : "!h") + whole(eM.maxn[pors] * eM.hFrac[pors]), (emergHr || emergHs ? "h" : "!he") + whole(eM.maxn[pors] * eM.heFrac[pors]), "max=" + whole(eM.maxn[pors]), "<<<<"));
      hist.add(new History(aPre, 7, ">>>end grow loop", "<<<<"));
      // recalculate growth and costs after all the swaps
      yphase = yrphase.GROW;
      lTitle = "grow & grow costs";
      histTitles(lTitle);
      if (History.dl > 4) {
        StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
        StackTraceElement aa = Thread.currentThread().getStackTrace()[2];
        StackTraceElement ab = Thread.currentThread().getStackTrace()[3];
        hist.add(new History(aPre, 5, "n" + n + "set preCosts", ">>>", a0.getMethodName(), "at", a0.getFileName(), wh(a0.getLineNumber()), (swapped ? "swapped" : "!swapped"), "n=" + wh(n), "yCalcCosts", "next", "<<<<<<"));
      }
      yCalcCosts(aPre, lightYearsTraveled, curGrowGoal, curMaintGoal); //renew rawProspects2 etc.
      sos = rawProspects2.curMin() < E.rawHealthsSOS;
      EM.wasHere = "before do live cccac=" + ++cccac;
      if (rawProspects2.curMin() > PZERO) { //proceed only if live,skip if dead
       
//        prevSwapResults(aPre);  // for the last loop

        /**
         * for the prevXn treat grow n Costs as a swap
         */
        EM.wasHere = "CashFlow.endYear after yCalcCosts cccad=" + ++cccad;
        n = 0;

        if (History.dl > History.informationMinor9) {
          StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
          hist.add(new History(aPre, History.informationMinor9, "n" + n + "preCosts", ">>>", a0.getMethodName(), "at", a0.getFileName(), wh(a0.getLineNumber()), (swapped ? "swapped" : "!swapped"), "n=" + wh(n), "<<<<<<"));
        }

        lTitle = "grow n costs";
        histTitles(lTitle);
        //     sumTotWorth = doTotalWorth(hist, "post2GSwaps", nextTotalWorths, difTs, postMTGCostsTotalWorths);

//      EM.gameRes.PREGROWTH.wet(pors, clan, preGrowLoop - preGWorth);
        //     EM.gameRes.CUMPREGROWTH.wet(pors, clan, preGrowLoop - preGWorth);
        growths.sendHist(hist, "G@");
        hist.add(new History("G@", History.valuesMajor6, "r.growth", r.growth));
        EM.wasHere = "CashFlow.endYear before growths cccad=" + ++cccad;
        if (r.growth != growths.A[2]) {
          eM.aErr("r.growth not the same as growths.A[0] ccca=" + ++ccca);
        }
        doGrowth(aPre);
        EM.wasHere = "CashFlow.endYear after doGrowth cccae" + ++cccae;
        gGrowW = new DoTotalWorths();
        sumTotWorth = gGrowW.getTotWorth();

        if (History.dl > History.informationMinor9) {
          StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
          //      hist.add(new History(aPre,History.informationMinor9, "post Growth", ">>>at", wh(a0.getLineNumber()), "H=" + df(yearStartHealth), "$$ didGrow", df(postGWorth), df(postGWorth - preGWorth), "trade$$", df(preTradeWorth), df(sumTotWorth - preTradeWorth), df(sumTotWorth)));

        }
        yphase = yrphase.PAY;
        double rem = 0;
        EM.wasHere = " CashFlow.yearEnd.after yrphase.pay cccd=" + ++cccd;
        if ((rem = bals.curSum() - mtgCosts10.curSum()) < PZERO) {
          E.myTest(true, "year end costsSum= %7.3f exceeds balancesSum= %7.3f,remnantSum= %7.3f age=" + ec.age + ", year=" + eM.year + ", rc sum=" + df(bals.getRow(0).sum()), mtgCosts10.curSum(), bals.curSum(), rem);
        }
        doMaintCost(aPre);
        EM.wasHere = "CashFlow yearEnd after doMaintCost cccaf=" + ++cccaf;
        doTravCost(aPre);
        doGrowthCost(aPre);
        //      DoTotalWorths syW, tW, gSwapW, gGrowW, gCostW, fyW;
        gCostW = new DoTotalWorths();
        sumTotWorth = gCostW.getTotWorth();

        EM.wasHere = "CashFlow.YearEnd after doGrowthCost ccce=" + ++ccce;
        if (History.dl > History.informationMinor9) {
          StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
          //       hist.add(new History(aPre, History.informationMajor8, "post nCosts", ">>>at", wh(a0.getLineNumber()), "H=" + df(yearStartHealth), "$$ didCosts", df(postGrowthTotalWorths[0] - postMTGCostsTotalWorths[0]), "pretrade$$", df(startYearTotalWorths[0]), df(sumTotWorth - preTradeTotalWorths[0]), df(sumTotWorth)));

        }
        yphase = yrphase.END;
        swapCosts.zero();
//      hist.add(new History(20, "yEnd Health", "0Life", "1Struct", "2Energy", "3Propel", "4Defense", "5Gov", "6Col", "Min", "Sum", "Ave"));
        lTitle = "HealthCosts " + name;
        //       doLoop(aPre, yrphase.END, prevns[0]);

        //     sumTotWorth = doTotalWorth(hist, "endYear", postMTGCostsTotalWorths, difTs, endYearTotalWorths);
        //    EM.gameRes.HLTHSWAPCOSTS.set(pors, clan, difTs[0]);
//      health = Math.min(resource.health.min(), staff.health.min());
        if (History.dl > History.informationMinor9) {
          StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
          hist.add(new History(aPre, History.valuesMinor7, ">>>n" + n + "post Health at", wh(a0.getLineNumber()), (swapped ? "swapped" : "!swapped"), "n=" + wh(n), "H=" + df(health), "$=" + df(sumTotWorth)));

        }
        
       eM.clanFutureFunds[clan] += yearsFutureFund;
       yearsFutureFundTimes = 0;
       yearsFutureFund=0;
       
        fyW = new DoTotalWorths();
        fyW.setPrev(syW);
        sumTotWorth = fyW.getTotWorth();
        if (History.dl > History.informationMinor9) {
          StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
          hist.add(new History(aPre, History.valuesMinor7, "n" + n + "post Health", ">>> at", wh(a0.getLineNumber()), "H=" + df(rawProspects2.curMin()), "Ntrade$$", df(startYrSumWorth), df(sumTotWorth - startYrSumWorth), df(sumTotWorth)));

        }
        EM.wasHere = " CashFlow.yearEnd ner end of routine cccf=" + ++cccf;;
        ec.saveHist = false;
        hist.add(new History(History.loopMinorConditionals5, ">>>>end Health", "H=" + df(health), (fFlag ? "f" : "!f") + whole(eM.maxn[pors] * eM.fFrac[pors]), (gfFlag ? "gf" : "!gf") + whole(eM.maxn[pors] * eM.gfFrac[pors]), (gFlag ? "g" : "!g") + whole(eM.maxn[pors] * eM.gFrac[pors]), (hFlag ? "h" : "!h") + whole(eM.maxn[pors] * eM.hFrac[pors]), (emergHr || emergHs ? "eh" : "!eh") + whole(eM.maxn[pors] * eM.heFrac[pors]), "max=" + whole(eM.maxn[pors]), "<<<<"));
        //     hist.add(new History(4, ">>>end swap loop", "<<<<"));
      }
      else // end skip if already dead
      { // dead, be sure died is set
        /*   static final int LIVEWORTH = ++e4;
       static final int MISSINGNAME = ++e4;
       static final int DEADRATIO = ++e4;
       static final int DEADHEALTH = ++e4;
       static final int DEADFERTILITY = ++e4;
       static final int DEADSWAPSMOVED = ++e4;
       static final int DEADSWAPSCOSTS = ++e4;
       static final int DEADTRADED = ++e4;
       static final int DEADSWAPSNCOUNT = ++e4; */
        EM.wasHere = " CashFlow.yearEnd start of dead cccg=" + ++cccg;
        if (!died) {  // list only once
          // DoTotalWorths iyW, syW, tW, gSwapW, gGrowW, gCostW, fyW;
          double tt3 = 0;
          fyW = new DoTotalWorths();
          fyW.setPrev(syW);
          setStat("died", pors, clan, fyW.sumTotWorth, 1);
          EM.wasHere = " CashFlow.yearEnd into deac, and died ccch=" + ++ccch;
          if (swapsN < 0) {
            setStat("DeadNegN", pors, clan, fyW.sumTotWorth, 1);
          }
          else if (swapsN < 10) {
            setStat("DeadLt10", pors, clan, fyW.sumTotWorth, 1);
          }
          if (rawProspects2.curMin() < E.NZERO) {
            setStat("DeadNegProsp", pors, clan, fyW.sumTotWorth, 1);
          }
          setStat(EM.DEADHEALTH, pors, clan, rawProspects2.curMin(), 1);
          if ((tt3 = bals.getRow(1).sum() / bals.getRow(0).sum()) > 1.5) {
            setStat("DeadRatioS", pors, clan, tt3, 1);
          }
          if ((tt3 = bals.getRow(0).sum() / bals.getRow(1).sum()) > 1.5) {
            setStat("DeadRatioR", pors, clan, tt3, 1);
          }
          setStat(EM.DEADRATIO, pors, clan, bals.getRow(1).sum() / bals.getRow(0).sum(), 1);
          setStat(EM.DEADFERTILITY, pors, clan, rawFertilities2.curMin(), 1);
          setStat(EM.DEADSWAPSMOVED, pors, clan, swapsN, 1);
        }
        died = true;
        eM.clanFutureFunds[clan] += yearsFutureFund;
        yearsFutureFund = 0.;
        yearsFutureFundTimes = 0;
        hist.add(new History(aPre, 1, "n" + n + ">>>>>> aDEAD=" + df(health), "*dead*", "*dead*", "*dead*", "*dead*", "*dead*", "*dead*", "*dead*", "*dead*", "*dead*", "<<<<<<"));
        EM.wasHere = "CashFlow.yearEnd in dead just before return cccg=" + ++cccg;
        return rawProspects2.curMin();
      }

      //  startYearTotWorth = syW.getTotWorth();
      double tprev = 0.;
      // gameRes.LIVEWORTH.wet(pors, clan, sumTotWorth);
      EM.wasHere = "CashFlow.yearEnd before many setStat ccci=" + ++ccci;
      setStat(EM.LIVEWORTH, pors, clan, fyW.sumTotWorth, 1);
      setStat(EM.STARTWORTH, pors, clan, initialSumWorth, 1);
      eM.setStat(EM.WORTHIFRAC, (fyW.sumTotWorth - iyW.sumTotWorth)*100 / iyW.sumTotWorth, 1);
      eM.setStat(EM.WORTHINCR, (fyW.sumTotWorth - (tprev = syW.sumTotWorth))*100 / tprev, 1);

      double bcurSum = bals.curSum();
      double totWorth = fyW.getTotWorth();
      eM.setStat(EM.RCMTGC, pors, clan, mtgCosts10.getRow(0).sum() / bcurSum, 1);
      eM.setStat(EM.SGMTGC, pors, clan, mtgCosts10.getRow(1).sum() / bcurSum, 1);
      eM.setStat(EM.RRAWMC, pors, clan, maintCosts10.getRow(0).sum() / bcurSum, 1);
      eM.setStat(EM.SRAWMC, pors, clan, maintCosts10.getRow(1).sum() / bcurSum, 1);
      eM.setStat(EM.RRAWMC, pors, clan, maintCosts10.getRow(0).sum() / bcurSum, 1);
      eM.setStat(EM.SRAWMC, pors, clan, maintCosts10.getRow(1).sum() / bcurSum, 1);
      eM.setStat(EM.RCREQGC, pors, clan, reqMaintCosts10.getRow(0).sum() / bcurSum, 1);
      eM.setStat(EM.SGREQGC, pors, clan, reqMaintCosts10.getRow(1).sum() / bcurSum, 1);
      eM.setStat(EM.RCREQMC, pors, clan, reqGrowthCosts10.getRow(0).sum() / bcurSum, 1);
      eM.setStat(EM.SGREQMC, pors, clan, reqGrowthCosts10.getRow(1).sum() / bcurSum, 1);
      setStat("RCTBAL", pors, clan, fyW.getRCBal() / totWorth, 1);

      setStat("SGTBAL", pors, clan, fyW.getSGBal() / totWorth, 1);
      setStat("SBAL", pors, clan, fyW.getSBal() / totWorth, 1);
      setStat("GBAL", pors, clan, fyW.getGBal() / totWorth, 1);
      setStat("RBAL", pors, clan, fyW.getRBal() / totWorth, 1);
      setStat("CBAL", pors, clan, fyW.getCBal() / totWorth, 1);
      //      DoTotalWorths iyW,syW, tW, gSwapW, gGrowW, gCostW, fyW;
      eM.setStat(EM.POORKNOWLEDGEEFFECT, poorKnowledgeAveEffect, 1);
      eM.setStat(EM.POORHEALTHEFFECT, poorHealthAveEffect, 1);
      // gameRes.MANUALSB.wet(pors, clan, manuals.sum(), 1);
      eM.setStat(EM.MANUALSFRAC, pors, clan, manuals.sum(), 1);
      // gameRes.NEWKNOWLEDGEB.wet(pors, clan, newKnowledge.sum() / knowledge.sum(), 1);
      setStat(EM.NEWKNOWLEDGEFRAC, pors, clan, newKnowledge.sum(), 1);
      // gameRes.COMMONKNOWLEDGEB.wet(pors, clan, commonKnowledge.sum() / knowledge.sum(), 1);
      setStat(EM.COMMONKNOWLEDGEFRAC, pors, clan, commonKnowledge.sum(), 1);
      // gameRes.KNOWLEDGEINCR.wet(pors, clan, (knowledge.sum() - (tprev = asyW.getKnowledgeBal())) / tprev, 1);
      setStat(EM.KNOWLEDGEINCR, pors, clan, (knowledge.sum() - (tprev = syW.getKnowledgeBal()))*100 / tprev, 1);
      // gameRes.NEWKNOWLEDGEINCR.wet(pors, clan, (newKnowledge.sum() - (tprev = asyW.getNewKnowledgeBal())) / tprev);
      if ((tprev = syW.sumNewKnowledgeWorth) > PZERO) {
        setStat(EM.NEWKNOWLEDGEINCR, pors, clan, (fyW.sumNewKnowledgeWorth - tprev)*100 / tprev, 1);
      }
      // gameRes.COMMONKNOWLEDGEINCR.wet(pors, clan, (commonKnowledge.sum() - (tprev = asyW.getCommonKnowledgeBal())) / tprev, 1);
      if ((tprev = syW.sumCommonKnowledgeWorth) > PZERO) {
        setStat(EM.COMMONKNOWLEDGEINCR, pors, clan, (fyW.sumCommonKnowledgeWorth - tprev)*100 / tprev, 1);
      }
      // gameRes.MANUALSINCR.wet(pors, clan, (manuals.sum() - (tprev = asyW.getManualsBal())) / tprev, 1);
      if ((tprev = syW.sumManualsWorth) > PZERO) {
        setStat(EM.MANUALSINCR, pors, clan, (manuals.sum() - (tprev = syW.getManualsBal()))*100 / tprev, 1);
      }
     
      double worthincrPercent = (sumTotWorth - startYrSumWorth)*100 / startYrSumWorth;
      setStat(EM.WORTHINCR, pors, clan, worthincrPercent, 1);
      // gameRes.RCTBAL.wet(pors, clan, fyW.sumRCBal, 1);
      setStat(EM.RCfrac, pors, clan, fyW.sumRCWorth*100 / fyW.sumTotWorth, 1);
      // gameRes.SGTBAL.wet(pors, clan, fyW.sumSG, 1);
      setStat(EM.SGfrac, pors, clan, fyW.sumSGWorth*100 / fyW.sumTotWorth, 1);
      setStat(EM.KNOWLEDGEFRAC, pors, clan, fyW.sumKnowledgeBal*100 / sumTotWorth, 1);

      double criticalStrategicRequestsPercentTWorth = criticalStrategicRequests / startYrSumWorth;
      double criticalStrategicRequestsPercentFirst = (criticalStrategicRequestsFirst - criticalStrategicRequests) / criticalStrategicRequestsFirst;
      double criticalNominalReceiptsFracWorth = sumNominalRequests / startYrSumWorth;
      double criticalNominalRequestsFracFirst = criticalNominalRequests / criticalNominalRequestsFirst;
      tW = new DoTotalWorths();
      double worthincr1 = (fyW.sumTotWorth - syW.sumTotWorth)*100 / syW.sumTotWorth;
      if (fav >= 5.) {
        // gameRes.WTRADEDINCRF5.wet(pors, clan, worthincrPercent, 1);
        setStat("WTRADEDINCRF5", pors, clan, worthincrPercent, 1);
      }
      else if (fav >= 4.) {
        // gameRes.WTRADEDINCRF4.wet(pors, clan, worthincrPercent, 1);
        setStat("WTRADEDINCRF4", pors, clan, worthincrPercent, 1);
      }
      else if (fav >= 3.) {
        // gameRes.WTRADEDINCRF3.wet(pors, clan, worthincrPercent, 1);
        setStat("WTRADEDINCRF3", pors, clan, worthincrPercent, 1);
      }
      else if (fav >= 2.) {
        // gameRes.WTRADEDINCRF2.wet(pors, clan, worthincrPercent, 1);
        setStat("WTRADEDINCRF2", pors, clan, worthincrPercent, 1);
      }
      else if (fav >= 1.) {
        // gameRes.WTRADEDINCRF1.wet(pors, clan, worthincrPercent, 1);
        setStat("WTRADEDINCRF1", pors, clan, worthincrPercent, 1);
      }
      else if (fav >= 0.) {
        // gameRes.WTRADEDINCRF0.wet(pors, clan, worthincrPercent, 1);
        setStat("WTRADEDINCRF0", pors, clan, worthincrPercent, 1);
      }
      else if (fav >= -1.) {
        // gameRes.WREJTRADEDPINCR.wet(pors, clan, worthincrPercent, 1);
        setStat("WREJTRADEDPINCR", pors, clan, worthincrPercent, 1);
      }
      else if (fav >= -2.) {
        // gameRes.WLOSTTRADEDINCR.wet(pors, clan, worthincrPercent, 1);
        setStat("WLOSTTRADEDINCR", pors, clan, worthincrPercent, 1);
      }
      else {
        // gameRes.UNTRADEDWINCR.wet(pors, clan, worthincrPercent, 1);
        setStat("UNTRADEDWINCR", pors, clan, worthincrPercent, 1);
      }

      didGoods = false;
      // sLoops[0] = 
      n = 0;
      String aa[] = {"", "", "", "", ""};
      tradedShipNames[n] = aa;
      catastrophyBalIncr[n] = catastrophyPBalIncr[n] = 0.;
      catastrophyBalIncr[n]
      = catastrophyDecayBalDecr[n] = catastrophyDecayPBalDecr[n] = 0.;
      //   sLoops[n] = 0;
      clanRisk = eM.clanRisk[pors][clan];
      tradedShipOrdinal = 0;  // reset for both planet and ship
      tradedShipsTried = 0; // total trades tried
      didTrade = false;
      lostTrade = false;
      fav = -4;
      oTradedEcons = new Econ[20];
      oTradedEconsNext = 0;
      didStart = true;
      getTradingGoods();
      didStart = false; // force start at next initCashFlow
      didDecay = false;
      syW = null; // get rid of hanging DoTotalWorths
      EM.wasHere = "CashFlow.yearEnd just before final return cccj=" + ++cccj;
      //     yDestroyFiles();  no longer needed, Assets.yearEnd() nulls cur
      return health = rawProspects2.curMin();
    }

    void yDestroyFiles() {
      // DoTotalWorths syW, tW, gSwapW, gGrowW, gCostW, fyW;

      syW = null;
      tW = null;
      gSwapW = null;
      gGrowW = null;
      gCostW = null;
      fyW = null;
//      rawHealths2 = null;
      rawProspects2 = null;
      reqGrowthCosts = null;
      //   A6Row reqGrowthRemnants = new A6Row();
      rawGrowthCosts = null;
      //   A6Row reqFertilities = new A6Row(lev, "reqFertilities");
      rawFertilities2 = null;
//   A6Row limitedFertilities = new A6Row(lev, "limitedFertilities");
      //  A6Row mtCosts = new A6Row();
      //   A6Row mtRemnants = new A6Row();
      mtgCosts = null;
//    A6Row mtgRemnants = new A6Row();
//      mtgEmergNeeds = null;
      mtgNeeds6 = null;
      //     mtgGoalNeeds = null;
      //     mtgGoalCosts = null;
//    A6Row mtgReqRemnants = new A6Row();
      mtggCosts10 = null;
      //   A6Row mtggRemnants = new A6Row();
      //  mtggEmergNeeds = null;
      mtggNeeds6 = null;
      mtgFertilities = null;
      //     rawGoalFertilities = null;
      //     rawGoalHealths = null;
      mtggRawHealths = null;
      mtggRawFertilities = null;
      mtggRawHealths2 = null;
      mtggRawFertilities2 = null;
      fertilities = null;
      growthCosts = null;
      mtgReqFertilities = null;
      reqMaintCosts10 = null;
      reqGrowthCosts10 = null;
      rawGrowthCosts10 = null;
      maintCosts10 = null;
      travelCosts10 = null;
      mtgCosts10 = null;
      growthCosts10 = null;
      consumerHealthMTGCosts10 = null;
      consumerTrav1YrCosts10 = null;
      consumerMaintCosts10 = null;
      consumerReqGrowthCosts10 = null;
      consumerReqMaintCosts10 = null;
      consumerTravelCosts10 = null;
      consumerFertilityMTGCosts10 = null;
      rawFertilities2 = null;
      //  rawHealths2 = null;
      fertilities2 = null;
      rawProspects2 = null;
      mtggRawFertilities2 = null;
      mtggRawHealths2 = null;
      mtgNeeds6 = null;
      growths10 = null;
      bids = null;
      strategicValues = null;
// now for the firsts
      bidsFirst = null;
      strategicValuesFirst = null;
    }

    void nullNotTrade() {

    }

    void nullTrade() { // null if a trade

    }

    /**
     * test whether swap failed and it is time to move to the next set of
     * conditions do not move on is isDoNot is still active move on to the next
     * flag if notDoing and no isDoNot
     */
    void testForFailure() {

      prevn = n;
      int isDoNots = doNot.isDoNot();
      // if (isDoNots > 0) {
      //   swapped = true;
      //   swapType = 3;
      // }
      //  if (notDoing() && isDoNots <= 0) {
      if (notDoing()) {
        hist.add(new History("FL", History.loopIncrements3, nTitle("FAIL") + cmd.toString() + srcIx + "->" + destIx, "mov=" + df(mov), "src=" + df(balances.get(ixWRSrc, srcIx)), "r$" + rChrgIx + "=" + df(rcost), "s$" + sChrgIx + "=" + df(scost), "dst=" + df(balances.get(ixWRSrc, destIx)), "H" + rawProspects2.curMinIx() + "=" + df(rawProspects2.curMin()), "NR" + df(mtgNeeds6.getRow(0).sum()), "NS" + df(mtgNeeds6.getRow(1).sum()), "mtg=" + df(mtgNeeds6.curSum()), "bals=" + df(bals.curSum()), "<<<<<<<"));
        doNot = doNot.zero();
        if (preveHr) {
          emergHr = false;
        }
        if (preveHs) {
          emergHs = false;
        }
        prevn = n;
        fracN = n / eM.maxn[pors];
        double prevnFrac = fracN;
        nextN = 6.;
        // flags are no longer used, default just add 3
        if (true) {
          nextN = fracN + .5; //  
        }
        else /**
         * in case no more swaps can be done, change the flags by going to the
         * closest end of any existing flag, that flag will be turned off, and
         * swapping with another set of flags will be tried, until no more flag
         * end exist, or the end of swaps (eM.maxn[pors]) is reached. fracN is
         * the entering "n" fraction of maxn nextN is the proposed next "n"
         * fraction of maxn eM.maxn is the maximum swaps to be tried
         */
        if (fFlag && fracN < eM.fFrac[pors] && nextN > eM.fFrac[pors]) {
          prevNextN = nextN;
          nextN = eM.fFrac[pors];
          swapped = true;
          hist.add(new History(aPre, 7, "f raise n" + prevn + "to" + eM.maxn[pors] * nextN, ">>>>", (fFlag ? "f" : "!f") + whole(eM.maxn[pors] * eM.fFrac[pors]), (gfFlag ? "gf" : "!gf") + whole(eM.maxn[pors] * eM.gfFrac[pors]), (gFlag ? "g" : "!g") + whole(eM.maxn[pors] * eM.gFrac[pors]), (hFlag ? "h" : "!h") + whole(eM.maxn[pors] * eM.hFrac[pors]), (emergHr || emergHs ? "h" : "!he") + whole(eM.maxn[pors] * eM.heFrac[pors]), "max=" + whole(eM.maxn[pors]), "fracN=" + df(fracN), "prvNxtN=" + wh(eM.maxn[pors] * prevNextN))
          );
        }
        else if (hFlag && fracN < eM.hFrac[pors] && nextN > eM.hFrac[pors]) {
          prevNextN = nextN;
          nextN = eM.hFrac[pors];
          swapped = true;
          hist.add(new History(aPre, 7, "h raise n=" + prevn + "to" + eM.maxn[pors] * nextN, "fracN=" + wh(eM.maxn[pors] * fracN), "nxN=" + wh(eM.maxn[pors] * nextN), "pNN=" + wh(eM.maxn[pors] * prevNextN), (fFlag ? "f" : "!f") + df(eM.maxn[pors] * eM.fFrac[pors]), (gfFlag ? "gf" : "!gf") + df(eM.maxn[pors] * eM.gfFrac[pors]), (gFlag ? "g" : "!g") + df(eM.maxn[pors] * eM.gFrac[pors]), (hFlag ? "h" : "!h") + df(eM.maxn[pors] * eM.hFrac[pors]), (emergHr || emergHs ? "he" : "!he") + df(eM.maxn[pors] * eM.heFrac[pors]), "max=" + df(eM.maxn[pors])));

        }
        else if (hmFlag && fracN < eM.heFrac[pors] && nextN > eM.heFrac[pors]) {
          prevNextN = nextN;
          nextN = eM.heFrac[pors];
          swapped = true;
          hist.add(new History(aPre, 7, "he raise n=" + prevn + "to" + eM.maxn[pors] * nextN, "fracN=" + wh(eM.maxn[pors] * fracN), "nxN=" + wh(eM.maxn[pors] * nextN), "pNN=" + wh(eM.maxn[pors] * prevNextN), (fFlag ? "f" : "!f") + df(eM.maxn[pors] * eM.fFrac[pors]), (gfFlag ? "gf" : "!gf") + df(eM.maxn[pors] * eM.gfFrac[pors]), (gFlag ? "g" : "!g") + df(eM.maxn[pors] * eM.gFrac[pors]), (hFlag ? "h" : "!h") + df(eM.maxn[pors] * eM.hFrac[pors]), (emergHr || emergHs ? "he" : "!he") + df(eM.maxn[pors] * eM.heFrac[pors]), "max=" + df(eM.maxn[pors])));
        }
        else if (gfFlag && fracN < eM.gfFrac[pors] && nextN > eM.gfFrac[pors]) {
          prevNextN = nextN;
          nextN = eM.gfFrac[pors];
          swapped = true;
          hist.add(new History(aPre, 7, "gf raise n" + prevn + "to" + eM.maxn[pors] * nextN + ">>>>", (fFlag ? "f" : "!f") + whole(eM.maxn[pors] * eM.fFrac[pors]), (gfFlag ? "gf" : "!gf") + whole(eM.maxn[pors] * eM.gfFrac[pors]), (gFlag ? "g" : "!g") + whole(eM.maxn[pors] * eM.gFrac[pors]), (hFlag ? "h" : "!h") + whole(eM.maxn[pors] * eM.hFrac[pors]), (emergHr || emergHs ? "h" : "!he") + whole(eM.maxn[pors] * eM.heFrac[pors]), "fracN=" + df(fracN), "max=" + whole(eM.maxn[pors]), "prvNxtN=" + wh(eM.maxn[pors] * prevNextN)));
        }
        else if (gFlag && fracN < eM.gFrac[pors] && nextN > eM.gFrac[pors]) {
          prevNextN = nextN;
          nextN = eM.gFrac[pors];
          swapped = true;
          hist.add(new History(aPre, 7, "g raise n=" + prevn + "to" + eM.maxn[pors] * nextN, "fracN=" + wh(eM.maxn[pors] * fracN), "nxN=" + wh(eM.maxn[pors] * nextN), "pNN=" + wh(eM.maxn[pors] * prevNextN), (fFlag ? "f" : "!f") + df(eM.maxn[pors] * eM.fFrac[pors]), (gfFlag ? "gf" : "!gf") + df(eM.maxn[pors] * eM.gfFrac[pors]), (gFlag ? "g" : "!g") + df(eM.maxn[pors] * eM.gFrac[pors]), (hFlag ? "h" : "!h") + df(eM.maxn[pors] * eM.hFrac[pors]), (emergHr || emergHs ? "h" : "!he") + df(eM.maxn[pors] * eM.heFrac[pors]), "max=" + whole(eM.maxn[pors])));
        }
        else if (gmFlag && fracN < eM.gmFrac[pors] && nextN > eM.gmFrac[pors]) {
          prevNextN = nextN;
          nextN = eM.gmFrac[pors];
          swapped = true;
          hist.add(new History(aPre, 7, "gm raise n=" + prevn + "to" + eM.maxn[pors] * nextN, "fracN=" + wh(eM.maxn[pors] * fracN), "nxN=" + wh(eM.maxn[pors] * nextN), "pNN=" + wh(eM.maxn[pors] * prevNextN), (fFlag ? "f" : "!f") + df(eM.maxn[pors] * eM.fFrac[pors]), (gfFlag ? "gf" : "!gf") + df(eM.maxn[pors] * eM.gfFrac[pors]), (gFlag ? "g" : "!g") + df(eM.maxn[pors] * eM.gFrac[pors]), (hFlag ? "h" : "!h") + df(eM.maxn[pors] * eM.hFrac[pors]), (emergHr || emergHs ? "h" : "!he") + df(eM.maxn[pors] * eM.heFrac[pors]), "max=" + whole(eM.maxn[pors])));
        }
        else if (geFlag && fracN < eM.geFrac[pors] && nextN > eM.geFrac[pors]) {
          prevNextN = nextN;
          nextN = eM.geFrac[pors];
          swapped = true;
          hist.add(new History(aPre, 7, "ge raise n=" + prevn + "to" + eM.maxn[pors] * nextN, "fracN=" + wh(eM.maxn[pors] * fracN), "nxN=" + wh(eM.maxn[pors] * nextN), "pNN=" + wh(eM.maxn[pors] * prevNextN), "max=" + whole(eM.maxn[pors]), (gfFlag ? "gf" : "!gf") + df(eM.maxn[pors] * eM.gfFrac[pors]), (gFlag ? "g" : "!g") + df(eM.maxn[pors] * eM.gFrac[pors]), (gmFlag ? "gm" : "!gm") + df(eM.maxn[pors] * eM.gmFrac[pors]), (geFlag ? "ge" : "!ge") + df(eM.maxn[pors] * eM.geFrac[pors]), (hFlag ? "h" : "!h") + df(eM.maxn[pors] * eM.hFrac[pors]), (hmFlag ? "hm" : "!hm") + df(eM.maxn[pors] * eM.hmFrac[pors]), (heFlag ? "he" : "!he") + df(eM.maxn[pors] * eM.heFrac[pors]), (emergHr || emergHs ? "h" : "!he") + df(eM.maxn[pors] * eM.heFrac[pors])));
        }

        E.myTest(nextN < prevnFrac, "attempt to reduce nextN=N=" + df(prevnFrac) + " to " + df(nextN));

        nextN = eM.maxn[pors] < nextN ? eM.maxn[pors] : nextN;
        n = (int) Math.ceil(eM.maxn[pors] * nextN);

      }
      if (prevn != n) {
        hist.add(new History(aPre, 4, ((n == prevn) ? swapped ? "swapped n" : "passd n" : isDoNots > 0 ? "raised n" : "doNot raise n=") + prevn + "to" + n + ">>>>", (gfFlag ? "gf" : "!gf") + df(eM.maxn[pors] * eM.gfFrac[pors]), (gFlag ? "g" : "!g") + df(eM.maxn[pors] * eM.gFrac[pors]), (gmFlag ? "gm" : "!gm") + df(eM.maxn[pors] * eM.gmFrac[pors]), (geFlag ? "ge" : "!ge") + df(eM.maxn[pors] * eM.geFrac[pors]), (hFlag ? "h" : "!h") + df(eM.maxn[pors] * eM.hFrac[pors]), (hmFlag ? "hm" : "!hm") + df(eM.maxn[pors] * eM.hmFrac[pors]), (heFlag ? "he" : "!he") + df(eM.maxn[pors] * eM.heFrac[pors]), "nextN=" + wh(eM.maxn[pors] * nextN), "fracN=" + wh(eM.maxn[pors] * fracN), "max=" + whole(eM.maxn[pors]), "isDoNot=" + isDoNots, "<<<<"));
      }
    }

    /**
     * loop to adjust sector balances for growth, or endYear health
     *
     * @param aPre pefix for hist entries
     * @param yphase phase of year
     * @param xitLoop
     */
    void doLoop(String aPre, yrphase yphase, HSwaps xitLoop) {
      lTitle = " " + name + " Swaps";
      histTitles(lTitle);
//      yinitCosts();  only in startYear
      swapped = true;
      decrCnt = 0;
      decrGain = 0;
      decrCost = 0;
      int prevn = n;
      ARow rcOld = bals.getRow(BALANCESIX + RCIX);
      ARow sgOld = bals.getRow(BALANCESIX + SGIX);
      double rcAve = rcOld.ave();
      double sgAve = sgOld.ave();
      ARow cOld = bals.getRow(BALANCESIX + CIX);
      ARow gOld = bals.getRow(BALANCESIX + GIX);
      ARow rOld = bals.getRow(BALANCESIX + RIX);
      ARow sOld = bals.getRow(BALANCESIX + SIX);
      double cPre, gPre, rPre, sPre, sgPre, rcPre;
      double cFrac = eM.startSwapsCFrac[pors][clan];
      double gFrac = eM.startSwapsGFrac[pors][clan];
      double rFrac = 1. - eM.startSwapsCFrac[pors][clan];
      double sFrac = 1. - eM.startSwapsGFrac[pors][clan];
      // move some reserve to working before swapping for growth
      balances.reSum();
      double climit = bals.getRow(BALANCESIX + RCIX).ave() * eM.tradeReservFrac[pors];
      double glimit = bals.getRow(BALANCESIX + SGIX).ave() * eM.tradeReservFrac[pors];
      double rcb = 0., rbal, sbal, cbal, gbal;
      double sgb = 0.;

      // move reserves from trades back to working, leave only tradReservfrac
      for (n = 0; n < E.lsecs - 1; n++) {
        rcb = bals.get(BALANCESIX + RCIX, n);
        rbal = bals.get(BALANCESIX + RIX, n);
        cbal = bals.get(BALANCESIX + CIX, n);
        sbal = bals.get(BALANCESIX + SIX, n);
        gbal = bals.get(BALANCESIX + GIX, n);
        mov = rcb * rFrac - rbal;
        E.myTest(cbal < NZERO, "cbal = %7.3f less than zero", cbal);
        mov = Math.min(mov, cbal * (1. - .0001));
        if (mov > PZERO) {
          c.putValue(balances,mov, n, n, r, 0, .0001);
        }
        hist.add(new History(aPre, 7, "c=>r" + n + "=" + df(mov), "r=" + df(rbal), df(bals.get(BALANCESIX + RIX, n)), "c=" + df(cbal), df(bals.get(BALANCESIX + CIX, n))));

        sgb = balances.get(BALANCESIX + SGIX, n);
        mov = sgb * sFrac - sbal;
        E.myTest(gbal < NZERO, "gbal = %7.2f less than zero", gbal);
        mov = Math.min(mov, gbal * (1. - .0001));
        if (mov > PZERO) {
          guests.putValue(balances,mov, n, n, staff, 0, .0001);
        }
        hist.add(new History(aPre, 7, "g=>s" + n + "=" + df(mov), "s=" + df(sbal), df(bals.get(BALANCESIX + SIX, n)), "g=" + df(gbal), df(bals.get(BALANCESIX + GIX, n))));
      }
      double nextN = 2.;
      bals.unzero("balances", BALANCESIX, 4);
      /**
       * reset swap values to their initial value
       */
      cmd = E.SwpCmd.NOT;
      n = 0;
      doNot.zero();
      done = false;
      nn = 0;
      unDo = reDo = 0;
      // now reset reserves for swaps

      // loop swaps till done or not swappet or maxn
      maxn = (int) eM.maxn[pors];
      for (n = 0; swapped && !done && n < maxn; n++, nn++) {

        //move to swaps
        //  yCalcCosts("C#", lightYearsTraveled, curGrowGoal, curMaintGoal);  //includes yinitN
        lTitle = " Swaps " + name;
        histTitles(lTitle);
        // get the old swap values but the new Cost values
        //  prevns[0].copyn(cur, n);

        balances.checkBalances(cur);
        yphase = yrphase.SWAPING;
        swapped = swaps("S&", lightYearsTraveled); // do possible swaps
        failed = !swapped;
        if (History.dl > 4) {
          StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
          hist.add(new History(aPre, 5, "after swap " + wh(a0.getLineNumber()), "n=" + n, (failed ? "failed" : "!!!failed"), (swapped ? "swapped" : "!!!swapped"), swapLoops + "=swapLoops", "$=" + df(sumTotWorth),"srcIx"+srcIx,"ixWRSrc" + ixWRSrc,"destIx=" + destIx, "<<<<<<<<<<<<<"));
        }
        swapResults(aPre);
        testForFailure();  // raise n to the next failed value

        preveHr = emergHr;  // may be unneeded
        preveHs = emergHs;
        // only have yCalcCosts before test before call to swaps
        //     yCalcCosts(aPre, lightYearsTraveled, curGrowGoal, curMaintGoal);  //includes yinitN
        //   xitLoop = xitCalcCosts;
      } // loop on n
      swapsN = n;

    } // end doLoop

    /**
     * compute Total Worth, and find difference with previous worths
     *
     * @param prevTs
     * @param difTs
     * @param newTs
     * @return totalWorth
     */
    /**
     * calculate the subAsset costs for a year
     *
     * @param balances the number of units of each SubAsset per sector
     * @param rawUnitGrowths input rawUnitGrowths of each subasset
     * @param rawGrowths result raw growths before fertility applied
     * @param invMEfficiency inverse of Maint Efficiency
     * @param invGEfficiency inverse of Growth Efficiency
     * @param ix SubAsset being invoked
     * @param tIx line in costs array for these costs
     * @param consumerReqMaintCosts10 result consumer required maint costs
     * @param nReqMaint result services required maint costs
     * @param consumerReqGrowthCosts10 result consumer required growth costs
     * @param nReqGrowth result service required growth costs
     * @param consumerMaintCosts10 result consumer maint costs
     * @param nMaint result service maint costs
     * @param mTravel1Yr result consumer 1 year travel costs
     * @param nTravel1Yr result services 1 year travel costs
     * @param travelYearsCosts result service travel costs
     * @param consumerGrowthCosts10 result consumer growth costs
     * @param nGrowth result service growth costs
     * @param swork uboyt staff work
     * @param yearsTraveled input years traveled
     */
    void calcRawCosts(A6Row balances, A6Row rawUnitGrowths, A6Row rawGrowths, A6Row invMEfficiency, A6Row invGEfficiency, int ix, int tIx, A10Row consumerReqMaintCosts10, A10Row nReqMaint, A10Row consumerReqGrowthCosts10, A10Row nReqGrowth, A10Row consumerMaintCosts10, A10Row nMaint, A10Row mTravel1Yr, A10Row nTravel1Yr, A10Row travelYearsCosts, A10Row consumerGrowthCosts10, A10Row nGrowth, ARow swork, double yearsTraveled) {  // Assets.CashFlow.calcRawCosts
      double t1, t2, t3, t4 = -999., t5, t6, t7, rawG;
      int rcorsg = (int) (ix / 2);
      Double d, d1, d2;
      /**
       * now loop through i = consumer aspect of financial sectors j is the
       * services section of the financial sectors In general we measure the
       * demands on the financial sector, against the resource availability of
       * the financial sector. This process also gathers year totals yj... of
       * service requirements. The health of an economy is how close the weakest
       * sector is to supplying the demands from the consumers. Random factors
       * change relationships each year. The fertility of an economy is measured
       * by how close the service financial sectors, meet the demands from all
       * consumer aspects.
       */
      if (n < 999 && (ix == 0 || ix == 2)) {
        hist.add(new History("#a", History.valuesMajor6, "s balance", balances.A[4]));
        hist.add(new History("#a", History.valuesMajor6, "nMaint i=" + i, nMaint.A[6]));
        hist.add(new History("#a", History.valuesMajor6, "consumerMaintCosts10 i=" + i, consumerMaintCosts10.A[6]));
      }
      // i loops across the consumers, get rawGrowths and rawG here
      for (i = 0; i < E.lsecs; i++) {
        ARow kMaint = new ARow();
        rawGrowths.getRow(2 + ix).set(i,
                sys[ix].rawGrowth.set(i, rawG = s.work.get(i)
                        * sys[ix].rawUnitGrowth.get(i)
                        * cRand(i + ix + 10)));

        // j loops across services that as a sum are used by consumers
        for (j = 0; j < E.lsecs; j++) {

          // calculate required maintenance, a requirement not a cost subtracted
          // the prospects calculate from this and must be positive for health
          // a negative required maintenance remainder bal -reqm means death
          t1 = balances.get(2 + ix, i) * cRand(i * E.lsecs + j) * E.maintRequired[pors][i][j] * eM.rs[0][0][pors][rcorsg]
                  * eM.ps[pors][ix]
                  * (tIx == 0 ? 1. : E.maintRequired[pors][tIx][i])
                  * invMEfficiency.get(ix + 2, i);
          // these values are all staff counts, converted from work counts by bal/swork
          d = swork.get(j);
          d = (d.isInfinite() || d.isNaN()) || d < E.PZERO ? E.UNZERO : d;
          t2 = balances.get(2 + ix, i) * cRand(i * E.lsecs + ix + 8 + j) * E.maintRequired[pors][i][j + E.lsecs] * eM.rs[0][1][pors][rcorsg] * eM.ps[pors][ix] * (tIx == 0 ? 1. : E.maintRequired[pors][tIx][i + E.lsecs]) * invMEfficiency.get(ix + 2, i) * balances.get(4, j) / d;
          // gather 7 service requests to i  (7 j values, service by i
          consumerReqMaintCosts10.add(2 + ix, i, t1);
          // consumerReqMaintCosts10.add(0, i, t1);  done by auto resum
          consumerReqMaintCosts10.add(6 + ix, i, t2);
          //  consumerReqMaintCosts10.add(1, i, t2);
          nReqMaint.add(2 + ix, j, t1);
          nReqMaint.add(6 + ix, j, t2);
          //  nReqMaint.add(0, j, t1);
          // nReqMaint.add(1, j, t2);

          // calculate requried Growth resources, calculates growth fraction
          // is not part of yearly costs.
          t1 = balances.get(2 + ix, i) * cRand(i * E.lsecs + ix + j) * E.resourceGrowthRequirementBySourcePerConsumer[pors][i][j] * eM.rs[1][0][pors][rcorsg] * eM.ps[pors][ix] * (tIx == 0 ? 1. : E.resourceGrowthRequirementBySourcePerConsumer[pors][tIx][i]) * invMEfficiency.get(ix + 2, i);
          // these values are all staff costs, converted from work counts by bal/swork
          t2 = balances.get(2 + ix, i) * cRand(i * E.lsecs + 8 + j) * E.resourceGrowthRequirementBySourcePerConsumer[pors][i][j + E.lsecs] * eM.rs[1][1][pors][rcorsg] * eM.ps[pors][ix] * (tIx == 0 ? 1. : E.resourceGrowthRequirementBySourcePerConsumer[pors][tIx][i + E.lsecs]) * invMEfficiency.get(ix + 2, i) * balances.get(4, j) / d;
          consumerReqGrowthCosts10.add(ix + 2, i, t1);
          consumerReqGrowthCosts10.add(ix + 6, i, t2);
          nReqGrowth.add(ix + 2, j, t1);
          nReqGrowth.add(ix + 6, j, t2);
          consumerReqGrowthCosts10.add(0, i, t1);
          consumerReqGrowthCosts10.add(1, i, t2);
          nReqGrowth.add(0, j, t1);
          nReqGrowth.add(1, j, t2);

          if (n < 5 && i == 6 && j == 6) {
            hist.add(new History("#b", History.valuesMajor6, "s balance", balances.A[4]));
            hist.add(new History("#b", History.valuesMajor6, "mRGrowthC6 i=" + i + " ix" + ix + " n" + n, consumerReqGrowthCosts10.A[6]));
            hist.add(new History("#b", History.valuesMajor6, "nRGrowth6 i=" + i, nReqGrowth.A[6]));
          }

          t1 = balances.get(2 + ix, i) * cRand(i * E.lsecs + ix + j + 31) * E.maintCost[pors][i][j] * eM.rs[2][0][pors][rcorsg] * eM.ps[pors][ix] * (tIx == 0 ? 1. : E.maintCost[pors][tIx][i]) * invMEfficiency.get(ix + 2, i);
          t4 = t2 = balances.get(2 + ix, i) * cRand(i * E.lsecs + ix + j + 41) * E.maintCost[pors][i][j + E.lsecs] * eM.rs[2][1][pors][rcorsg] * eM.ps[pors][ix] * (tIx == 0 ? 1. : E.maintCost[pors][tIx][i + E.lsecs]) * invMEfficiency.get(ix + 2, i) * balances.get(4, j) / d;

          consumerMaintCosts10.add(ix + 2, i, t1);
          consumerMaintCosts10.add(ix + 6, i, t2);
          nMaint.add(ix + 2, j, t1);
          nMaint.add(ix + 6, j, t2);
          //    consumerMaintCosts10.add(0, i, t1);
          //   consumerMaintCosts10.add(1, i, t2);
          //    nMaint.add(0, j, t1);
          //    nMaint.add(1, j, t2);
          kMaint.add(j, t2);
          if (n < 5 && i == 6 && j == 6) {
            hist.add(new History("#c", History.valuesMajor6, "nM i=" + i + " ix" + ix + " n" + n, nMaint.A[6]));
            hist.add(new History("#c", History.valuesMajor6, "mM v=" + df(t2), consumerMaintCosts10.A[6]));
            hist.add(new History("#c", History.valuesMajor6, "kM i=" + i + " j=" + j, kMaint));
          }

          t1 = balances.get(2 + ix, i) * cRand(i * E.lsecs + ix + j + 46) * tCosts[pors][i][j] * eM.rs[3][0][pors][rcorsg] * eM.ps[pors][ix] * (tIx == 0 ? 1. : tCosts[pors][tIx][i]) * invMEfficiency.get(ix + 2, i);
          if ((t7 = swork.get(j)) < PZERO) {
            t2 = 0.0;
          }
          else {
            t2 = balances.get(2 + ix, i) * cRand(i * E.lsecs + ix + j + 55) * tCosts[pors][i][j + E.lsecs] * eM.rs[3][1][pors][rcorsg] * eM.ps[pors][ix] * (tIx == 0 ? 1. : tCosts[pors][tIx][i + E.lsecs]) * invMEfficiency.get(ix + 2, i) * balances.get(4, j) / d;
            //    d = t2;
            //   E.myTestDouble(d,"t2","calcRawCosts process ix=%d, i=%d,j=%d,swork=%7.2f,t2=%7.5f, d string=%s",ix,i,j,t7,t2,String.valueOf(d));
          }
          mTravel1Yr.add(ix + 2, i, t1);
          mTravel1Yr.add(ix + 6, i, t2);
          nTravel1Yr.add(ix + 2, j, t1);
          nTravel1Yr.add(ix + 6, j, t2);
          //   mTravel1Yr.add(0, i, t1);
          //   mTravel1Yr.add(1, i, t2);
          //  nTravel1Yr.add(0, j, t1);
          // nTravel1Yr.add(1, j, t2);
          if (n < -5 && ix == 0) {
            hist.add(new History("#d", History.valuesMajor6, "nM i=" + i + " " + df(t4), nMaint.A[6]));
            hist.add(new History("#d", History.valuesMajor6, "mM i=" + i, consumerMaintCosts10.A[6]));
            hist.add(new History("#d", History.valuesMajor6, "lYT=" + df(lightYearsTraveled), nTravel1Yr.A[6]));
          }

          t1 = rawG * gCosts[pors][i][j] * eM.rs[4][0][pors][rcorsg] * eM.ps[pors][ix] * (tIx == 0 ? 1. : gCosts[pors][tIx][i]) * invGEfficiency.get(ix + 2, i);
          t2 = rawG * gCosts[pors][i][j + E.lsecs] * eM.rs[4][1][pors][rcorsg] * eM.ps[pors][ix] * (tIx == 0 ? 1. : gCosts[pors][tIx][i + E.lsecs]) * invGEfficiency.get(ix + 2, i) * balances.get(4, j) / d;

          consumerGrowthCosts10.add(ix + 2, i, t1);
          consumerGrowthCosts10.add(ix + 6, i, t2);
          nGrowth.add(ix + 2, j, t1);
          nGrowth.add(ix + 6, j, t2);
          //       consumerGrowthCosts10.add(0, i, t1);
          //      consumerGrowthCosts10.add(1, i, t2);
          //      nGrowth.add(0, j, t1);
          //     nGrowth.add(1, j, t2);

        } // end j

      } // end i  in SubAsset
      travelYearsCosts.setAmultV(nTravel1Yr, yearsTraveled);
    }  // end calcRawCosts

    /**
     * calculate yearly costs at the cur AssetsYear level all changes are taken
     * from SubAssets added together and left in appropriate working SubAssets
     * variable (resource and staff) all costs are applied to working members,
     * but may involve the partners 9/2/15 convert all staff "work unit" costs
     * to "balance unit" costs immediately after summing them. All remnants etc
     * are in terms of "balance unit" costs.
     *
     * @param aPre prefix for log(History) entries
     * @param lightYearsTraveled ship:distance traveled, planet: plaines,trains
     * etc
     * @param curMaintGoal goal for maintenance
     * @param curGrowthGoal goal for growth
     */
    void yCalcCosts(String aPre, double lightYearsTraveled, double curMaintGoal, double curGrowthGoal) {  //CashFlow
      int mcnt = E.msgcnt;
      bals.unzero("bals", BALANCESIX, 4);
      E.msgcnt = mcnt;
      bals.unzero("growth", GROWTHSIX, 4);
      E.msgcnt = mcnt;
      rawGrowthCosts.unzero("rawGC", 2, 4);
      E.msgcnt = mcnt;
      rawGrowths.unzero("rawG", 2, 4);
      E.msgcnt = mcnt;
      yCalcRawCosts(lightYearsTraveled, aPre, curMaintGoal, curGrowthGoal);
      //     xitCalcCosts = hcopyn(cur);
    } // trade.Assets.CashFlow.yCalcCosts

    /**
     * CashFlow variables created in yCalcRawCosts
     *
     */
    /**
     * calculate the Raw Server costs for Maintenance and Growtb Requirements,
     * Maintenance and Travel costs increased by any health penalty full growth
     * costs, These are all costs against the elements as servers, and do not
     * separate whether costs came from resource or staff consumers.
     *
     * @task call the SubAssets CalcRawCosts
     * @Task reaggregate SubAsseets Costs using A6Row variables and
     * constructions
     * @task use the A6Row setNeeds to calculate, health, rawHealths,
     * rawFertililties, growths, growthCosts, mtgCosts and needs setNeeds
     * derives the variables for both swaps, and costsAndGrowth YSwaps is called
     * in method yearEnd
     *
     * yCalcCosts is also called from Assets.CashFlow.Trade to set up the
     * initial offer for ether seekPlanet, or barter
     *
     * The swaps attempt to raise the server poductivity, increasing health and
     * fertility, and the actual growth of both resources and staff. All of
     * these costs are subject to random multipliers which are different for
     * each year Thus the tactics that worked for one year, may not work for
     * another year. The challenge of how large growth is for resources and
     * staff is left for after the swaps, assuming that the better server
     * productivity creates more growth. Because of the random factors the
     * function between server productivity and growth is complex and not
     * necessarily the best possible.
     *
     * @see exit costs and growth values
     * @see s.hptMTGCosts. exit cost maint, trav, growth against staff/guests
     * balance
     *
     * @param lightYearsTraveled
     */
    void yCalcRawCosts(double lightYearsTraveled, String aPre, double curMaintGoal, double curGrowthGoal) {  //CashFlow.yCalcRawCosts
      double t1, t2, t3, t4, t5, t6;
      // zero output objects
      reqMaintCosts = makeZero(reqMaintCosts, "reqGCosts");
      reqGrowthCosts = makeZero(reqGrowthCosts, "reqGCosts");
      maintCosts = makeZero(maintCosts, "maintCosts");
      travelCosts = makeZero(travelCosts, "travelCosts");
      rawGrowthCosts = makeZero(rawGrowthCosts, "rawGCosts");
      maintCosts10 = makeZero(maintCosts10, "mCosts10");
      travelCosts10 = makeZero(travelCosts10, "tCosts10");
      rawGrowthCosts10 = makeZero(rawGrowthCosts10, "rawGCosts10");
      reqMaintCosts10 = makeZero(reqMaintCosts10, "reqMCosts10");
      reqGrowthCosts10 = makeZero(reqGrowthCosts10, "reqGCosts10");
      rawFertilities2 = makeZero(rawFertilities2, "rawFertilities2");
      rawProspects2 = makeZero(rawProspects2, "rawProspects2");
      //  rawHealths2 = makeZero(rawHealths2, "rawHealths2");
      mtggRawProspects2 = makeZero(mtggRawProspects2, " mtggRawProspects2");
      mtggRawFertilities2 = makeZero(mtggRawFertilities2, "mtggRawF2");
      mtggRawHealths2 = makeZero(mtggRawHealths2, "mtggRawH2");
      mtggNeeds6 = makeZero(mtggNeeds6, "mtggNeeds6");
      mtGNeeds6 = makeZero(mtGNeeds6, "mtGNeeds6");
      mtNeeds6 = makeZero(mtNeeds6, "mtNeeds6");
      mtgCosts10 = makeZero(mtgCosts10, "mtgCosts10");
      mtCosts10 = makeZero(mtCosts10, "mtCosts10");
      mtggCosts10 = makeZero(mtggCosts10, " mtggCosts10");
      growthCosts10 = makeZero(growthCosts10, "growthCosts10");
      growths10 = makeZero(growths10, "growths10");
      mtggGrowths6 = makeZero(mtggGrowths6, "mtggGrowths6");
      mtgNeeds6 = makeZero(mtgNeeds6, "mtgNeeds6");
      goalmtg1Needs6 = makeZero(goalmtg1Needs6, "goalmtg1Needs6");
      goalmtg1Neg10 = makeZero(goalmtg1Neg10, "goalmtg1Neg10");
      consumerReqMaintCosts10 = makeZero(consumerReqMaintCosts10, "consReqMCosts");
      consumerReqGrowthCosts10 = makeZero(consumerReqGrowthCosts10, "consReqGCosts");
      consumerMaintCosts10 = makeZero(consumerMaintCosts10, "consMCosts");
      consumerTrav1YrCosts10 = makeZero(consumerTrav1YrCosts10, "consT1Costs");
      consumerTravelCosts10 = makeZero(consumerTravelCosts10, "consTCosts");
      consumerRawGrowthCosts10 = makeZero(consumerRawGrowthCosts10, "consRawGCosts");
      consumerHealthMTGCosts10 = makeZero(consumerHealthMTGCosts10, "consHMTGCosts");
      consumerFertilityMTGCosts10 = makeZero(consumerFertilityMTGCosts10, "consFMTGCosts");
      consumerHealthEMTGCosts10 = makeZero(consumerHealthEMTGCosts10, "consEHMTGCosts");
      consumerFertilityEMTGCosts10 = makeZero(consumerFertilityEMTGCosts10, "consEFMTGCosts");

// gather the input for the SubAsset calcRawCosts
      growths.sendHist(hist, "C@");
      hist.add(new History("C@", History.valuesMajor6, "r.growth", r.growth));
      //   if (r.growth != growths.A[2]) {        
      //  E.myTest(true, "r.growth not the same as growths.A[2]");
      //    }
      staff.checkSumGrades();
      guests.checkSumGrades();

      travelCosts.zero();

      // instantiate other objects we may want to list
      A10Row nTrav1Yr = new A10Row(7, "nTrav1Yr");
      int defeat789 = 1; // 1 to not defeat
      //   growths.sendHist(hist, "C@");
      //   hist.add(new History("C@", History.valuesMajor6, "r.growth", r.growth));
      //  if (r.growth != growths.A[2]) {
      //     E.myTest(true, "r.growth not the same as growths.A[0]");
      //   }
      lTitle = "calcRawCosts";
      histTitles(lTitle);
      //    lightYearsTraveled = newTradeYear2? 0.9:lightYearsTraveled;
      calcRawCosts(balances, rawUnitGrowths, rawGrowths, invMEff, invGEff, 0, 0, consumerReqMaintCosts10, reqMaintCosts10, consumerReqGrowthCosts10, reqGrowthCosts10, consumerMaintCosts10, maintCosts10, consumerTrav1YrCosts10, nTrav1Yr, travelCosts10, consumerRawGrowthCosts10, rawGrowthCosts10, s.work, lightYearsTraveled);
      calcRawCosts(balances, rawUnitGrowths, rawGrowths, invMEff, invGEff, 1, 9 * defeat789, consumerReqMaintCosts10, reqMaintCosts10, consumerReqGrowthCosts10, reqGrowthCosts10, consumerMaintCosts10, maintCosts10, consumerTrav1YrCosts10, nTrav1Yr, travelCosts10, consumerRawGrowthCosts10, rawGrowthCosts10, s.work, lightYearsTraveled);
      calcRawCosts(balances, rawUnitGrowths, rawGrowths, invMEff, invGEff, 2, 7 * defeat789, consumerReqMaintCosts10, reqMaintCosts10, consumerReqGrowthCosts10, reqGrowthCosts10, consumerMaintCosts10, maintCosts10, consumerTrav1YrCosts10, nTrav1Yr, travelCosts10, consumerRawGrowthCosts10, rawGrowthCosts10, s.work, lightYearsTraveled);
      calcRawCosts(balances, rawUnitGrowths, rawGrowths, invMEff, invGEff, 3, 8 * defeat789, consumerReqMaintCosts10, reqMaintCosts10, consumerReqGrowthCosts10, reqGrowthCosts10, consumerMaintCosts10, maintCosts10, consumerTrav1YrCosts10, nTrav1Yr, travelCosts10, consumerRawGrowthCosts10, rawGrowthCosts10, s.work, lightYearsTraveled);
      lTitle = "yCalcRawCosts";
      histTitles(lTitle);

      //   travelCosts10.setAmultV(nTrav1Yr, lightYearsTraveled);
      consumerTravelCosts10.setAmultV(consumerTrav1YrCosts10, lightYearsTraveled);
      int blev = History.debuggingMinor11;
      int alev = History.debuggingMajor10;
      if ((pors == E.S) && newTradeYear2) {
        maintCosts10 = bals.getMrows();
        travelCosts10 = bals.getTrows();

      }
      /**
       * the resource server costs include costs from resources,cargo,staff and
       * guests. The staff servers costs include staff charges for
       * resource,cargo,staff and guest. cargo costs are included above in
       * resource, guests included above in staff
       */
      if (n < 2 && ec.age < 2 && History.dl > History.debuggingMinor11) {
        alev = History.valuesMinor7;
        blev = alev + 1;
        lTitle = "initResource";
        histTitles(lTitle);
        hist.add(new History("c$", 20, "postCosts", "0Life", "1Struct", "2Energy", "3Propel", "4Defense", "5Gov", "6Col", "Min", "Sum", "Ave"));
        //       reqMaintCosts10.addJointCosts();
        //       consumerReqMaintCosts10.addJointCosts();
        //      maintCosts10.addJointCosts();
        //      consumerMaintCosts10.addJointCosts();
        //      rawGrowthCosts10.addJointCosts();

        reqMaintCosts10.sendHist(blev, alev, aPre, "reqMCsts10");
        consumerReqMaintCosts10.sendHist(blev, alev, aPre, "consReqMCosts");
        reqGrowthCosts10.sendHist(blev, alev, aPre, "reqGCsts10");
        consumerReqGrowthCosts10.sendHist(hist, aPre);
        maintCosts10.sendHist(blev, alev, aPre, "maintCosts10");
        consumerMaintCosts10.sendHist(blev, alev, aPre, "mMaintC");
        hist.add(new History("##", alev, "lightYTrav=", df(lightYearsTraveled), "sum T Cost=", df(travelCosts10.curSum()), "<<<<<<<"));
        travelCosts10.sendHist(blev, alev, aPre, "TravCosts10");
        rawGrowthCosts10.sendHist(blev, alev, aPre, "rawGCosts10");
        consumerRawGrowthCosts10.sendHist(blev, alev, aPre, "mGCosts10");
        invMEff.sendHist(blev, aPre, alev, "invMEff");
        invGEff.sendHist(blev, aPre, alev, "invGEff");
      }
      //   reqGrowthCosts10.sendPercent8(hist, blev, alev, aPre, "percents", balances, rawGrowths, reqMaintCosts10, reqGrowthCosts10, maintCosts10, travelCosts10, rawGrowthCosts10, growthCosts10, mtgCosts10);}
      // rebuild and zero mtgEmergNeeds
      //  mtgEmergNeeds = new A6Row(History.valuesMajor6, "mtgENeeds");
      //  mtgEmergNeeds.setNeeds(hist, History.valuesMajor6, balances, maintCosts, travelCosts, rawGrowthCosts, rawGrowths, reqMaintCosts, reqGrowthCosts, rawFertilities, fertilities, rawHealths, mtgCosts, growthCosts, growths, heResults, eM.emergHealth[pors][clan], eM.emergGrowth[pors][clan], 0., 0.);
      //     mtgEmergNeeds.setNeeds10c(hist, History.valuesMajor6, balances, maintCosts10, travelCosts10, rawGrowthCosts10, rawGrowths, reqMaintCosts10, reqGrowthCosts10, consumerReqMaintCosts10, consumerReqGrowthCosts10,consumerMaintCosts10,consumerTravelCosts10,consumerRawGrowthCosts10,consumerHealthEMTGCosts10,consumerFertilityEMTGCosts10,rawFertilities2,rawProspects2,rawHealths2,mtgCosts10,growthCosts10, growths, mtgResults, eM.emergHealth[pors][clan], eM.emergGrowth[pors][clan], 0., 0.);
      growths.sendHist(hist, "C@");
      hist.add(new History("C@", History.valuesMajor6, "r.growth", r.growth));
      //   if (r.growth != growths.A[2]) {
      //      E.myTest(true, "r.growth not the same as growths.A[0]");
      //    }
      //   mtggNeeds.setNeeds10(hist, History.valuesMinor7, balances, maintCosts10, travelCosts10, rawGrowthCosts10, rawGrowths, reqMaintCosts10, reqGrowthCosts10, rawFertilities2, rawProspects2, rawHealths2, mtgCosts10, growthCosts10, growths10, heResults, eM.emergHealth[pors][clan], eM.emergGrowth[pors][clan], eM.futGrowthFrac[pors][clan], eM.futGrowthYrMult[pors][clan]);
      //  hist.add(new History(aPre, History.loopMinorConditionals5, "mtgGoalNeeds", "use goals to calculate needs"));

      // mtgGoalNeeds.setNeeds(hist, History.valuesMajor6, balances, maintCosts, travelCosts, rawGrowthCosts, rawGrowths, reqMaintCosts, reqGrowthCosts, rawGoalFertilities, fertilities, rawGoalHealths, mtgGoalCosts, growthCosts, growths, hgResults, curMaintGoal, curGrowthGoal, 0., 0.);
      //     hist.add(new History(aPre, History.loopMinorConditionals5, yphase.name(), "n=" + n, df(eM.futGrowthYrMult[pors][clan]), df(eM.futGrowthFrac[pors][clan]), "now mtggNeeds6", "with goals, multYear and growYears"));
      //   mtggNeeds.setNeeds(hist, History.valuesMajor6, balances, maintCosts, travelCosts, rawGrowthCosts, rawGrowths, reqMaintCosts, reqGrowthCosts, rawMTGGFertilities, fertilities, rawMTGGHealths, mtgCosts, growthCosts, growths, mtggResults, -999., -999., eM.futGrowthFrac[pors][clan], eM.futGrowthYrMult[pors][clan]);
      //     mtggNeeds.setNeeds10(hist, History.valuesMinor7, balances, maintCosts10, travelCosts10, rawGrowthCosts10, rawGrowths, reqMaintCosts10, reqGrowthCosts10, rawMTGGFertilities2, rawProspects2, rawMTGGHealths2, mtgCosts10, growthCosts10, growths10, mtggResults, curMaintGoal, curGrowthGoal, eM.futGrowthFrac[pors][clan], eM.futGrowthYrMult[pors][clan]);
      double mtgResults10[] = new double[40];
      mtgNeeds6 = getNeeds("mtgNeeds6", "goals and  nyear,fracGrowth", yphase, n, n < 2 && !ec.clearHist() ? History.debuggingMinor11 : History.loopMinorConditionals5, bals, maintCosts10, travelCosts10, rawGrowthCosts10, rawGrowths, reqMaintCosts10, reqGrowthCosts10, rawFertilities2, rawProspects2, mtCosts10, growthCosts10, mtgCosts10, growths, curMaintGoal, curGrowthGoal, eM.futGrowthFrac[pors][clan], eM.futGrowthYrMult[pors][clan], mtggNeeds6, mtNeeds6, mtgAvails6, mtGNeeds6, goalmtg1Needs6, goalmtg1Neg10);
      //    hist.add(new History(aPre, History.loopMinorConditionals5, "mtgNeeds6", "set all the costs etc, poorHealthEfft, fertility, health"));
      // mtgNeeds6.setNeeds(hist, History.debuggingMinor11, balances, maintCosts, travelCosts, rawGrowthCosts, rawGrowths, reqMaintCosts, reqGrowthCosts, rawFertilities, fertilities, rawHealths, mtgCosts, growthCosts, growths, mtgResults, -999., -999., 0., 0.);
      histTitles("C@", "rtd yCalcRawCosts");
      growths.sendHist(hist, "C@");
      hist.add(new History("C@", History.valuesMajor6, "r.growth", r.growth));
      //    if (r.growth != growths.A[2]) {
      //      E.myTest(true, "r.growth not the same as growths.A[2]");
      //   }
      totNeeds = mtgNeeds6.curSum();
      hist.add(new History(aPre, History.loopMinorConditionals5, "mtgNeeds6", "set all the costs10 etc, poorHealthEfft, fertility, health"));

      //  A10Row mtgNeeds6,mtgCosts10,growthCosts10,mtgResults10;
      // A6Row mtgCosts10, growthCosts10, growths10;
      //  mtgNeeds6 = makeZero(mtgNeeds6, "mtgNeeds6");
      //    mtgNeeds6 = getNeeds("mtgNeeds6", "current needs, no goals, not future years of growth", yphase, n, n < 8 && ec.age < 2 && econCnt < 10 ? History.debuggingMinor11 : 2, bals, maintCosts10, travelCosts10, rawGrowthCosts10, rawGrowths, reqMaintCosts10, reqGrowthCosts10, rawFertilities2, rawProspects2, rawHealths2, mtgCosts10, growthCosts10, growths, -999., -999., 0., 0.);
      mNeeds.setMax(mtgNeeds6, mtggNeeds6); // largest need, smallest available
      growths.sendHist(hist, "C@");
      hist.add(new History("C@", History.valuesMajor6, "r.growth", r.growth));
      if (r.growth != growths.A[2]) {
        E.myTest(true, "r.growth not the same as growths.A[2]");
      }
      //   rawFertilities2 = makeZero(rawFertilities2, "rawFertilities2");
      //  rawFertilities2.set(rawFertilities2);
      // rawHealths2 = makeZero(rawHealths2, "rawHealths2");
      //   rawHealths2.set(rawHealths2);
      health = rawProspects2.curMin();
      //     poorHealthEffect = mtgResults[A6Row.resPoorHealtEffect];
      fertility = rawFertilities2.ave();
      minFert = rawFertilities2.min();
      minH = rawProspects2.min();
      bLev = Math.min(History.informationMinor9, History.dl);
      rlev = History.valuesMajor6;
      if (bLev < History.dl) {
        lev = rlev;
        ec.aPre = aPre = "c&";
        //  hist.add(new History(aPre, rlev, "reqMaintFractions", "mtgg=" + df(mtggResults[0]), "he=" + df(heResults[0]), "hg=" + df(hgResults[0]), "H=" + df(health), "fertility", "mtgg=" + df(mtggResults[2]), "fe=" + df(heResults[2]), "fg=" + df(hgResults[2]), "F=" + df(mtgResults[2]), df(eM.maintMinPriority)));
        hist.add(new History("c$", rlev, "health=", df(health), "fertility=", df(fertility)));
        balances.sendHist(bLev, aPre, rlev, "balances");
        mtggNeeds6.sendHist(bLev, aPre, rlev, "mtggN6");
        //     mtgEmergNeeds.sendHist(bLev, rlev, aPre, "emergN");
//        mtgGoalNeeds.sendHist(bLev, rlev, aPre, "goalN");
        mtgCosts10.sendHist(bLev, rlev, aPre, "mtgC10");
        mtgNeeds6.sendHist(bLev, aPre, rlev, "mtgNeeds");
        mtNeeds6.sendHist(blev, aPre, rlev, "mtNeeds");
        mtGNeeds6.sendHist(blev, aPre, rlev, "mtGNeeds6");
        if (n < 2 && !ec.clearHist()) {
          ec.lev = alev = rlev;
          ec.aPre = aPre = "&d";
          reqGrowthCosts10.sendPercent8(hist, blev, alev, aPre, "percents", balances, growths, reqMaintCosts10, reqGrowthCosts10, maintCosts10, travelCosts10, rawGrowthCosts10, growthCosts10, mtgCosts10);
          reqGrowthCosts10.sendPercent2(hist, blev, alev, aPre, "percents", balances, growths, reqMaintCosts10, reqGrowthCosts10, maintCosts10, travelCosts10, rawGrowthCosts10, growthCosts10, mtgCosts10);
        }

      }

      double tot = 0;
      ec.lev = rlev = History.informationMajor8;
      ec.aPre = aPre = "&e";
      histTitles(lTitle);
      sos = rawProspects2.min() < eM.sosTrigger[pors];
      int lev = History.informationMinor9;

      /* planet ship */
      // healths limits  rawProspects2  mtggRawProspects2
      double dAve = 1. / E.l2secs;  // inverse of 2Secs
      // boolean hGood = gfFlag || (gFlag && !(hFlag || heFlag) && rawProspects2.curMin() > .2);
      double[] hLim = {1.3, 1.25};
      double[] fLim = {1.25, 1.15};
      double[] hggLim = {1.15, 1.35};
      double[] fggLim = {1.25, 1.25};
      double[] nLimG = {-1.15, -1.15};
      double[] nLimGG = {-1.13, -1.13};
      // greater hmtgg greater Strat value
      emergHr = rawProspects2.getRow(0).min() < eM.mtgWEmergency[pors][clan];
      emergHs = rawProspects2.getRow(1).min() < eM.mtgWEmergency[pors][clan];
      hEmerg = emergency = emergHr || emergHs;
      if (yphase == yrphase.TRADE || yphase == yrphase.SEARCH) {// replace vals if TRADE
        //double[] hLim3 = {1.3, 1.3};
        double[] hLim3 = {1.3, 1.2};
        hLim = hLim3;
        // neg healths decreases effect
        //double[] hLim5 = {1.35, 1.35};
        double[] hLim5 = {1.35, 1.2};
        hggLim = hLim5;
        double[] hLim6 = {1.4, 1.5};
        // hmtggLim = hLim6;
        double[] fLim2 = {1.25, 1.15};
        fLim = fLim2;
        double[] fLim4 = {1.18, 1.15};
        fggLim = fLim4;
      }
      else if (eM.mtgWEmergency[pors][clan] > rawProspects2.min()) {
        //double[] hLim2 = {1.4, 1.4};
        double[] hLim2 = {1.4, 1.3};
        hLim = hLim2;
        // double[] fLim2 = {1.2, 1.2};
        double[] fLim2 = {1.01, 1.01};
        fLim = fLim2;
        // double[] hggLim2 = {3.3, 3.3};
        double[] hggLim2 = {1.01, 1.01};
        hggLim = hggLim2;
        //double[] fggLim2 = {1.6, 1.6};
        double[] fggLim2 = {1.01, 1.01};
        fggLim = fggLim2;
      }
      double[] gLim = {1.5, 1.5};
      if (!gFlag) {
        //double[] hLim2 = {1.15, 1.25};
        double[] hLim2 = {1.01, 1.01};
        gLim = hLim2;
      }

      double[] unitGrowLim = {1.17, 1.03};
      if (yphase == yrphase.TRADE) {
        double[] hLim3 = {1.2, 1.03};
        gLim = hLim3;
// reduce staff strategicValue for planet during trade

      }
      bLev = n < 2 && ec.age < 2 ? History.debuggingMinor11 : History.valuesMajor6;
      lev = History.valuesMinor7;
      ec.aPre = aPre = "&f";
      rawUnitGrowths = new A6Row().setUseBalances(History.valuesMajor6, "rawUGrowths", r.rawUnitGrowth, c.rawUnitGrowth, s.rawUnitGrowth, g.rawUnitGrowth);
      A2Row mtFrac = new A2Row(lev, "mtFrac").setFracAsubBdivByC(bals, mtCosts10, mtCosts10);

      // lower healths or growths => higher strategic value
      // more positive needs => higher strategic value
      A2Row stratHealths = new A2Row(bLev, "stratHealths").strategicRecipValBbyLim("stratHealths", rawProspects2, hLim[pors]);
      A2Row stratMT = new A2Row(lev, "stratMT").strategicRecipValBbyLim("stratMT", mtFrac, hLim[pors]);
      A2Row stratFertilities = new A2Row(lev, "stratFertilities").strategicRecipValBbyLim("stratFertilities", rawFertilities2, fLim[pors]);
      A2Row stratGNeeds = new A2Row(lev, "stratGNeeds").strategicRecipValBbyLim("stratGNeeds", mtgNeeds6, nLimG[pors]);
      A2Row stratGGNeeds = new A2Row(lev, "stratGGNeeds").strategicRecipValBbyLim("stratGGNeeds", mtggNeeds6, nLimGG[pors]);

      stratVarsHG = stratVarsHG.mult(stratHealths, stratFertilities, stratMT, stratGNeeds, stratGGNeeds);
      stratVars.set(stratVarsHG);
      // higher rawUnitGrowths, higher strategic value
      //    rawUnitGrowths.titl = "rawUGroths";
      //   rawUnitGrowths.sendHist(hist, bLev, aPre, "r rawUGrowth", "s rawUGrowth");
      //   A2Row stratUGrowths = new A2Row(bLev, "stratUGrowths").strategicRecipValBbyLim("stratUGrowths", rawUnitGrowths, unitGrowLim[pors]);
      // raise staff strategic value if Unit Growths are higher
      //  stratVars.getRow(1).mult(stratUGrowths.getRow(0)).mult(stratUGrowths.getRow(1));

      blev = 13;
      rawProspects2.sendHist(hist, bLev, aPre, lev, "r rawProspects2", "s rawProspects2");
      stratHealths.sendHist(hist, bLev, aPre, lev, "r stratHealths", "s stratHealths");
      rawFertilities2.sendHist(hist, bLev, aPre, lev, "r rawFerti2", "s rawFerti2");
      stratFertilities.sendHist(hist, bLev, aPre, lev, "r stratFertil", "s stratFertil");
      stratMT.sendHist(hist, bLev, aPre, lev, "r stratMT", "s stratMT");
      mtFrac.sendHist(hist, bLev, aPre, lev, "r mtFrac", "s mtFrac");
      mtgNeeds6.sendHist2(hist, bLev, aPre, lev, "RCmtgNeeds", "SGmtgNeeds");
      stratGNeeds.sendHist(hist, bLev, aPre, lev, "r stratGNeeds", "s stratGNeeds");
      mtggNeeds6.sendHist2(hist, bLev, aPre, lev, "RCmtggNeeds", "SGmtggNeeds");
      stratGGNeeds.sendHist(hist, bLev, aPre, lev, "r stratGGNeeds", "s stratGGNeeds");

      ec.aPre = aPre = "&g";
      //  balances.setLevel(ec.lev = History.valuesMajor6);
      //     balances.sendHist2(ec.lev, aPre);
      // rawFertilities2.sendHist(hist, bLev, aPre, lev, "r rawFertilities2", "s rawFertilities2");
      // stratGrowths.sendHist(hist, bLev, aPre, "r stratGrowths", "s stratGrowths");
      //  stratVarsHG.sendHist(hist, bLev, aPre, "r stratVarsHG", "s stratVarsHG");
      //  stratUGrowths.sendHist(hist, bLev, aPre, "r stratRUG", "s stratRUG");
      stratVars.sendHist(hist, bLev, aPre, "r stratVars", "s stratVars");
      bals.sendHist2(lev, aPre);
      // this is good for both trades and yearEnd (swaps)
      ec.blev = bLev = n < 2 && ec.age < 2 ? History.debuggingMinor11 : History.valuesMajor6;
      rawCW = new DoTotalWorths();
      sumTotWorth = rawCWTotWorth = rawCW.getTotWorth();
      NeedsPlusSum = mtgNeeds6.curPlusSum();
      NeedsNegSum = mtgNeeds6.curNegSum();
      rawProspectsMin = rawProspects2.min();
      rawProspectsMin2 = rawProspects2.min(2);
      rawProspectsNegSum = rawProspects2.negSum();

    } //CashFlow.yCalcRawCosts

    /**
     * Calculate the needs for each sector to either reach the goals, If no
     * goals are set, the implied goal is health +.01, fertility .01, the need
     * is what each sector needs so that all sectors are at or above the goal.
     * <P>
     * the parameters rawFertilities, rawHealth, rawProspects are fractions
     * related to the goals, any negative fraction means an unmet goal. The
     * fractions of rawHealth and rawFertility are calculated with the surplus
     * after the required amounts, so that rawHealth of .5 = (balance -
     * reqHealthCost)/reqHealthCost, %lt; 0 means no survival, %gt; 0 and %lt;
     * .5 is poor health with a cost penalty, %gt; 1 means super health with a
     * bonus against costs
     * <p>
     * The effective growth in units is calculated for each sector, the costs
     * for that growth is also calculated.
     * <p>
     * The costs are calculated separately
     *
     * @param title title of the returned file
     * @param description description of the purpose of the invocation
     * @param yphase the phase at call TRADE or GROWTH or pay
     * @param rawCostsN total raw costs
     * @param aDl adjustable Dl display level, no log=hist.adds above this
     * @param bals balances of SubAssets rc,sg,r,c,s,g, also growth for 4
     * SubAssets, plus other Assets leval ARow s
     * @param maintCosts input annual service costs of maintenance from
     * yCalcCosts
     * @param travelCosts input annual service travel costs: from yCalcCosts
     * @param rawGrowthCosts input cost of services to this sector for the
     * rawGrowths:
     * @param rawGrowths input growths before fertility is applied
     * @param reqMaintCosts input cost of services to this sector for
     * maintenence healths = (bal-rqM)/rqM
     * @param reqGrowthCosts input service cost of growth balances to ensure
     * possible growth fertilities = (bal - rqC)/rqC
     * @param rawFertilities2 output Min frac of required Growth&Maint and
     * growth before any growth limits
     * <ol start=0><li>rc<li>sg<li</ol>
     * @param rawProspects2 output each sector availW*14/(rcSum+sgSum)
     * @param rawHealths output SubAssets ??? mostly not used
     * @param mtNegs output costs of maint and travel with PHE
     * @param growthNegs output cost of growths
     * @param mtgNegs output SubAssets: the sum of maint,travel,growth costs
     * including needGoal output<br>
     * @param growths output amount of growths (also part of bals)
     * @param maintGoal input if &gt; 0 force health and maint calc costs
     * 1+maint goal
     * @param growthGoal input if &gt; 0 force fertility and growth cost
     * 1+growthGoal
     * @param growMult input if &gt; 0 and growYears &gt 0 mult mtgNegs & growth
     * for growYears -1 by growMult
     * @param growYears input if &gt; 0 and growYears &gt 0 mult mtgNegs &
     * growth for growYears -1 by growMult
     * @param goalmtgNeeds needs using goals and growYears, growMult
     * @param mtNeeds needs without costs and benefits of growth
     * @param mtgAvails6 -needs, available after costs and benefits of growth
     * @param goalmtNeeds goal needs without costs and benefits of growth
     * @param goalmtg1Needs needs with goal only 1 year, no growMult
     * @param goalmtg1Negs Negs for 1 year with goals, no growMult
     * @return mtgNeeds survivalNeeds only for r,s, c,g are 0 or less, rc=r+c,
     * sg=s+g
     */
    // Assets.CashFlow.getNeeds
    public A6Row getNeeds(String title, String description, Assets.yrphase yphase, int rawCostsN, int aDl, ABalRows bals, A10Row maintCosts, A10Row travelCosts, A10Row rawGrowthCosts, A6Row rawGrowths, A10Row reqMaintCosts, A10Row reqGrowthCosts, A2Row rawFertilities2, A2Row rawProspects2, A10Row mtNegs, A10Row growthNegs, A10Row mtgNegs, A6Row growths, double maintGoal, double growthGoal, double growMult, double growYears, A6Row goalmtgNeeds, A6Row mtNeeds, A6Row mtgAvails6, A6Row goalmtNeeds, A6Row goalmtg1Needs, A10Row goalmtg1Negs) {
      A6Row rtn = new A6Row(History.valuesMajor6, "needs");
      if (aDl > 3) {
        hist.add(new History("@n", History.valuesMinor7, title, ec.name + " >getNeeds", "phase=" + yphase.name() + ", " + description));
      }
      String aPre = "@n"; // local only
      ec.blev2 = aDl;

      if (maintCosts == null) {
        maintCosts = new A10Row(History.informationMinor9, "maintCosts");
      }

      if (rawGrowthCosts == null) {
        rawGrowthCosts = new A10Row(History.informationMinor9, "rawGrowthCosts");
      }
      rawGrowthCosts.setType(TCOST);
      if (rawGrowths == null) {
        growMult = 0.;
        growYears = 0.;
        rawGrowths = new A6Row(History.informationMinor9, "rawGrowths");
      }
      rawGrowths.setType(TBAL);
      if (mtgNegs == null) {
        mtgNegs = new A10Row(History.informationMinor9, "mtgNegs");
      }

      if (growthNegs == null) {
        growthNegs = new A10Row(History.valuesMajor6, "growthNegs");
      }
      double mtgMult = 1.;
      if (growYears > PZERO) {
        mtgMult = growYears;
      }
      goalmtgNeeds.zero();
      double gGoal = growthGoal > .01 ? growthGoal : .01;
      double mGoal = maintGoal > .01 ? maintGoal : .01;
      int pors = ec.getPors();
      int clan = ec.getClan();
      Double t1;
      double s1;
      aPre = "#a";
      if (ec.blev2 > 3) {
        histTitles(aPre, "in getNeeds");
        hist.add(new History(History.valuesMinor7, "enter getNeeds", "aDl=" + aDl,
                "maintGoal=", df(maintGoal), "growthGoal=", df(growthGoal), "growMult", df(growMult), "growYears", df(growYears)));
      }
      // ensure cost rows 0,1 are set correctly

      double minLimHealths = 999., minLimFertilities = 999.;

      // A10Row reqGCMore.set(reqMCMore.set(balsMore.set(mCMore.set(tCMore.set(gCMore = new A10Row())))));
      lev = History.valuesMajor6;
      int bLev = ec.blev;
      ec.aPre = aPre = "i#";

      A10Row rqGC = reqGrowthCosts;  // make another reference name
      A10Row rqMC = reqMaintCosts;
      A6Row rqGCRem = new A6Row(lev, "rqGCRem");//rem after force growthGoal
      A6Row rqMCRem = new A6Row(lev, "rqMCRem");//rem after maintGoal
      A2Row rqGFrac = new A2Row(lev, "rqGFrac");
      A2Row rqMFrac = new A2Row(lev, "rqMFrac");
      A6Row rqNeed = new A6Row(lev, "rqNeed");
      A2Row maddMC = new A2Row(lev, "maddMC");
      A2Row maddGC = new A2Row(lev, "maddGC");
      A2Row maddrqMC = new A2Row(lev, "maddrqMC");
      A2Row maddrqGC = new A2Row(lev, "maddrqGC");

      A6Row rqNeedGG = new A6Row(lev, "rqNeedGG");
      A6Row rqNeedGM = new A6Row(lev, "rqNeedGM");
      A2Row mrqMaxC = new A2Row(lev, "mrqMax");
      A10Row mAddC = new A10Row(lev, "mAddC"); // does .zero
      double dmrqMCFmin = 9999;
      A2Row mrqGCLimitedFrac = new A2Row(lev, "mrqGCLimFrac");
      A2Row mrqMCLimitedFrac = new A2Row(lev, "mrqMCLimFrac");

      //  A6Row effectiveFertilities = new A6Row(lev, "effFert");
      double mGrowthGoal = growthGoal > PZERO ? growthGoal : 1.;
      double mMaintGoal = maintGoal > PZERO ? maintGoal : 1.;
      // use the sum of resouce and staff costs to derive the 
      // health or fertility fraction (balance -cost)/cost s
      // divid the r,s balance in proportion to the costs being subtracted
      // calculate req Growth and Maint costs using growthGoal and MaintGo
      //    A10Row dmores = new A10Row(6,"dmores");
      double subMoreBals = 0.; // each SubAsset excess needs by maint or growth goal
      double submBalsSum = 0.; // each SubAsset sum of real bals
      // double subCostSum=0.; // each SubAsset sum of real costs;
      // if there are no goals, still use m... which holds the original values
      Double t2, t3, t4;
      for (int n : E.ASECS) {
        for (int m : IA01) {  // for rc and sg (bals-mcosts) - (bals-cost)
          // A6Row below
          rqGCRem.set(2 + 2 * m, n, bals.get(2 + 2 * m, n));  //r,s 
          rqMCRem.set(2 + 2 * m, n, bals.get(2 + 2 * m, n));
          rqGCRem.set(3 + 2 * m, n, bals.get(3 + 2 * m, n));// c,g
          rqMCRem.set(3 + 2 * m, n, bals.get(3 + 2 * m, n));
          rqGCRem.set(m, n, bals.get(2 + 2 * m, n) + bals.get(3 + 2 * m, n));// rc
          rqMCRem.set(m, n, bals.get(2 + 2 * m, n) + bals.get(3 + 2 * m, n)); //sg
          //calculate needs, the negative of available balances
          rqNeedGG.set(2 + 2 * m, n, -bals.get(2 + 2 * m, n)); // r, s
          rqNeedGM.set(2 + 2 * m, n, -bals.get(2 + 2 * m, n)); //r,s
          rqNeedGG.set(3 + 2 * m, n, -bals.get(3 + 2 * m, n)); // c,g
          rqNeedGM.set(3 + 2 * m, n, -bals.get(3 + 2 * m, n));

          // calculate remainders bal-required cost to find % cost 
          for (int mm : IA03) {
            // Remainders after subtrace units costs type A10Row
            // Note A10row subtracts from SG && RC
            rqGCRem.add(2 + 2 * m, n, -(rqGC.get(2 + 4 * m + mm, n))); //-sum r,s costs
            rqMCRem.add(2 + 2 * m, n, -(rqMC.get(2 + 4 * m + mm, n)));
            //rqGCRem.add(m, n, -(rqGC.get(2 + 4 * m + mm, n))); //-sum rc,sg costs
            //rqMCRem.add(m, n, -(rqMC.get(2 + 4 * m + mm, n)));
            // needs -bal + costs all units
            rqNeedGG.add(2 + 2 * m, n, (1. + mGrowthGoal) * rqGC.get(2 + 4 * m + mm, n));
            rqNeedGM.add(2 + 2 * m, n, (1. + mMaintGoal) * rqMC.get(2 + 4 * m + mm, n));
            //rqNeedGG.add(m, n, (1. + mGrowthGoal) * rqGC.get(2 + 4 * m + mm, n));
            //rqNeedGM.add(m, n, (1. + mMaintGoal) * rqMC.get(2 + 4 * m + mm, n));
          } // xit mm
          // set 0,1 rows
          rqGCRem.set(m, n, rqGCRem.get(2 + 2 * m, n) + rqGCRem.get(3 + 2 * m, n));// rc,sg
          rqMCRem.set(m, n, rqMCRem.get(2 + 2 * m, n) + rqMCRem.get(3 + 2 * m, n));
          rqNeedGG.set(m, n, rqNeedGG.get(2 + 2 * m, n) + rqNeedGG.get(3 + 2 * m, n));// rc,sg
          rqNeedGM.set(m, n, rqNeedGM.get(2 + 2 * m, n) + rqNeedGM.get(3 + 2 * m, n));
          // fracs just the 0,1 rows units/units
          //         E.myTest((t2=rqGC.get(m,n)) == 0.0 ||t2 == -0. ,"rqGC[%d][%d]=%7.2f zero",m,n,t2); 
          //       E.myTest((t2=rqMC.get(m,n)) == 0.0 ||t2 == -0.,"rqMC[%d][%d]=%7.2f zero",m,n,t2); 
          // decide that zero cost is legal, so just make results a very large Frac
          t4 = ((t3 = rqGC.get(m, n)) < E.PZERO) || t3.isInfinite() || t3.isNaN() ? E.UNZERO : t3; //r,s

          rqGFrac.set(m, n, rqGCRem.get(2 + 2 * m, n) / t4); //r,s
          t4 = (t3 = rqMC.get(m, n)) < E.PZERO || t3.isInfinite() || t3.isNaN() ? E.UNZERO : t3; //r,s
          rqMFrac.set(m, n, rqMCRem.get(2 + 2 * m, n) / t4);
        } // xit m
      } // xit n

      double minH = rqMFrac.min();
      //  poorHealthAveEffect = poorHealthEffect = PHE = eM.poorHealthPenalty[pors]
      // minH < 0 increases to -.5 == 2.5
      // minH < -.5 == 2.5, minH < 0. 2 + minH, minH == .5  to 1.3, 1. = 1.
      // minH ==2.= .7 
      // minH > 2 top benifit
      poorHealthAveEffect = poorHealthEffect =
      PHE = minH < 0.?2 - minH : minH < .5 ? 2. - minH * 2 *.7: minH < 1.? 1.7 + (minH-.5)* 2. *.3: minH <= 2.? 1. - (minH-1.) * .3 : 7.; 
                                           

      ec.blev2 = bLev = Math.min(History.debuggingMinor11, aDl);
      // now compute the effective reqhealth and reqfertility
      //   A6Row effectiveHealths = new A6Row(History.debuggingMinor11, "effHealths");
      ec.aPre = aPre = "#c";
      int alev = History.valuesMajor6;
      int alev2 = History.valuesMinor7;
      growths.titl = "growths";
      A10Row rawGC = rawGrowthCosts;
      A6Row rawG = rawGrowths;

      if (alev <= bLev) {
        hist.add(new History(aPre, History.loopMinorConditionals5, " values", "minMFrac", ec.mf(minH), "mGoal", ec.mf(mMaintGoal), "mGrowthGoal", ec.mf(growthGoal), "growthYrs", "" + growYears, "growthMult", ec.mf(growMult)));
        bals.sendHist(alev, aPre);
        rawGC.sendHist(blev, alev, aPre, "rawGC");
        rawG.sendHist(blev, aPre, alev, "rawG");
        //   mbals.sendHist24(bLev, aPre, alev, "r mbal", "s mbal");
        rqGC.sendHist(blev, alev, aPre, "rqGC");
        rqMC.sendHist(blev, alev, aPre, "rqMC");
        rqGCRem.sendHist(alev, aPre);
        rqMCRem.sendHist(alev, aPre);
        rqGFrac.sendHist(alev, aPre);
        rqMFrac.sendHist(alev, aPre);
        ec.aPre = aPre = "*C";
        rqNeedGG.sendHist(alev, aPre);
        rqNeedGM.sendHist(alev, aPre);
        rawGrowthCosts.sendHist(alev, aPre);
      }

      A10Row mtCosts10 = new A10Row(alev, "mtCosts10").setAdd(maintCosts, travelCosts);
      //  mtNegs.setAmultV(mtCosts10, PHE);  // output
      // apply the poor health penalty to mt costs
      A10Row pmtC = new A10Row(alev, "pmtC").setAmultV(mtCosts10, PHE);
      mtNegs.set(pmtC);
      A10Row pRawGC = new A10Row(alev, "pRawGC").setAmultV(rawGC, PHE);
      

      A6Row pRemMT = new A6Row(alev, "pRemMt");
      // (bals-mt) = remMt amount left for growth cost
      // remMt/gCost = mtgFraqc possible growth frac
      A2Row mtgFrac = new A2Row(alev, "mtgFrac").setFracAsubBdivByCnRem(bals, pmtC, pRawGC, pRemMT);
      // rawFertilities2 is the frac min of mtg frac, the the required fracs
      A2Row minFracs = rawFertilities2.setMin(mtgFrac, rqGFrac);
      // set limits on fertility
      A2Row minLFrac = new A2Row(alev, "minLFrac").setLimits(rawFertilities2, eM.minFertility[pors], eM.maxFertility[pors]);
      // now apply limited fertility to get actual growths
      growths = growths.setAmultF(rawGrowths, minLFrac);
      // from the actual growths get negs (costs)
      growthNegs = growthNegs.setAmultF(pRawGC, minLFrac);
      // now get total costs mt and growth
      mtgNegs.setAdd(pmtC, growthNegs);
      // finish the return value
      // now start needs6 calculation for C and G & R and S
      // recalc rawProspects using only working R & S
      // save the least remnant of bal - mtgNegs: rqMCRem:rqGCrem
      double balSum = bals.curSum();
      double tt1=0.,tt2=0., tt3=0.;
      for (int n = 0; n < LSECS; n++) {
        for (int m = 0; m < 2; m++) {
          tt1 = rqMCRem.get(m,n);
          tt2 = rqGCRem.get(m,n);
          tt3 = bals.get(2 + 2 * m, n) + growths.get(m, n) - mtgNegs.get(m, n); // +2 r, +4 s
          rtn.set(3 + 2 * m, n, -mtgAvails6.set(3 + 2 * m, n, bals.get(3 + 2 * m, n))); // +3 c, +5 g
          //now needs are -bal + negs(costs) - any additional growth
          // avails is the least remnant
          rtn.set(2 + 2 * m, n, -mtgAvails6.set(2 + 2 * m, n, tt1 < tt2? tt1<  tt3? tt1:tt3:tt2< tt3?tt2: tt3));
        }
      }

      for (int n = 0; n < LSECS; n++) {
        for (int m = 0; m < 2; m++) {
          rawProspects2.set(m, n, (mtgAvails6.get(m, n)) * 14 / balSum);
        }
      }
      /*
         double maintGoal, double growthGoal, double growMult, double growYears, A6Row goalmtgNeeds, A6Row mtNeeds, A6Row goalmtNeeds) {     
       */
      // =========== start process goal costs =================================
      // bals + needs = pmtC + (pRawGC -rawG) * growthGoal
      // needs = -bals + gYears*(pmtC + (pRawGC - gMult*rawG) * growthGoal)
      double gYears = growYears > 1. ? growYears : 1.;
      double gMult = growMult > .4 ? growMult : 1.;
      mtNegs.setAmultV(mtNegs, gYears);
      A6Row mtgGNeeds = new A6Row(alev, "mtgGNeeds");
      A6Row mtg1GNeeds = new A6Row(alev, "mtg1GNeeds");
      A2Row mRG = new A2Row(alev, "mRg");
      A2Row pRawGGC = new A2Row(alev, "pRawGGC");
      //  A6Row goalmtgNeed = goalNeeds;
      // now calculate goalmtNeeds and goalmtgNeeds
      // mGG = (gYear* (bal - (pMt + gmult*gGC))/gmult*gGC
      double na, nb, nc, nd;
      for (int n = 0; n < LSECS; n++) {
        for (int m = 0; m < 2; m++) {
          nb = rqNeedGG.get(2 + 2 * m, n); // required Growth Need
          nc = rqNeedGM.get(2 + 2 * m, n);  // required Maint Need
          //r,s needs total g costs - r,s g growths
          mtgGNeeds.set(2 + 2 * m, n, na = -bals.get(2 + 2 * m, n) + gYears * (pmtC.get(m, n) + (mGrowthGoal * gMult * (pRawGC.get(m, n) - rawG.get(2 + 2 * m, n)))));
          goalmtNeeds.set(2 + 2 * m, n, -bals.get(2 + 2 * m, n) + gYears * (pmtC.get(m, n)));
          goalmtNeeds.set(3 + 2 * m, n, -bals.get(3 + 2 * m, n) + gYears * (pmtC.get(m, n)));
          mtg1GNeeds.set(2 + 2 * m, n, nd = -bals.get(2 + 2 * m, n) + (pmtC.get(m, n) + (mGrowthGoal * gMult * (pRawGC.get(m, n) - rawG.get(2 + 2 * m, n)))));
          goalmtg1Negs.set(2 + 2 * m, n, -bals.get(2 + 2 * m, n) + (pmtC.get(m, n) + mGrowthGoal * pRawGC.get(m, n)));
          goalmtgNeeds.set(2 + 2 * m, n, na > nb ? na > nc ? na : nc : nb > nc ? nb : nc);
          goalmtg1Needs.set(2 + 2 * m, n, nd > nb ? nd > nc ? nd : nc : nb > nc ? nb : nc);
          nd = -bals.get(3 + 2 * m, n);
          goalmtg1Needs.set(3 + 2 * m, n, -bals.get(3 + 2 * m, n) - (mGrowthGoal * gMult * rawG.get(3 + 2 * m, n)));
          mtgGNeeds.set(3 + 2 * m, n, na = -bals.get(3 + 2 * m, n) - (gYears * mGrowthGoal * gMult * rawG.get(3 + 2 * m, n)));
          goalmtgNeeds.set(3 + 2 * m, n, na > nb ? na > nc ? na : nc : nb > nc ? nb : nc);
          na = -bals.get(2 + 2 * m, n) + gYears * (pmtC.get(m, n));
          goalmtNeeds.set(2 + 2 * m, n, na > nb ? na > nc ? na : nc : nb > nc ? nb : nc);
          na = -bals.get(3 + 2 * m, n);
          goalmtNeeds.set(3 + 2 * m, n, na > nb ? na > nc ? na : nc : nb > nc ? nb : nc);
        }
      }
      A6Row goalGG = new A6Row(alev, "goalGG").setAmultV(rawGrowths, mGrowthGoal);

      // A10Row mmtRemnants = new A10Row(alev2, "mmtRemnants");
      //  A2Row mmtgFertilities = new A2Row(alev2, "mmtgFert"); // before min with reqFertility
      // mtggCosts sum of (maint,travel,growth costs)*phe*growYrs
      // - growth*growMult*growYears
      ec.aPre = aPre = "#d";
      if (alev <= bLev) {
        pmtC.sendHist(hist, blev, aPre, alev, "pmtC");
        pRawGC.sendHist(blev, aPre, alev, "pRawGC");
        pRemMT.sendHist(blev, aPre, alev, "pRemMT");
        mtgFrac.sendHist(alev, aPre);
        minFracs.sendHist(alev, aPre);
        minLFrac.sendHist(alev, aPre);
        rawGrowths.sendHist(bLev, aPre, alev, "rawGrowths");
        growths.sendHist(alev, aPre);
        growthNegs.sendHist(alev, aPre);
        mtgNegs.sendHist(alev, aPre);
        rqNeedGG.sendHist(alev, aPre);
        rqNeedGM.sendHist(alev, aPre);
        goalmtgNeeds.sendHist(alev, aPre);
        goalmtNeeds.sendHist(alev, aPre);
        // goalNeeds.sendHist(alev, aPre);
        // rawProspects.sendHist(alev,aPre);
      }
      //  bLev = aDl;
      lev = alev = History.valuesMajor6;
      //    lev = alev = 5;

      hist.add(new History(aPre, History.valuesMajor6, " PHE=" + ec.mf(poorHealthEffect), "gy=" + ec.df(gYears), "gm=" + ec.df(growMult), "maintGoal=", ec.df(maintGoal), "mGrowthGoal=", ec.df(mGrowthGoal), "<<<<<<<<<<"));

      growths.sendHist(hist, aPre);
      growthNegs.sendHist(hist, bLev, aPre, alev, "growthNegs");
      //   rawFRem.sendHist01(bLev, aPre, lev, " rc rawFRem", " sg rawFRem");
      //  rawF.sendHist(hist, bLev, aPre, lev, " rc rawF", " sg rawF");
      mtgNegs.sendHist(hist, bLev, aPre, lev, "mtgNegs");

      // mtgNegs.sendHist(alev, aPre);
      //   j6Remnants.titl = "j6Remnants";
      // now set the result = needs,  needed>0, available = -this;
      alev = History.loopMinorConditionals5;
      rtn.blev = aDl;   // set this blev
      aPre = "n#";

      lev = alev;  // set this level
      if (aDl > 3 || true) {
        hist.add(new History(History.valuesMinor7, "xit getNeeds", "aDl=" + aDl,
                "mainGoal=", df(maintGoal), "growthGoal=", df(growthGoal), "growMult", df(growMult), "growYears", df(growYears)));

        hist.add(new History(aPre, History.loopMinorConditionals5, " health=" + df(rawProspects2.min()), "phe=" + df(poorHealthEffect), "F=" + df(rawFertilities2.min()), "sumB=" + df(rtn.curSum()), "sumR=" + df(rtn.getRow(2).sum()), "sumC=" + df(rtn.getRow(3).sum()), "sumS=" + df(rtn.getRow(4).sum()), "sumG=" + df(rtn.getRow(5).sum()), "<<<<"));
        //  rawHealths.sendHist(hist, bLev, aPre, alev, "rc rawHealths", "sg rawHealths");
        rawProspects2.sendHist(hist, bLev, aPre, alev, "rrawProspects2", "srawProspects2");
        rawFertilities2.sendHist(hist, bLev, aPre, alev, "r rawFertilities2", "s rawFertilities2");
        mtggNeeds6.sendHist2(History.valuesMajor6, aPre);
        bals.listBalances(aDl, aPre, alev);
        rtn.sendHist(History.dl, "x#", History.valuesMajor6, "needs");
      }
      return rtn;

    }  // xit getNeeds

    int swapXtra = 0;

    /**
     * utility tool to generate name with resource type and index
     *
     * @param aname first part of name
     * @param pNq the array of resource type
     * @param aa the index into A2Row
     * @param d the value to be displayed
     * @return aname + pNq[si] + ix = value
     */
    String nameXnIx(String aname, String[] pNq, int aa, double d) {
      int ix = aa % E.lsecs;
      int si = (int) (aa / E.lsecs);
      String ret1 = pNq[si];
      return aname + ret1 + ix + "=" + df(d);
    }

    /**
     * utility tool to generate name with resource type and index
     *
     * @param aname first part of name
     * @param pNq the array of resource type
     * @param a1 major index int pNq
     * @param a2 index into lsecs of the value
     * @return aname + pNq[si] + ix
     * @param d the value to be displayed
     * @return aname + pNq[si] + ix = value
     */
    String nameXnIx(String aname, String[] pNq, int a1, int a2, double d) {
      int ix = a2;
      int si = a1;
      String ret1 = pNq[si];
      return aname + ret1 + ix + "=" + df(d);
    }

    /**
     * set swap title prefix
     *
     * @param titl
     * @return prefixed title
     */
    String nTitle(String titl
    ) {
      return n + (swapXtra > 0 ? ":" + swapXtra + ":" : " ") + titl;
    }

    class HSwaps {  // Assets.CashFlow.HSwaps

      // swap requests and activities
      E.SwpCmd cmd;
      // set values to illegal to catch unset values
      int srcIx , destIx, ixWRSrc, ixFor , forIx,sourceIx;
      int nSource, nDest, swapType = 10, n, rChrgIx, sChrgIx;
      int reDo, unDo =0, rt;
      String resTypeName = "anot";
      double yearsFutureFund=0;
      int yearsFutureFundTimes=0;
      double rsval=0;
      A10Row doNot;

      // balances
      ABalRows hbals;
      // needs and requirements
      A2Row rawProspects2 = new A2Row();
      A2Row rawFertilities2 = new A2Row();
      A6Row healths;
      A6Row mtgNeeds6 = new A6Row(), mtgAvails6 = new A6Row();
      A2Row fertilities;
      Assets as;

      int[] stopped;
      boolean doingTrade, swapped;
      double health, fertility, sumTotWorth, totNeeds, scost, rcost;

      ;
      HSwaps() {
      }

      String df(double v){return ec.mf(v);}
      /**
       * copy swap values from CashFlow to HSwaps
       *
       * @param ay a reference to the current CashFlow
       * @return the updated HSwaps object
       */
      HSwaps copyn(CashFlow ay) {
        cmd = ay.cmd;
        this.as = ay.as;
        this.n = as.n - 1;
        this.resTypeName = as.resTypeName;
        this.yearsFutureFund = as.yearsFutureFund;
        this.yearsFutureFundTimes = as.yearsFutureFundTimes;
        this.rsval = as.rsval;
        hbals = new ABalRows(BALSLENGTH, TBAL, 7, "bals").copyValues(as.bals);
       // rawProspects2 = as.rawProspects2.copy();
        rawFertilities2 = as.rawFertilities2.copy();
        rawProspects2.copyValues(as.rawProspects2);
        mtgNeeds6.copyValues(as.mtgNeeds6);
        mtgAvails6.copyValues(as.mtgAvails6);
        totNeeds = ay.totNeeds;
        srcIx = ay.srcIx;
        sourceIx = ay.sourceIx;
        destIx = ay.destIx;
        ixWRSrc = as.ixWRSrc;
        ixFor = ay.ixWRFor;
        sChrgIx = ay.sChrgIx;
        rChrgIx = ay.rChrgIx;
        scost = ay.scost;
        rcost = ay.rcost;
        forIx = ay.forIx;;
        nSource = ay.source.sIx;
        nDest = ay.dest.sIx;
        healths = ay.healths;
        fertilities = ay.fertilities;
        health = as.health;
        fertility = ay.fertility;
        doingTrade = ay.source != null && ay.dest != null && ay.source.as1 != ay.dest.as1;
        swapped = ay.swapped;
        swapType = ay.swapType;
        sumTotWorth = as.sumTotWorth;
        stopped = ay.stopped;
        doNot = ay.doNot.copy();
        reDo = ay.reDo;
        unDo = ay.unDo;
        return this;
      } // copyn

      /**
       * copy request type swap values from CashFlow to HSwaps
       *
       * @param ay a reference to the current CashFlow
       * @return the updated HSwaps object
       */
      HSwaps copyReq(CashFlow ay) {
        cmd = ay.cmd;
        this.as = ay.as;
        this.n = as.n - 1;
        hbals = new ABalRows(BALSLENGTH, TBAL, 7, "bals").copyValues(as.bals);
        rawProspects2 = as.rawProspects2.copy();
        srcIx = ay.srcIx;
        destIx = ay.destIx;
        ixWRSrc = as.ixWRSrc;
        ixFor = ay.ixWRFor;
        sChrgIx = ay.sChrgIx;
        rChrgIx = ay.rChrgIx;
        scost = ay.scost;
        rcost = ay.rcost;
        forIx = ay.forIx;;
        nSource = ay.source.sIx;
        nDest = ay.dest.sIx;
        healths = ay.healths;
        doingTrade = ay.source != null && ay.dest != null && ay.source.as1 != ay.dest.as1;
        swapped = ay.swapped;
        swapType = ay.swapType;
        sumTotWorth = as.sumTotWorth;
        stopped = ay.stopped;
        doNot = ay.doNot.copy();
        reDo = ay.reDo;
        unDo = ay.unDo;
        return this;
      } // copyReq

      /**
       * copy need type swap values from CashFlow to HSwaps
       *
       * @param ay a reference to the current CashFlow
       * @return the updated HSwaps object
       */
      HSwaps copyNeeds(CashFlow ay) {
        cmd = ay.cmd;
        this.as = ay.as;
        this.n = as.n - 1;
        rawProspects2 = as.rawProspects2.copy();
        rawFertilities2 = as.rawFertilities2.copy();
        mtgNeeds6.copyValues(as.mtgNeeds6);
        mtgAvails6.copyValues(as.mtgAvails6);
        totNeeds = ay.totNeeds;
        healths = ay.healths;
        fertilities = ay.fertilities;
        health = as.health;
        fertility = ay.fertility;
        doingTrade = ay.source != null && ay.dest != null && ay.source.as1 != ay.dest.as1;
        swapped = ay.swapped;
        sumTotWorth = as.sumTotWorth;
        stopped = ay.stopped;
        doNot = ay.doNot.copy();
        reDo = ay.reDo;
        unDo = ay.unDo;
        return this;
      } // copyNeeds

      /**
       * call from the current prevns to redo the swap restore the good values
       * to go into yCalcCosts to redo the swap however, prevent repeating the
       * same swap, let n increase and redo increase redo should not increase
       * beyond 3
       *
       * @param ay the pointer to current Assets.CashFlow, ay.as points to
       * Assets
       * @param good the good HSwaps to restore to cur
       *
       */
      void restoreUpdate(CashFlow ay, HSwaps good) {
        as.bals.copyValues(good.hbals); // do not change references to balances
        //    as.balances = as.bals.getBalances(as.balances.lev,"balances");
        ay.source = ay.sys[good.nSource];
        // remove the bad Freedom Fund change
        if(resTypeName != "anot"){setStat(resTypeName, pors, clan, -as.rsval, -1);
          setStat(resTypeName.contains("Emerg")?"EmergFF":"SizeFF", pors, clan, -rsval, -1);
          setStat("FutureFund", pors, clan, -rsval, -1);
        }
        as.resTypeName = good.resTypeName;
        as.rsval = good.rsval;
        as.yearsFutureFund = good.yearsFutureFund;
        as.yearsFutureFundTimes = good.yearsFutureFundTimes;
        ay.dest = ay.sys[good.nDest];
        ay.srcIx = good.srcIx;
        ay.destIx = good.destIx;
        as.ixWRSrc = good.ixWRSrc;
        ay.ixWRFor = good.ixFor;
        // as.mtgNeeds6.copyValues(good.mtgNeeds6);
        //   as.mtgAvails6.copyValues(good.mtgAvails6);
        ay.forIx = good.forIx;
        ay.sChrgIx = good.sChrgIx;
        ay.rChrgIx = good.rChrgIx;
        ay.scost = good.scost;
        ay.rcost = good.rcost;
        if (this.swapType == 1) { //DECR
          good.doNot.setDoNot(1, this.ixWRSrc, this.srcIx, doNotDays5); //avoid srcIx sect0r 5 times
          good.stopped[1] = 2;  // no DECR 1 times (2 .age())
        }
        else if (this.swapType == 0) { // incr
          good.doNot.setDoNot(0, this.ixWRSrc, this.srcIx, doNotDays2);  //no incr from sector for 2 times
        }
        else if (this.swapType == 2) { // XDECR
          good.doNot.setDoNot(2, this.ixWRSrc, this.srcIx, doNotDays2);  // no xdecr from this sector 2 times
        }
        ay.swapType = good.swapType;
        ay.swapped = good.swapped;
        ay.stopped = good.stopped;
        ay.doNot = good.doNot;
        ay.reDo = good.reDo += 1;
        if (good.reDo < 2) {
          ay.unDo = good.unDo += 1; // only at first redo
        }
        as.sumTotWorth = ay.rawCWTotWorth = good.sumTotWorth;
        mtgAvails6 = as.mtgAvails6.copy6(0, History.valuesMinor7, "mtgAvails6");
        // as.n = this.n;

      }

      /**
       * Find if the last swap increased value of the Econ if swapType &lt; 0,
       * swap failed rt=10; if prospects sum increased rt = 1 if prospects
       * negSum decreased rt = 2 if prospects min inceased rt = 3 if worth
       * increased rt = 4 if need decreased rt = 5 otherwise leave 0 failed to
       * increase
       *
       * @return rt if swapType &lt; 0, swap failed rt=10; if prospects sum
       * increased rt = 1 if prospects negSum decreased rt = 2 if prospects min
       * inceased rt = 3 if worth increased rt = 4 if need decreased rt = 5
       * otherwise leave 0 failed to increase
       */
      int betterResult(HSwaps prev) {
        double t1 = 0., t2 = 0., t3 = 0., t4 = 0.;
        rt = 0;
        if (swapType < 0) {
          rt = -10;
        } else if(swapType == 3) { rt = 6;}// future fund
        else if (prev == this) {
          rt = -11;
        }
        else if (rawProspects2.curSum() > prev.rawProspects2.curSum() && rawProspects2.curMin() > .1) {
          rt = 1;
        }
        else if (rawProspects2.negSum() < prev.rawProspects2.negSum() && rawProspects2.curMin() > .1) {
          rt = 2;
        }
        else if (rawProspects2.min() < prev.rawProspects2.min()) {
          rt = -1;
        }
        else if (rawProspects2.min() > prev.rawProspects2.min()) {
          rt = 3;
        }
        else if (sumTotWorth > prev.sumTotWorth) {
          rt = 4;
        }
        else if (mtgNeeds6.curSum() < prev.mtgNeeds6.curSum()) {
          rt = 5;
        }
      if(n > 1 && (srcIx < 0 || srcIx > 6)){
        EM.doMyErr("srcIx="+ srcIx + " n=" + n);
      }
        if (rt > -20) {
          prevns[1].listRes("&g",5);
          prev.listRes("&m",5);
          this.listRes("&n",4);
        }
        if(n > 1 && (srcIx < 0 || srcIx > 6)){
        EM.doMyErr("srcIx="+ srcIx + " n=" + n);
      }
        return rt;
      }

      /**
       * List a HSwap entry
       *
       * @param pre prefix
       */
      void listRes(String pre, int level) {
        int xt;
        hist.add(new History(pre, level, n + "=" + reDo + "B" + this.rt + "st=" + swapType + " " + E.rNsIx(ixWRSrc, srcIx) + "->" + E.rNsIx(ixWRSrc, destIx),
                "mov=" + df(mov),
                "src" + E.rNsIx(ixWRSrc, srcIx) + "=" + df(balances.get(ixWRSrc, srcIx)),
                "p" + E.rNsIx(ixWRSrc, srcIx) + "=" + df(rawProspects2.get(ixWRSrc, srcIx)),
                "dst" + E.rNsIx(ixWRSrc, destIx) + "=" + df(balances.get(ixWRSrc, destIx)),
                "p" + E.rNsIx(ixWRSrc, destIx) + "=" + df(rawProspects2.get(ixWRSrc, destIx)),
                "M" + rNs[(int) (xt = rawProspects2.curMinIx()) / E.LSECS] + (int) xt % E.LSECS + "=" + df(rawProspects2.curMin()),
                "Ps" + df(rawProspects2.curSum()),
                "-" + df(rawProspects2.curNegSum()),
                // "B=" + df(bals.curSum()),
                "r$" + df(rcost),
                "s$" + df(scost),
                "<<<<<<<"));
      }

    } //HSwaps

    // strings arrays for nameXnIx
    /*
     void swapHist(ARow A, String titl) {
     hist.add(new History("swp", E.debuggingInformation, n + (swapXtra > 0 ? ":" + swapXtra : "") + titl, A));
     }

     void swapHist(A2Row A, String titl0, String titl1) {
     A.sendHist(hist, E.debuggingInformation, "swp", n + (swapXtra > 0 ? ":" + swapXtra : "") + titl0, n + (swapXtra > 0 ? ":" + swapXtra : "") + titl1);
     }

     void swapHist4(A6Row A, String titl2, String titl3, String titl4, String titl5) {
     A.sendHist4(hist, E.debuggingInformation, "swp", n + (swapXtra > 0 ? ":" + swapXtra : "") + titl2, n + (swapXtra > 0 ? ":" + swapXtra : "") + titl3, n + (swapXtra > 0 ? ":" + swapXtra : "") + titl4, n + (swapXtra > 0 ? ":" + swapXtra : "") + titl5);
     }

     void swapHist2(A6Row A, String titl0, String titl1) {
     A.sendHist2(hist, E.debuggingInformation, "swp", E.debuggingInformation, n + (swapXtra > 0 ? ":" + swapXtra : "") + titl0, n + (swapXtra > 0 ? ":" + swapXtra : "") + titl1);
     }
     */
    int aaab = 0, aaaa = 0, aaac = 0, aaad = 0, aaae = 0, aaaf = 0, aaag = 0, aaah = 0, aaai = 0, aaaj = 0;
    int aaaba = 0;

    /**
     * swap resources and staff
     * <p>
     * Phase SWAPPING<br>
     * Goal: get health above 0 at any cost, starting with highest strategic
     * value:<br>
     * <ol start=0><li> move reserves to working
     * <li> try moving max working to reserve, to lower other costs
     * <ol><li> only if at least 2 rawHealths below 0
     * <li>move upto .5 + Assets.clanRisk*.5 of max available, &lt .4 +
     * Assets.clanRisk*.6 of partner sum to reserve.
     * <li>use upto 5 least strategic value staff or resource (high balances)
     * </ol>
     * <li> if a staff rawHealths &lt 0, move staff from higher available
     * guest/staff
     * <ul><li>minimum cost
     * <li>redo with smaller move if this leaves another staff rawHealths below
     * the first
     * <li>undo and try another option if 2 redo's do not succeed
     * </ul>
     * <li>if a resource rawHealths &lt 0, move resources from a higher
     * available resource
     * <ul><li>if now another resource/staff is &lt the first, redo with a
     * smaller move
     * <li>if 2 redo's fail continue, than go to next &lt 0
     * <li> terminate done if all rawHealths &gt .0001
     * </ul>
     * <p>
     * phase GROW: Goal get high growth, grow staff where resource rawHealths
     * &lt 0.
     * <ol><li>for top 7 high Strat value if reserve exists move reserve to
     * working set donot decrease this sector for 5 swaps.
     * * <ol><li> only if at least 2 rawHealths below 0
     * <li>move upto Assets.clanRisk*.8 of max available, &lt Assets.clanRisk*.6
     * of partner sum to reserve.
     * <li>if move decreases the sum2 of rawFertilities or decreases the sum2 of
     * rawHealths undo it and donot do it for 5 swap iterations
     * <li>no more than 5 least strategic value staff or resource
     * <li> if 2 successive swap decreases do not increase rawFertilities.sum2,
     * stop decreases for 5 swaps.
     * </ol>
     * <li>for High Strat Resource rawHealths &lt 0 and associated staff &lt
     * 5*resource and no reserve move upto Assets.clanRisk*.6 of max available,
     * and &lt Assets.clanRisk*.5 staffGuestPartners from low Strat partner sums
     * <li>for High Strat r or s and rawHealths &lt .1 move from a lower strat
     * corresponding r or s upto Assets.clanRisk*.5 of available and &lt
     * Assets.clanRisk*.5 of partner sum with penalty.
     * <li>when moving between sectors if staff*Assets.gameStaffMoveCostFactor
     * &gt resource then charge staff the move high cost.
     * <li>if moving between sectors does not increase the sum2 of
     * rawFertilities than undo and donot try this sector again for 5 swap
     * iterations .
     * </ol>
     *
     * between active and reserve then if needed exchange resources and staff
     * between financial sectors the cost of exchanges is high, and is done only
     * if no swap between active and reserve works
     * [CRes,CStaf][TR,TX,TT][iW,iR][oW,oR][Plan,Ship] double [][][][][]
     * swapcosts = {swapRrxtcost,swapSrxtcost}; int
     * resource=0,staff=1,top=0,middle=1,bottom=2,none=3;
     */
    boolean swaps(String aPre, double travelyears
    ) { 
      final double nFlag = -99.;
      int sr = 0, ss = 1, ps = pors;
      double t1 = 0., t2 = 0., bmov = 1., nmov = 1, gmov1 = 1., gmov = 1.;
      
      // save sourceIx
      if(n > 0 && (ixWRSrc < 0 || ixWRSrc > 1)){
        EM.doMyErr("ixWRSrc="+ixWRSrc + " n=" + n);
      }
      if(n > 0 && (srcIx < 0 || srcIx > 6)){
        EM.doMyErr("srcIx="+ srcIx + " n=" + n);
      }
      int savIxSrc = sourceIx = ixWRSrc * E.LSECS + srcIx;
      
      prevprevns = prevns;  // all references good for later
      if (n > 0) { // move prevns up only if last round did something.
        EM.wasHere = "CashFlow.swaps at entry n>0 cnt=" + ++aaaa + " n=" + n;
        for (m = prevns.length - 1; m > 1; m--) {
          if (prevns[m - 2] != null) {
            prevns[m] = prevns[m - 2];
          }
        }
        prevns[0] = new HSwaps();
      }
      EM.wasHere = "CashFlow.swaps after new prevns[o] aaab=" + ++aaab + " n=" + n;
      prevns[1].copyn(cur);
      lTitle = " Costs " + name;
      // add another prevns except if n==0, then no swap for results

      histTitles(lTitle);
      yCalcCosts("C#", lightYearsTraveled, curGrowGoal, curMaintGoal);  //includes yinitN
      EM.wasHere = "CashFlow.endYear.swaps after yCalcCosts before test savIxSrc too large";
      if(n > 0 && (savIxSrc  >= E.L2SECS || savIxSrc < 0)) {
        throw new MyErr(String.format("savIxSrc " + savIxSrc + " would make ixWRSrc more than 0 or 1"));
      }
      ixWRSrc = (int)(savIxSrc/E.LSECS)%2;   // restore to 0,1
      if(n > 1 && (ixWRSrc < 0 || ixWRSrc > 1)){
       throw new MyErr(String.format("ixWRSrc bad="+ixWRSrc + " n=" + n));
      }
      if(n > 1 && (srcIx < 0 || srcIx > 6)){
        throw new MyErr(String.format("srcIx bad="+ srcIx + " n=" + n));
      }
      srcIx = savIxSrc % E.LSECS;
      lTitle = " Swaps " + name;
      prevns[0].copyn(cur);
      histTitles(lTitle);
      EM.wasHere = "CashFlow.endYear after resetting ixWRSrc,srcIx aaac=" + ++aaac + "  n=" + n;

      // exit only if goals are met and rawProspect (worst balance problem > 0
      if ((n > eM.maxn[pors] *.5 && rawProspects2.curMin() > eM.minProspects[0] )|| (rawFertilities2.curMin() > curGrowGoal && rawProspects2.curMin() > curMaintGoal && rawProspects2.curMin() > eM.mtgWEmergency[pors][clan])) {
        done = true;  // terminate looping
        hist.add(new History("GR", History.loopIncrements3, nTitle("TERM ") + cmd.toString() + srcIx + "->" + destIx, "mov=" + df(mov), "src=" + df(balances.get(ixWRSrc, srcIx)), "r$" + rChrgIx + "=" + df(rcost), "s$" + sChrgIx + "=" + df(scost), "dst=" + df(balances.get(ixWRSrc, destIx)), "Hl" + rawProspects2.curMinIx() + "=" + df(rawProspects2.curMin()), "HlB" + rawProspects2.curMinIx() + "=" + df(rawProspects2.get(rawProspects2.curMinIx())), "Ha" + "=" + df(rawProspects2.ave()), "mtgC=" + df(mtgCosts10.curSum()), "bals=" + df(bals.curSum()), "<<<<<<<"));
        EM.wasHere = " CashFlow.swaps just before return if done aaad=" + ++aaad + " n=" + n;
        return swapped = false;  // terminate looping success
      } // exit if we have satisfied END health

      // continuing with swap., save for redo
      //get the previous swap values and the Cost values for that swap
      balances.checkBalances(cur);
      double dstCst = 0;
      double dstRCst = 0, dstSCst = 0;
      int rlev = History.loopMinorConditionals5;
      int xt, bres = 8;
      if (n > 2 && prevns[0] == prevgood[0]) { // no test until results for n=1
        // at n=2, prevns[1] are n=0 swap results, prevns[0] n=1 swap results
        EM.doMyErr("prevens[0] match prevgood[0] but should not n=" +n + " prevgood[0].n=" + prevgood[0].n + " prevns[0].n=" + prevns[0].n );
      }
      if(n > 1 && (srcIx < 0 || srcIx > 6)){
        EM.doMyErr("srcIx="+ srcIx + " n=" + n);
      }
       if(n > 1 && (ixWRSrc < 0 || ixWRSrc > 1)){
        EM.doMyErr("ixWRSrc="+ixWRSrc + " n=" + n);
      }
      if(n > 2){ // n0 not yet, n1 first try set prevgood, n2 second set prevns,
        bres = prevns[0].betterResult(prevgood[0]);
      }
       if(n > 1 && (ixWRSrc < 0 || ixWRSrc > 1)){
        EM.doMyErr("ixWRSrc="+ixWRSrc + " n=" + n);
      }
       if(n > 1 && (srcIx < 0 || srcIx > 6)){
        EM.doMyErr("srcIx="+ srcIx + " n=" + n);
      }
       if(n > 1 && (destIx < 0 || destIx > 6)){
        EM.doMyErr("destIx="+ destIx + " n=" + n);
      }
      if(n > 1)hist.add(new History("##", History.loopMinorConditionals5, n + " " + cmd + " " + E.rNsIx(ixWRSrc, srcIx), "m" + df(mov), "src" + E.rNsIx(ixWRSrc, srcIx) + df(bals.get(ixWRSrc, srcIx)), "d" + E.rNsIx(ixWRSrc, destIx) + "=" + df(bals.get(ixWRSrc, destIx)), "bres=" + bres, "reDo=" + reDo, "st=" + "<<<<<<<<<<"));
      if(n > 1 && (ixWRSrc < 0 || ixWRSrc > 1)){
        EM.doMyErr("ixWRSrc="+ixWRSrc + " n=" + n);
      }
      // if result not better restore the old balances and recalc
      if (n > 2 && bres < 1 && reDo < 4) { // redo bad swaps up to 4 times, than accept
        // prevns = prevprevns; // restore the prevns 
        balances.checkBalances(this);
        hist.add(new History("##", History.loopMinorConditionals5, n + "OLD " + cmd + " " + E.rNsIx(ixWRSrc, srcIx), "m" + df(mov), "src" + E.rNsIx(ixWRSrc, srcIx) + df(bals.get(ixWRSrc, srcIx)), "d" + E.rNsIx(ixWRSrc, destIx) + "=" + df(bals.get(ixWRSrc, destIx)), "bres=" + bres, "reDo=" + reDo, "st=" + "<<<<<<<<<<"));
        // input is the current bad, to the current good`
        prevns[0].restoreUpdate(cur, prevgood[0]);
        hist.add(new History("##", History.loopMinorConditionals5, n + "RESTORD " + cmd + " " + E.rNsIx(ixWRSrc, srcIx), "m" + df(mov), "src" + E.rNsIx(ixWRSrc, srcIx) + df(bals.get(ixWRSrc, srcIx)), "d" + E.rNsIx(ixWRSrc, destIx) + "=" + df(bals.get(ixWRSrc, destIx)), "bres=" + bres, "reDo=" + reDo, "st=" + "<<<<<<<<<<"));
        balances.checkBalances(this);
        // now recalculate costs, may be slightly different than original
        yCalcCosts("X*", lightYearsTraveled, curGrowGoal, curMaintGoal);
        // ignore the result we are overwriting
//        prevns[0].copyn(cur);
      }
      // accept current swap as good, do the next one
      else {
// then continue with the new setDoNot s
        reDo = 0;
        // doing not undo or redo, increased rawFertilities and or rawHealths
        doNot.age(); //age only once per n, reduce doNots
        // and reduce stopped back toward 0
        stopped[0] += stopped[0] > 0 ? -1 : stopped[0];
        stopped[1] += stopped[1] > 0 ? -1 : stopped[1];
        stopped[2] += stopped[2] > 0 ? -1 : stopped[2];
        if (n > 0) { // move prevgood up.
          for (m = prevgood.length - 1; m > 0; m--) {
            if (prevgood[m - 1] != null) {
              prevgood[m] = prevgood[m - 1];
            }
          }
        }
      }
      if(n > 0) {
      prevgood[0] = new HSwaps();
      prevgood[0] = prevgood[0].copyn(cur);
      }
      doNot.sendHist(History.loopMinorConditionals5, "g^");

      //    ARow[] bwp = {r.balanceWithPartner, s.balanceWithPartner};
      balances.checkBalances(this);
      A2Row swapNeeds = new A2Row(7, "swapNeeds");
      SubAsset[] sources = {resource, staff, cargo, guests};
      String[] srcNames = {" r ", " s "};
      //resource.balance.negError("resource.balance");
      double src0Bal = 0., src1Bal = 0., src01Bal = 0., destBal = 0.;
      int swap4Steps[] = {0, 1, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};
// 0,1,2,3
      int swap7Steps[] = {0, 1, 2, 3, 4, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6};
      // loop through tests with decreasing requirements, to see if anything
      // can be swapped
      //for (swapLoops = 0; swapLoops < 7; swapLoops++) {
      for (swapLoops = 0; swapLoops < swapLoopMax; swapLoops++) {
        swap4Step = swap4Steps[swapLoops];
        swap7Step = swap7Steps[swapLoops];
        int swapLoop2 = swapLoops / 2;  // 0,0,1,1
        int swapLoop1 = swapLoops % 2;  // 0,1
        minRF = rawFertilities2.min();
        minRFIx = rawFertilities2.curMinIx();
        minRH = rawProspects2.min();
        minRHIx = rawProspects2.curMinIx();
        //double mSumRem = mtgSumRemnant;

        //see description at start of swaps
        boolean hNot1 = prevFlagm && prevFlagh;
        if (swapLoops == 0 && n > 1) {
          

          rawFertilities2.sendHist(hist, 20, "C", 4, "r rawF", "s rawF");
          prevns[0].rawFertilities2.sendHist(hist, 20, "C", 4, "r prvrawF", "s prvrawF");
          rawProspects2.sendHist(hist, 20, "C", 4, "r rawH", "s rawH");
          prevns[0].rawProspects2.sendHist(hist, 20, "C", 4, "r prvrawH", "s prvrawH");
          growths.sendHist2(20, "C", 4, "r G", "s G");
          //          prevns[0].growths.sendHist2(20, "C", 4, "r prvG", "s prvrawG");
          balances.sendHist2(20, "C", 4, "r bal", "s bal");
          //           prevns[0].balances.sendHist2(20, "C", 4, "r prvbal", "s prvbal");
          //           balances.set(prevns[0].balances);
          rawFertilities2.set(prevns[0].rawFertilities2);
          rawProspects2.set(prevns[0].rawProspects2);

          doNot.sendHist();
          //       doNot.sendHist4(hist, History.informationMinor9, "do", History.informationMinor9, "r dec doNot", "s dec doNot", "r xfr doNot", "s xfr doNot");
          //   prevSwapResults(aPre);

          if (History.dl > 40) {
            StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];

            hist.add(new History(aPre, History.debuggingMinor11, ">>>>" + nTitle("swaps"), wh(a0.getLineNumber()), (failed ? "failed" : "!!!failed"), (swapped ? "swapped" : "!!!swapped"), swapLoops + "=swapLoops", "$=" + df(sumTotWorth)));
          }
          errHistory = null;
        }

        //  }
        //     calcSwapValues(aPre, lightYearsTraveled);
        // do some calculations for a previous decr
        balances.checkBalances(this);
        //     swapType = 4;
        // index on swap4Step
        // A6Row[] avails = {mtgGoalNeeds, mtgGoalNeeds, mtgEmergNeeds, mtgNeeds6};
        //A6Row[] avails = {mtgNeeds6, mtgNeeds6, mtgNeeds6, mtgNeeds6};
        // A6Row[] needs = {mtggNeeds6, mtggNeeds6, mtgNeeds6, mtgNeeds6};
        A2Row stratV = new A2Row().set(stratVars);
        A2Row aorder = new A2Row();
        mtgAvails6.sendHist2(rlev, aPre, rlev, "rc avails", "sg avails");
        mtgNeeds6.sendHist2(rlev, aPre, rlev, "rc needs", "sg needs");
//        mtggEmergNeeds.sendHist2(rlev, aPre, rlev, "rc mEmergN", "sg mggEmergN");
        //  mtgNeeds6.sendHist2(rlev, aPre, rlev, "rc mtgN", "sg mtgN");
        rawProspects2.sendHist(hist, 29, aPre, rlev, "rc rawProspects2", "sg rawProspects2");

        //   mNeeds.addJointBalances();
        //balances.addJointBalances();
        double frac1 = 0., frac2 = 0., tmp1 = 0., sum1 = 0., sum2 = 0., dif1 = 0.;
        int minFx = rawFertilities2.curMinIx();
        int min1Fx = rawFertilities2.curMinIx(1);
        int minHx = rawProspects2.curMinIx();
        int min1Hx = rawProspects2.curMinIx(1);
        int minNx = mtgNeeds6.curMinIx();
        int min1Nx = mtgNeeds6.curMinIx(1);
        int maxNx = mtgNeeds6.curMaxIx(0);
        double maxNd = mtgNeeds6.curMax(0);
        int maxSx = stratVars.curMaxIx();
        double maxSd = stratVars.curMax();
        int max1Sx = stratVars.maxIx(1);
        int minHSx = stratVars.findMinIx(minHx);
        int min1HSx = stratVars.findMinIx(min1Hx);
        int minFSx = stratVars.findMinIx(minFx);
        int min1FSx = stratVars.findMinIx(min1Fx);
        double minFd = rawFertilities2.get(minFx);
        double min1Fd = rawFertilities2.get(min1Fx);
        double minHd = rawProspects2.get(minHx);
        double min1Hd = rawProspects2.get(min1Hx);
        double minNd = mtgNeeds6.curGet(minNx);
        double min1Nd = mtgNeeds6.curGet(min1Nx);
        double bav = balances.getRow(0).ave();
        double[] maxMove = {.45 * bav, .65 * bav, .75 * bav, .75 * bav};
        // double needAvailValue = -mtgNeeds6.curMax(needIx); // value available at needIx
        int incLeastStrategicIx[] = {6, 7, 8, 8};//most is most strategic
        // Index into the first 2 rows
        int stratIx = stratVars.maxIx(incLeastStrategicIx[swap4Step]);
        double leastStrategicValue = mtgNeeds6.curMax(stratIx);
        double theSum = bals.curSum();

        hist.add(new History("i@", History.loopMinorConditionals5, "loop data", nameXnIx("minH", rNs, minHx, minHd), nameXnIx("N", rNs, minHx, mtgNeeds6.curGet(minHx)), nameXnIx("b", rNs, minHx, balances.curGet(minHx)), nameXnIx("strat", rNs, minHSx, balances.curGet(minHSx)), nameXnIx("min1H", rNs, min1Hx, min1Hd), nameXnIx("N", rNs, min1Hx, mtgNeeds6.curGet(min1Hx)), nameXnIx("b", rNs, min1Hx, balances.curGet(min1Hx)), nameXnIx("strat", rNs, min1HSx, balances.curGet(min1HSx))));

        hist.add(new History("i@", History.loopMinorConditionals5, "loop data", nameXnIx("minF", rNs, minFx, minFd), nameXnIx("N", rNs, minFx, mtgNeeds6.curGet(minFx)), nameXnIx("b", rNs, minFx, balances.curGet(minFx)), nameXnIx("strat", rNs, minFSx, balances.curGet(minFSx)), nameXnIx("min1F", rNs, min1Fx, min1Fd), nameXnIx("N", rNs, min1Fx, mtgNeeds6.curGet(min1Fx)), nameXnIx("b", rNs, min1Fx, balances.curGet(min1Fx)), nameXnIx("strat", rNs, min1FSx, balances.curGet(min1FSx))));

      // now check for futureFund processing
      if (calcFutureFund()) {
        swapType=3;
        destIx = srcIx;
        done = false;
        EM.wasHere = "CashFlow.endYear just after calcFutureFund aaaba=" + aaaba;
        return swapped= true;
      }
        /**
         * ****************** RINCR SINCR *************************************
         */
        // swap   RINCR  SINCR  move reserve to working (cargo->resource,quests->staff
        // swaploops increase avails, increase needs
        // do charge for a swap after a trade, trade moved units all to working
        //  boolean ncharg = yphase == yrphase.GROW && n < 10 && didTrade > 0;
        // fraction of source balances to use in swap
         if (stopped[0] > 0) {  // incr stopped
          hist.add(new History(aPre, History.loopMinorConditionals5, nTitle(cmd.name() + ">>stopped "), "mov" + df(mov), "dest" + df(bals.getRow(3 + 2 * ixWRSrc).get(srcIx)), "src" + df(bals.getRow(2 + 2 * ixWRSrc).get(srcIx))));
        }
         else {
           
        double bFrac = bals.curSum() * .33 / 14; // bFrac maxMove 1/3 of balances ave
        double iMaxMove = bFrac;
        // double incrBalFrac = 1.; // fraction of C or G sources to use in swap
        double incrBalFracs[] = {.9, .92, .94, .98, 1., 1., 1., 1., 1.};
        double incrBalFrac = incrBalFracs[swap4Step]; // fraction of R or S sources to use in swap
        double incrAvailFracs[] = {.85, .87, .89, .91, .93, 1., 1., 1., 1.};
        double incrAvailFrac = incrAvailFracs[swap4Step];  // fraction of C or G source to use in swap
        double incrResrvFrac = 1. - incrAvailFrac;
        // int ixAF = n < incrAvailFrac.length ? n : incrAvailFrac.length - 1;

        // decrease min as steps increase as n increases
        movMin = .005 * bFrac * 3 / (3. + swap4Step) * (2 * maxn / (2 * maxn + n));  // skip INCR if move < movMin
        // mult against incrAvail.sum()
        double[] incrMovMult = {.15, .27, .25, .27};;

        //Initialize variables possibly used in swaps
        ixWRFor = forIx = srcIx = destIx = -1;
        rchrg = schrg = osource = forRes = dest = source = null;
        rcost = scost = mov = fmov = smov = 0;

        // set to a general incr, specific is later
        cmd = E.SwpCmd.RSINCR;
        swapType = 0;
        aorder = rawFertilities2;
        if (rawProspects2.curMin() < .2) {
          aorder = rawProspects2;
        }
        needIx = aorder.curMinIx();  // greatest need
        ec.aPre = aPre = "H@";
        // [ixWRSrc][r/scost][pors]
        double swapCost[][][] = {{eM.swapCtoRRcost, eM.swapCtoRScost}, {eM.swapGtoSRcost, eM.swapGtoSRcost}};
        // calculate the max move from C to R or G to S for each position 
        A2Row incrAvail0 = new A2Row().setAvailableToIncr(incrBalFrac, incrAvailFrac, iMaxMove, mtgAvails6, swapCost[sr][sr][ps], swapCost[sr][ss][ps], swapCost[ss][sr][ps], swapCost[ss][ss][ps]);
        A2Row incrAvail1 = new A2Row();
        incrAvail1 = doNot.filterByDoNot(0,incrAvail0,nFlag);
        // Eliminate positions recently changed
        // A2Row incrAvail1 = new A2Row().set(incrAvail0);

        hist.add(new History(History.valuesMajor6, "Incr vals", "movMin=", df(movMin), "iMaxMov", df(iMaxMove), "iBalFrac", df(incrBalFrac), "iAvailFrac", df(incrAvailFrac)));
        incrAvail0.sendHist(hist, History.valuesMinor7, "inc", "r incrAvail0", "s incrAvail0");
        incrAvail1.sendHist(hist, History.valuesMinor7, "inc", "r incrAvail1", "s incrAvail1");
        bals.sendHist(hist, aPre);
        //  rawFertilities2.sendHist(hist, 21, "$a", "rcFertilities", "sgFertilities");
        ec.aPre = aPre = "I@";
        stratVars.sendHist(hist, History.valuesMinor7, aPre, nTitle("r stratVars"), nTitle("s stratVars"));
        rawProspects2.sendHist(hist, aPre);

        // A2Row aorder1 = doNot.filterByDoNot(0,aorder,999.);
        A2Row aStrat1 = doNot.filterByDoNot(0, stratVars,nFlag);
        // now find a need that increment can help
        needIx = -2;
        mov = 999. + movMin;  // preset very large
        int imax = incLeastStrategicIx[swap4Step];

        // find r move = j = srcIx  if move greater than movMin
        for (int i = 0; i < imax; i++) {
          j = aStrat1.curMaxIx(i); // start with lowest val, highest strategic value        
          ixWRSrc = (int) j / LSECS;
          destIx = srcIx = (int) j % LSECS;
          // bmov is the max Avail mov 
          bmov = incrAvail1.get(j);
          // nmov is the amount needed to meet all current needs
          nmov = bals.get(3 + ixWRSrc * 2, srcIx) * incrBalFrac; // c or g.balance(srcIx)
          mov3 = mtgAvails6.curGet(j); //rc or sg values
          mov2 = mov3 < PZERO ? movMin * 3. : mov3;
          // gmov is the amount needed to reach the goals
          gmov1 = goalmtg1Needs6.curGet(j);
          gmov = gmov1 < PZERO ? movMin * 3 : gmov1;

          // may exceed nmov if prospects are good over .1
          mov = rawProspects2.curGet(j) < eM.mtgWEmergency[pors][clan] ? Math.min(nmov,Math.min(mov2, bmov)) : Math.min(nmov,Math.min(bmov, Math.max(mov2, gmov)));
          if (mov > movMin) {  // only accept moves above the min
            break;
          }
        } // end for i or break

        source = sources[ixWRSrc + 2]; // c or g
        dest = source.partner; // r or s
        destIx = srcIx;
        rchrg = resource;
        schrg = staff;
        rcost = mov * swapCost[ixWRSrc][sr][pors];
        scost = mov * swapCost[ixWRSrc][ss][pors];
       // dstCst = mov + (ixWRSrc == 0?rcost:scost);
        dstCst = mov + mov*swapCost[ixWRSrc][ixWRSrc][pors];
        swapCosts.getRow(0).add(rcost);
        swapCosts.getRow(1).add(scost);
        prevsrc = source.balance.get(srcIx);
        prevdest = dest.balance.get(destIx);
        cmd = E.incrs[ixWRSrc][swap4Step]; //revise to detail cmd

        ec.aPre = aPre = "K@";

        if (mov < movMin) { //mov < movMin
          hist.add(new History(aPre, History.loopMinorConditionals5, nTitle(">SKIP " + cmd.name() + " too small"), "mov" + df(mov), "movMin=", df(movMin))); // 
          doNot.setDoNot(0, ixWRSrc, srcIx, 3); //disallow decrement
        }
        else if (mov < PZERO) { // neg move
          hist.add(new History(aPre, History.loopMinorConditionals5, nTitle(cmd.name() + ">SKIP " + " -mov"), "mov" + df(mov), "dest" + df(bals.getRow(3 + 2 * ixWRSrc).get(srcIx)), "src" + df(bals.getRow(2 + 2 * ixWRSrc).get(srcIx)), "nmov" + df(nmov), "gmov" + df(gmov), "dest big enough")); // 
        }
        else if (bals.getRow(3 + 2 * ixWRSrc).get(srcIx) < mov) {  // C too small
          hist.add(new History(aPre, History.loopMinorConditionals5, nTitle(cmd.name() + ">>too large "), "mov" + df(mov), "dest" + df(bals.getRow(3 + 2 * ixWRSrc).get(srcIx)), "src" + df(bals.getRow(2 + 2 * ixWRSrc).get(srcIx)), "nmov" + df(nmov), "gmov" + df(gmov), "dest big enough"));

        }
        else {
          hist.add(new History(aPre = "L@", History.loopMinorConditionals5, nTitle(cmd.name()) + " " + i + "=>" + j + (j < E.lsecs ? "r" + j : "s" + (j - E.lsecs)), "bal" + df(bals.get(2 + 2 * ixWRSrc, srcIx)), "prt" + df(bals.get(3 + 2 * ixWRSrc, srcIx)), "mov" + df(mov), "min" + df(movMin), "golm" + df(gmov), "nedm" + df(nmov), "incm" + df(bmov)));
          hist.add(new History(aPre, History.loopIncrements3, nTitle("Pre") + cmd.name() + srcIx + "->" + destIx, "mov=" + df(mov), "mMin" + df(movMin), "r$" + rChrgIx + "=" + df(rcost), "s$" + sChrgIx + "=" + df(scost), "H" + rawProspects2.curMinIx() + "=" + df(rawProspects2.curMin()), "HS" + rawProspects2.curSum(), "rS" + df(bals.getRow(0).sum()), "sS" + df(bals.getRow(1).sum()), "mtg" + df(mtgNeeds6.getRow(0).sum()), df(mtgNeeds6.getRow(1).sum()), "<<<<<<<"));
          // a pretest for problems with putValue and cost2
          if ((balances.get(ixWRSrc*2 + 3,srcIx) - dstCst) < NZERO) {
            E.myTest(true, "incr " + srcIx + " cost too high, balance=" + df(balances.getRow(ixWRSrc).get(srcIx)) + " -cost " + df(dstCst) + " => " + df((balances.getRow(ixWRSrc).get(srcIx) - dstCst)));
          }
          if (ixWRSrc == 0) {
            setStat("swapRIncr", pors, clan, mov * 100 / bals.getRow(0).sum(), 1);
          }
          else {
            setStat("swapSIncr", pors, clan, mov * 100 / bals.getRow(1).sum(), 1);
          }
          source.putValue(balances,mov, srcIx, destIx, dest, 0, 0.);
          rchrg.cost3(rcost, srcIx, .0001);
          schrg.checkGrades();
          schrg.cost3(scost, srcIx, .0001);
          setStat(EM.SWAPRINCRCOST, pors, clan, rcost / bals.getRow(2).sum(), 1);
          setStat(EM.SWAPSINCRCOST, pors, clan, scost / bals.getRow(4).sum(), 1);
          swapType = 0;
          rxfers = sxfers = 0;
          // allow repeat incr of min values
          // doNot.setDoNot(0, ixSrc, srcIx, E.doNotYears - n / 7);
          // do not decrement ever
          doNot.setDoNot(1, ixWRSrc, srcIx, 100);
          hist.add(new History(aPre, History.valuesMajor6, nTitle(" INCR ") + cmd.toString() + source.aschar + srcIx + "->" + source.partner.aschar + srcIx, "mov=" + df(mov), "src=" + df(balances.get(ixWRSrc, srcIx)), "r$" + rChrgIx + "=" + df(rcost), "s$" + sChrgIx + "=" + df(scost), "dst=" + df(balances.get(ixWRSrc, destIx)), "Hl" + rawProspects2.curMinIx() + "=" + df(rawProspects2.curMin()), "HlB" + rawProspects2.curMinIx() + "=" + df(rawProspects2.get(rawProspects2.curMinIx())), "Ha" + "=" + df(rawProspects2.ave()), "mtgC=" + df(mtgCosts10.curSum()), "bals=" + df(bals.curSum()), "<<<<<<<"));
          return swapped = true;
        }
         }// end of stopped else

        // look for null pointer errors
        String ccc = source.aschar;

        //      hist.add(new History(aPre, History.valuesMajor6, nTitle(" doNotINCR ")+source.aschar + srcIx,  prevns[0].cmd.name(), prevns[1].cmd.name(), prevns[2].cmd.name(), prevns[3].cmd.name(), prevns[4].cmd.name())); 
        hist.add(new History(aPre, History.valuesMajor6, nTitle(" DidNot INCR ") + source.aschar + srcIx, "mov" + df(mov), "mMin" + df(movMin), prevns[0].cmd.name(), prevns[1].cmd.name(), prevns[2].cmd.name(), prevns[3].cmd.name(), prevns[4].cmd.name()));


        /*---------------------- RDECR SDECR ----------------------------*/
        // SDECR  RDECR decrease some S or R to reduce services request
        // use S or R with low strategic values to be swapped to G or C
        // don't decr for small needs, it is not cost effective
        // fraction of source -needs to use in swap
        // use neg because bal not need is expected
        double decrBalFrac[] = {.5, .5, .6, .7};
        // osst balances limits
        double[] decrResrvWRFrac = {.8, .9, .94, .95};
        aPre = "#g";
        // row 3 is cargo
        double dbav = balances.curSum() * E.invL2secs;
        double[] decrMovMin = {dbav * .0003, dbav * .00007, dbav * .000003, dbav * .000002,};
        // [ixWRSrc][r/scost][pors]
        double swapCostd[][][] = {{eM.swapRtoCRcost, eM.swapRtoCScost}, {eM.swapStoGRcost, eM.swapStoGScost}};
        //    double[] dSwapRcost = {E.swapRtoCRcost[pors], E.swapStoGRcost[pors]};
        //    double[] dSwapScost = {E.swapRtoCScost[pors], E.swapStoGScost[pors]};
        int dR = 0, dS = 1;
        //       mtgNeeds6.addJointBalances();
        //      balances.addJointBalances();
        double[] dmaxMove = {.95 * dbav, 1.4 * dbav, 1.8 * dbav, 2.3 * dbav};

        A2Row decrAvail1 = new A2Row(History.loopMinorConditionals5, "decrAvail1").setAvailableToDecrement(decrBalFrac[swap4Step], decrResrvWRFrac[swap4Step], dmaxMove[swap4Step], mtgAvails6, swapCostd[sr][sr][ps], swapCostd[sr][ss][ps], swapCostd[ss][sr][ps], swapCostd[ss][ss][ps]);
        decrAvail1.sendHist(hist, History.informationMinor9, aPre = "f%", "r decrAvail1", "s decrAvail1");
        A2Row decrAvail = doNot.filterByDoNot(1, decrAvail1,nFlag);
        if (swapLoops < 20 && n < 20) { // try to list only 1? time per swap
          hist.add(new History(aPre, History.informationMajor8, nTitle("4mins,4maxs"), "dbav" + df(dbav), "swap4Step=" + swap4Step, df(decrMovMin[0]), df(decrMovMin[1]), df(decrMovMin[2]), df(decrMovMin[3]), df(dmaxMove[0]), df(dmaxMove[1]), df(dmaxMove[2]), df(dmaxMove[3])));
          stratVars.sendHist(History.loopMinorConditionals5, aPre);
          mtgNeeds6.sendHist(History.loopMinorConditionals5, aPre);
          decrAvail.sendHist(History.loopMinorConditionals5, aPre);
        }

        aorder = rawFertilities2;
        if (rawProspects2.curMin() < .2) {
          aorder = rawProspects2;
        }
        needIx = aorder.curMinIx();  // greatest need
        mov2 = mtgNeeds6.curGet(needIx);
        movMin = decrMovMin[swap4Step];
        forIx = needIx % E.lsecs;
        ixWRSrc = ixWRFor = needIx / E.lsecs;  // 0,1 find row of need
        srcIx = decrAvail.getRow(ixWRFor).maxIx();  // largest value in need row
        maxAvail = decrAvail.get(ixWRFor, srcIx);
        destIx = srcIx;
        maxAvail = Math.min(maxAvail, bals.get(ixWRSrc * 2 + 2, srcIx));
        mov1 = Math.max(Math.min(Math.min(bals.get(ixWRSrc * 2 + 2, srcIx), mov2), maxAvail), PZERO);
        mov = mov1 > PZERO && PZERO < maxAvail ? mov1 : maxAvail;
        EM.addlErr = "";
        // if incr just transfered from reserve to working reduce transfer prevent cycle
        cmd = E.decrs[ixWRSrc][swap4Step];
        if (swapLoops < 3) {
          hist.add(new History(aPre, History.loopMinorConditionals5, nTitle(" skip" + swapLoops + " < 3 " + cmd.name()), "mov" + df(mov), "dest" + df(bals.getRow(3 + 2 * ixWRSrc).get(srcIx)), "src" + df(bals.getRow(2 + 2 * ixWRSrc).get(srcIx)), "nmov" + df(nmov), "gmov" + df(gmov), "dest big enough"));
        }
        else if (bals.get(2 + 2 * ixWRSrc, srcIx) < (bals.getRow(2 + 2 * ixWRSrc).ave() * 4)) {
          hist.add(new History(aPre, History.loopMinorConditionals5, nTitle(" decr too small" + swapLoops + " " + cmd.name()), "mov" + df(mov), "dest" + df(bals.getRow(3 + 2 * ixWRSrc).get(srcIx)), "src" + df(bals.getRow(2 + 2 * ixWRSrc).get(srcIx)), "nmov" + df(nmov), "gmov" + df(gmov), "dest big enough"));
        }
        else if (stopped[1] > 0) {
          hist.add(new History(aPre, History.loopMinorConditionals5, nTitle(" stop" + swapLoops + " " + cmd.name()), "mov" + df(mov), "dest" + df(bals.getRow(3 + 2 * ixWRSrc).get(srcIx)), "src" + df(bals.getRow(2 + 2 * ixWRSrc).get(srcIx)), "nmov" + df(nmov), "gmov" + df(gmov), "dest big enough"));
        }
        else if (mov < movMin) {
          hist.add(new History(aPre, History.loopMinorConditionals5, nTitle(" skip") + swapLoops + " " + cmd.toString() + " for" + rNs[ixWRFor] + forIx, "mov=" + df(mov), "lt" + df(decrMovMin[swap4Step])));
        }
        else {

          // but no more than is available from forIx
          cmd = E.decrs[ixWRSrc][swap4Step];
          hist.add(new History(aPre, History.loopMinorConditionals5, nTitle("check xDecr"), "forIx=" + rNs[ixWRFor] + forIx, "destIx=" + rNs[ixWRSrc] + destIx, "mov" + df(mov), "m1=" + df(mov1), "m2=" + df(mov2), " max=" + df(maxAvail), "<<<<<<<<<<<"));
          //       hist.add(new History("dec", History.loopMinorConditionals5, nTitle(" prev swap1"), "prv0=" + prevns[0].swapType, "prv1=" + prevns[1].swapType, "prv2=" + prevns[2].swapType, "prv3=" + prevns[3].swapType, "prv4=" + prevns[4].swapType, "prv5=" + prevns[5].swapType, "dm=" + df(decrMostMaxGap), "abcdefghijklmnopqrst"));
          //      hist.add(new History(aPre, History.loopMinorConditionals5, nTitle(" prev cmds="), "prev=", prevns[1].cmd.toString(), prevns[2].cmd.toString(), prevns[3].cmd.toString(), prevns[4].cmd.toString(), prevns[5].cmd.toString(), prevns[6].cmd.toString()));

          //        hist.add(new History("d@", History.loopMinorConditionals5, nTitle(" Strategic Value !!too low "), "max=", df(maxAvail), "m2=" + df(mov2), "mov=" + df(mov), "<<<<<<"));
          chrgIx = srcIx;
          forRes = sources[ixWRFor];
          source = sources[ixWRSrc];
          osource = source.other;
          dest = source.partner;
          //    rcost = mov * dSwapRcost[ixWRSrc];
          //    scost = mov * dSwapScost[ixWRSrc];
          balances.checkBalances(cur);
          if(E.debugDouble){
            EM.addlErr = String.format("mov%7.3g, swapCostd[ixWRSrc%d][sr%d][pors%d]%7.3g",mov,ixWRSrc,sr,pors,swapCostd[ixWRSrc][sr][pors]);
            EM.wasHere="before rcost&scost";
          rcost = doubleTrouble(doubleTrouble(mov) * doubleTrouble(swapCostd[ixWRSrc][ss][pors]));
           EM.addlErr = String.format("mov%7.3g, swapCostd[ixWRSrc%d][ss%d][pors%d]%7.3g",mov,ixWRSrc,ss,pors,swapCostd[ixWRSrc][ss][pors]);
          scost = doubleTrouble(doubleTrouble(mov) * doubleTrouble(swapCostd[ixWRSrc][ss][pors]));
          }else {
          rcost = mov * swapCostd[ixWRSrc][sr][pors];
          scost = mov * swapCostd[ixWRSrc][ss][pors];
          }
          rchrg = resource;
          schrg = staff;
          EM.addlErr= String.format("mov%7.3g, xcost%7.3g",mov,(ixWRSrc == 0?rcost:scost));
            EM.wasHere = "before dstCst";
          if(E.debugDouble){
          dstCst = doubleTrouble(mov + doubleTrouble(ixWRSrc == 0?rcost:scost));
          } else {
             dstCst = mov + (ixWRSrc == 0?rcost:scost);
          }
          EM.wasHere = "after dstCst";
          prevsrc = source.balance.get(srcIx);
          prevdest = dest.balance.get(destIx);
          prevosrc = osource.balance.get(srcIx);
          swapCosts.getRow(2).add(rcost);
          swapCosts.getRow(3).add(scost);
          EM.wasHere = "before setStat SWAPRDECRCOST";
          setStat(EM.SWAPRDECRCOST, pors, clan, rcost / bals.getRow(2).sum(), 1);
          setStat(EM.SWAPSDECRCOST, pors, clan, scost / bals.getRow(4).sum(), 1);
          doNot.setDoNot(0, ixWRSrc, srcIx, doNotDays5);
          balances.sendHist(hist, aPre);
          //      decrAvail.sendHist(hist, bLev, aPre, "r decrAvail", "s decrAvail");
          //         hist.add(new History(aPre, 3, nTitle("Pre") + cmd.toString() + " " + source.aschar + srcIx + " -> " + dest.aschar + destIx, "mov=" + df(mov), "src=" + df(source.balance.get(srcIx)), "dst=" + df(dest.balance.get(destIx)), "Fl" + minFx + "=" + df(minFd), "Hl" + minHx + "=" + df(minHd), "Nm" + maxNx + "=" + df(maxNd), "Nl" + minNx + "=" + df(minNd), "Sm" + maxSx + "=" + df(maxSd)));
          hist.add(new History(aPre, History.loopIncrements3, nTitle("Pre") + swapLoops + cmd.toString() + srcIx + "->" + destIx, "mov=" + df(mov), "mMin" + df(movMin), "r$" + rChrgIx + "=" + df(rcost), "s$" + sChrgIx + "=" + df(scost), "H" + rawProspects2.curMinIx() + "=" + df(rawProspects2.curMin()), "HS" + rawProspects2.curSum(), "rS" + df(bals.getRow(0).sum()), "sS" + df(bals.getRow(1).sum()), "mtg" + df(mtgNeeds6.getRow(0).sum()), df(mtgNeeds6.getRow(1).sum()), "<<<<<<<"));
          hist.add(new History(aPre, History.loopMinorConditionals5, nTitle(cmd.toString() + " " + source.aschar + srcIx + " -> " + dest.aschar + destIx), "mov=" + df(mov), "rcst=" + df(rcost), "scst=" + df(scost), "dsrc=" + df(source.balance.get(srcIx)), "=>" + df(source.balance.get(srcIx) - dstCst), "ddst=" + df(dest.balance.get(destIx)), "=>" + df(dest.balance.get(destIx) + mov)));
          rmov1 = ixWRSrc == 0 ? mov : 0.;
          smov1 = ixWRSrc == 0 ? 0. : mov;
          //now test balances before doing move
          if (bals.get(2, srcIx) < (rmov1 + rcost)) {
            E.myTest(true, "ERR  %s, rcbal%d=%7.2g <  rmov1=%7.2g + rcost=%7.2g = rChrg=%7.5g * mov=%7.2g ,rshort=%7.2g, age=%d, n=%d, swap4Step=%d, redo=%d<<<<<", cmd.toString(), srcIx, bals.get(2, srcIx), rmov1, rcost, rChrg, mov, bals.get(2, srcIx) - rmov1 - rcost, ec.age, n, swap4Step, reDo);
          }
          if (bals.get(4, srcIx) < (smov1 + scost)) {
            E.myTest(true, "ERR  %s, scbal%d=%7.2g <  smov1=%7.2g + scost=%7.2g = sChrg%7.5g * mov=%7.2g ,sshort=%7.2g, age=%d, n=%d, swap4Step=%d, redo=%d<<<<<", cmd.toString(), srcIx, bals.get(4, srcIx), smov1, scost, sChrg, mov, bals.get(4, srcIx) - smov1 - scost, ec.age, n, swap4Step, reDo);
          }
          rchrg.cost3(rcost, srcIx, .0001);
          schrg.cost3(scost, srcIx, .0001);
          source.putValue(balances,mov, srcIx, destIx, dest, E.sSwapPenalty[pors], 0.);
          swapped = true;
          rxfers = sxfers = 0;
          hist.add(new History(aPre, History.loopMinorConditionals5, nTitle("POST " + cmd.toString() + " " + source.aschar + srcIx + " -> " + dest.aschar + destIx), (swapped ? "swapped" : "!swapped"), "mov=" + df(mov), "rcst=" + df(rcost), "scst=" + df(scost), "dsrc=" + df(prevsrc - source.balance.get(srcIx)), "ddst=" + df(dest.balance.get(destIx) - prevdest)));
          swapType = 1;
          return swapped = true;
        }

        /*------------------XFER both, ---------------------------*/
        // XFER both, 
        //       curSum >  clanStartFutureFundDues[pors][clan]
        ec.aPre = aPre = "#J";

        if (History.dl > 40) { // prevent listing
          StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
          hist.add(new History(aPre, rlev, "n" + n + " xfer crossa", wh(a0.getLineNumber()), (failed ? "" : "!") + "failed", (swapped ? "" : "!") + "swapped", swapLoops + "=swapLoops"));
        }
        //boolean xferCross = false;
        int rev = 0;   // 0=not cross charged, 1=cross charged
        /**
         * limit using the available (-need) amount to prevent loops because of
         * swapping too much
         */
        bav = balances.curSum() / 14.; //balance ave.
        //  double xchgBalFrac[] = {.9, .95, .98, .99};
        double xchgBalFrac[] = {.8, .85, .9, .92};

        // oost balances limits
        // double[] xchgWRReservFrac = {.95, .96, 97., 98.};
        // double xferMaxMove[] = {.9 * bav, .9 * bav, .90 * bav, .98 * bav};
        double[] xchgWRReservFrac = {.3, .26, .22, .17};
        double xferMaxMove[] = {1.2 * bav, 1.8 * bav, 2.4 * bav, 3.1 * bav};
        /**
         * if a high need, relax the limits a little, but be careful to avoid
         * loops
         */
        //---if ((yrphase.SWAPING == yphase && (rawProspects2.curMin() < .15 ))) {
        if ((rawProspects2.curMin() < .15)) {
          double xF[] = {.75, .80, .84, .90};
          xchgBalFrac = xF;
          //     double xaf[] = {1.02, 1.03, 1.05, 1.05};
          double xaf[] = {.75, .80, .84, .90};
          xchgWRReservFrac = xaf;
        }
        src1Bal = 0.;
        //  double src2Bal = 0.;
        //      double dest1Bal = 0.;
        //      double dest2Bal = 0.;

        double s1, r1, r2, s2;
        int s1ix, r1ix, s2ix, r2ix;
        maxAvail = -999;
        A6Row[] xferAvails = {mtggNeeds6, mtggNeeds6, mtgNeeds6, mtgNeeds6};
        double[] xchgMovMin = {balances.curSum() * .0003, balances.curSum() * .0002, balances.curSum() * .0001, balances.curSum() * .00005};
        double xferBalMin = xferMovMin = movMin = xchgMovMin[swap4Step] * redoFrac[reDo];
        A6Row[] xferNeeds = {mtgNeeds6, mtgNeeds6, mtgNeeds6, mtgNeeds6};
        //  mtgNeeds6.addJointBalances();
        // swapNeeds has no filter, it is straight stratVars
        //    swapNeeds = stratVars;

        /**
         * force planet resource xfers if resource availability (-mtgneeds) is
         * higher than staff availability, and resource rawHealths.min are &lt
         * .5 and GROW rawFertilities2 &lt.5
         */
        double rNeedSum = mtgNeeds6.getRow(0).sum();
        double sNeedSum = mtgNeeds6.getRow(1).sum();
        double maxNeed = mtgNeeds6.curMax(0);
        int maxNeedIx = mtgNeeds6.curMaxIx(0);
        // needIx = stratVars.curMaxIx(); // highest need
        double stratH0 = stratVars.max();
        double stratH4 = stratVars.max(4); //high 0,1,2,3
        double stratL3 = stratVars.min(3); //low 0,1,2
        double stratL0 = stratVars.min();
        double stratML0 = 0;
        int stratML0Ix = 0;
        int fillNeedIx = stratVars.curMinIx(); // the least need,high bal
        double dRNeedSum = maxNeed - rNeedSum;
        double dSNeedSum = maxNeed - sNeedSum;
        boolean sNeedy = false; // false: force possible r xfer

        double swaprr = EM.swapXRtoRRcost[pors];
        double swaprs = EM.swapXRtoRScost[pors];
        double swapsr = EM.swapXStoSRcost[pors];
        double swapss =EM.swapXStoSScost[pors];

        // now compute possible avails
        // find the max stratVars
        rawProspects2.sendHist(hist, aPre);
        //stratV.set(stratVars); no change stratVars
        stratV = doNot.filterByDoNot(2, stratVars,nFlag); //elim recent xfers
        needIx = stratV.curMaxIx(); // highest need after doNot
        ixWRSrc = needIx / E.lsecs; // 0:1
        destIx = needIx % E.lsecs;
        
        mov4 = Math.max(movMin + 1., mtgNeeds6.get01(needIx) * 3.);
        // deal with -need: available, set to part of average
        mov3 = mov4 < PZERO ? bav * .1 : mov4;
        hist.add(new History(aPre, History.loopMinorConditionals5, name + " need" + E.rNsIx(needIx) + " =", df(mov4), df(mov3), "rH" + rawProspects2.curMinIx() + "=" + df(rawProspects2.curMin()), "r=" + df(balances.getRow(2).get(needIx % E.lsecs)), "s=" + df(balances.getRow(4).get(needIx % E.lsecs)), "mvMin=" + df(movMin), "xAF=" + df(xchgWRReservFrac[swap4Step]), "mMax" + df(xferMaxMove[swap4Step])));
        hist.add(new History("@x", History.loopMinorConditionals5, name + " step=" + swap4Step, "MovMins", df(xchgMovMin[0]), df(xchgMovMin[1]), df(xchgMovMin[2]), df(xchgMovMin[3]), "availFracs", df(xchgWRReservFrac[0]), df(xchgWRReservFrac[1]), df(xchgWRReservFrac[2]), df(xchgWRReservFrac[3])));
        A2Row rAvail = new A2Row(History.loopMinorConditionals5, "rAvail");
        A2Row sAvail = new A2Row(History.loopMinorConditionals5, "sAvail");
        A2Row rRevAvail = new A2Row(History.loopMinorConditionals5, "rRevAvail");
        A2Row sRevAvail = new A2Row(History.loopMinorConditionals5, "sRevAvail");
        A2Row rAvail1 = new A2Row(History.loopMinorConditionals5, "rAvail1");
        A2Row sAvail1 = new A2Row(History.loopMinorConditionals5, "sAvail1");
        A2Row rRevAvail1 = new A2Row(History.loopMinorConditionals5, "rRevAvail1");
        A2Row sRevAvail1 = new A2Row(History.loopMinorConditionals5, "sRevAvail1");

        //now determine size of possible moves
        rAvail1.setAvailableToExchange(sAvail1, rRevAvail1, sRevAvail1, xchgBalFrac[swap4Step], xchgWRReservFrac[swap4Step], xferMaxMove[swap4Step], balances, xferNeeds[swap4Step], swaprr, swaprs, swapsr, swapss);
      

        //   planet resource, staff needix in highest 4 stratVal
        // first select staff to increase then resource
        if (needIx < E.lsecs && pors == E.P && stratVars.get(1, needIx) > stratH4 && rawProspects2.curMin() > .1) {
          needIx += E.lsecs;  // move needIx to a staff sector
        }
        ixWRSrc = needIx / E.lsecs; // =1:staff
        destIx = needIx % E.lsecs; //sector of most need
       
        // do not take need as a possible source
        doNot.setDoNot(2, ixWRSrc, needIx, doNotDays2);
        aPre = "#a";
        doNot.sendDoNot(0, bLev, aPre, History.loopMajorConditionals4);
        doNot.sendDoNot(1, bLev, aPre, History.loopMajorConditionals4);
        doNot.sendDoNot(2, bLev, aPre, History.loopMajorConditionals4);
        sAvail = doNot.filterByDoNot(2,1, sAvail1,nFlag); 
        sRevAvail = doNot.filterByDoNot(2,1, sRevAvail1,nFlag);
        rAvail = doNot.filterByDoNot(2,0, rAvail1,nFlag);
        rRevAvail = doNot.filterByDoNot(2,0, rRevAvail1,nFlag);
        
          int alev = History.valuesMajor6;
        aPre = "#a";
        rAvail.sendHist(hist,blev, "#wa",alev,"rc rAvail","sg rAvail");
        rAvail1.sendHist(alev, aPre);
        aPre = "#b";
        sAvail.sendHist(hist,blev, aPre,alev,"rc sAvail","sg sAvail");
        sAvail1.sendHist(alev, aPre);
        aPre = "#c";
        rRevAvail.sendHist(hist,blev, aPre,alev,"rc rRevAvail","sg rRevAvail");
        rRevAvail1.sendHist(alev, aPre);
        aPre = "#d";
        sRevAvail.sendHist(hist,blev, aPre,alev,"rc sRevAvail","sg sRevAvail");
        sRevAvail1.sendHist(alev, aPre);
        
        // start with default r straight
        // r xfer
        if (ixWRSrc < 1) {  //do r:0 option
          
          if(false && rAvail.get(0,destIx) != nFlag) {
            String aLine = String.format("ERROR in rAvail needIx %d,destIx %d  should be nFlag %6.2g not %7.5g%n",needIx,destIx,nFlag,rAvail.get(0,destIx));
            rAvail.sendHist(3,"!!");
         //   new Throwable().printStackTrace(System.err);
            throw new MyErr(String.format(String.format("ERROR in rAvail needIx %d,destIx %d  should be nFlag %6.2g not %7.5g%n",needIx,destIx,nFlag,rAvail.get(0,destIx))));
           }
           if(false && rRevAvail.get(0,destIx) != nFlag) {
           // System.err.format("ERROR in rRevAvail needIx %d,destIx %d  should be nFlag %6.2g not %7.5g%n",needIx,destIx,nFlag,rRevAvail.get(0,destIx));
            rRevAvail.sendHist(3,"!!");
    //        new Throwable().printStackTrace(System.err);
            throw new MyErr(String.format("ERROR in rRevAvail needIx %d,destIx %d  should be nFlag %6.2g not %7.5g%n",needIx,destIx,nFlag,rRevAvail.get(0,destIx)));
           }
          //   rAvail.sendHist(hist, History.informationMinor9, "x#", "r rAvail", "s rAvail");
          //  rRevAvail.sendHist(hist, History.informationMinor9, "x#", "r rRevA", "s rRevA");
          r1ix = rAvail.getRow(0).maxIx();
          r1 = rAvail.getRow(0).get(r1ix);
          s1ix = rAvail.getRow(1).maxIx();
          s1 = rAvail.getRow(1).get(s1ix);
          r2ix = rRevAvail.getRow(0).maxIx();
          r2 = rRevAvail.getRow(0).get(r2ix);
          s2ix = rRevAvail.getRow(1).maxIx();
          s2 = rRevAvail.getRow(1).get(s2ix);
          aPre = "#f";
          rAvail.sendHist(alev, aPre);
          rRevAvail.sendHist(alev, aPre);
          mtgNeeds6.sendHist2(alev, aPre);
          source = dest = r;

          // xfer go straight if straight is better
          // remember avail is really size of possible move
          //  1. straight min r avail > rev r min avail--higher max avail
          //  2. stratVars.min r (r health) *1.1 < min s ( S health)
          //      and rhealth better than shealth, rstrat > sstrat
          //  3. mtgNeeds6.ave (Maxed needs) r less need than s (r avail > s avail)
          if (Math.min(r1, s1) > Math.min(r2, s2) || stratVars.getRow(0).min() * 1.1 < stratVars.getRow(1).min() || mtgNeeds6.getRow(0).ave() * 1.1 < mtgNeeds6.getRow(1).ave()) { // do straight option
            rev = 0;
            srcIx = rChrgIx = rAvail.getRow(0).maxIx();
            sChrgIx = rAvail.getRow(1).maxIx();
            rChrg = swaprr;
            if(false && (srcIx == destIx || rAvail.get(0,destIx) != nFlag ||  doNot.get(4+0,destIx) < doNotDays2 )) {
              System.err.format("Error3 xfer source==dest straight r, srcIx %d,destIx %d,rAvail[0][%d] %7.5g, doNot xFer,r,destIx %3.1g %n",srcIx,destIx,destIx,rAvail.get(0,destIx),doNot.get(4+0,destIx));
              rAvail.sendHist(3,"!!");
              throw new MyErr(String.format("Error3 xfer source==dest straight r, srcIx %d,destIx %d,rAvail[0][%d] %7.5g, doNot xFer,r,destIx %3.1g %n",srcIx,destIx,destIx,rAvail.get(0,destIx),doNot.get(4+0,destIx)));
            }
            dest = source = r;
            sChrg = swaprs;
            maxavail1 = maxAvail = Math.min(r1, s1);//max mov
            // mov is min of the 2 max's min'd with original move
            mov2 = Math.min(mov3, Math.min(r1, s1));

          }
          else { // r Rev Avail
            rev = 1;// largest charge to s
            srcIx = rChrgIx = rRevAvail.getRow(0).maxIx();
            sChrgIx = rRevAvail.getRow(1).maxIx();
            if(false && (srcIx == destIx || rRevAvail.get(0,destIx) != nFlag ||  doNot.get(4+0,destIx) < doNotDays2 )) {
              System.err.format("Error3 xfer source==dest strt r, srcIx %d,destIx %d,rRevAvail[0][%d] %7.5g, doNot xFer,s,destIx %3.1g %n",srcIx,destIx,destIx,rRevAvail.get(0,destIx),doNot.get(4+0,destIx));
              rRevAvail.sendHist(3,"!!");
              new Throwable().printStackTrace(System.err);
              throw new MyErr();
            }
            if( false && rRevAvail.get(1,destIx) != nFlag  ) {
              rRevAvail.sendHist(3,"!!");
              throw new MyErr(String.format("Error3 xfer source==dest strt r, srcIx=%d,destIx=%d,rRevAvail[0][%d] %7.5g, doNot xFer,s,destIx %3.1g %n",srcIx,destIx,destIx,rRevAvail.get(1,destIx),doNot.get(4+0,destIx)));
            }
            dest = source = r;
            rChrg = swaprs;
            sChrg = swaprr;
            maxavail2 = maxAvail = Math.min(r2, s2);
            // mov is min of the 2 max's min'd with original move
            mov2 = Math.min(mov3, Math.min(r2, s2));
          }
          // src dest = s
        }
        else {  // now do the s option
          sAvail.sendHist(hist, History.informationMinor9, "x@", "r sAvail", "s sAvail");
          sRevAvail.sendHist(hist, History.informationMinor9, "x@", "r sRevAvail", "s sRevAvail");
          r1 = sAvail.getRow(0).max();
          s1 = sAvail.getRow(1).max();
          r2 = sRevAvail.getRow(0).max();
          s2 = sRevAvail.getRow(1).max();
          r1ix = sAvail.getRow(0).maxIx();
          r1 = sAvail.getRow(0).get(r1ix);
          s1ix = sAvail.getRow(1).maxIx();
          s1 = sAvail.getRow(1).get(s1ix);
          r2ix = sRevAvail.getRow(0).maxIx();
          r2 = sRevAvail.getRow(0).get(r2ix);
          s2ix = sRevAvail.getRow(1).maxIx();
          s2 = sRevAvail.getRow(1).get(s2ix);
          source = dest = s;
          // go straight if
          //  1. straight min s avail > rev min avail
          //  2. stratVars.max(how bad) of RC > SG
          //  3. balances of RC * .8 > SG
          if (Math.min(r1, s1) > Math.min(r2, s2) || stratVars.getRow(0).max() * 1.1 < stratVars.getRow(1).max() || balances.getRow(0).ave() * 1.1 < balances.getRow(1).ave()) { // do straight option
            rev = 0;  // straight s
            rChrgIx = sAvail.getRow(0).maxIx();
            srcIx = sChrgIx = sAvail.getRow(1).maxIx();
            // never xfer from the destination (should have been a INCR)
            if((srcIx == destIx || sAvail.get(1,destIx) != nFlag ||  doNot.get(4+1,destIx) < PZERO)) {
              sAvail.sendHistcg();
               throw new MyErr(String.format("Error3 xfer source==dest strt s, srcIx=%d,destIx=%d,sAvail[1][%d] %7.5g, doNot xFer,s,destIx %3.1g %n",srcIx,destIx,destIx,sAvail.get(1,destIx),doNot.get(4+1,destIx)));
            }
            maxavail3 = maxAvail = Math.min(r1, s1);
            rChrg = swapsr;
            sChrg = swapss;
            // mov is min of the 2 max's min'd with original move
            mov2 = Math.min(mov3, Math.min(r1, s1));
          }
          else {  // s  xferCross
            rev = 1;
            rChrgIx = sRevAvail.getRow(0).maxIx();
            srcIx = sChrgIx = sRevAvail.getRow(1).maxIx();
            double sMax = sRevAvail.getRow(1).get(srcIx);
            if(false && (srcIx == destIx || sRevAvail.get(1,destIx) != nFlag ||  doNot.get(4+1,destIx) < doNotDays2 )) {
              sRevAvail.sendHistcg();
            //  new Throwable().printStackTrace(System.err);
              throw new MyErr(String.format("Error3 %s n=%d xfer source==dest strt s, srcIx=%d,destIx=%d,sRevAvail[1][%d] %7.5g, sMax %7.5g doNot xFer,s,destIx %3.1g, doNot[5] srcIx %3.1g doNot[1] %3.1g %n",ec.name,n,srcIx,destIx,destIx,sRevAvail.get(1,destIx),sMax,doNot.get(4+1,destIx), doNot.get(4+1,srcIx),doNot.get(1,srcIx)));
            }
            if( sRevAvail.get(0,destIx) != nFlag ) {
              sRevAvail.sendHistcg();
              throw new MyErr(String.format("Error3 xfer source==dest strt s, srcIx=%d,destIx=%d,sRevAvail[0][%d] %7.5g, doNot xFer,s,destIx %3.1g %n",srcIx,destIx,destIx,sRevAvail.get(0,destIx),doNot.get(4+1,destIx)));
            }
            maxavail4 = maxAvail = Math.min(r2, s2);
            // mov is min of the 2 max's min'd with original move
            mov2 = Math.min(mov3, Math.min(r2, s2));
            rChrg = swapss;
            sChrg = swapsr;

          }// end srev type
        } // end staff xfer

        // join all 4 options
        //     double xferMin[] = {.0001, .00005, .00001, .000003};
        //     double xferBalMin = Math.max(balances.curSum() * xferMin[swap4Step], 1.);
        //  movMin = xchgMovMin[swap4Step]; previously set
        aPre = "#g";

        //       xferAvail.sendHist(hist, "xfer");
        // swapNeeds.titl = "swapNeeds";
        stratV.sendHist(alev, aPre);
        bals.listBalances(History.dl, aPre, History.valuesMajor6);
        // recalculate the maxMov= min of r and s try to ignore
        double rdiv = ixWRSrc < 1 ? 1.09 + rChrg : .09 + rChrg;
        double sdiv = ixWRSrc < 1 ? .09 + sChrg : 1.09 + sChrg;
        //determine index for each SubAsset
        // srcIx means the source of move and the cost for the same sector
        int rmIx = ixWRSrc < 1 ? srcIx : rChrgIx;
        int smIx = ixWRSrc < 1 ? sChrgIx : srcIx;
        rmov = xchgBalFrac[swap4Step] * bals.get(0, rmIx) / rdiv;
        smov = xchgBalFrac[swap4Step] * bals.get(1, smIx) / sdiv;
        double rFracC = -xchgWRReservFrac[swap4Step] * mtgNeeds6.get(0, rmIx) / rdiv;
        double sFracC = -xchgWRReservFrac[swap4Step] * mtgNeeds6.get(1, smIx) / sdiv;
        double rsmov = Math.min(Math.min(rmov, smov), Math.min(rFracC, sFracC));
        // above looks right

        mov1 = mov2; // no change
        // mov1 least of both max in each case
        mov = Math.min(mov1, bals.get(ixWRSrc, srcIx) - xchgBalFrac[swap4Step] * bals.get(ixWRSrc, srcIx));
        // mov = Math.min(mov, (source.balance.get(srcIx) + source.partner.balance.get(srcIx)) * .5);

        //determine where xfer mov is applied to r or s cost
        rmov1 = ixWRSrc < 1 ? mov : 0.;
        smov1 = ixWRSrc < 1 ? 0. : mov;
        // source is from the same SubAsset as the dest
        cmd = E.uxdecrs[rev][ixWRSrc][swap4Step];
       if(E.debugDouble){
          rcost = doubleTrouble(
                  doubleTrouble(mov) * 
                          doubleTrouble(rChrg));
        scost = doubleTrouble(
                doubleTrouble(mov) * 
                        doubleTrouble(sChrg)); 
         
       }else {
        rcost = mov * rChrg;
        scost = mov * sChrg;
       }
        // 9/6/15 change to charge reserve SubAssets first
        hist.add(new History(aPre, 4, nTitle(cmd.toString() + source.aschar + srcIx + " => " + dest.aschar + destIx),
                "m2=" + df(mov2), "m1=" + df(mov1), "m=" + df(mov),
                "r$" + rChrgIx + "=" + df(rChrg), df(rcost), "s$" + sChrgIx + "=" + df(sChrg), df(scost),
                "rev=" + rev, "mA" + df(maxAvail), "src" + df(balances.get01(ixWRSrc, srcIx)), "<<<<<<<L"));
        hist.add(new History(aPre, History.valuesMajor6, nTitle("more"), "movMin", df(movMin), "Nds" + (ixWRSrc == 0 ? "r" : "s") + srcIx + "=", df(mtgNeeds6.get(ixWRSrc, srcIx)), "bal" + (ixWRSrc == 0 ? "r" : "s") + srcIx + "=", df(balances.get01(ixWRSrc, srcIx)), "<<<<<<3L"));
        balances.checkBalances(this);
        if (stopped[2] > 0) {
          hist.add(new History(aPre, History.loopMinorConditionals5, nTitle("stop" + swapLoops + " " + cmd.name()), "mov" + df(mov), "dest" + df(bals.getRow(3 + 2 * ixWRSrc).get(srcIx)), "src" + df(bals.getRow(2 + 2 * ixWRSrc).get(srcIx)), "nmov" + df(nmov), "gmov" + df(gmov), "dest big enough"));
        } else if (mov < PZERO) {
            hist.add(new History(aPre, 4, nTitle("skip" + swapLoops + " " + cmd.name() + srcIx), " mov=" + df(mov), "lt 0", "needIx=" + needIx, "nv=" + swapNeeds.max()));
          } else if (mov < movMin) {
            hist.add(new History(aPre, 4, nTitle(" skip" + swapLoops + " " + cmd.name() + srcIx), "mov=", df(mov), "lessThan", "required", df(movMin)));
          } else if(destIx == srcIx){
            hist.add(new History(aPre,History.loopMajorConditionals4,nTitle("SKIP =="  + " " + cmd.name() + srcIx),"mv"+df(mov),"destIx"+destIx + "==srcIx" + srcIx,"<<<<<<<<<<"));
          } else {
            hist.add(new History(aPre, History.loopMinorConditionals5, nTitle(cmd + source.aschar + srcIx + " => " + dest.aschar + destIx), " mov=" + df(mov), "rc=" + df(balances.get(0, rChrgIx)), "r$" + rChrgIx + "=" + df(rcost), "=>" + df(balances.get(0, rChrgIx) - (ixWRSrc < 1 ? rcost + mov : rcost)), "s=" + df(balances.get(1, sChrgIx)), "s$" + sChrgIx + "=" + df(scost), "=>" + df(balances.get(1, sChrgIx) - (ixWRSrc < 1 ? scost : scost + mov)), dest.aschar + destIx + "=" + df(balances.get(ixWRSrc, destIx)), "=>" + df(balances.get(ixWRSrc, destIx) + mov), "<<<<<<<<<<"));
            resource.balance.negError("resource.balance");
            balances.checkBalances(this);
            // continue the join both branches
            prevsrc = balances.get(ixWRSrc, srcIx);
            prevdest = balances.get(ixWRSrc, destIx);
            prevosrc = source.partner.balance.get(srcIx);
            prevodest = dest.partner.balance.get(destIx);
            setStat(EM.SWAPRXFERCOST, pors, clan, rcost / bals.getRow(2).sum(), 1);
            setStat(EM.SWAPSXFERCOST, pors, clan, scost / bals.getRow(4).sum(), 1);
            bals.sendHist(hist, aPre);

            String strRS[] = {"r", "s"};
            int tt1 = rawProspects2.curMinIx();
            int tt2 = (int) tt1 / E.lsecs;
            int tt3 = tt1 % E.lsecs;
            int tt4 = rawProspects2.curMinIx(1);
            int tt5 = (int) tt4 / E.lsecs;
            int tt6 = tt4 % E.lsecs;
            hist.add(new History(aPre, History.loopIncrements3, nTitle("p") + cmd + srcIx + "->" + destIx, "mov=" + df(mov), "mMin" + df(movMin), "r$" + rChrgIx + "=" + df(rcost), "s$" + sChrgIx + "=" + df(scost), "H" + rorss[tt2] + tt3 + "=" + df(rawProspects2.curMin()), "HS" + rawProspects2.curSum(), "rS" + df(bals.getRow(0).sum()), "sS" + df(bals.getRow(1).sum()), "mtg" + df(mtgNeeds6.getRow(0).sum()), df(mtgNeeds6.getRow(1).sum()), "<<<<<<<"));

            hist.add(new History(aPre, History.loopMajorConditionals4, "n" + n + " " + cmd.toString() + " " + source.aschar + srcIx + " -> " + dest.aschar + destIx, "mov=" + df(mov), "rcst=" + df(rcost), "scst=" + df(scost), "dR=" + df(dstRCst), "dS=" + df(dstSCst), "dosrc=" + df(prevosrc - source.partner.balance.get(srcIx)), "dodst=" + df(dest.partner.balance.get(destIx) - prevodest)));

            hist.add(new History(aPre, rlev, "n" + n + " resource" + srcIx, r.balance));

            //now test balances before doing move
            if (bals.get(0, rmIx) < (rmov1 + rcost)) {
              E.myTest(true, "ERR  %s, rcbal%d=%7.2g <  rmov1=%7.2g + rcost=%7.2g = rChrg=%7.5g * mov=%7.2g ,rshort=%7.2g, age=%d, n=%d, swap4Step=%d, redo=%d<<<<<", cmd.toString(), rmIx, bals.get(0, rmIx), rmov1, rcost, rChrg, mov, bals.get(0, rmIx) - rmov1 - rcost, ec.age, n, swap4Step, reDo);
            }
            if (bals.get(1, smIx) < (smov1 + scost)) {
              E.myTest(true, "ERR  %s, scbal%d=%7.2g <  smov1=%7.2g + scost=%7.2g = sChrg%7.5g * mov=%7.2g ,sshort=%7.2g, age=%d, n=%d, swap4Step=%d, redo=%d<<<<<", cmd.toString(), smIx, bals.get(1, smIx), smov1, scost, sChrg, mov, bals.get(1, smIx) - smov1 - scost, ec.age, n, swap4Step, reDo);
            }
            balances.checkBalances(this);
            r.cost3(rcost, rChrgIx, .00);
            hist.add(new History(aPre, rlev, "n" + n + " resource" + srcIx, r.balance));
           
            resource.balance.negError("resource.balance");
            s.checkGrades();
            s.cost3(scost, sChrgIx, .00);
            bals.sendHist(hist, "xc");
            source.putValue(balances, mov, srcIx, destIx, dest, 1, 0.);

            hist.add(new History(aPre, History.loopMinorConditionals5, "n" + n + " " + cmd.toString() + " " + source.aschar + srcIx + " -> " + dest.aschar + destIx, "mov=" + df(mov), "rcst=" + df(rcost), "scst=" + df(scost)));
            bals.sendHist(hist, aPre);
            // count successive r or s xfers
            if (ixWRSrc == 1) {
              rxfers++;
              //sxfers = 0;
            }
            else {
              sxfers++;
              //  rxfers = 0;
            }
            swapped = true;
            swapType = 2;
            doNot.setDoNot(swapType, ixWRSrc, destIx,doNotDays3);
            if (History.dl > 4) {
              StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];

              hist.add(new History(aPre, rlev, ">>>>n" + n + "XFERD ", a0.getFileName(), wh(a0.getLineNumber()), (failed ? "failed" : "!!!failed"), (swapped ? "swapped" : "!!!swapped"), swapLoops + "=swapLoops", "$=" + df(sumTotWorth), "<<<<<<<<<<"));
            }

            return true;
          }

          // no success this loop
        } // loop back from end on swapLoops
        if (History.dl > History.valuesMajor6) {
          StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
          hist.add(new History(aPre, rlev, "n" + n + " end swaps", a0.getFileName(), wh(a0.getLineNumber()), (failed ? "failed" : "!!!failed"), (swapped ? "swapped" : "!!!swapped"), swapLoops + "=swapLoops", "$=" + df(sumTotWorth)));
        }
  
    //  }// for swapLoops
      swapType = -5;
      return false;
    } // end CashFlows.swaps

    void swapResults(String aPre
    ) {
      if (History.dl > 4) {
        StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];

        hist.add(new History(aPre, 5, ">>>>n" + n + " swapres", a0.getFileName(), wh(a0.getLineNumber()), (failed ? "failed" : "!!!failed"), (swapped ? "swapped" : "!!!swapped"), swapLoops + "=swapLoops", "$=" + df(sumTotWorth),"srcIx="+srcIx,"destIx=" + destIx,"ixWRSrc=" + ixWRSrc, "<<<<<<<<<<<"));
      }
      if (source == null || dest == null || srcIx < 0 || destIx < 0) {
        hist.add(new History(aPre, 4, "n" + prevns[0].n + " notswapped?", (cmd == null ? "!cmd" : cmd.toString()), "s=" + (source == null ? "null" : source.aschar) + srcIx, "d=" + (dest == null ? "null" : dest.aschar) + destIx, "f=" + (forRes == null ? "null" : forRes.aschar) + forIx, "-----", "-----"));
        return;
      }
      String sw = (swapped ? source.as1 != dest.as1 ? "Trd=" : srcIx != destIx ? "xfr=" : "swp=" : " !!! ");
      sw = " " + cmd.toString() + " " + srcIx + (forIx >= 0 ? (" for " + forIx) : "") + "=>" + destIx;
      if(ixWRSrc < 2){
      hist.add(new History(aPre, 4, "n" + n + sw, "F" + rawFertilities2.curMinIx() + "=" + df(rawFertilities2.min()), "f" + rawFertilities2.curMinIx(1) + "=" + df(rawFertilities2.curMin(1)), "H" + rawProspects2.curMinIx() + "=" + df(rawProspects2.min()), "h" + rawProspects2.curMinIx(1) + "=" + df(rawProspects2.curMin(1)), " m=" + df(mov), "rc=" + df(rcost), "sc=" + df(scost), "Fd=" + df(rawFertilities2.get(ixWRSrc, destIx)), "Hd=" + df(rawProspects2.get(ixWRSrc, srcIx))));
      }
      if (History.dl > 4) {
        StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];

        hist.add(new History(aPre, 7, ">>>>n" + n + " skips", wh(a0.getLineNumber()), (failed ? "failed" : "!!!failed"), (swapped ? "swapped" : "!!!swapped"), swapLoops + "=swapLoops", "$=" + df(sumTotWorth),"<<<<<<<<<"));
      }
    }

    void prevSwapResults(String aPre
    ) {
      if (History.dl > 4) {
        StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];

        hist.add(new History(aPre, 5, "enter n" + n + " prevSwapResults at", wh(a0.getLineNumber()), (failed ? "failed" : "!!!failed"), (swapped ? "swapped" : "!!!swapped"), swapLoops + "=swapLoops"));
      }
      if (n == 0) {
        hist.add(new History(aPre, 4, "n=" + n, "no Prev swap ", "results", "------", "------", "------", "--------", "-------", "-------", "-------", "-------"));
        return;  // no previous results yet
      }
      if (source == null || dest == null || srcIx < 0 || destIx < 0 || prevns[0].nSource == 4 || prevns[0].nDest == 4) {
        hist.add(new History(aPre, 4, "n" + prevns[0].n + " unswapped", "s=" + (source == null ? "null" : source.aschar) + srcIx, "d=" + (dest == null ? "null" : dest.aschar) + destIx, "f=" + (forRes == null ? "null" : forRes.aschar) + forIx, "?????????????????", "", "", ""));
        return;
      }

      if (healths != null && (prevns[0].healths != null) && fertilities != null && prevns[0].rawFertilities2 != null) {
        String sw = (prevns[0].swapped ? prevns[0].doingTrade ? "Trad=" : prevns[0].srcIx != prevns[0].destIx ? "xfr=" : "swp=" : " !!! ");
        sw = cmd.toString() + " ";
        // so current swap values are what is being shown, not prevns[0] values
        hist.add(new History(aPre, 4, "n=" + prevns[0].n + " "
                + sw
                + aChar[source.sIx] + srcIx
                + (forIx >= 0 ? " for " + forIx : " to "
                        + aChar[dest.sIx] + destIx), " m=" + df(mov), "rc=" + df(rcost), "sc=" + df(scost), "difH=" + (df(health - prevns[0].health)), "H=" + df(prevns[0].health), "=>" + df(health), "F=" + df(prevns[0].fertility), "=>" + df(fertility), "$" + df(prevns[0].sumTotWorth), "=>$" + df(sumTotWorth)));

        /*
         hist.add(new History(aPre, 4, "n=" + prevns[0].n + " moreH "
         + aChar[source.sIx] + srcIx
         + (forIx >= 0 ? " for " + forIx : " to "
         + aChar[dest.sIx] + destIx), "HR" + healths.getRow(0).minIx() + "=" + df(rawHealths.getRow(0).min()), rawHealths.getRow(0).minIx(1) + "=" + df(rawHealths.getRow(0).min2()), "ave=" + df(prevns[0].rawHealths.getRow(0).ave()), "HS" + rawHealths.getRow(1).minIx() + "=" + df(rawHealths.getRow(1).min()), rawHealths.getRow(1).minIx(1) + "=" + df(rawHealths.getRow(1).min2()), "ave=" + df(rawHealths.getRow(0).ave()), "", "", ""));

         hist.add(new History(4, "n=" + prevns[0].n + " moreF "
         + aChar[source.sIx] + srcIx
         + (forIx >= 0 ? " for " + forIx : " to "
         + aChar[dest.sIx] + destIx), "FR" + rawFertilities.getRow(0).minIx() + "=" + df(rawFertilities.getRow(0).min()), rawFertilities.getRow(0).minIx(1) + "=" + df(rawFertilities.getRow(0).min2()), "ave=" + df(prevns[0].rawFertilities.getRow(0).ave()), "FS" + rawFertilities.getRow(0).minIx() + "=" + df(rawFertilities.getRow(0).min()), rawFertilities.getRow(0).minIx(1) + "=" + df(rawFertilities.getRow(0).min2()), "ave=" + df(rawFertilities.getRow(0).ave())));
         */
      }
      else {
        hist.add(new History(aPre, 4, "n=" + prevns[0].n, "no swap this n"));
      }

      //  double difrc = prevns[0].sumRCWorth - sumRCWorth;
      //    double difsg = prevns[0].sumSGWorth = sumSGWorth;
      if (E.setIncr.contains(cmd)) {
        //      EM.gameRes.SWAPINCRWRCOSTSCUM.wet(pors, clan, difrc);
        // setStat("SWAPINCRWRCOSTSCUM", pors, clan, difrc);
        //       EM.gameRes.SWAPINCRWSCOSTSCUM.wet(pors, clan, difsg);
        // setStat("SWAPINCRWSCOSTSCUM", pors, clan, difsg);
      }
      else if (E.setDecr.contains(cmd)) {
        //     EM.gameRes.SWAPDECRWRCOSTSCUM.wet(pors, clan, difrc);
        //  setStat("SWAPDECRWRCOSTSCUM", pors, clan, difrc);
        //     EM.gameRes.SWAPINCRWSCOSTSCUM.wet(pors, clan, difsg);
        // setStat("SWAPINCRWSCOSTSCUM", pors, clan, difsg);
      }
      else if (E.setXXdecr.contains(cmd)) {
        //   EM.gameRes.SWAPXCHGWRCOSTSCUM.wet(pors, clan, difrc);
        //setStat("SWAPXCHGWRCOSTSCUM", pors, clan, difrc);
        //   EM.gameRes.SWAPXCHGWSCOSTSCUM.wet(pors, clan, difsg);
        //setStat("SWAPXCHGWSCOSTSCUM", pors, clan, difsg);
      }
      //    histdifs(7, "resource", resource.balance, prevns[0].resource.balance);
      //   histdifs(7, "cargo", cargo.balance, prevns[0].cargo.balance);
      //   histdifs(7, "staff", staff.balance, prevns[0].staff.balance);
      //   histdifs(7, "guests", guests.balance, prevns[0].guests.balance);
      //   histdifs(7, "work", staff.work, prevns[0].staff.work);
      //    histdifs(4, "RFertility", r.fertility, prevns[0].r.fertility);
      //   histdifs(4, "SFertility", s.fertility, prevns[0].s.fertility);
      //   histdifs(4, "RFertility1", r.fertility1, prevns[0].r.fertility1);
      //    histdifs(4, "SFertility1", s.fertility1, prevns[0].s.fertility1);
      //   histdifs(4, "RReqFertility", r.tReqGrowthFertility, prevns[0].r.tReqGrowthFertility);
      //  histdifs(4, "SReqFertility", s.tReqGrowthFertility, prevns[0].s.tReqGrowthFertility);
      //   histdifs(4, "RHealth", r.health, prevns[0].r.health);
      //   histdifs(4, "SHealth", s.health, prevns[0].s.health);
      if (History.dl > 44) {
        StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];

        hist.add(new History(aPre, 5, "n" + n + " prevSwapRes", wh(a0.getLineNumber()), (failed ? "failed" : "!!!failed"), (swapped ? "swapped" : "!!!swapped"), swapLoops + "=swapLoops", "$=" + df(sumTotWorth)));
      }
      /*
       histdifs(7, "r.tReqMaintRemnant", r.tReqMaintRemnant, prevns[0].r.tReqMaintRemnant);
       histdifs(7, "r.nptMTCosts", r.nptMTCosts, prevns[0].r.nptMTCosts);
       histdifs(7, "r.nptMTRemnant", r.nptMTRemnant, prevns[0].r.nptMTRemnant);
       histdifs(7, "yRReqGroMTHPenRemnant", yRReqGrowthMTHPenRemnant, prevns[0].yRReqGrowthMTHPenRemnant);
       histdifs(7, "yRRawGroMTHPenRemnant", r.hptRawGrowthRemnant, prevns[0].r.hptRawGrowthRemnant);
       histdifs(7, "yRMTLimGHPenRemnant", yRMTLimitedGHPenRemnant, prevns[0].yRMTLimitedGHPenRemnant);
       histdifs(7, "s.tReqMaintRemnant", s.tReqMaintRemnant, prevns[0].s.tReqMaintRemnant);
       histdifs(7, "s.nptMTCosts", s.nptMTCosts, prevns[0].s.nptMTCosts);
       histdifs(7, "s.nptMTRemnant", s.nptMTRemnant, prevns[0].s.nptMTRemnant);
       histdifs(7, "r.tReqMaintHealth", r.tReqMaintHealth, prevns[0].r.tReqMaintHealth);
       histdifs(7, "s.tReqMaintHealth", s.tReqMaintHealth, prevns[0].s.tReqMaintHealth);
       histdifs(7, "r.tMTHealth", r.tMTHealth, prevns[0].r.tMTHealth);
       histdifs(7, "s.tMTHealth", s.tMTHealth, prevns[0].s.tMTHealth);

       histdifs(7, "ySReqGMTHPenRemnant", ySReqGrowthMTHPenRemnant, prevns[0].ySReqGrowthMTHPenRemnant);
       histdifs(7, "ySRawGMTHPenRemnant", s.hptRawGrowthRemnant, prevns[0].s.hptRawGrowthRemnant);
       histdifs(7, "ySMTLimGHPenRemnant", ySMTLimitedGHPenRemnant, prevns[0].ySMTLimitedGHPenRemnant);
       */
 /*
       histdifs(7, "RjRSerReqForMaintenance", resource.jRServerRequiredForMaintenance, prevns[0].resource.jRServerRequiredForMaintenance);
       histdifs(7, "CjRSerReqForMaintenance", cargo.jRServerRequiredForMaintenance, prevns[0].cargo.jRServerRequiredForMaintenance);

       histdifs(7, "CiRConT1yrCost", cargo.iRConsumerT1yrCost, prevns[0].cargo.iRConsumerT1yrCost);
       histdifs(7, "CiConMTCost", cargo.iConsumerMTCost, prevns[0].cargo.iConsumerMTCost);
       histdifs(7, "CiConReqForMaintenance", cargo.iConsumerRequiredForMaintenance, prevns[0].cargo.iConsumerRequiredForMaintenance);
       histdifs(7, "CiRConReqFGrowth", cargo.iRConsumerRequiredForGrowth, prevns[0].cargo.iRConsumerRequiredForGrowth);
       histdifs(7, "CiConMTGCst", cargo.iConsumerMTGCost, prevns[0].cargo.iConsumerMTGCost);
       */
      if (rawFertilities2.getRow(0).min(3) > 1. && rawFertilities2.getRow(0).min() > 1. && rawProspects2.getRow(0).min() > 1. && rawProspects2.getRow(1).min() > 1.) {
        hist.add(new History(4, "N" + n + " Finished", "RF=" + df(rawFertilities2.getRow(0).min()), "SF=" + df(rawFertilities2.getRow(1).min()), "RH=" + df(rawProspects2.getRow(0).min()), "SH=" + df(rawProspects2.getRow(1).min()), "Health=", df(health), "$=" + df(sumTotWorth)));
        dest = source = null;
      }
      if (History.dl > 50) {
        StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];

        hist.add(new History(aPre, 7, ">>>>n" + n + " skips", wh(a0.getLineNumber()), (failed ? "failed" : "!!!failed"), (swapped ? "swapped" : "!!!swapped"), swapLoops + "=swapLoops", "$=" + df(sumTotWorth)));
      }
    }

    void histdifs(int lev, String title, ARow A, ARow prevA
    ) {
      A.get(0);
      prevA.get(0);
      hist.add(new History(lev, "n" + n + " new " + title, A));
      hist.add(new History(lev, "n" + n + " old " + title, prevA));
      hist.add(new Difhist(lev, "n" + n + " dif " + title, A, prevA));
    }

    void doGrowth(String aPre
    ) {
      bals.getRow(GROWTHSIX + RIX).set(r.growth);
      bals.getRow(GROWTHSIX + CIX).set(c.growth);
      bals.getRow(GROWTHSIX + SIX).set(s.growth);
      bals.getRow(GROWTHSIX + GIX).set(g.growth);
      double preGrow = sumTotWorth;
      resource.doGrow(aPre);
      cargo.doGrow(aPre);
      guests.doGrow(aPre);
      staff.doGrow(aPre);

    }

    void doMaintCost(String aPre
    ) {
      r.doCost(aPre, "r mtgCosts", mtgCosts10.getRow(0));
      s.doCost(aPre, "s mtgCosts", mtgCosts10.getRow(1));
    }

    void doTravCost(String aPre
    ) {
      //  r.doCost(aPre, "r travelCosts", travelCosts.getRow(0));
      //  s.doCost(aPre, "s travelCosts", travelCosts.getRow(1));
    }

    void doGrowthCost(String aPre
    ) {
      //  r.doCost(aPre, "r growthCosts", growthCosts.getRow(0));
      //  s.doCost(aPre, "s growthCosts", growthCosts.getRow(1));
    }

    void costsAndGrowth(A6Row mtgCosts
    ) {
      ARow prevrbal = new ARow(), prevcbal = new ARow(), prevsbal = new ARow(), prevgbal = new ARow();
      ARow prevrbal2 = new ARow(), prevcbal2 = new ARow(), prevsbal2 = new ARow(), prevgbal2 = new ARow();
      ARow prevKnowledge = new ARow(), prevcommonKnowledge = new ARow(), prevnewKnowledge = new ARow(), prevManuals = new ARow();;
      if (History.dl > 6) {
        prevrbal = new ARow().set(resource.balance);
        prevcbal = new ARow().set(cargo.balance);
        prevsbal = new ARow().set(staff.balance);
        prevgbal = new ARow().set(guests.balance);
        prevKnowledge = new ARow().set(knowledge);
        prevcommonKnowledge = new ARow().set(commonKnowledge);
        prevnewKnowledge = new ARow().set(newKnowledge);
        prevManuals = new ARow().set(manuals);
        hist.add(new History(3, "R Grow", r.growth));
        hist.add(new History(3, "S Grow", s.growth));
        hist.add(new History(3, "C Grow", c.growth));
        hist.add(new History(3, "G Grow", g.growth));
        hist.add(new History(3, "r cost", r.hptMTGCosts));
        hist.add(new History(3, "s cost", s.hptMTGCosts));
        // ARow rgrowunit = new ARow().setAdivbyB(resource.growth, resource.balance);
        histdifs(7, "r growdif", resource.rawGrowth, r.growth);
        // ARow cgrowunit = new ARow().setAdivbyB(cargo.growth, cargo.balance);
        histdifs(7, "c growdif", cargo.rawGrowth, c.growth);
        //  ARow sgrowunit = new ARow().setAdivbyB(staff.growth, staff.balance);
        histdifs(7, "s growdif", staff.rawGrowth, s.growth);
        //  ARow ggrowunit = new ARow().setAdivbyB(guests.growth, guests.balance);
        histdifs(7, "g growdif", guests.rawGrowth, g.growth);
      }
      resource.doGrow(aPre);
      cargo.doGrow(aPre);
      guests.doGrow(aPre);
      staff.doGrow(aPre);
     // resource.sumGrades(-4,0.); // should be in doGrow
     // cargo.sumGrades(-4,0.);
      //staff.sumGrades(-4,0.);
     // guests.sumGrades(-4,0.);
      if (History.dl > 6) {

        histdifs(7, "rcost bal", resource.balance, prevrbal);
        histdifs(7, "ccost bal", cargo.balance, prevcbal);
        histdifs(7, "scost bal", staff.balance, prevsbal);
        histdifs(7, "gcost bal", guests.balance, prevsbal);

      }
      prevrbal2 = new ARow().set(resource.balance);
      prevcbal2 = new ARow().set(cargo.balance);
      prevsbal2 = new ARow().set(staff.balance);
      prevgbal2 = new ARow().set(guests.balance);

      resource.doCost(aPre, mtgCosts.getRow(2));
      staff.doCost(aPre, mtgCosts.getRow(4));
      staff.checkSumGrades();
      if (History.dl > 6) {
        histdifs(7, "rgrow bal", resource.balance, prevrbal2);
        histdifs(7, "cgrow bal", cargo.balance, prevcbal2);
        histdifs(7, "sgrow bal", staff.balance, prevsbal2);
        histdifs(7, "ggrow bal", guests.balance, prevsbal2);
        histdifs(7, "rdo bal", resource.balance, prevrbal);
        histdifs(7, "cdo bal", cargo.balance, prevcbal);
        histdifs(7, "sdo bal", staff.balance, prevsbal);
        histdifs(7, "gdo bal", guests.balance, prevsbal);
        histdifs(7, " newKnowledge", newKnowledge, prevnewKnowledge);
        histdifs(7, " commonKnowledge", commonKnowledge, prevcommonKnowledge);
        histdifs(7, " knowledge", knowledge, prevKnowledge);
        histdifs(7, " manuals", manuals, prevManuals);

      }
    }

  } // end trade.Assets.CashFlow   }  //end calcRawCosts

  void yDisplayBalances(int level, String title
  ) {  //CashFlow  now unused

  }
//    hist.add(new History(20, title, "0Life", "1Struct", "2Energy", "3Propel", "4Defense", "5Gov", "6Col", "Min", "Sum", "Ave"));

  /**
   * create memory class to remember some of the CashFlow variables used in
   * prev1Yr,..., prev1n...
   */
  /**
   * determine if current min rawHealths is more positive than the rawHealth min
   * at most recent decr
   *
   * @param nCnt number of previous n to check
   * @param curRawHealths current raw healths
   * @param curRawFertilities current raw Fertilities
   * @param sourceIx proposed source move working to reserve
   * @param forIx ix 0-13 we hope to increase rawHealths
   * @param move amount of proposed move
   * @return true, prev raised rawHealths, false did not
   */
  boolean prevDecrIncHealth(int nCnt, A6Row rawHealths, A6Row rawFertilities, int sourceIx, int forIx, double move) {
    boolean ret = false;
    for (int j = 0; j < nCnt; j++) {

    }

    return ret;
  }

} // end Assets
