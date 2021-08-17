/*
 Copyright (C) 2015 Albert Steiner


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

/**
 * This is in many ways and extension of StarTrader the main program which also
 * contains the user interface and methods to see results and change priorities
 * EM contains the variable which can be changed, it also contains related
 * constants.
 *
 * @author albert Steiner
 */
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.TreeMap;
import javax.swing.JTable;

/**
 * this is an output interface, it specifies where to put values using values
 * should be in EM param name.accept(data)
 *
 * @author albert
 */
interface Consumer1 {

  public double apply(E d, double v);
}

class EM {

  /**
   * eE and eM are set when a different eM is chosen, it is the only value
   * needed. so they can be static across all instances of eM
   */
  static E eE;   // EM.eE
  static EM eM;  // EM.eM
  EM myEM;  // This is the this of an instance
  static StarTrader st;
  public static boolean myTestDone = false;

  // The following is a start of the capability to save and instance of
  // a game at the current level with new referents for arrays etc.
  // the cureent game could continue or another EM instance could be made active
  // could be reloded onto the active instance of 
  EM copyTo; // during a copy, doVal, doRes etc must copy
  boolean doingCopy = false;

  static final public String statsButton0Tip = "0: Cum Game Worths,";
  static final public String statsButton1Tip = "1: cum Favors and trade effects";
  static final public String statsButton2Tip = "2: Catastrophes, deaths, randoms, forwardfund";
  static final public String statsButton3Tip = "3: Deaths. trades acc";
  static final public String statsButton4Tip = "4:  dth, Rej misd Trades";
  static final public String statsButton5Tip = "5: trades acc, rej, missed ";
  static final public String statsButton6Tip = "6: ForwardFund, ForFunds&deaths";
  static final public String statsButton7Tip = "7: Resource, staff, knowledge values";
  static final public String statsButton8Tip = "8: creates. growth and costs details";
  static final public String statsButton9Tip = "9: Catastrophes, Fertility, health and effects";
  static final public String statsButton10Tip = "10: years 0,1,2,3 worth inc, costs, efficiency,knowledge,phe";
  static final public String statsButton11Tip = "11: years 4,5,6,7 worth inc, costs, efficiency,knowledge,phe ";
  static final public String statsButton12Tip = "12: years 8->15 worth inc, costs, efficiency,knowledge,phe ";
  static final public String statsButton13Tip = "13: years 16->31 worth inc, costs, efficiency,knowledge,phe ";
  static final public String statsButton14Tip = "14: years 32+ worth inc, costs, efficiency,knowledge,phe ";
  static final public String statsButton15Tip = "15: swap factors";
  static final public String statsButton16Tip = "16: Swaps years incr skips, redos and dos";
  static final public String statsButton17Tip = "17: Swaps years decr skips, redos and dos";
  static final public String statsButton18Tip = "18: Swaps years xfer skips, redos and dos";
  static final public String statsButton19Tip = "19: Swaps years Forward Fund imbalance or save";
  static final public String statsButton20Tip = "20: TB assigned";

  static final int lStatsWaitList = 10;
  static String[] statsWaitList = new String[lStatsWaitList];
  static volatile int ixStatsWaitList = 0;
  static String description = "";
//  Econ ec;
  ArrayList<Econ> ships = new ArrayList<Econ>();
  ArrayList<Econ> deadPlanets = new ArrayList<Econ>();
  ArrayList<Econ> deadShips = new ArrayList<Econ>();
  static volatile  ArrayList<Econ> planets = new ArrayList<Econ>();
  static volatile  ArrayList<Econ> econs = new ArrayList<Econ>();
  static volatile  ArrayList<EM> ems = new ArrayList<EM>();
  static volatile  TreeMap<String, Econ> names2ec = new TreeMap<String, Econ>();
  static volatile  ArrayList<String> emNames = new ArrayList<String>();
  static int[] envsPerYear = {10, 20, 30, 40, 50, 60, 10};
  // int porsClanCntd[][] = {{0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}}; // defaults
  static volatile int porsClanCnt[][] = {{0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}};
  //int clanCntd[] = {0, 0, 0, 0, 0};
  static volatile int clanCnt[] = {0, 0, 0, 0, 0};
  static volatile Integer econCnt = 0;
  // int porsCntd[] = {0, 0};
  static volatile int porsCnt[] = {0, 0};
  
  static volatile int porsClanTraded[][] = {{0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}};
  static volatile int clanTraded[] = {0, 0, 0, 0, 0};
  static volatile int porsTraded[] = {0, 0};
  static volatile int tradedCnt = 0;
  static volatile  int porsClanVisited[][] = {{0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}};
  static volatile  int clanVisited[] = {0, 0, 0, 0, 0};
  static volatile  int porsVisited[] = {0, 0};
  static volatile  int visitedCnt = 0;
  static volatile  int porsClanDead[][] = {{0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}};
  static volatile int clanDead[] = {0, 0, 0, 0, 0};
  static volatile int porsDead[] = {0, 0};
  static volatile int cntDead = 0;

  public final static int LSECS = E.LSECS;
  public final static int L2SECS = E.L2SECS;
  public final static double INVLSECS = E.INVLSECS;
  public final static double INVL2SECS = E.INVL2SECS;
  public final static int[] ASECS = E.ASECS;
  public final static int[] A2SECS = E.A2SECS;
  
  static final Charset CHARSET = Charset.forName("US-ASCII");
  //                                                                        TUE 04/02/2021 14:03:02:233 cdt
  static final public SimpleDateFormat MYDATEFORMAT = new SimpleDateFormat("EEE MM/dd/YYYY HH:mm:ss:SSSz");
  static final Path REMEMBER = Paths.get("remember");
  static final Path KEEP = Paths.get("keep");
  static BufferedWriter bRemember=null,bKeep=null;
  static BufferedReader bKeepr = null;
  
  /* each keep goes false after end of page process new page or settings ended */
  static boolean keepFromPage = false;  // keep clicked titles on this page
  static boolean keepHeaderPrinted = false; // header already printed
  static boolean keepBuffered = false; // true if flush needed
  static final String keepInstruct = "keep any changes made in this page, describe why you kept this changes";
  static final String initialKeepCmt = "put the why you changed the settings on this page";
  static String prevKeepCmt = initialKeepCmt + "";
  static String nextLineOfOutput = ""; //both keep and remember
  
  /* each remember goes false at next page request or end stats */
  static boolean rememberFromPage=false; // remember clicked lines on this page
  static boolean rememberHeader=false; //header for this page printed
  static boolean rememberBuffered=false;// flush needed
  static final String rememberInstruct = "click the name of a line in this table to remember it, also indicate why.";
  static final String initialRememberCmt = "put the why you chose to remember ths lines in this page.";
  static String prevRememberCmt = initialRememberCmt + "";
  
          
  double[] maxLY = {15.};// ship max light years for search
  static double[][] mMaxLY = {{.5, 25.}};//planet or ship max light years
  double[] addLY = {.9}; // add to ly in planet search per round of search
  static double[][] mAddLY = {{.3, 5.}};
  double[] multLYM = {1.1, 1.2, 1.4, 1.6, 1.9, 2.2, 2.7, 3.5}; //8
  // multiplier of the ship goals.
  double[] addGoal = {0.0, 0.015, .025, .045, .06, .08, 2.0, 2.3};//9
  // .25 = 1ship/3 planets
  static double[] gameShipFrac = {.70};  // 2.3 ships / econs .75 means 3ships/1 planet, .8 = 4ships/1planet
  static double[][] mGameShipFrac = {{.25, 1.20}, {.25, 1.20}};
// double[][] clanShipFrac = {{.70, .70, .70, .501, .6}, {.70, .70, .70, .501, .6}}; // .3->5. clan choice of clan ships / clan econs
  double[][] clanShipFrac = {{.501, .501, .501, .501, .6}};
  static double[][] mClanShipFrac = {{.25, .81}, {.20, 1.20}};
  static double[][] clanAllShipFrac = {{.501, .501, .501, .501, .501} }; // clan (ships/econs)
  static double[][] mClanAllShipFrac = {{.25, 1.20}, {.2, 1.20}};
  double econLimits1[] = {300.}; // start limiting econs
  static double mEconLimits1[][] = {{200., 500.}, {200., 500.}};
  double econLimits2[] = {350.}; // more limiting of econs
  static double mEconLimits2[][] = {{275., 550.}};
  double econLimits3[] = {400.}; // max of econs
  static double mEconLimits3[][] = {{300., 600.}};
  //double[][] LimitEcons = {{140.}};
  static double[][] mLimitEcons = {{100., 300.}, {100., 300.}};

  static Econ curEcon;  //eM only changes at the end a a year run, EM.curEcon
  static Econ otherEcon;  // other Econ when trading
  double[][] wildCursCnt = {{7.}};
  static double[][] mWildCursCnt = {{3., 20.}};
  static double[] difficultyPercent = {30.};
  static final double[][] mDifficultyPercent = {{1., 90.}, {1., 90.}};
  static double[][] minEconsMult = {{1.5}};
  static final double[][] mminEconsMult = {{.5, 10.0}, {.5, 10.0}};
  static double[][] maxThreads = {{5.2}};
  static final double[][] mmaxThreads = {{1.0, 12.0}};
  double[][] haveColors = {{ .3}};
  static final  double [][] mHaveColors = {{ 0.2,2.2}};
  double[][] haveCash = {{ 2.0}};
  static final  double [][] mHaveCash = {{ 0.2,2.2}};
  double[] sendHistLim = {20};
  static final  double[][] mSendHistLim = {{5., 50}, {-1, -1}};
  double[] nominalWealthPerCommonKnowledge = {.2, .3}; // was .9
  static final double[][] mNominalWealthPerCommonKnowledge = {{.15, .5}, {.15, .8}};
  // worth = Wealth/ frac ??
  double[] fracCommonKnowledge = {1.5};//value CommonKnowledge/Worth
 static final  double[][] mFracCommonKnowledge = {{.5, 3.5}, {-1, -1}};
  double[] fracNewKnowledge = {1.5};//value newKnowledge/nominalWealthPerCommonKnowledge
  static final double[][] mFracNewKnowledge = {{.5, 3.5}, {-1, -1}};
  double[] nominalWealthPerTrade = {5., 5.};  // guests and cargo
  // double[][] pNominalWealthPerTrade = {{nominalWealthPerTrade[0]},{nominalWealthPerTrade[1]}};
  static final double[][] mNominalWealthPerTrade = {{2., 9.}, {2., 9.}};
  double[] tradeWealthPerStaff = {2.6, 3.5};
  static final double[][] mTradeWealthPerStaff = {{1.3, 6.}, {1.3, 6.}};
  double[] tradeWealthPerResource = {2., 3.5};  // and cargo
  static final double[][] mTradeWealthPerResource = {{1., 6.}, {-1., -1.}};
  double[] tradeReservFrac = {.15, .15};
  static final double[][] mTradeReservFrac = {{.05, .5}, {.05, .5}};
//  static protected double nominalWealthPerTradeShipGuests = 5.2;
  double[] nominalWealthPerResource = {3., 4.};  // and cargo
  static final double[][] mNominalWealthPerResource = {{1., 6.}, {1.5, 8.}};
  //  nominalCGWealth[pors]
  int nameCnt = 1;
  double[] nominalWealthPerStaff = {3., 4.5};  // and guests
  static final double[][] mNominalWealthPerStaff = {{1.7, 6.}, {1.7, 8.}};
  
   // double[][] tradeRSWealth = {tradeWealthPerResource, tradeWealthPerStaff};
  double[][] tradeRSWealth = {nominalWealthPerResource , nominalWealthPerStaff};
  double[][] rawHealthsSOS0 = {{.15}};  // rawHealths , rawProspects2 max rawProspects2.min() cnt
  double[][] rawHealthsSOS1 = {{.05}};  // rawHealths , rawProspects2 below this cause sos
  double[][] rawHealthsSOS2 = {{.02}};
  double[][] rawHealthsSOS3 = {{-.02}};

  // [rors][pors]
  double[] maxFertility = {2.0, 2.0};
  static double[][] mMaxFertility = {{1., 2.5}, {1., 3.5}};
  double[] minFertility = {0., 0.};
  static double[][] mMinFertility = {{0., .05}, {0., .05}};
  double[][] nominalRSWealth = {nominalWealthPerResource, nominalWealthPerStaff};

  double[][] nominalWealthPerNewKnowledge = {{1.4}, {2.}};
  double[][] mNominalWealthPerNewKnowledge = {{.6, 4.}, {.7, 6.}};
  double[] manualFracKnowledge = {.7, .8};
  double[][] mManualFracKnowledge = {{.3, 1.5}, {.3, 2.}};
  double[] nominalWealthPerTradeManual = {manualFracKnowledge[0] * nominalWealthPerCommonKnowledge[0], manualFracKnowledge[1] * nominalWealthPerCommonKnowledge[1]};
  double[] initialWorth = {10000., 12000.};
  static double[][] mInitialWorth = {{5000., 20000.}, {2000., 30000.}};
  double[] initialKnowledge = {1000., 1000.}; //900
  static double[][] mInitialKnowledge = {{500., 4000.}, {500., 6000}};
  double[] initialCommonKnowledgeFrac = {0.106383, .106383};
  static double[][] mInitialCommonKnowledgeFrac = {{.1, .3}, {.08, .5}};
  double[] initialColonists = {1300., 1300.}; //3900
  static double[][] mInitialColonists = {{500., 3000.}, {500., 3000.}};
  double[] initialColonistFrac = {.188298, .27};
  static double[][] mInitialColonistFrac = {{.1, .3}, {.08, .5}};
  double[] initialResources = {1300., 1300.};
  static double[][] mInitialResources = {{500., 3000.}, {500., 3000.}};
  double[] initialResourceFrac = {.166596, .22}; // 1566
  static double[][] mInitialResourceFrac = {{.1, .3}, {.1, .5}};
  double[] initialReserve = {.1, .5};
  static double[][] mInitialReserve = {{.05, 1.5}, {.05, 1.5}};
  double[] initialWealth = {500., 500.};
  static double[][] mInitialWealth = {{100., 2000.}, {100., 2000.}};
  double[] initialWealthFrac = {0.2127659574, 0.2127659574};
  static double[][] mInitialWealthFrac = {{.1, .5}, {.1, .9}};
  double[] upWorth = {1.3, 1.3};
  double[][] mUpWorth = {{1.05, 1.7}, {1.05, 1.7}};
  //double nominalColonistsInWorkerDeaths = .8;
  double[][] initStaffGrossAdjustmentPerEnvirn = {{2.}, {2.}};
  double[][] initGuestGrossAdjustmentPerEnvirn = {{0.2}, {0.7}};
  double[] effBias = {.5, .5}; // 170309 .25->.5
  double[][] mEffBias = {{.25, .75}, {.25, .75}}; // 170309 .25->.5
  // this value is in units or staff and resource not cash;
  volatile double[] clanFutureFunds = {0., 0., 0., 0., 0.};
  // dues start the first year if this is less than initial worth
  //static double[][] clanStartFutureFundDues = {{8000., 8000., 8000., 8000, 8000.}, {8000, 8000., 8000., 8000., 8000.}};  //place to start future fund dues
   static double[][] clanStartFutureFundDues = {{4000., 4000., 4000., 4000, 4000.}, {8000, 8000., 8000., 8000., 8000.}};  //place to start future fund dues
  //static double[][] clanStartFutureFundDues = {{1700., 1700., 1700., 1700, 1700.}, {1700, 1700., 1700., 1700., 1700.}};  //place to start future fund dues
  static double[][] mClanStartFutureFundDues = {{1000., 50000.}, {1000., 50000.}};
  static double[][] clanStartFutureFundFrac = {{.07, .07, .07, .07, .07}, {.02, .02, .02, .02, .02}};  //frac of bals.curSum();
  static final double[][] mClanStartFutureFundFrac = {{.03, 1.6}, {.01,1.6}};

  static double[][] emergFundFrac = {{3.0, 3.0, 3.0, 3.0, 3.0}, {3.0, 3.0, 3.0, 3.0, 3.0}}; //frac of bals.curSum();
  static final double[][] mEmergFundFrac = {{5., .01}, {5., .01}};
  static double[][] futureFundFrac = {{.5, .5, .5, .5, .5}, {.53, .53, .53, .53, .53}}; //frac of bals.curSum();
  static final double[][] mFutureFundFrac = {{.001, .6}, {.001, .6}};
  static double[][] futureFundTransferFrac = {{.5, .5, .5, .5, .5}, {.5, .5, .5, .5, .5}};
  static final double[][] mFutureFundTransferFrac = {{.3, 1.4}, {.3, 1.4}};
  static double[] gameStartSizeRestrictions = {800., 600.};
  static final double[][] mGameStartSizeRestrictions = {{300., 5000.}, {300., 5000.}};
  // double[] effMax = {2., 2.}; // 170309
  // double[][] mEffMax = {{.5, 3.}, {.5, 3.}}; // 170309
  static double[][] rsefficiencyMMin = {{.15}, {.15}};  // .3 => .15
  static final double[][] mRsefficiencyMMin = {{.1, .8}, {.1, .8}};
  static double[][] rsefficiencyGMin = {{.15}, {.15}};
  static final double[][] mRsefficiencyGMin = {{.1, .8}, {.1, .8}};
  static double[][] rsefficiencyMMax = {{2.}, {2.}};
  static final double[][] mRsefficiencyMMax = {{1., 5.}, {1., 5.}};
  static double[][] rsefficiencyGMax = {{2.}, {2.}};
  static final double[][] mRsefficiencyGMax = {{1., 5.}, {1., 5.}}; // 170309
  // [pors] of difficulty control
 // static double[] difficultyByPriorityMin = {.4, .4}; //increase increases costs
   static double[] difficultyByPriorityMin = {.6 ,.6}; //increase increases costs
  static final double[][] mDifficultyByPriorityMin = {{.2, .6}, {.2, .8}}; //
  static double[] difficultyByPriorityMult = {2.4, 2.4}; // increase increases costs of low priority
  static final double[][] mDifficultyByPriorityMult = {{1., 6.}, {1., 6.}}; //
  static double[][] randFrac = {{0.5}, {0.4}};  // game risk
  static final double[][] mRandFrac = {{.0, .9}, {0., .7}};  // range 0. - .9,.7
  double[][] clanRisk = {{.5, .4, .6, .3, .5}, {.5, .4, .6, .3, .5}};  //risk taken with assets
  static double[][] mClanRisk = {{.0, .7}, {.0, .7}};
  double[][] catastrophyUnitReduction = {{.6}, {.6}};
  static double[][] mCatastrophyUnitReduction = {{.1, .9}, {.1, .9}};
  double[][] catastrophyBonusYears = {{8.}, {12.}};  // 2 - 25
  static double[][] mCatastrophyBonusYears = {{02., 25.0}, {1., 25.}};  // 
  double[][] catastrophyBonusYearsBias = {{1.6}, {1.9}}; // adds to the divisor year into bonus units
  static double[][] mCatastrophyBonusYearsBias = {{.5, 15.}, {.5, 15.}};
  double[][] catastrophyBonusGrowthValue = {{.9}, {.9}};  // unit values .2 - .7  *.5,
  static double[][] mCatastrophyBonusGrowthValue = {{.2, 2.}, {.2, .7}};
  double[][] catastrophyBonusDecayMultSumSectors = {{.00005}, {.00005}};
  static double[][] mCatastrophyBonusDecayMultSumSectors = {{.00002, .0002}, {.00005, .0002}};
  double[][] catastrophyManualsMultSumKnowledge = {{0.}, {2.}};//  .5 -10.
  static double[][] mCatastrophyManualsMultSumKnowledge = {{0., .0}, {.5, 10.}};//  .5 -10
  
  Dimension screenSize;
  int screenHeight = -2, screenWidth = -2, myHeight = -2, myWidth = -2, myH2 = -2, myW2 = -2;
  int  table2W=-2,table2H=-2;
  int myW3 = -2;
  //16,9:1920,1080  1600,900  1280,720  800,450  .5625
  //16,12: 1680,1050  1024,768  800,600 .75
  //            1280,1024  .8
  static double[][] screenW = {{1200.}};
  static final double[][] mScreenW = {{800.},{1920.}};
  static double[][] panelW = {{1920.}};
  static final double[][] mPanelW = {{800.},{1920.}};
  static double[][] panelH = {{1080.}};
  static final double[][] mPanelH = {{600.},{1080,}};
  static double[][] tableW = {{1280.}};
  static final double[][] mTableW = {{800.},{1920.}};
  static double[][] tableH = {{1080.}};
  

  int allGameErrMax = 40;  //EM.allGameErrMax
  static int allGameErrCnt = 0;
  int gameErrMax = 30;
  int gameErrCnt = 0;
  int yearErrMax = 0; // was 20
  int yearErrCnt = 0;
  static String prevLine = "";
  static String addlErr = "";
  static String wasHere = "";
  static String wasHere2 = "";
  static String wasHere3 = "";
  int year = -1;  // year of StarTrader, updated in StarTrader runYear;
  int keepHistsByYear[] = {99, 4, 2};

  /**
   * instantiate another EM, set static eE and static eM = new EM
   *
   * @param d pointer to E
   * @param f pointer to StarTrader;
   */
  EM(E aE, StarTrader aST) {
    eE = aE;
    st = aST;
    eM = this;
    myEM = this;
  }

  /**
   * initialize eM
   * read the existing keeps, close that and open as 
   *
   */
  int rende3 = 500;
  void init(){
    try{
    String dateString = MYDATEFORMAT.format(new Date());
    String rOut = "New Game " + dateString + "\r\n";
    Econ.nowThread = Thread.currentThread().getName(); // goes into Static Econ
    // define each of the first dimension of res or stats values
    resS = new String[rende3][]; //space for desc, comment
    resV = new double[rende3][][][];
    resI = new long[rende3][][][];
    defRes();
    runVals();
    System.out.println("++++counts at init EM doVal vvend=" + vvend + ", doRes rend4=" + rende4 + ", assiged doRes arrays rende3=" + rende3 + " +++++++");
    doReadKeepVals();
    if(bKeepr != null) bKeepr.close(); //close reading keep
    
  // bRemember = Files.newBufferedWriter(REMEMBER, CHARSET);     
 //  bRemember.write(rOut,0,rOut.length());
   bKeep = Files.newBufferedWriter(KEEP, CHARSET,StandardOpenOption.CREATE,StandardOpenOption.APPEND); 
   bKeep.write(rOut,0,rOut.length());
   keepBuffered = true;
     } catch (Exception ex) {
      flushes();
      System.err.println("Error " + new Date().toString() + " " + (new Date().getTime() - startTime) + " cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + ", addlErr=" + eM.addlErr + addMore());
      fatalError = true;
      flushes();
      ex.printStackTrace(System.err);
      System.err.flush();
      flushes();
      
     }
  }

  /**
   * possibly add some extra information lines to an error report
   *
   * @return the possible lines
   */
  protected static String andMore() {
    String rtn = (addlErr.isEmpty() && wasHere.isEmpty() && wasHere2.isEmpty() && wasHere3.isEmpty() && prevLine.isEmpty() ? "" : "\n")
            + (prevLine.isEmpty() ? "" : " :" + prevLine + "\n")
            + (addlErr.isEmpty() ? "" : " :" + addlErr + "\n")
            + (wasHere.isEmpty() ? "" : " :" + wasHere + "\n")
            + (wasHere2.isEmpty() ? "" : " :" + wasHere2 + "\n")
            + (wasHere3.isEmpty() ? "" : " :" + wasHere3 + "\n");
    rtn += andStats();
    rtn += andWaiting();
    rtn += andET();
    return rtn;
  }

  /**
   * possibly add some extra information lines to an error report
   *
   * @return the possible lines
   */
  protected static String addMore() {
    return andMore();
  }

  /**
   * possibly add some stats information lines to an error report
   *
   * @return the possible lines
   */
  static String andStats() {
    String rtn = "";
    for (int ii = 0; ii < lStatsWaitList; ii++) {
      rtn += (statsWaitList[ii] == null || statsWaitList[ii].isEmpty() ? "+" : statsWaitList[ii].length() < 5 ? ">>" + statsWaitList[ii] + "<< " : " :>>" + statsWaitList[ii] + "<<<\n");
    }
    return rtn;
  } // andStats

  /**
   * possibly add some waiting information lines to an error report
   *
   * @return the possible lines
   */
  static String andWaiting() {
    String rtn = "";
    for (int ii = 0; ii < Econ.lImWaitingList; ii++) {
      rtn += Econ.imWaitingList[ii] == null || Econ.imWaitingList[ii].isEmpty() ? "" : " :" + Econ.imWaitingList[ii] + "\n";
    }
    return rtn;
  }

  /**
   * possibly add some waiting information lines to an error report
   *
   * @return the possible lines
   */
  static String andET() {
    String rtn = "";
    for (int ii = 0; ii < Econ.lETList; ii++) {
      rtn += Econ.sETList[ii] == null || Econ.sETList[ii].isEmpty() ? "" : " :" + Econ.sETList[ii] + "\n";
    }
    return rtn;
  }

  static String eo = "none";
  protected static void flushes() {
    try {
    eo = "out";
    System.out.flush();
    System.out.flush();
    System.out.flush();
    System.out.flush();
    eo = "err";
    System.err.flush();
    System.err.flush();
    eo = "keep";
    if(keepBuffered) { bKeep.flush(); keepBuffered = false;}
 //   if(rememberBuffered) {bRemember.flush(); rememberBuffered = false; }
    } catch (Exception ex) {
      System.err.println("Ignore " + eo + " flush() error " + (new Date().getTime() - startTime) + " cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + ", addlErr=" + eM.addlErr + addMore());
     }
  }

  /**
   * issue the MyErr line, stack and then throw
   *
   * @param aLine the line of output
   */
  protected static void doMyErr(String aLine) {
    flushes();
    //  System.err.println(aLine + andMore()); //later
    // new Throwable().printStackTrace(System.err); // later
    throw new MyErr(Econ.nowName  + " " + Econ.nowThread + " " + aLine);
  }

  static long startTime;

  /**
   * get seconds since starting job
   *
   * @return seconds format ssss.mmm
   */
  protected static String sinceStartTime() {
    long now = (new Date()).getTime();
    double nu = now - startTime;
    return mf(nu * .001);
  }

  static long runYearsTime;

  /**
   * get seconds since runYears
   *
   * @return seconds ssss.mmm
   */
  protected static String sinceRunYear() {
    long now = (new Date()).getTime();
    double nu = now - runYearsTime;
    return mf(nu * .001);
  }

  static long doYearTime;

  /**
   * get seconds since the last doYear
   *
   * @return seconds ssss.mmm
   */
  protected static String sinceDoYear() {
    long now = (new Date()).getTime();
    double nu = now - doYearTime;
    return mf(nu * .001);
  }

  EM newCopy(String name, String title, EM oldEM, E aE, StarTrader ast) {
    EM rtn = new EM(eE, st);
    Econ tmpEcon;
    for (int iEcons = 0; iEcons < oldEM.econs.size(); iEcons++) {
      rtn.econs.add(tmpEcon = oldEM.econs.get(iEcons).newCopy(oldEM, rtn, aE, ast));
    }
    // much more
    return rtn;
  }

  //for Assets.AssetsYr.Trades
  // [pors][clan] multiply strategic sums
  //static NumberFormat dFrac = NumberFormat.getNumberInstance();
  //static NumberFormat whole = NumberFormat.getNumberInstance();
  static private NumberFormat dFrac = NumberFormat.getNumberInstance();
  static private NumberFormat whole = NumberFormat.getNumberInstance();
  static private NumberFormat exp = new DecimalFormat("0.####E0");

  static public int dfN = 2;

  /**
   * format the value
   *
   * @param v input value
   * @return value as a string
   */
  static public String mf(double v) {
    NumberFormat dFrac = NumberFormat.getNumberInstance();
    NumberFormat whole = NumberFormat.getNumberInstance();
    NumberFormat exp = new DecimalFormat("0.####E0");
    if (v % 1 > E.NZERO && v % 1 < E.PZERO) {  //very close to zero
      return whole.format(v);
    }
    if (v == .0 || v == -0) {  // actual zero
      dFrac.setMinimumFractionDigits(0);
      dFrac.setMaximumFractionDigits(1);
      return dFrac.format(v);
    } else if ((v > -999999. && v < -.001) || (v > .001 && v < 999999.)) {
      dFrac.setMinimumFractionDigits(0);
      dFrac.setMaximumFractionDigits(3);
      return dFrac.format(v);
       } else if ((v > -9999999. && v < -.001) || (v > .001 && v < 9999999.)) {
      dFrac.setMinimumFractionDigits(0);
      dFrac.setMaximumFractionDigits(2);
      return dFrac.format(v);
       } else if ((v > -99999999. && v < -.001) || (v > .001 && v < 99999999.)) {
      dFrac.setMinimumFractionDigits(0);
      dFrac.setMaximumFractionDigits(1);
      return dFrac.format(v);
      } else if ((v > -999999999. && v < -.001) || (v > .001 && v < 999999999.)) {
      dFrac.setMinimumFractionDigits(0);
      dFrac.setMaximumFractionDigits(0);
      return dFrac.format(v);
    } else if ((v > -.001 && v < -.0000001) || (v > .0000001 && v < .001)) {
      dFrac.setMinimumFractionDigits(0);
      dFrac.setMaximumFractionDigits(7);
      return dFrac.format(v);
    } else {
      return exp.format(v);
    }
  }

  /**
   * format the value
   *
   * @param v input value
   * @return value as a string
   */
  public String mf2(double v) {
      if(!eE.outputLess){  // don't need to delete old mf2 code
     return mf(v);
      } else {
    NumberFormat dFrac = NumberFormat.getNumberInstance();
    NumberFormat exp = new DecimalFormat("0.####E0");
    if (v == .0 || v == -0) {
      dFrac.setMinimumFractionDigits(0);
      dFrac.setMaximumFractionDigits(1);
      return dFrac.format(v);
    } else if ((v > -999999. && v < -.001) || (v > .001 && v < 999999.)) {
      dFrac.setMinimumFractionDigits(0);
      dFrac.setMaximumFractionDigits(3);
      return dFrac.format(v);
    } else if ((v > -.001 && v < -.0000001) || (v > .0000001 && v < .001)) {
      dFrac.setMinimumFractionDigits(0);
      dFrac.setMaximumFractionDigits(7);
      return dFrac.format(v);
    } else {
      return exp.format(v);
    }
      }
  }

  /**
   * format the value
   *
   * @param v input value
   * @return value as a string
   */
  protected String df(double v) {
    return mf(v);
  }

  /**
   * format the value
   *
   * @param v input value
   * @param n ignored
   * @return value as a string
   */
  protected String df(double v, int n) {
    return mf(v);
  }

  /** return just a whole number, rounded up
   * 
   * @param n number to print
   * @return n rounded up
   */
  static public String wh(double n) {
    whole.setMaximumFractionDigits(0);
    return whole.format(n +.4999999);
  }

  // static int clans = 5; E.lclans
  static int lRow = E.lclans * 2;

  static int lDisp = 10;  // 0-9 = last of display for game or clan
  int gCntr = -1; //counts number of game doVals
  int cCntr = -1; //counts number of clan doVal s
  // gStart,cStart are arrays of tab game page start points in dovals valD, valI valS
  int gStart[] = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
  int cStart[] = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}; // start c displays
  int gPntr = 0;  //vv value of current start of game gamemaster display of gStart
  int cPntr = 0;  //vv value of current start of game clanmaster display of cStart
  // int vgc = 0; // game cpde game(vone,vtwo) or clan (vten)
  static int vone = 1;  // only one value difficulty -game {n}
  static int v1 = vone;
  static int vtwo = 2; // p and s values  -- game {n,n}
  static int v2 = vtwo;
  static int vthree = 3; // [][] reference with one value {{n}}
  static int v11 = vthree;
  static int vfour = 4; // [][] reference with 2 values [pors] {{m},{n}}
  static int v21 = vfour;
  static int vfive = 5; // [] with 5 values
  static int vseven = 72; // [7] [lsecs][pors]
  static int v72 = vseven; // unused
  static int vten = 10; // p and s clan values -- clan values
  static int v25 = vten; // [5 clans][2 p|s]
  static int v725 = 725; // unused [7 lsecs][2 p|s][5 clans];
  static int v162 = 162; // unused[grades][pors] staff/guests grades
  static int v4 = 0;  // counts doVal entries
  /**
   * valI [vv][modeC][sliderC][prevSliderC][prev2SliderC][prev3SliderC] above
   * all SliderC columns are [pors][clan] valI[23][0][2][1] master = valI[23] =
   * {{{25},{33}}} valI[24][0][2][5] clans =
   * valI[24]{{{1.,2.,3.,4.,5.},{1.2,2.2,3.2,4.2,5.6}}},{{1.,2.,3.,4.,5.},{1.2,2.2,3.2,4.2,5.6}}}};
   * //valI[24][0][4][] ;* valI[25][7][2][5] sectors= valI[25]
   * {{{1.,2.,3.,4.,5.},{1.2,2.2,3.2,4.2,5.6}}},{{1.,2.,3.,4.,5.},{1.2,2.2,3.2,4.2,5.6}}},{{1.,2.,3.,4.,5.},{1.2,2.2,3.2,4.2,5.6}}},{{1.,2.,3.,4.,5.},{1.2,2.2,3.2,4.2,5.6}}},{{1.,2.,3.,4.,5.},{1.2,2.2,3.2,4.2,5.6}}},{{1.,2.,3.,4.,5.},{1.2,2.2,3.2,4.2,5.6}}},{{1.,2.,3.,4.,5.},{1.2,2.2,3.2,4.2,5.6}}}};
   */
  static int lvals = 200;
  int valI[][][][] = new int[lvals][][][];
  static int modeC = 0; // gc in valI
  static int sevenC = 1;  //unused index into a 7 sector array
  // static int divByC = 2;  // unused number of entry7 to divide by
  static int sliderC = modeC + 2; //2 [][] slider values of valD[vv][gameAddrC];
  //static int sliderC = modeC;// [][] active real values of valD[vv][modeC][p|s}[clan]
  // valI[vv][sliderC][p!s][1|2] slider values for V1,2,3,4
  // vali[vv][sliderC][p|s][0-d] slider values for v10
  static int prevSliderC = sliderC + 1; //3 [][] previous slider values
  static int prev2SliderC = prevSliderC + 1; //4 prevprev slider val
  static int prev3SliderC = prev2SliderC + 1; //5 prevprevprev slider values
  static int prevOriginalC = prev3SliderC; //5
  static final int sliderLow = 0;  // lowest value of slider
  static final double sliderExtent = 100.;// 0 - 99
  static final double invSliderExtent = 1. / sliderExtent;//.01
  static int gameAddrC = 0;  // valD[rn][0 gameAddrC]
  static int gameLim = 1; // valD[rn][1 gameLim]  lims[pors]{{vLowLim},{vHighLim}}
  static int vLowLim = 0;
  static int vHighLim = 1;
  static int dPrevRealC = 2; // original value of vaddr
  static int vDesc = 0;  // part of valS
  static int vDetail = 1; // valS
  static int vMore = 2; // valS
  String valS[][] = new String[lvals][]; // second column [desc,detail]
  /**
   * [vv][column][pors][clan]
   */
  double valD[][][][] = new double[lvals][][][];
  // eventually column = modeC,p

  /**
   * references of Environments being logged
   */
  Econ[] logEnvirn = new Econ[2];
  /**
   * references the history ArrayList in each Econ
   */
  ArrayList<History>[] hists = new ArrayList[2];
  static boolean fatalError = false;
  static boolean stopExe = false;

  // priority settings of sectors for new planets and ships
  double[][] uLifePriAdj = {{.01, .01, .01, .01, .01}, {.01, .01, .01, .01, .01}};
  double[][] uStrucPriAdj = {{.01, .01, .01, .01, .01}, {.01, .01, .01, .01, .01}};
  double[][] uEnergyPriAdj = {{.01, .01, .01, .01, .01}, {.01, .01, .01, .01, .01}};
  double[][] uPropelPriAdj = {{.01, .01, .01, .01, .01}, {.01, .01, .01, .01, .01}};
  double[][] uDefensePriAdj = {{.01, .01, .01, .01, .01}, {.01, .01, .01, .01, .01}};
  double[][] uGovPriAdj = {{.01, .01, .01, .01, .01}, {.01, .01, .01, .01, .01}};
  double[][] uColonistsPriAdj = {{.01, .01, .01, .01, .01}, {.01, .01, .01, .01, .01}};
  double[][][] userPriorityAdjustment = {uLifePriAdj, uStrucPriAdj, uEnergyPriAdj, uPropelPriAdj, uDefensePriAdj, uGovPriAdj, uColonistsPriAdj};
  double[][] mUserPriorityAdjustment = {{.01, .15}, {.01, .15}};
  double[] nominalPriorities = {23, 21, 2, 3, 5, 6, 7};
  double[] uLifeNomPri = {23, 23};
  double[] uStrucNomPri = {21, 21};
  double[] uEnergyNomPri = {2, 2};
  double[] uPropelNomPri = {3, 3};
  double[] uDefenseNomPri = {5, 5};
  double[] uGovNomPri = {6, 6};
  double[] uColonistsNomPri = {7, 7};
  double[][] nomPriAdjustment = {uLifeNomPri, uStrucNomPri, uEnergyNomPri, uPropelNomPri, uDefenseNomPri, uGovNomPri, uColonistsNomPri};
  double[][] mNomPriAdjustment = {{1., 25.}, {1., 25.}};
  //double[] prioritiesRandomMult = {7., 6., 2., 2., 2., 3., 3.5};
  double[][] uLifePriRanMult = {{7., 7., 7., 7., 7.}, {7., 7., 7., 7., 7.}};
  double[][] uStrucPriRanMult = {{6., 6., 6., 6., 6.}, {6., 6., 6., 6., 6.}};
  double[][] uEnergyPriRanMult = {{2., 2., 2., 2., 2.}, {2., 2., 2., 2., 2.}};
  double[][] uPropelPriRanMult = {{2., 2., 2., 2., 2.}, {2., 2., 2., 2., 2.}};
  double[][] uDefensePriRanMult = {{2., 2., 2., 2., 2.}, {2., 2., 2., 2., 2.}};
  double[][] uGovPriRanMult = {{3., 3., 3., 3., 3.}, {3., 3., 3., 3., 3.}};
  double[][] uColonistsPriRanMult = {{3.5, 3.5, 3.5, 3.5, 3.5}, {3.5, 3.5, 3.5, 3.5, 3.5}};
  double[][][] userPriorityRanMult = {uLifePriRanMult, uStrucPriRanMult, uEnergyPriRanMult, uPropelPriRanMult, uDefensePriRanMult, uGovPriRanMult, uColonistsPriRanMult};
  double[][] mUserPriorityRanMult = {{.01, .15}, {.01, .15}};

  static double[] uPrioritiesRandomMult = {7., 6., 2., 2., 2., 3., 3.5};
  // users adjust priority random additions
  double[][] priorityRandAdditions = {{1., 1., 1., 1., 1.}, {1., 1., 1., 1., 1.}};
  static double[][] mPriorityRandAdditions = {{.3, 2.}, {.3, 2.}};
  double[][] manualEfficiencyMult = {{.02}, {.02}}; // .01 - .08
  static double[][] mManualEfficiencyMult = {{.01, .09}, {.01, 2.}}; // .01 - .08
  double[][] gRGrowthMult1 = {{.15}, {.15}}; // higher growth .03 - .1
  static double[][] mGRGrowthMult1 = {{.03, .4}, {.01, .4}}; // higher growth .03 - .1
  double[][] gRGrowthMult2 = {{.3}, {.3}}; // lower growth .01 - .06;
  static double[][] mGRGrowthMult2 = {{.01, .6}, {.01, .6}};
  // freq .2 means chance for today is .2 or 1 in 5years, .333 = 1 in 3 years
  // goal freq from 1 in 2yrs to 1 in 10 yrs .5
  double[][] userCatastrophyFreq = {{.35, .33, .27, .35, .4}, {.28, .29, .34, .26, .4}};
  static double[][] mUserCatastrophyFreq = {{.2, .55}, {.2, .55}};
  // value 1.5 means  mult user value by 1.5 so .2 * 1.5= .3 about 1 in 3 yrs
  // remember there are also random multipliers
  double[][] gameUserCatastrophyMult = {{1.5}, {1.}};
  static double[][] mGameUserCatastrophyMult = {{.5, 2.2}, {.5, 2.2}};
  double[][] gameClanRiskMult = {{.5}, {.5}};  // range .0 - .6
  static double[][] mGameClanRiskMult = {{.0, .6}, {.0, .6}};  // range .0 - .6

  static final double[][] mGuestWorthBias = {{.2, 1.5}, {.2, 1.5}};
  static final double[][] mScoreMult = {{0., 1.}, {0., 1.}};
  static final double[][] mNegScoreMult = {{0., -1.}, {0., -1.}};
  static final double[][] mNegPluScoreMult = {{-1.,1.0},{-1.0,1.0}};
  static final double[][] mPlusNegScoreMult = {{ 1.0,-1.0},{1.0,-1.0}};
  double[][] wLiveWorthScore = {{.01}};
  double[][] iLiveWorthScore = {{.01}};
  double[][] iBothCreateScore = {{.01}};
  double[][] wYearTradeV = {{.01}};
  double[][] wYearTradeI = {{.01}};
  double[][] iNumberDied = {{.01}};
  double[][] wGiven = {{.8}};
  double[][] wGiven2 = {{.8}};
  double[][] wGenerous = {{-.8}};
  double[][] iGiven = {{.7}};
  double[][] iDeadLost = {{.01}};
  double[][] wDeadLost = {{.01}};

  // multiply table cargo costs by cargoBias when calculating Maint Travel Growth Req cargo costs
  double[] guestWorthBias = {1.};
  double[] cargoWorthBias = {1.}; // reservs have the same value as working
  double[] resourceWorth= {1.};
  double[] staffWorth = {1.};
  double[][] worthBias = {resourceWorth,cargoWorthBias,staffWorth,guestWorthBias};
  static final double[][] mCargoWorthBias = {{.2, 1.5}, {.2, 1.5}}; //no changeable
  /**
   * multipliers for annual costs 3/10/27 Required assets should be 2 to 3 times
   * the annual cost Resource maintenance includes maintenance for each sector
   * and should be around .2 to .3 of the total resources (including staff of
   * resources)(life of 4 to 8 years). Staff Annual costs should probably be
   * about 10% of staff (8-15years) religion is not a separate sector, but it is
   * part of defense (against disease, disaster etc.) government (influences how
   * well people work together, col[onists] because it supports colonist
   * collective life. guests cost about 30% of staff, they use no working
   * resources. they cost as much to transport cargo costs about .1 because it
   * uses sheltered space, transport cost the same as resource planet guests are
   * unemployed workers, not youth, babies or seniors all of whom in some way
   * are working transmuting or repurposing resource (magic or politics) is very
   * costly in resources and staff, but may be the only solution to allow
   * growth, or fix health problems
   *
   *
   * staff costs should about match resource per unit costs raw growth should
   * average 2.5 of all balance with penalty at 50 percent difficulty
   */
  static int res = 0;
  static int stf = 1;
  double[] pa = {5.};  // planet mult 3/10/17 3. -> 5.,181008->11 181213 5.
  double[] sa = {.25}; //ship mult 3/10/17 .4 -> .25
  double[] rb = {1.};
  double[] sb = {1.};
  static final double[][] mcb = {{.1, .95}, {.1, .95}};
  double[] cb = {.3, .3};  // cargo cost this fraction of resource costs p,s
  double[] gb = cb;  // guest cost this fraction of staff costs
  double[] rcsgMult = {rb[0], cb[0], sb[0], gb[0]};
  //[p|s][r|c|s|g]
  double econMult[][] = {{.5, .15, .5, .15}, {.25, .075, .25, .075}};

  double[][] multEconsUnused(double pa, double sa, double cb, double gb) {
    int m = 0, n = 0;
    double dd[][] = new double[2][];
    for (m = 0; m < 2; m++) {
      dd[m] = new double[4];
      for (n = 0; n < 4; n++) {
        dd[m][n] = 0.;

      }
    }
    return dd;
  }
  double aa[] = {pa[0], sa[0]};

  // the following is {{planet r,c,s,g},{ship r,c,s,g} using the above numbers
  //double ps[][] = {{pa[0], pa[0] * cb[0], pa[0], pa[0] * gb[0]}, {sa[0], sa[0] * cb[0], sa[0], sa[0] * gb[0]}};
  double ps1[] = {1., 1., 1., 1.};
  double ps[][] = {ps1, ps1};

  /**
   * sysBias, bias to cost, work by sIx
   */
  public double[] sysBias = {1., cb[0], 1., gb[0]};
  //public double[] xwBias = {1., 1., 1., 1.}; // not really used

//  static double growthFactor = .5;  // reduces growth after meeting req
//  static double healthFactor = .45; // reduces health after meeting req
  static double mReqAdjPRes1[] = {1.5, 1.5};
  static double gReqAdjPRes1[] = {1.5, 1.5};
  static double mCstAdjPRes1[] = {.4, .4};
  static double gCstAdjPRes1[] = {.06, .15};
  static double tCstAdjPRes1[] = {.12, .2,};

  double mReqAdjPStf1[] = {1.5, 1.5};
  double gReqAdjPStf1[] = {1.5, 1.5};
  double mCstAdjPStf1[] = {.4, .4};
  double gCstAdjPStf1[] = {.06, .15};
  double tCstAdjPStf1[] = {.12, .2,};

  double mReqAdjSRes1[] = {1.5, 1.5};
  double gReqAdjSRes1[] = {1.5, 1.5};
  double mCstAdjSRes1[] = {.4, .4};
  double gCstAdjSRes1[] = {.06, .15};
  double tCstAdjSRes1[] = {.12, .2,};

  double mReqAdjSStf1[] = {1.5, 1.5};
  double gReqAdjSStf1[] = {1.5, 1.5};
  double mCstAdjSStf1[] = {.4, .4};
  double gCstAdjSStf1[] = {.06, .15};
  double tCstAdjSStf1[] = {.12, .2,};

  // this assumes [r or staff cost][p or ship][ rc(r) or sg(staff) ]
  double maintReqAdj1[][][] = {{mReqAdjPRes1, mReqAdjSRes1}, {mReqAdjPStf1, mReqAdjSStf1}};
  double growthReqAdj1[][][] = {{gReqAdjPRes1, gReqAdjSRes1}, {gReqAdjPStf1, gReqAdjSStf1}};
  double maintCostAdj1[][][] = {{mCstAdjPRes1, mCstAdjSRes1}, {mCstAdjPStf1, mCstAdjSStf1}};
  double growthCostAdj1[][][] = {{gCstAdjPRes1, gCstAdjSRes1}, {gCstAdjPStf1, gCstAdjSStf1}};
  double travelCostAdj1[][][] = {{tCstAdjPRes1, tCstAdjSRes1}, {tCstAdjPStf1, tCstAdjSStf1}};
  //[reqM reqG m,t,g][r or s][p or s][rc sg]
  double rs4[][][][] = {maintReqAdj1, growthReqAdj1, maintCostAdj1, travelCostAdj1, growthCostAdj1};
  // a second set of options
  double rs[][][][]; //[5 reqM reqG m t g][2 r s][2 p s][4 rcsg]
  int maintReqTabRow[] = {0, 9, 7, 8};
  int growthReqTabRow[] = {0, 9, 7, 8};
  int maintCostTabRow[] = {0, 0, 7, 7};
  int growthCostTabRow[] = {0, 0, 7, 7};
  int travelCostTabRow[] = {0, 0, 7, 7};

  double[] multReqMaintC = {1., 1.}; // mult ReqM costs p,s
  static final double[][] mmult5Ctbl = {{.2, 2.2}, {.2, 2.2}}; // limits all 5
  double[] multReqGrowthC = multReqMaintC;
  double[] multMaintC = multReqMaintC;
  double[] multTravC = multReqMaintC;
  double[] multGrowthC = multReqMaintC;

  double mult5Ctbl[][] = {multReqMaintC, multReqGrowthC, multMaintC, multTravC, multGrowthC};
  double mab1[] = {.3,.3}; // resource costs planet,ship
  //double mab1[] = {.6,.6}; // resource costs planet,ship
  //double mac1[] = {.6,.6}; // staff costs planet ship 
  double mac1[] = {.3,.3}; // staff costs planet ship 
  double mabc[][] = {mab1, mac1}; //r or s, p or s
  static double mmab1[][] = {{.01, 2.}, {.01, 2.1}}; // resource costs planet,ship
  static double[][] mmab2 = {{.5, 1.9}, {.5, 1.9}};
  static double mmac1[][] = {{.01, 3.}, {.01, 3.}};
  double mab2[] = {.9, .9}; // resource, staff cost
  static double[][] mmac2 = {{.1, 2.6}, {.1, 2.6}};
  double mac2[] = {.5, 1.8}; //planet or ship costs
  double mad[] = {1., 1.}; //rc costs, sg costs
  // multiply the rs4 above by the above maa to mad

  double[][][][] makeRS(double[][][][] rs4, double[][] mult5Ctbl, double[][] ps) {
    rs = new double[5][][][];
    //make the table
    for (int aa = 0; aa < 5; aa++) { //type
      rs[aa] = new double[2][][];
      for (int ab = 0; ab < 2; ab++) { //
        rs[aa][ab] = new double[2][];
        for (int ac = 0; ac < 2; ac++) {
          rs[aa][ab][ac] = new double[4];
        }
      }
    }
    String stss = ">>>>>>>>>>>>>>>>>>>>>>>>>>>\n";
    String strs = "", strs4 = "", stdif = "", stm5t = "", stmabc = "";
    String staa[] = {"reqMaint ", "reqGro ", "maint, ", "travel, ", "growth, "};
    String stabrs4[] = {"rs4_r ", "rs4_s "};
    String stabm5t[] = {"m5t_r ", "m5t_s "};
    String stabdif[] = {"dif_r ", "difr_s "};
    String stabmabc[] = {"mabc_r ", "mabc_s "};
    String stabrs[] = {"rs_r  ", "rs_s "};
    String stac[] = {"plnt ", "ship "};
    String stad[] = {"r=", "c=", "s=", "q= "};
    double vrs = 0., vrs4 = 0., vdif = 0., vm5t = 0., vmabc = 0.;
    //now set the table elements
    for (int aa = 0; aa < 5; aa++) { // typegrReq
      for (int ab = 0; ab < 2; ab++) { // res or stf
        strs += staa[aa] + stabrs[ab];
        strs4 += stabrs4[ab];
        stdif += stabdif[ab];
        stm5t += stabm5t[ab];
        stmabc += stabmabc[ab];
        for (int ac = 0; ac < 2; ac++) { // plnt or ship
          strs += stac[ac];
          strs4 += stac[ac];
          stdif += stac[ac];
          stm5t += stac[ac];
          stmabc += stac[ac];
          for (int ad = 0; ad < 4; ad++) {  // rcsg
            /*
            rs[aa][ab][ac][ad] = 1.;  // finding out of range
            double xx = rs4[aa][ab][ac][(int)ad/2];
            double xy = ps[ac][ad];
            double x1 = mult5Ctbl[aa][ac];
            double x2 = mabc[ab][ac];
             */
            vrs = rs[aa][ab][ac][ad]
                    = // add difficultyPercent as a cost factor 50% = 1. mult
                    //(vdif = difficultyPercent[ac]) * .1  *
                  (vdif = difficultyPercent[0] * 0.025)
                    // (vdif = difficultyPercent[0] * 0.0990)
                    * (vrs4 = rs4[aa][ab][ac][(int) ad / 2])
                    * (vm5t = mult5Ctbl[aa][ac])
                    * //mabc[ab][ac] * ps[ac][ad];
                    (vmabc = mabc[ab][ac]);
            if (E.debugLogsOut) {
              strs += mf(vrs) + " ";
              strs4 += mf(vrs4) + " ";
              stdif += mf(vdif) + " ";
              stm5t += mf(vm5t) + " ";
              stmabc += mf(vmabc) + " ";

            }
          } //ad
          // stss += "\n"; // start another line
        }//ac
      }//ab
      if (E.debugLogsOut) {
        stss += strs + "\n";
        stss += strs4 + "\n";
        stss += stdif + "\n";
        stss += stm5t + "\n";
        stss += stmabc + "\n";

        strs = " ";
        strs4 = " ";
        stdif = " ";
        stm5t = " ";
        stmabc = " ";
      }
    } // aa 
    if (E.debugLogsOut) {
      System.out.println(stss + "<<<<<<<<<<<<<<<<<"); // 12 lines
    }
    return rs;
  }

  /**
   * Calculate cost of cargo and quests for planets and ships
   *
   * @param cb currently .3 of resource for cargo costs
   * @param gb currently .3 of staff for quest costs
   * @return
   */
  double[][] makePS(double[] cb, double[] gb) {
    double ps0[] = {1., cb[0], 1., gb[0]};
    double ps1[] = {1., cb[1], 1., gb[1]};
    double[][] ps3 = {ps0, ps1};
    return ps3; // p,s  r,c,s,g
  }

  // find the smallest ship frac for this clan
  /**
   * calculate the number of ships per a planet for this clan. Use each of the
   * ship limits to find ships per econ convert that ships per Econ to ships per
   * planet and return it
   *
   * @param kln The clan for this calculation
   * @return The numbers of ships per planet for this clan
   */
  double shipsPerPlanet(int kln) {
    double smallestShipFrac = clanAllShipFrac[E.P][kln] < gameShipFrac[E.P]
            ? clanAllShipFrac[E.P][kln] < clanShipFrac[E.P][kln]
                    ? clanAllShipFrac[E.P][kln] : clanShipFrac[E.P][kln]
            : gameShipFrac[E.P] < clanShipFrac[E.P][kln]
                    ? gameShipFrac[E.P] : clanShipFrac[E.P][kln];
    double shipsPerP = smallestShipFrac / (1. - smallestShipFrac);
    return shipsPerP;
  }

  /**
   * set some values dependent on some values set by StarTrader.getGameValues
   * called in StarTrader after input of gameValues
   */
  void setMoreValues() {
    double[][] ps3 = makePS(cb, gb);
    makeRS(rs4, mult5Ctbl, ps3);
  }
  // the following variables are used to calculate the knowledgeBias which is
  // used to calculate the SEfficiency and REfficiency in CalcReq see CalcReq
  //calcEfficiency
  double[] knowledgeForPriority = {.40}; //init assign commonknowledge
  double[] knowledgeByDefault = {.60};  //init assign commonknowledge
  double[] commonKnowledgeTradeManualFrac = {.10};
  double[] newKnowledgeTradeManualFrac = {.8};
  double[] manualTradeManualFrac = {.05};
  double[] commonKnowledgeDifTradeManualFrac = {.5};
  double[][] mknowledgeForPriority = {{.2, .50}}; //init assign commonknowledge
  double[][] mknowledgeByDefault = {{.3, .9}};  //init assign commonknowledge
  double[][] mcommonKnowledgeTradeManualFrac = {{.05, .25}};
  double[][] mnewKnowledgeTradeManualFrac = {{.5, 1.}};
  double[][] mmanualTradeManualFrac = {{.03, .1}};
  double[][] mcommonKnowledgeDifTradeManualFrac = {{.3, .8}};
  double KLearnManuals = 1.;  // manuals convertable because of created knowledge

  static double[][] mNominalKnowledgeForBonus = {{60000., 1900000.}, {60000., 1900000}};
  double[] nominalKnowledgeForBonus = {900000.};
  double[] additionalKnowledgeGrowthForBonus = {.05}; // .2=>.05 reduce 200624
  double[] additionToKnowledgeBiasSqrt = {.6};
  double[] nominalDistance = {7.};
  double[] nominalStrategicDif = {3.};
  double[] knowledgeGrowthPerResearcher = {25.};

  /**
   * ..Growth is for planet [resource,cargo]growth *swork, ship growth
   * [resource,cargo]growth * yearsTravel*resource.balance // public // percent
   * /** adjust second use of priority in growth, fraction of priority to use
   * growth is limited by fertility, availableResource, availableStaff after
   * removal of maintenance and travel costs, multiplied by any poor health
   * costs
   */
  //3/15/15 more staff  public static double[] fracPriorityInGrowth = {.5, .5};  //mult priority in growth calc
  static Double minRand = .1;
  double[] fracBiasInGrowth = {.2, .2};
  static double[][] mFracBiasInGrowth = {{.02, .9}, {.02, .9}};
  double[] fracPriorityInGrowth = {.6, .5};  //mult priority in growth calc and percent to frac
  static final double[][] mFracPriorityInGrowth = {{.1, .9}, {.1, .9}};
  double[] resourceGrowth = {2.7, .002}; // growth per work
  static final double[][] mResourceGrowth = {{.01, 6.}, {0.002, .9}};
  // decay mining cumulative related to each years growth
  double[] resourceGrowthDecay = {.0000006, .0000001}; //per unit
  // decay mining cumulative related to each years growth
  static final double[][] mResourceGrowthDecay = {{.00000003, .0000009}, {.00000001, .0000009}};
  double[] cargoGrowth = {0.000001, .00000001};
  static final double[][] mCargoGrowth = {{0.0000001, 0.00009}, {0.000000001, 0.000009}};
  // cargo decay use resourceGrowthDecay
  double[] staffGrowth = {2.7, .002}; // growth per work
  static final double[][] mStaffGrowth = {{.01, 6.}, {0.0002, .9}};
  double[] staffGrowthDecay = {.0000002, .00000016};
  static final double[][] mStaffGrowthDecay = {{.00000005, .000005}, {.00000003, .000003}};
  double[] travelGrowth = {.0015, .0025}; // this multiplies against work
  static final double[][] mTravelGrowth = {{.0001, .001}, {.0001, .01}}; //
  double[] guestsGrowth = {0.000001, .00000001};
  static final double[][] mGuestsGrowth = {{0.0000001, 0.00009}, {0.000000001, 0.0000009}};
  static double[][][] mRCSGGrowth = {mResourceGrowth, mCargoGrowth, mStaffGrowth, mGuestsGrowth};
  // growth is in terms of staff units, decay is in term of previous yr growth
  double[][] assetsGrowth = {resourceGrowth, cargoGrowth, staffGrowth, guestsGrowth};
  double[][] growthDecay = {resourceGrowthDecay, resourceGrowthDecay, staffGrowthDecay, staffGrowthDecay};
  double[][] mfracBiasInGrowth = {{.1, .3}, {.1, .3}};
  double[][] mfracPriorityInGrowth = {{.3, .7}, {.3, .7}};  //mult priority in growth calc and percent to frac
  double[][] clanFutureFundEmerg2 = {{.15, .15, .15, .15, .15}, {.15, .15, .15, .15, .15}};
  double[][] mClanFutureFundEmerg = {{.01, .3}, {.01, .3}};
  double[][] clanFutureFundEmerg1 = {{.25, .25, .25, .25, .25}, {.25, .25, .25, .25, .25}};
  double[][] swapDif = {{3., 3., 3., 3., 3.}, {3., 3., 3., 3., 3.}};
  static double[][] mSwapDif = {{2., 5.}, {2., 5.}};

  /**
   * (1-health)*penalty -> (1-.5)*.4 => .2, travel cost=>travelCost*(1+.2) if
   * health = 1.5 (1-1.5)*.4 => -.2 travelCost=>travelCost*(1 -.2) reduces cost
   * raw poor health effect = (1. - health) * poorHealthPenalty poorHealthEffect
   * = poorHealthEffectLimit[0] < raw poor health effect <
   * poorHealthEffectLimit[1]
   */
  // [pors]
  double[] poorHealthPenalty = {1., 1.}; // (1.-rqGCfracMin)*poorHealthPenalty
  static double[][] mPoorHealthPenalty = {{.7, 1.3}, {.7, 1.3}};

  //poorHealthEffectLimits[min,max]
  double[] poorHealthEffectLimitsL = {.5};  // use -poorHealthEffectLimitsL[0]
  static double[][] mPoorHealthEffectLimitsL = {{.0, .7}};
  double[] poorHealthEffectLimitsH = {.5};  // use poorHealthEffectLimitsH[0]
  static double[][] mPoorHealthEffectLimitsH = {{.3, .8}};
  double[] moreOfferBias = {.2, .3};
  static double[][] mMoreOfferBias = {{.05, .4}, {.07, .5}};

  // [low,high][0]
  double[][] poorHealthEffectLimits = {poorHealthEffectLimitsL, poorHealthEffectLimitsH};
  /**
   * Fractions used to evaluate the sum value trades. Critical members get
   * additional criticalFrac added to their value High Critical numbers really
   * must be requested, never offered Offers above the low critical
   *
   */
  double[][] searchStrategicFrac = {{.5, .5, .5, .5, .5}, {.5, .5, .5, .5, .5}};
  static double[][] mTradeSCNFracs = {{.1, 1.}, {.1, 1.}};
  double[][] tradeStrategicFrac = {{.5, .5, .5, .5, .5}, {.5, .5, .5, .5, .5}};
  double[][] searchCriticalFrac = {{.5, .5, .5, .5, .5}, {.5, .5, .5, .5, .5}};
  double[][] tradeCriticalFrac = {{.05, .05, .05, .05, .05}, {.05, .05, .05, .05, .05}};
  static final double[][] mTradeCriticalFrac = {{.01,.5},{.01,.5}};
  double[][] tradeManualsFrac = {{.01, .01, .01, .01, .01}, {.01, .01, .01, .01, .01}};
  static double[][] mTradeMFracs = {{.0, 1.}, {.0, 1.}};
  double[][] searchCriticalNumber = {{3, 3, 3, 3, 3}, {3, 3, 3, 3, 3}};
  double[][] tradeCriticalNumber = {{3, 3, 3, 3, 3}, {3, 3, 3, 3, 3}};
  double[][] searchNominalFrac = {{.1, .1, .1, .1, .1}, {.1, .1, .1, .1, .1}};
  double[][] tradeNominalFrac = {{.1, .1, .1, .1, .1}, {.1, .1, .1, .1, .1}};
  // at the start of search or trade,use trade as the frac of rc,sg for reserve
  double[][] searchStartTradeCFrac = {{.7, .7, .7, .7, .7}, {3, .3, .3, .3, .3}};
  double[][] searchYearBias = {{.05, .05, .05, .05, .05}, {.05, .05, .05, .05, .05}};
  static double[][] mSearchYearBias = {{.005, .5}, {.005, .5}};
  double[][] tradeStartTradeCFrac = {{.7, .7, .7, .7, .7}, {.3, .3, .3, .3, .3}};
  double[][] searchStartTradeGFrac = {{.7, .7, .7, .7, .7}, {.3, .3, .3, .3, .3}};
  double[][] tradeStartTradeGFrac = {{.7, .7, .7, .7, .7}, {.3, .3, .3, .3, .3}};
  static final double[][] mTradeSearchStart  = {{.2,.8},{.2,.8}};
  double[][] startSwapsCFrac = {{.3, .3, .3, .3, .3}, {.4, .4, .4, .4, .4}};
  double[][] startSwapsGFrac = {{.3, .3, .3, .3, .3}, {.4, .4, .4, .4, .4}};
  static final double [][] mStartSwaps = {{.2,.7},{.15,.8}};
  double[][] strategicOfferFrac = {{.3, .3, .3, .3, .3}, {.3, .3, .3, .3, .3}};
  // clan value amount of change to c or g for trade reserving c & g
  double[][] tradeReserveIncFrac = {{.4, .5, .4, .3, .4}, {.5, .4, .5, .4, .5}};
  static double[][] mTradeReserveIncFrac = {{.02, .6}, {.01, .5}};
  double[][] mtgWEmergency = {{.1, .1, .1, .1, .1}, {.1, .1, .1, .1, .1}};
  static final double[][] mMtgWEmergency = {{.03, .5}, {.03, .5}};
  double[][] searchStrategicFrac2 = {{.5, .5, .5, .5, .5}, {.5, .5, .5, .5, .5}};
  static double[][] mTradeSCNFracs2 = {{.1, 1.}, {.1, 1.}};
  double[][] tradeStrategicFrac2 = {{1., 1.0, 1.0, 1.0, 1.0}, {1.0, 1.0, 1.0, 1.0, 1.0}};
  double[][] searchCriticalFrac2 = {{1.5, 1.5, 1.5, 1.5, 1.5}, {1.5, 1.5, 1.5, 1.5, 1.5}};
  double[][] tradeCriticalFrac2 = {{1.5, 1.5, 1.5, 1.5, 1.5}, {1.5, 1.5, 1.5, 1.5, 1.5}};
  double[][] tradeManualsFrac2 = {{1.0, 1.0, 1.0, 1.0, 1.0}, {1.0, 1.0, 1.0, 1.0, 1.0}};
  static double[][] mTradeMFracs2 = {{.0, 1.}, {.0, 1.}};
  double[][] searchCriticalNumber2 = {{3, 3, 3, 3, 3}, {3, 3, 3, 3, 3}};
  double[][] tradeCriticalNumber2 = {{3, 3, 3, 3, 3}, {3, 3, 3, 3, 3}};
  double[][] searchNominalFrac2 = {{1.0, 1.0, 1.0, 1.0, 1.0}, {1.0, 1.0, 1.0, 1.0, 1.0}};
  double[][] tradeNominalFrac2 = {{1.0, 1.0, 1.0, 1.0, 1.0}, {1.0, 1.0, 1.0, 1.0, 1.0}};
  // at the start of search or trade,use trade as the frac of rc,sg for reserve
  double[][] searchStartTradeCFrac2 = {{.7, .7, .7, .7, .7}, {3, .3, .3, .3, .3}};
  double[][] searchYearBias2 = {{.05, .05, .05, .05, .05}, {.05, .05, .05, .05, .05}};
  static double[][] mSearchYearBias2 = {{.005, .5}, {.005, .5}};
  double[][] tradeStartTradeCFrac2 = {{.7, .7, .7, .7, .7}, {.3, .3, .3, .3, .3}};
  double[][] searchStartTradeGFrac2 = {{.7, .7, .7, .7, .7}, {.3, .3, .3, .3, .3}};
  double[][] tradeStartTradeGFrac2 = {{.7, .7, .7, .7, .7}, {.3, .3, .3, .3, .3}};
  double[][] startSwapsCFrac2 = {{.3, .3, .3, .3, .3}, {.4, .4, .4, .4, .4}};
  double[][] startSwapsGFrac2 = {{.3, .3, .3, .3, .3}, {.4, .4, .4, .4, .4}};
  double[][] strategicOfferFrac2 = {{.3, .3, .3, .3, .3}, {.3, .3, .3, .3, .3}};
  // clan value amount of change to c or g for trade reserving c & g
  double[][] tradeReserveIncFrac2 = {{.4, .5, .4, .3, .4}, {.5, .4, .5, .4, .5}};
  static double[][] mTradeReserveIncFrac2 = {{.02, .6}, {.01, .5}};
  double[][] mtgWEmergency2 = {{1.0, 1.0, 1.0, 1.0, 1.0}, {1.0, 1.0, 1.0, 1.0, 1.0}};
  static final double[][] mMtgWEmergency2 = {{.03, .5}, {.03, .5}};

  //[search | trade] [P | S] [clan]
  static final int DOINGSEARCH = 0, DOINGTRADE = 1;
  double[][][] strategicFracs = {tradeStrategicFrac, tradeStrategicFrac};
  //double[][][] strategicFracs = {searchStrategicFrac, tradeStrategicFrac};
  double[][][] criticalFracs = {tradeCriticalFrac, tradeCriticalFrac};
  double[][][] criticalNumbers = {tradeCriticalNumber, tradeCriticalNumber};
  double[][][] nominalFracs = {tradeNominalFrac, tradeNominalFrac};
  double[][][] startTradeCFrac = {tradeStartTradeCFrac, tradeStartTradeCFrac};
  double[][][] startTradeGFrac = {tradeStartTradeGFrac, tradeStartTradeGFrac};

  /**
   * values used in Assets.AssetsYr.Trades values that are not subject to change
   */
  // static double maxStrategicFrac = 10., minStrategicFrac = .03;
  /**
   * years to keep TradeRecords
   */
  double[][] yearsToKeepTradeRecord = {{12.}, {12.}};
  static double[][] mYearsToKeepTradeRecord = {{6., 20.}, {6., 20.}};

  double fava[][] = {{3., 3., 3., 3., 3.}, {2., 2., 2., 2., 2.}, {1., 1., 1., 1., 1.}, {4., 4., 4., 4., 4.}, {5., 5., 5., 5., 5.}};
  double fav0[] = {3., 3., 3., 3., 3.};
  double fav1[] = {2., 2., 2., 2., 2.};
  double fav2[] = {1., 1., 1., 1., 1.};
  double fav3[] = {4., 4., 4., 4., 4.};
  double fav4[] = {5., 5., 5., 5., 5.};
  double fav[][] = {fav0, fav1, fav2, fav3, fav4};
  double mfavs[][] = {{0.5, 5.5}};
  // decrease required strategicFrac for fav > 3, increase if < 3
  // ,28 - FavMult*5 == -.2
  // .28 - FavMult*1 == .2
  // clanBias -FavMult*5
  double clanBias = .28;
  double favMult = .08;
  double oClanMult = .5;
  double randMax = 1.95;
  double randMin = .1;
  double randMult = .3;
  // double[][] randFrac = {{0.0}, {0.0}};  // range 0. - .7
  // ssFrac is a special frac for ship to ship trade
  double[][] ssFrac = {{1.2, 1., .9, 1.1, 1.2}};
  static double mSsFrac[][] = {{.7, 1.4}};
  // [pors][clan]
  // tradeFrac is initial goal of requests/offers
  // ships get much more to survive and grow with planets
  // the fracs get reduced as the trades continue
  static double mTradeFrac[][] = {{.12, .4}, {.7, 3.0}};
  double[][] tradeFrac = {{.15, .15, .15, .18, .2}, {1.7, 1.55, 1.5, 1.55, 1.45}, ssFrac[0]};
  // termFrac = (goalTermBias )/(goalTermBias + barterStart - term)
  //    gtb=18 t=18  18/18 = 1;  t=9  18/(18 + 18-9=27) = .6666; t=`0 18/36 = .5
  // related to decrement per term
  double goalTermBias[] = {18., 18.};
  double[] sosTrigger = {1.1, -.1}; // sos = rawFertilities2.min() < sosTrigger
  double sosfrac[] = {.3, .35};
  static final double msosfrac[][] = {{.2, .4}, {.25, .45}};
  static int barterStart = 18; // initial term for bartering
  static double[][] mTradeEmergFrac = {{.03, .4}, {.03, .4}};
  // emergency if min rawProspects2 lt tradeEmergFrac
  // [pors][clan]
  double[][] tradeEmergFrac = {{.2, .2, .2, .1, .3}, {.2, .1, .2, .3, .2}};

  boolean trade2HistOutputs = true;
  int trade1PlanetOverrideShipGoods = 6;
  int tradePlanetAcceptHigherOffer = 7;
  double[] cntMult = {2.0, 2.5};
  static final double[][] mCntMult = {{1., 5.}, {2., 7.}};
  double[] maxTries = {2., 3.};
  static final double[][] mIncTriesPTerm = {{.1, 1.1}, {.1, 1.1}};
  double incTriesPTerm[] = {.4, .5};
  static double[][] mMaxTries = {{1., 5.}, {1., 5.}};
  double[][] startAvail = {{8., 8., 8., 8., 8.}, {8., 8., 8., 8., 8.}};
  static double[][] mStartAvail = {{4., 10.}, {4., 10.}};
  double[][] availFrac = {{.6, .6, .6, .6, .6}, {.6, .6, .6, .6, .6}};
  static double[][] mAvailFrac = {{.3, .9}, {.3, .9}};
  double[][] emergFrac = {{.9, .9, .9, .9, .9}, {.9, .9, .9, .9, .9}};
  static double[][] mEmergFrac = {{.5, .99}, {.5, .99}};
  double[] availMin = {.33, .33};
  static double[][] mAvailMin = {{.1, .9}, {.1, .9}};

  /* swaps taken from E, a gradual move to these */
  // penalty in move to earlier position if swapped across resources
  public static int[] sXSwapPenalty = {2, 2};
  // swap to same Resource
  public static int[] sSwapPenalty = {0, 0};
  // trade to the same resource in a different env use sSwapPenalty
  public static int[] sTSwapPenalty = {0, 0};
  // swapPenalty[TR,TX,TT][P, S];
  public static int[][] iSwapPenalty = {sSwapPenalty, sXSwapPenalty, sTSwapPenalty};

  final static public double[] swapTRtoRRcost = {.5, .5};
  final static public double[] swapTRtoCRcost = {.5, .5};
  final static public double[] swapTRtoRScost = {.02, .02};
  final static public double[] swapTRtoCScost = {.02, .02};
  final static public double[] swapTCtoRCcost = {.1, .1};
  final static public double[] swapTCtoCCcost = {.01, .01};
  final static public double[] swapTCtoRGcost = {.02, .02};
  final static public double[] swapTCtoCGcost = {.005, .005};
  final static public double[] swapTStoSScost = {.1, .1};
  final static public double[] swapTStoSRcost = {.5, .5};
  final static public double[] swapTStoGRcost = {.5, .5};
  final static public double[] swapTStoGScost = {.1, .1};
  final static public double[] swapTGtoSCcost = {.5, .5};
  final static public double[] swapTGtoSGcost = {.1, .1};
  final static public double[] swapTGtoGCcost = {.005, .005};
  final static public double[] swapTGtoGGcost = {.1, .1};
  //static double [][] mXferCosts = {{10.,60.},{10,60}};
  public double xferrC = 45.;
  // final static public double[] swapXRtoRRcost = {17., 17.};
  // final static public double[] swapXRtoCRcost = {18., 18.};
  // swapXRtoRRcost, swapXRtoCRcost, swapXStoSRcost, swapXStoGRcost
  // swapXRtoRScost, swapXRtoCScost, swapXStoSScost, swapXStoGScost;
  static double[][] mSwapXRtoRRcost = {{5.0, 35.0}, {5.0, 35.0}};
  double[] swapXRtoRRcost = {18., 18.};
  double[] swapXCtoCCcost = swapXRtoRRcost;
  static double[][] mSwapXRtoCRcost = {{.3, 5.}, {.3, 5.}};
  double[] swapXRtoCRcost = {.5, .5};
  double[] swapXCtoRCcost = swapXRtoCRcost;
  static double[][] mSwapXStoSRcost = {{.03, 5.}, {.03, 5.}};
  public double[] swapXStoSRcost = {.005, .005};
  public double[] swapXStoGRcost = swapXStoSRcost;

  static double[][] mSwapXRtoRScost = {{.3, 15.}, {.3, 15.}};
  double[] swapXRtoRScost = {.5, .5};
  double[] swapXCtoCScost = swapXRtoRScost;
  double[] swapXCtoCGcost = swapXRtoRScost;
  static double[][] mSwapXRtoCScost = {{.03, 5.}, {.03, 5.}};
  double[] swapXRtoCScost = {.05, .05};
  double[] swapXCtoRScost = swapXRtoCScost;
  double[] swapXCtoRGcost = swapXRtoCScost;
  static double[][] mSwapXStoSScost = {{.03, 5.}, {.03, 5.}};
  double[] swapXStoSScost = {.1, .1};
  static double[][] mSwapXSgoGScost = {{.003, .5}, {.003, .5}};
  public double[] swapXStoGScost = {.01, .01};
  public double[] swapXGtoSCcost = swapXStoGScost;
  public double[] swapXGtoSGcost = swapXStoGScost;
  public double[] swapXGtoGCcost = swapXStoGScost;
  public double[] swapXGtoGGcost = swapXStoGScost;
  public double[] swapRtoRRcost = swapXRtoRRcost;
  final static public double[] swapRtoCRcost = {.005, .005};
  public double[] swapRtoRScost = swapXRtoRScost;
  final static public double[] swapRtoCScost = {.002, .002};
  final static public double[] swapCtoRRcost = {.001, .001};
  final static public double[] swapCtoRCcost = swapCtoRRcost;
  public double[] swapCtoCCcost = swapXRtoRRcost;
  final static public double[] swapCtoRScost = {.002, .002};
  final static public double[] swapCtoRGcost = swapCtoRScost;
  public double[] swapCtoCGcost = swapXRtoRScost;
  public double[] swapStoSScost = swapXStoSScost;
  public double[] swapStoSRcost = swapXStoSRcost;
  public double[] swapStoGRcost = swapXStoSRcost;
  final static public double[] swapStoGScost = {.001, .001};
  final static public double[] swapGtoSCcost = {.005, .005};
  final static public double[] swapGtoSScost = {.005, .005};
  final static public double[] swapGtoSGcost = {.001, .001};
  final static public double[] swapGtoGCcost = {.0005, .0005};
  final static public double[] swapGtoSRcost = {.0005, .0005};
  final static public double[] swapGtoGGcost = {.0001, .0001};

  // index [iEl][oEl][pors] costs of trade Rswap
  final static public double[][][] swapRtradeRcost = {{swapTRtoRRcost, swapTRtoCRcost}, {swapTCtoRCcost, swapTCtoCCcost}};

  // index [iEl][oEl][pors] costs of trade Sswap
  final static public double[][][] swapStradeScost = {{swapTStoSScost, swapTStoGScost}, {swapTGtoSGcost, swapTGtoGGcost}};

  // in growth phase, multiply the min positive by the following numbers this
  // should cause some resources to violate a growthReq, and cause either .INCR
  // P=0  planet
  // S=1; ship
  final static public int W = 0; // to working
  final static public String[] Els = {"W", "R"};
  final static public int R = 1; // to reserve
  //  final static public int[] oswpr = {swpMaxAvailTo,swpCargoTo};
  //  final static public int[] oswps = {swpMaxStaffTo,swpGuestsTo};
  //index [iEl][oEl][pors] costs of regular Rswap
  public double[][][] swapRregRcost = {{swapRtoRRcost, swapRtoCRcost}, {swapCtoRCcost, swapCtoCCcost}};
  //index [iEl][oEl][pors] costs of xmute (transmute) Rswap
  // final static public double[][][] swapRtransRcost = {{swapXRtoRRcost, swapXRtoCRcost}, {swapXCtoRCcost, swapXCtoCCcost}};
  // index [iEl][oEl][pors] costs of regular Sswap
  public double[][][] swapSregScost = {{swapStoSScost, swapStoGScost}, {swapGtoSGcost, swapGtoGGcost}};
  // index [iEl][oEl][pors] costs of Xmute Sswap
  // final static public double[][][] swapStransScost = {{swapXStoSScost, swapXStoGScost}, {swapXGtoSGcost, swapXGtoGGcost}};
  int maxEconHist = Econ.keepHist; // Econs later than 5 null hist to save heap space
  int maxClrHist = 200000; // don't clear hist until it reach this number
  static final public int TR = 0;  // regular W R of same resource
  static final public int TX = 1;  // Transmute W R of different resources
  static final public int TT = 2;  // Trade  W R of different environment
  // [TR,TX,TT][iW,iR][oW,oR][P,S]
  // static final public double[][][][] swapRrxtcost = {swapRregRcost, swapRtransRcost, swapRtradeRcost};  
  // res cargo cost
  //  final static public double[][][][] swapSrxtcost = {swapSregScost, swapStransScost, swapStradeScost};  
  // staff guests cost
  static final public int CR = 0;  // class resource
  static final public int CS = 1;  // class staff
  /**
   * [CRes,CStaf][TR,TX,TT][iW,iR][oW,oR][Plan,Ship]
   */
  // static final public double[][][][][] swapcosts = {swapRrxtcost, swapSrxtcost};

  double swapResourcesAveMinMult = .3;  //use AssetsYr inline values
  double swapSubAssetMinSwap = .01;
  double[] minSwapIncrAveMult = {.03, .001};
  double[] minSwapDecrAveMult = {.5, .3};
  double[] minXferAveMult = {.03, .001};

  /**
   * the below fractions are the amount of swaps devoted to the g = growth or
   * fertility with the fertility goal h = health or wellness with the health
   * goal f = future maximizing the future needs gf = grow first ignore any
   * health or future need
   */
  double maxHealth = 1.5;
  double minHealth = 0.0;
  double initHealth = 0.4;
  double initFertility = 0.6;
  // travel on planets are trains, planes, rockets,cars, trucks,boats,bikes ...
  double[][] initTravelYears = {{0.65}, {7.0}}; // default travel if none other
  static double[][] mInitTravelYears = {{0.3, 2.0}, {3., 20.}};
  double maintMinPriority = 1.;
  double growthMinPriority = .5;  // only each limited fertility
  double maxGrowth[] = {900000, 900000};
  double max7Growth[] = {7 * maxGrowth[0], 7 * maxGrowth[1]};
  double max14Growth[] = {14 * maxGrowth[0], 14 * maxGrowth[1]};
  static double mMaxGrowth[][] = {{100000, 99990000}, {100000, 99990000}};
  // [pors][clan]
  double goalResvFrac[][] = {{.1, .1, .1, .1, .1}, {.5, .5, .5, .5, .5}};
  double goalGrowth[][] = {{.6, .6, .6, .6, .6}, {.5, .5, .5, .5, .5}};
  double goalHealth[][] = {{.5, .5, .5, .5, .5}, {.5, .5, .5, .5, .5}};
  static double[][] mRegGoals = {{.05, .95}, {.05, .95}};
  static double[][] mAllGoals = {{.05, .90}, {.01, .9}};
  double emergGrowth[][] = {{.3, .3, .3, .3, .3}, {.3, .3, .3, .3, .3}};
  double emergHealth[][] = {{.3, .3, .3, .3, .3}, {.3, .3, .3, .3, .3}};
  double tradeGrowth[][] = {{.7, .7, .7, .7, .7}, {.8, .9, .8, .9, .9}};
  double tradeHealth[][] = {{.4, .4, .4, .4, .4}, {.7, .8, .6, .8, .8}};
  double offerAddlFrac[] = {.001, .2};
  static double[][] mOfferAddlFrac = {{.0001, .1}, {.001, .4}};
//  double tradeDistance[] = {0.65, 7.};
  double reqGrowthFertilityMinMult[][] = {{.5, .5, .5, .5, .5}, {.5, .5, .5, .5, .5}};
  double emergTradeReserve[][] = {{.9, .9, .9, .9, .9}, {.9, .9, .9, .9, .9}};
  double availTradeReserve[][] = {{.7, .7, .7, .7, .7}, {.7, .7, .7, .7, .7}};
  double futGrowthFrac[][] = {{.7, .8, .7, .8, .7}, {.8, .8, .7, .4, .3}};
  static double mFutGrowthFrac[][] = {{.2, 2.2}, {.2, 2.2}};
  double futGrowthYrMult[][] = {{5., 5., 5., 4., 5.}, {8., 5., 3., 5., 5.}};
  static double[][] mFutGrowthYrMult = {{1.5, 11.5}, {1.5, 11.5}};
  double futtMTGCostsMult[][] = {{2., 2., 4., 4., 3.}, {6., 4., 5., 3., 4.}};
  double[][] mFuttMTGCostsMult = {{.7, 7.}, {.7, 7.}};
 // double growthGoals[][][] = {emergGrowth, goalGrowth, tradeGrowth};
  //double maintGoals[][][] = {emergHealth, goalHealth, tradeHealth};
  // A2Row and A6Row sum the min cnt values
  int minSumCnt = 7;  // for large sum of min's
  int minSum2Cnt = 3;  // for smaller sum of min's
//                 [pors]
  public double[] minProspects = {.1, .1};
  public double[][] mMinProspects = {{.03, .5}, {.03, .5}};
  public double[] maxn = {50., 50.}; // max swaps
  public double[][] mMaxn = {{15., 70.}, {15., 70.}};
  double[] fFrac = {.5, .6};   //future frac
  double[] gFrac = {.5, .5};    // growth frac
  double[] gmFrac = {.85, .85};  //g more from 0
  double[] gfFrac = {.25, .20};   //grow first before health
  double[] geFrac = {.75, 1.1};  // g emerg above this
  double[] nheFrac = {.35, .35}; // not yet health emergency
  double[] hFrac = {.50, .50};   //  health frac
  double[] hmFrac = {.7, .7};  // h more from 0
  double[] heFrac = {.85, .85};// health emergency above this value

  // static double gameMaxRandomSum = .7;
  /**
   * status values input for the game tab
   */
  static int gameClanStatus = 5; // 0-4 regular clans, 5 = gameMaster\
  int gameDisplayNumber[] = {-1, -1, -1, -1, -1, -1};//-1=not set, 0-nn start of current disp in the array of game or clan enums
  int clanisplayNumber[] = {-1, -1, -1, -1, -1, -1};//-1=not set, 0-nn start of current disp in the array of game or clan enums
  static int gamePorS = 0;  // 0=p,1=ship, used in getIval and setIval
  int vv = -1, gc = -2, vFill = 0, lowC = 0, highC = 1, vvend = -1;

  /**
   * values of game where the next display will start
   */
  int prevGameClanStatus = -1;  // not yet set
  int prevGameDisplayNumber[] = {-1, -1, -1, -1, -1, -1};
  int prevClanDisplayNumber[] = {-1, -1, -1, -1, -1, -1};
  String vDetailPrefix = "1.23 2.5=>2.7 4.56";

  /**
   * get a game value that may be either clan or game value, check the length of
   * the arrays to decide whether to us PorS and clan. Use PorS if A.length == 2
   * If A.length == 2 use PorS if A[PorS].length == 5 than use A[PorS][clan]
   *
   * @param A reference to an array holding game or clan values
   * @param PorS specify 0:planet or 1:ship
   * @param clan clan used if A[PorS].length == 5
   * @return
   */
  double getVal(double[][] A, int PorS, int clan) {
    if (A.length == 1) {
      return A[0].length == 5 ? A[0][clan] : A[0][0];
    } else if (A.length == 2) {
      return A[PorS].length == 5 ? A[PorS][clan] : A[PorS][0];
    }
    return 5 / 0.;  // I think infinite not NaN
  }

  /**
   * get a value, either game master or clan value if A.length == 1 this is a
   * master single value if A.length == 2 this is a master PorS value if
   * A.length == 5 this is a clan value for both P and S
   *
   * @param A
   * @param PorS
   * @param clan
   * @return a value
   */
  double getval(double[] A, int PorS, int clan) {
    if (A.length == 1) {
      return A[0];
    } else if (A.length == 2) {
      return A[PorS];
    } else if (A.length == 5) {
      return A[clan];
    } else {
      return 5 / 0.; // infinite
    }
  }

  /**
   * get the multiple of 2 values A B
   *
   * @param A root of first value
   * @param B root of the second value
   * @param PorS planet or ship
   * @param clan clan value of caller
   * @return values for A times B
   */
  double getVal(double[][] A, double[][] B, int PorS, int clan) {
    return getVal(A, PorS, clan) * getVal(B, PorS, clan);
  }

  /**
   * get the multiple of 3 value A B C
   *
   * @param A root of the first value
   * @param B root of the second value
   * @param C root of the third value
   * @param PorS caller planet or ship value
   * @param clan caller clan
   * @return multiple of A * B * C
   */
  double getVal(double[][] A, double[][] B, double[][] C, int PorS, int clan) {
    return getVal(A, PorS, clan) * getVal(B, PorS, clan) * getVal(C, PorS, clan);
  }

  /**
   * doVal with vaddr only a double diff[] = {.5} or {.5,.5} Find gc=vone one
   * value {val}, gc=vtwo {pVal,sVal}
   *
   * @param vdesc title of the input
   * @param vaddr address of the input
   * @param lims limits of the input
   * @param vdetail details about the input
   * @return vv the number of the input in valI,valD,valS
   */
  int doVal(String vdesc, double[] vaddr, double[][] lims, String vdetail) throws IOException {
    int vl = -1;
    if (E.debugSettingsTab) {
      if (vaddr.length > 2 && vaddr.length != 5) {
        throw new MyErr("doVal {1} illegal length vdesc=" + vdesc + ", vaddr.length=" + vaddr.length);
      }
    }
    gc = (vl = vaddr.length) == 2 ? vtwo : vl == 1 ? vone : vl == 5 ? vfive : 11;
    vv = doVal1(gc, vdesc, lims, vdetail);
    double[][] vacc = {vaddr};
    valD[vv][gameAddrC] = vacc; //valD[vv][0][vaddr] //valD[vv][0][0]{addr0,addr1}
    doVal3(vv);
    return vv;
  }

  /**
   * doVal with vaddr a 7 sector array {0.3} => vone {0.1,0.2} => vtwo {
   * 0.,1.,2.,3.,4.,5.,6.} = vseven not used I think
   *
   * @param vdesc title of the input
   * @param vaddr address of the input
   * @param vindex index into the 7 sector addr
   * @param lims limits of the input
   * @param vdetail details about the input
   * @return vv the number of the input in valI,valD,valS
   */
  int doVal(String vdesc, double[] vaddr, int vindex, double[][] lims, String vdetail) throws IOException {
    if (E.debugSettingsTab) {
      if (vaddr.length > 2 && vaddr.length != 7) {
        throw new MyErr("doVal {1.1} vdesc=" + vdesc + ", vaddr.length=" + vaddr.length);
      }
    }
    gc = vaddr.length == 1 ? vone : vaddr.length == 2 ? vtwo : vaddr.length == 7 ? vseven : 11;
    vv = doVal1(gc, vdesc, lims, vdetail);
    double[][] vacc = {vaddr}; // {{val0}}  or {{val0,val1}} 
    valD[vv][gameAddrC] = vacc; //valD[vv][vFill] {vaddr} //valD[vv][vFill][] {{addr0,addr1}}
    valI[vv][vFill][vFill][sevenC] = vindex; //valI[vv][0][0][vindex] (0-6)
    doVal3(vv);
    return vv;
  }

  /**
   * doVal with vaddr full double val[][p,s] = {{.5}} or {{.5},{.5}} gc vone
   * val[vv][0]{val}, valD {{val}} gc vtwo val[vv][0][pors] {pVal,sVal}, valD
   * {{pVal,sVal}} gc vthree val[vv][0][val1] {{val}}, valD {{val}} same as vone
   * gc vfour val[vv][pors][val1] {{pVal},{sVal}}. valD {{pVal},{sVal}} gc vfive
   * val[vv][0][val5] {{1,2,3,4,5}}, valD{{1,2,3,4,5}}; gc vten
   * valD[vv][0][pors][val5] {{1,2,3,4,5},{6,7,8,9,10}}
   *
   * @param vdesc title of the input
   * @param vaddr address of the input
   * @param lims limits of the input
   * @param vdetail details about the input
   * @return vv the number of the input in valI,valD,valS
   */
  int doVal(String vdesc, double[][] vaddr, double[][] lims, String vdetail) throws IOException {
   // int v1 = -1, v2 = -1, v3 = -1;
      int v1 = vaddr.length;
      int v2 = vaddr[0].length;
      int v3 = v1 == 2 ? vaddr[1].length : v1 == 3 ? vaddr[1].length : -1;
    if (E.debugSettingsTab) {
    
      if ((v1 <= 2 && v2 != 1 && v2 != 2 && v2 != 5) || (v1 == 2 && v3 != 1 && v3 != 2 && v3 != 5) || (v1 == 3 && v2 != 5 && v3 != 5)) {
        throw new MyErr("doVal{2} illegal length vdesc=" + vdesc + ", vaddr.length=" + vaddr.length + ", vaddr[0].length=" + v2 + (v1 == 2 ? "vaddr[1].length =" + v3 : ""));
      }
    }
    // 11 should create an error some where
    gc = v1 == 1 ? v2 == 1 ? vone
            : v2 == 2 ? vtwo
                    : v2 == 5 ? vfive : 11
            : v1 == 2 && v2 == 1 && v3 == 1 ? vfour
                    : v1 == 2 && v2 == 5 && v3 == 5 ? vten 
            : v1 == 3 && v2 == 5 && v3 == 5 ? vten : 11; // specail case
    if (E.debugSettingsTab) {
      if (gc == 11) {
        throw new MyErr("doval{3} illegal length vdesc=" + vdesc + ", vaddr.length=" + vaddr.length + ", vaddr[0].length=" + v2 + (v1 == 2 ? "vaddr[1].length =" + v3 : "v1!=2"));
      }
    }
    vv = doVal1(gc, vdesc, lims, vdetail);
    valD[vv][gameAddrC] = vaddr; //valD[vv][0][pors][valu]
    doVal3(vv);
    return vv;
  }

  /**
   * sub doVal1 assign the next vv and the initial storage that will be filled
   * in doVal3
   *
   * @param gc the storage type code
   * @param vdesc the title of the storage
   * @param lims the limits of the values
   * @param vdetail the extended details of this input
   * @return vv the current input number
   */
  int doVal1(int gc, String vdesc, double[][] lims, String vdetail) {
    vv = v4++;
    //set up valMap for reading file keep
    if (valMap.containsKey(vdesc)) {
      doMyErr(">>>>>>vdesc=" + vdesc + " already exists, vv=" + vv);
    }
    valMap.put(vdesc, vv);
    valI[vv] = new int[prev3SliderC + 1][][];
    valD[vv] = new double[dPrevRealC + 1][][];
    valD[vv][gameLim] = lims; //valD[vv][1]...
    int[][] val7 = {{-1}}; // unused
    valI[vv][sevenC] = val7; //unused
    if (E.debugSettingsTabOut) {
      System.out.printf("in doVal1 vv=%2d, gc=%1d, desc=%7s, detail=%7s, %n", vv, gc, vdesc, vdetail);
    }

    String[] valSn = {vdesc, vdetail, "change detail" + vv};
    valS[vv] = valSn;
    int[][] mode = {{gc}};
    valI[vv][modeC] = mode;  //valI[vv][0][0]{gc}
    return vv;
  }

  /**
   * now fill out the rest of the valD, valI, valS field Also check for range
   * errors, and method errors set arrays cstart and gstart the starts of the
   * display panels
   *
   * @param vv
   * @return vv
   */
  int doVal3(int vv) throws IOException {
    int[][] slider, prevSlider, prev2Slider, prev3Slider;
    int svalp = -1, ib = -1;
    int klan = 0, clan = 0;
    int pors = E.P;
    double vR, lL, lH;
    int gc = valI[vv][modeC][0][0];

    if (gc >= vone && gc <= vfour) { // count display starts
      gCntr++;
      clan = 5;
      if(E.debugSettingsTabOut)System.out.format("doval3 vone tst1 +  vv=%3d,name=%5s,gCntr=%2d,cCntr=%2d%n",vv,valS[vv][0],gCntr,cCntr);
      if ((gCntr % lDisp) == 0) {
        gStart[(int) (gCntr / lDisp)] = vv;
        if(E.debugSettingsTabOut)System.out.format("doval3 vone tst1 +  vv=%3d,name=%5s,gCntr=%2d,cCntr=%2d%n",vv,valS[vv][0],gCntr,cCntr);
      }
    } else if (gc == vten || gc == vfive) {
      clan = 0;
      cCntr++;
     // System.out.format("doval3 vten tst1 +  vv=%3d,name=%5s,gCntr=%2d,cCntr==%2d%n",vv,valS[vv][0],gCntr,cCntr);
      if ((cCntr % lDisp) == 0) {
        cStart[(int) (cCntr / lDisp)] = vv;
        System.out.format("doval3 vten tst1 +  vv=%3d,name=%5s,gCntr=%2d,cCntr==%2d%n",vv,valS[vv][0],gCntr,cCntr);
      }
    } else if (E.debugSettingsTab) {
      throw new MyErr("doVal3 err, vDesc=" + valS[vv][vDesc] + ", vv=" + vv + ", invalid gc=" + gc + ", vaddr[1].length =" + valD[vv][gameAddrC][1].length);
    }
    // Save 4 different versions of the values to go into the slider
    if (vone == gc || gc == vtwo) { // gameMaster
      int[][] slidern = {{-1, -1}};
      valI[vv][sliderC] = slidern;
      valI[vv][prevSliderC] = slidern;
      valI[vv][prev2SliderC] = slidern;
      valI[vv][prev3SliderC] = slidern;
      pors = E.P;
      clan = 5;
      klan = clan % 5;
      svalp = valToSlider(vR = valD[vv][gameAddrC][clan % 5][pors], lL = valD[vv][gameLim][pors][vLowLim], lH = valD[vv][gameLim][pors][vHighLim]);
      valI[vv][sliderC][0][pors] = svalp;
      valI[vv][prevSliderC][0][pors] = svalp;
      valI[vv][prev2SliderC][0][pors] = svalp;
      valI[vv][prev3SliderC][0][pors] = svalp;
      double[][] dPrevRealn = {{valD[vv][gameAddrC][0][pors]}, {-1}};
      valD[vv][dPrevRealC] = dPrevRealn;
      doVal5(vv, gCntr, gStart, gc, svalp, pors, clan, vR, lL, lH);
      if (gc == vtwo) {  // double [] version of addrs'
        pors = E.S;
        svalp = valToSlider(vR = valD[vv][gameAddrC][0][pors], lL = valD[vv][gameLim][pors][vLowLim], lH = valD[vv][gameLim][pors][vHighLim]);
        valI[vv][sliderC][0][pors] = svalp;
        valI[vv][prevSliderC][0][pors] = svalp;
        valI[vv][prev2SliderC][0][pors] = svalp;
        valI[vv][prev3SliderC][0][pors] = svalp;
        doVal5(vv, gCntr, gStart, gc, svalp, pors, clan, vR, lL, lH);
      }
    } else if (gc == vthree || gc == vfour) { // more gameMaster
      int[][] slidern = {{-1}, {-1}};
      valI[vv][sliderC] = slidern;
      valI[vv][prevSliderC] = slidern;
      valI[vv][prev2SliderC] = slidern;
      valI[vv][prev3SliderC] = slidern;
      pors = E.P;
      clan = 5;
      svalp = valToSlider(vR = valD[vv][gameAddrC][pors][0], lL = valD[vv][gameLim][pors][vLowLim], lH = valD[vv][gameLim][pors][vHighLim]);
      valI[vv][sliderC][pors][0] = svalp;
      valI[vv][prevSliderC][pors][0] = svalp;
      valI[vv][prev2SliderC][pors][0] = svalp;
      valI[vv][prev3SliderC][pors][0] = svalp;
      double[][] dPrevRealn = {{vR}, {-1}};
      valD[vv][dPrevRealC] = dPrevRealn;
      doVal5(vv, gCntr, gStart, gc, svalp, pors, clan, vR, lL, lH);
      if (gc == vfour) {  // double [pors][0] version of address
        pors = E.S;
        klan = 0;
        svalp = valToSlider(vR = valD[vv][gameAddrC][pors][0], lL = valD[vv][gameLim][pors][vLowLim], lH = valD[vv][gameLim][pors][vHighLim]);
        valI[vv][sliderC][pors][0] = svalp;
        valI[vv][prevSliderC][pors][0] = svalp;
        valI[vv][prev2SliderC][pors][0] = svalp;
        valI[vv][prev3SliderC][pors][0] = svalp;
        doVal5(vv, gCntr, gStart, gc, svalp, pors, clan, vR, lL, lH);
      }
      // for vone vthree the valI[vv][0-4][E.S]{-1} not {svalp}
      // for vtwo vfour the valI[vv][0-4][E.P]{svalp} display value
    } else if (gc == vten || gc == vfive) {
      int[][] slidern = {{-1, -1, -1, -1, -1}, {-1, -1, -1, -1, -1}};
      int[][] prev2Slidern = {{-1, -1, -1, -1, -1}, {-1, -1, -1, -1, -1}};
      int[][] prevSlidern = {{-1, -1, -1, -1, -1}, {-1, -1, -1, -1, -1}};
      int[][] prev3Slidern = {{-1, -1, -1, -1, -1}, {-1, -1, -1, -1, -1}};
      valI[vv][sliderC] = slidern;
      valI[vv][prevSliderC] = prevSlidern;
      valI[vv][prev2SliderC] = prev2Slidern;
      valI[vv][prev3SliderC] = prev3Slidern;
      if(E.debugSettingsTabOut)System.out.format("doval3 vten tst2 +  vv=%3d,name=%5s,gCntr=%2d,cCntr==%2d%n",vv,valS[vv][0],gCntr,cCntr);
      int porslim = gc == vfive ? 1 : 2;
      for (pors = 0; pors < porslim; pors++) {
        for (clan = 0; clan < 5; clan++) {
          svalp = valToSlider(vR = valD[vv][gameAddrC][pors][clan], lL = valD[vv][gameLim][pors][vLowLim], lH = valD[vv][gameLim][pors][vHighLim]);
          valI[vv][sliderC][pors][clan] = svalp;
          valI[vv][prevSliderC][pors][clan] = svalp;
          valI[vv][prev2SliderC][pors][clan] = svalp;
          valI[vv][prev3SliderC][pors][clan] = svalp;
          doVal5(vv, cCntr, cStart, gc, svalp, pors, clan, vR, lL, lH);
        }
      }// for vten all of the valI[vv][0-4][0-1]{svalp[0-4]} set &gt; 0
    } else { // case 11 etc fatal error
      int[][] slidern = {{-1, -1}};
      int[][] prev2Slidern = {{-1, -1}};
      int[][] prevSlidern = {{-1, -1}};
      int[][] prev3Slidern = {{-1, -1}};
      System.out.flush();
      System.err.flush();
      String verr = "doVal3 illegal gc =" + gc + ", name=" + valS[vv][vDesc] + ", vv=" + vv + ", pors=" + pors + ", clan=" + clan;
      myTestDone = true;
      throw new MyErr(verr);
    }
    return vv;
  }

  /**
   * check for errors by the previous doVal methods
   *
   * @param vv counter into which val
   * @param xCntr counter gCntr or cCntr
   * @param xStart start gStart or cStart
   * @param gc type of val
   * @param iinput the slider value for this entry
   * @param pors planet or ship
   * @param clan clan being tested
   * @param val the val that was set
   * @param low
   * @param high
   */
  void doVal5(int vv, int xCntr, int[] xStart, int gc, int iinput, int pors, int clan, double val, double low, double high) throws IOException {
    double t1 = 0., t2 = 0., t3 = 0.;
    int j1 = -3, j2 = -4, j3 = -5, j4 = -6, j5 = -7;
    int klan = clan < 5 ? clan : clan == 5 ? 0 : clan % 5;
    if (E.debugSettingsTabOut) {
      System.out.format("in doval5 gc=%1d, lmode=%1d, mode=%1d, vv=%3d =\"%5s\",xCnt=%1d, xStrt[xCnt]=%2d,  iinput=%3d, pors=%1d,klan=%1d,val=%7.2f, low=%7.2f,high=%7.2f%n", gc, valI[vv][modeC].length, valI[vv][modeC][0][0], vv, valS[vv][vDesc], xCntr, xCntr < 0 ? 9999 : xStart[(int) (xCntr / lDisp)], iinput, pors, clan, val, low, high);
    }
    // test for legal gc
    if (E.debugSettingsTab && (gc != vone && gc != vtwo && gc != vthree && gc != vfour && gc != vfive && gc != vten)) {
      throw new MyErr("doVal5 Illegal gc=" + gc + ", vv=" + vv + ", desc=" + valS[vv][vDesc]);
    }
    // test value between low and high. note high may be &lt; low
    if (E.debugSettingsTab && !((val >= low && val <= high) || (val >= high && val <= low))) {
      throw new MyErr("doval5 " + valS[vv][vDesc] + " value=" + mf(val) + " out of limits high=" + mf(high) + " low=" + mf(low));
    }
    // test gc == saved gc
    if (E.debugSettingsTab && gc != valI[vv][modeC][vFill][0]) {
      throw new MyErr("doval5 " + valS[vv][vDesc] + "gc =" + gc + "not equal stored gc=" + valI[vv][modeC][vFill][0]);
    }
    // test getVal matches iinput(the converted game slider Value
    if (E.debugSettingsTab && iinput != (j1 = getVal(vv, pors, clan))) {
      throw new MyErr("doval5 vv=" + vv + ", desc=" + valS[vv][vDesc] + " iinput=" + iinput + " not equal to saved slider  value =" + j1);
    }
    // test that input matches the value derived from the saved slider value
    if (gc == vone || gc == vtwo) {
      j2 = valI[vv][sliderC][vFill][pors];
    } else if (gc == vthree || gc == vfour) {
      j2 = valI[vv][sliderC][pors][vFill];
    } else if (gc == vten || gc == vfive) {
      j2 = valI[vv][sliderC][pors][klan];
    }
    if (E.debugSettingsTab && iinput != j2) {
      throw new MyErr("doval5 vv=" + vv + ", desc=" + valS[vv][vDesc] + " iinput=" + iinput + " not equal to saved slider  value =" + j2);
    }
    // now test that the value save in valI results in a real number about 5% of original value
    double dif0 = Math.abs(high - low);
    double dif1 = dif0 * .5;
    // overwrite the real number with the putVal value
    // leave the original value without change
    j3 = putVal(iinput, vv, pors, clan);
    if (gc == vone || gc == vtwo) {
      t1 = valD[vv][gameAddrC][vFill][pors];
      //   valD[vv][gameAddrC][vFill][pors] = val;
    } else if (gc == vthree || gc == vfour) {
      t1 = valD[vv][gameAddrC][pors][vFill];
      // valD[vv][gameAddrC][pors][vFill] = val;
    } else if (gc == vseven) {
      t1 = valD[vv][gameAddrC][vFill][valI[vv][sevenC][vFill][vFill]];
    } else if (gc == vten || gc == vfive) {
      t1 = valD[vv][gameAddrC][pors][klan];
      //   valD[vv][gameAddrC][pors][klan] = val;
    }
    if (E.debugSettingsTab && Math.abs(val - t1) > dif1) {
      throw new MyErr("doVal5.6 regenerated value too different=" + mf(val - t1) + ", allowed=" + mf(dif1) + ", val=" + mf(val) + ", reval=" + mf(t1) + ", frac dif=" + mf((val - t1) / dif0) + ", vv=" + vv + " name=" + valS[vv][vDesc] + ", gc=" + gc + ", pors=" + pors + ", clan=" + clan % 5);

    }

    if (gc == vone || gc == vtwo) {
      j1 = valI[vv][sliderC][vFill][pors];  // j1 should be val
      valI[vv][sliderC][vFill][pors] = -3;// force a different old gc value
      j4 = putVal(iinput, vv, pors, klan); // change the gamefare to regen val
      t1 = valD[vv][gameAddrC][vFill][pors];
      valD[vv][gameAddrC][vFill][pors] = val; // restore val
      valI[vv][sliderC][vFill][pors] = j1; // restore sliderC val
    } else if (gc == vthree || gc == vfour) {
      j1 = valI[vv][sliderC][pors][vFill];
      valI[vv][sliderC][pors][vFill] = -3;// force a different old value
      j4 = putVal(iinput, vv, pors, klan); // change the gamefare to regen val
      t1 = valD[vv][gameAddrC][pors][vFill];
      valD[vv][gameAddrC][pors][vFill] = val;
      valI[vv][sliderC][pors][vFill] = j1;
    } else if (gc == vfive || gc == vten) {
      j1 = valI[vv][sliderC][pors][klan]; // save iinput
      valI[vv][sliderC][pors][klan] = -3;// force a different old value
      j4 = putVal(iinput, vv, pors, klan); // change the gamefare to regen val
      t1 = valD[vv][gameAddrC][pors][klan];
      valD[vv][gameAddrC][pors][klan] = val; // restore original
      valI[vv][sliderC][pors][klan] = j1;
    }
    if (E.debugSettingsTab && Math.abs(val - t1) > dif1) {
      throw new MyErr("doVal5.7 regenerated value too different=" + mf(val - t1) + ", allowed=" + mf(dif1) + ", val=" + mf(val) + ", reval=" + mf(t1) + ", frac dif=" + mf((val - t1) / dif0) + ", vv=" + vv + " name=" + valS[vv][vDesc] + ", gc=" + gc + ", pors=" + pors + ", clan=" + clan);
    }
  }//doVal5

  String ret = "234f";

  String doReadKeepVals(){
    try {
      String dateString = MYDATEFORMAT.format(new Date());
      String rOut = "New Game " + dateString + "\n";
      bKeepr = Files.newBufferedReader(KEEP, CHARSET);
      Scanner s = new Scanner(bKeepr);
      while (s.hasNext()) {
        String fname = s.next();
        String lname = "line";
        switch (fname) {
          case "new":
          case "year":
           
            lname = s.nextLine();
            System.out.println("line=" + fname + " " + lname);
            break;
          case "keep":
            fname = s.useDelimiter("#\\s*").next();
            s.useDelimiter("\\s");
            Object vo = valMap.get(fname);
            if(vo != null){
              int vv = (int)vo;
            int ps = s.nextInt();
            int klan = s.nextInt();
            double val = s.nextDouble();
            //  svalp = valToSlider(vR = valD[vv][gameAddrC][pors][0], lL = valD[vv][gameLim][pors][vLowLim], lH = valD[vv][gameLim][pors][vHighLim]);
            if (val > valD[vv][gameLim][ps][vHighLim] || val < valD[vv][gameLim][ps][vLowLim]) {
              double val0 = val;

              val = val > valD[vv][gameLim][ps][vHighLim] ? valD[vv][gameLim][ps][vHighLim] : val < valD[vv][gameLim][ps][vLowLim] ? valD[vv][gameLim][ps][vLowLim] : val;
              if (E.debugScannerOut) {
                System.out.println("keep  val out of range" + fname + " " + vv + "  " + ps + " " + mf(val0) + " =>>" + mf(val));
              }
            }
            valD[vv][gameAddrC][ps][(klan > 4?0:klan)] = val; // restore val
             
            lname = s.nextLine();
            if (E.debugScannerOut) {
              System.out.println("keep saved " + fname + " " + vv + "  " + ps + " " + klan + " " + mf(val) + " " + lname);
            }
            } else {
             
            lname = s.nextLine();
            if (E.debugScannerOut) {
              System.out.println("keep unknown " + fname + " " + lname);
            }
            }
            break;
          default:
            lname = s.nextLine();
            System.out.println("Unknow line=" + fname + " " + lname);
        } // switch
      } // while
      if(bKeepr != null) {
        bKeepr.close();
      }

    } catch (Exception ex) {
      flushes();
      System.err.println("doKeepVals error" + Econ.nowName  + " " + Econ.nowThread + "Caught Exception cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + " " + andMore());
      System.err.flush();
      ex.printStackTrace(System.err);
      System.err.flush();
      flushes();
      System.err.println(Econ.nowName  + " " + Econ.nowThread + "Ignore this error " + new Date().toString() + " " + (new Date().getTime() - startTime) + " cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + ", addlErr=" + eM.addlErr + addMore());
      flushes();

    } finally {
     
      return ret;
    }
  }
  /*
   static boolean keepFromPage = false;  // keep clicked titles on this page
  static boolean keepHeaderPrinted = false; // header already printed
  static boolean keepBuffered = false; // true if flush needed
  static final String keepInstruct = "keep any ettings changes made in this page, describe why you kept this changes at KeepCmt";
  static final String initialKeepCmt = "KeepCmt put the why you changed the settings on this page";
  static String prevKeepCmt = initialKeepCmt + "";
  static String nextLineOfOutput = ""; //both keep and remember
   */
     int keepYear = -1; // initial unused year
     /** write the keep file if the keepFromPage flag set, a new page clears the flag
      * 
      * @param vv  the index of the val used to get the title
      * @param ps  the pors value
      * @param klan the clan value
      * @param val  the value to be saved
      * @param val  the previous value before the change
      * @param slider  the new slider value
      * @param prevSlider the previous slider value
      * @param prev2Slider the previous previous slider value
      * @throws IOException 
      */
 public void doWriteKeepVals(int vv,int ps,int klan,double val, double prevVal,int slider, int prevslider, int prev2slider) throws IOException {
 
  String ll = " ";
  if(keepFromPage) {
    if(year != keepYear || !st.settingsComment.getText().matches(prevKeepCmt)){ // need another year comment page
      keepYear = year;
      prevKeepCmt = st.settingsComment.getText() + ""; // force a copy
      String dateString = MYDATEFORMAT.format(new Date());
  //    String rOut = "New Game " + dateString + "\r\n";
      ll = "year" + year +" version " + st.versionText  +  " " + dateString + " " + st.settingsComment.getText() + "\r\n";
      bKeep.write(ll, 0, ll.length());
    }
    ll = "title " + valS[vv][1] + "\r\n"; // the detail description of the keep
    bKeep.write(ll,0,ll.length());
    ll = "keep " + valS[vv][0] + "# " + ps + " " + klan + " " + mf(val) + " <= " + mf(prevVal) + " sliders " + slider + " <= " + prevslider + " <= " + prev2slider + "\r\n";
    bKeep.write(ll,0,ll.length());
    
  }//keep from page
}//doWriteKeepVals

  /**
   * get value from valD and turn it into a slider int between 0-100 This is
   * used to generate the slider window
   *
   * @param vv The entry being set to a slider to show its value in slider
   * @param pors 0,1 planet or ship being set
   * @param clan 0-4,5 5 means a game value, 0-4 are the 5 clans
   * @return the value to set in the slider
   */
  int getVal(int vv, int pors, int clan) {
    int slider1 = -1;
    int klan = clan % 5;
    int gc = valI[vv][modeC][0][0];
    if (clan == 5 && gc <= vfour) {
      if ((gc == vone && pors == E.P) || gc == vtwo) {
        return valToSlider(valD[vv][gameAddrC][0][pors], valD[vv][gameLim][pors][lowC], valD[vv][gameLim][pors][highC]);
      } else if ((gc == vthree && pors == E.P) || gc == vfour) {
        return valToSlider(valD[vv][gameAddrC][pors][0], valD[vv][gameLim][pors][lowC], valD[vv][gameLim][pors][highC]);

      } else if (gc == vseven) { // ignored
        return valToSlider(valD[vv][gameAddrC][vFill][valI[vv][sevenC][vFill][vFill]], valD[vv][gameLim][vFill][lowC], valD[vv][gameLim][vFill][highC]);
      } else if (E.debugSettingsTab) {  //problem with clan == 5, unknown gc

        String verr = "getVa; illegal clan =" + clan + " with gc=" + gc + ", desc=" + valS[vv][vDesc] + ", vv=" + vv + ",  pors=" + pors;
        throw new MyErr(verr);
      }
    } // end of gameMaster clan == 5
    // now do clan entries gc == vten
    else if ((gc == vfive || gc == vten) && pors >= 0 && pors <= 1 && klan >= 0 && klan <= 4) {
      return valToSlider(valD[vv][gameAddrC][pors][klan], valD[vv][gameLim][pors][lowC], valD[vv][gameLim][pors][highC]);

    } else if (E.debugSettingsTab) {
      String verr = "getVa; illegal clan =" + clan + " with gc=" + gc + ", desc=" + valS[vv][vDesc] + ", vv=" + vv + ",  pors=" + pors;
      throw new MyErr(verr);
    }
    return 50;
  }

  /**
   * put value from slider to the gameValue in the EM. .. values for the game if
   * the slider value DID NOT CHANGE DO NOT CHANGE gameValue, do not change the
   * prevs and return 0 that NOTHING CHANGED
   *
   * @param slider new value from the slider, probably didn't change
   * @param vv position in the values arrays
   * @param pors planet or ship
   * @param clan 0-4 a clan-master 5=game-master
   * @return 1 == change gameValue, 0=noChange
   */
  int putVal(int slider, int vv, int pors, int clan) throws IOException  {
    int va  = -1;
    int gc = valI[vv][modeC][vFill][0];
    int klan = clan < 5 ? clan : clan == 5 ? 0 : clan % 5;
    if (E.debugPutValue2) {
     // System.out.print("EM putVal gc=" + gc + " " + ", vv=" + vv + " " + valS[vv][0] + ", " + E.cna[pors] + ", clan=" + clan + ":" + klan);
    }

    if (gc <= vfour || gc == vfive) {
      //double sosfrac[] = {.3, .35};
      if ((gc == vone && pors == E.P) || gc == vtwo) {
        if (slider == (va  = valI[vv][sliderC][vFill][pors])) {
          if (E.debugPutValue2) {
            System.out.println("EM putVal gc=" + gc + " " + ", vv=" + vv + " " + valS[vv][0] + ", " + E.cna[pors] + ", clan=" + clan + ":" + klan + " no change");
          }
          return 0; // no change
        }
        //value must change for vone and vtwo
        double val0 = valD[vv][gameAddrC][vFill][pors];
        double val1 = valD[vv][gameAddrC][vFill][pors] = sliderToVal(slider, valD[vv][gameLim][pors][vLowLim], valD[vv][gameLim][pors][vHighLim]);
        
        int prev2Slider = valI[vv][prev2SliderC][vFill][pors] = valI[vv][prevSliderC][vFill][pors];
        int prevSlider = valI[vv][prevSliderC][vFill][pors] = valI[vv][sliderC][vFill][pors];
        valI[vv][sliderC][vFill][pors] = slider; // a new value for slider
        if (E.debugPutValue) {
          System.out.println("EM putVal gc=" + gc + " " + ", vv=" + vv + " " + valS[vv][0] + ", " + E.cna[pors] + ", clan=" + clan + ":" + klan + ", was=" + mf(val0) + ", to=" + mf(val1) + " sliders " + prev2Slider + " => " + prevSlider + " => " + slider);
        }
        doWriteKeepVals(vv,pors,clan,val1,val0,slider,prevSlider,prev2Slider);
        return 1;
      } // note different way gameMaster values stored
      //double[][] rsefficiencyGMax = {{2.}, {2.}}
      else if ((gc == vthree && pors == E.P) || gc == vfour) {
        if (slider == (va  = valI[vv][sliderC][pors][vFill])) {
          if (E.debugPutValue2) {
            System.out.println("EM putVal gc=" + gc + " " + ", vv=" + vv + " " + valS[vv][0] + ", " + E.cna[pors] + ", clan=" + clan + ":" + klan + "no change");
          }
          return 0; // no change
        }
        double val0 = valD[vv][gameAddrC][pors][0];
        double val1 = valD[vv][gameAddrC][pors][0] = sliderToVal(slider, valD[vv][gameLim][pors][vLowLim], valD[vv][gameLim][pors][vHighLim]);
        int prev2Slider = valI[vv][prev2SliderC][pors][vFill] = valI[vv][prevSliderC][pors][vFill];
        int prevSlider = valI[vv][prevSliderC][pors][vFill] = valI[vv][sliderC][pors][vFill];
        valI[vv][sliderC][pors][vFill] = slider; // a new value for slider
        if (E.debugPutValue) {
          System.out.println("EM putVal gc=" + gc + " " + ", vv=" + vv + " " + valS[vv][0] + ", " + E.cna[pors] + ", clan=" + clan + ":" + klan + ", was=" + mf(val0) + ", to=" + mf(val1) + " sliders " + prev2Slider + " => " + prevSlider + " => " + slider);
        }
        doWriteKeepVals(vv,pors,clan,val1,val0,slider,prevSlider,prev2Slider);
        return 1;
      } else if (gc == vseven) {
        if (slider == (va  = valI[vv][sliderC][pors][vFill])) {
          if (E.debugPutValue2) {
            System.out.println("EM putVal gc=" + gc + " " + ", vv=" + vv + " " + valS[vv][0] + ", " + E.cna[pors] + ", clan=" + clan + ":" + klan + " no change");
          }
          return 0; // no change
        }
        // I think this address is wrong, but not used

        double val0 = valD[vv][gameAddrC][vFill][valI[vv][sevenC][vFill][vFill]];
        double val1 = valD[vv][gameAddrC][vFill][valI[vv][sevenC][vFill][vFill]] = sliderToVal(slider, valD[vv][gameLim][vFill][vLowLim], valD[vv][gameLim][vFill][vHighLim]);
        int prev2Slider = valI[vv][prev2SliderC][vFill][valI[vv][sevenC][vFill][vFill]] = valI[vv][prevSliderC][vFill][valI[vv][sevenC][vFill][vFill]];
        int prevSlider = valI[vv][prevSliderC][vFill][valI[vv][sevenC][vFill][vFill]] = valI[vv][sliderC][vFill][valI[vv][sevenC][vFill][vFill]];
        valI[vv][sliderC][vFill][valI[vv][sevenC][vFill][vFill]] = slider; // a new value for slider
        if (E.debugPutValue) {
          System.out.println("EM putVal gc=" + gc + " " + ", vv=" + vv + " " + valS[vv][0] + ", " + E.cna[pors] + ", clan=" + clan + ":" + klan + ", was=" + mf(val0) + ", to=" + mf(val1) + " sliders " + prev2Slider + " => " + prevSlider + " => " + slider);
        }
        doWriteKeepVals(vv,pors,clan,val1,val0,slider,prevSlider,prev2Slider);
        return 1;
      }
    } //  double[][] clanStartFutureFundDues = {{700., 700., 700., 700., 700.}, {600., 600., 600., 600., 600.}};
    else if ((gc == vten || gc == vfive)) {
      va  = valI[vv][sliderC][pors][klan];
      if (E.debugPutValue2) {
        System.out.print(" old slider=" + va  + ", new slider=" + slider);
      }
      if (slider == va) {
        if (E.debugPutValue2) {
          System.out.println("no change");
        }
        return 0; // no change
      }

      double val0 = valD[vv][gameAddrC][pors][klan];
      double val1 = valD[vv][gameAddrC][pors][klan] = sliderToVal(slider, valD[vv][gameLim][pors][vLowLim], valD[vv][gameLim][pors][vHighLim]);
      int prev2Slider = valI[vv][prev2SliderC][pors][clan] = valI[vv][prevSliderC][pors][clan];
      int prevSlider = valI[vv][prevSliderC][pors][clan] = valI[vv][sliderC][pors][clan];
      valI[vv][sliderC][pors][clan] = slider; // a new value for slider
      if (E.debugPutValue) {
          System.out.println("EM putVal gc=" + gc + " " + ", vv=" + vv + " " + valS[vv][0] + ", " + E.cna[pors] + ", clan=" + clan + ":" + klan + ", was=" + mf(val0) + ", to=" + mf(val1) + " sliders " + prev2Slider + " => " + prevSlider + " => " + slider);
        }
        doWriteKeepVals(vv,pors,clan,val1,val0,slider,prevSlider,prev2Slider);
      return 1;
    } else if (E.debugSettingsTab) {  //problem with gc
      if (E.debugPutValue2) {
        System.out.println("gc oops =" + valS[vv][0] + ", old slider=" + va  + ", new slider=" + slider + ", gc=" + gc + ", pors=" + pors + ", clan=" + clan);
      }
      String verr = "putval illegal gc=" + gc + "  " + valS[vv][vDesc] + ", vv=" + vv + ",  pors=" + pors + ", klan=" + klan;
      throw new MyErr(verr);
    }
    return 1;
  }

  /**
   * convert val to slider int
   *
   * @param val real value being converted
   * @param low 1st limit usually lowest from doVal initialization
   * @param high 2nd limit usually highest from doVal initialization
   * @return int for the slider 0 %le; int %le; 99 sliderExtend (100)
   */
  int valToSlider(double val, double low, double high) {
    // dif1 20 = 29-10+1  dif2 = -20 10 - 29-1
    double gameValueExtent = high - low; // accept both limits
    double gameFrac = (val - low) / gameValueExtent;
    int sliderVal = sliderLow + (int) (sliderExtent * gameFrac);
    // dif = dif < 0? dif -1: dif+1; // include both upper and lower limits
    // dif1 rtn 25 = 15 -10 (5) *(5) 100 / 20
    // dif2 rtn 75  = 14 - 29 (-15) * (-5) 100 / -20  75 = 3/4 from 1st lim
    //int rtnVal = (int) ((val - low) * sliderExtent / dif);
    return sliderVal;
  }

  /**
   * convert a slider value back to a real game value the full formula is
   * sliderExtent defined for slider the gameValExtent = limit2 limit1
   * sliderFrac = (slider -sliderLow)/sliderExtent or * invSliderExtent gameVal1
   * = sliderFrac * gameValueExtent result = val2 = gameVal1 + limit1
   *
   * @param slider value from the slider
   * @param limit1 first game value limit usually low
   * @param limit2 second game value limit usually high not included in extent
   * @return result
   */
  double sliderToVal(int slider, double limit1, double limit2) {
    double gameValueExtent = limit2 - limit1;
    double sliderFrac = (slider - sliderLow) * invSliderExtent;
    double gameVal1 = (sliderFrac * gameValueExtent);
    double val2 = gameVal1 + limit1; // if gameLow > gameHigh, val1 < 0, high + -val
    if (E.debugSettingsTabOut) {
      System.out.println("sliderToVal slider=" + slider + ", game limit1=" + mf(limit1) + ",game limit2=" + mf(limit2) + ", gameValueExtent=" + mf(gameValueExtent) + ",sliderExtent=" + mf(sliderExtent) + ", invSliderExtent=" + mf(invSliderExtent) + ", sliderFrac=" + mf(sliderFrac) + ", gameVal1=" + mf(gameVal1) + ", game val2 result=" + mf(val2));
    }
    //if(E.debugSettingsTab)System.out.format("sliderToVal slider=%3d, gameLow=%7.5f,gameHigh=%7.5f, gamevalueExtent= %7.5f,sliderExtent=%5.2f, invSliderExtent=%5.2f, sliderFrac=%5.2f, val add=%5.2f, val result=%5.2f\n", slider, gameLow, gameHigh, gameValueExtent, sliderExtent, invSliderExtent, sliderFrac, val1, val2);
    return val2;
  }

  /**
   * return true if this doVal matches the gameClanStaus currently being
   * displayed
   *
   * @param vv index of the doVal
   * @return true if match, false otherwise
   */
  boolean matchGameClanStatus(int vv) {
    int gc = valI[vv][modeC][0][0];
    boolean rtn = ((gc >= vone && gc <= vfour) && gameClanStatus == 5)
            || (vten == gc || gc == vfive ) && (0 <= gameClanStatus && 4 >= gameClanStatus);
    if (E.debugSettingsTabOut) {
      System.out.println("at 2146 match game clan vv=" + vv + " valS=" + valS[vv][vDesc] + " match game clan status=" + gameClanStatus + ", gc=" + gc + ", " + (rtn ? "" : "!!") + "rtn");
    }
    return rtn;
  }

  /**
   * return the number of slider lines this doVal will need
   *
   * @param vv index of doVal
   * @return number of slider lines
   */
  int dispLen(int vv) {
    int gc = valI[vv][modeC][vFill][0];
    return (vone <= gc && vfour >= gc) || gc == v25 ? 1 : gc == v72 || gc == v725 ? 7 : gc == v162 ? 16 : 1;
  }

  /**
   * return the detail about this input doVal
   *
   * @param vv index of the input doVal
   * @return
   */
  String getDetail(int vv) {
    return valS[vv][0] + "  " + valS[vv][1];
  }

  void checkLims(double v, double[][] lims, String sDesc, String sDetail) {
    int age = curEcon.age;
    int clan = curEcon.clan;
    int pors = curEcon.pors;
    if (v > lims[pors][1]) {
      bErr(" value=" + df(v) + " exceeds max=" + df(lims[pors][1]) + " for " + sDesc + " with " + sDetail);
    }
    if (v < lims[pors][0]) {
      bErr(" value=" + df(v) + " below min=" + df(lims[pors][0]) + " for " + sDesc + " with " + sDetail);
    }
  }

  /**
   * force limits on int[] result values
   *
   * @param pors indication if this is planet or ship being processed
   * @param res result array
   * @param src source values from doVal
   * @param imask double array of limits
   */
  void doLims(int pors, int[] res, double[] src, int[][] imask) {
    res[pors] = (int) src[pors] < imask[pors][0] ? imask[pors][0] : (int) src[pors] > imask[pors][1] ? imask[pors][1] : (int) src[pors];
  }

  /**
   * force limits on double[] result values
   *
   * @param pors indication if this is planet or ship being processed
   * @param res result array
   * @param src source values from doVal
   * @param imask double array of limits
   */
  void doLims(int pors, double[] res, double[] src, double[][] mask) {
    res[pors] = src[pors] < mask[pors][0] ? mask[pors][0] : src[pors] > mask[pors][1] ? mask[pors][1] : src[pors];
  }

  /**
   * run a series of adjustments from values set by doVal's, to a set of values
   * derived by function of doVal's with some
   */
  void runAdjusts() {
    // process those values for both P and S
    for (int mpors = 0; mpors < 2; mpors++) {
      maxTries[mpors] = maxTries[mpors];
      // doLims(mpors, maxTries, dMaxTries, mMaxTries);

      // within pors process clans
      for (int nclan = 0; nclan < LCLANS; nclan++) {
      }

    } //mpors

  }

  /**
   * run the initialization of the valD, valI, valS arrays that set the sliders
   * also run settings adjustments at the end
   */
  void runVals() throws IOException {
    doVal("difficulty  ", difficultyPercent, mDifficultyPercent, "For ships as well as  Planets , set the difficulty of the game, more difficulty increases costs of  resources and staff each year, increases the possibility of economy death.  More difficulty requires more clan boss expertise.");
    doVal("randomActions  ", randFrac, mRandFrac, "increased random, increases possibility of gain, and of loss, including possibility of death");
    doVal("Min Econs by Year", minEconsMult, mminEconsMult, "increase slider, increase min econs, create new free econs for clans starting at a random clan");
    doVal("wGiven", wGiven,mNegPluScoreMult, "increase slider, increase the value of  the percent given in trade by the clan.");
    doVal("wGiven2", wGiven2,mNegPluScoreMult, "increase slider, increase the value of  the percent given in trade by the clan.");
    doVal("wGenerous", wGenerous, mNegPluScoreMult, "iincrease slider, increase the value of  the percent given in trade by the clan.");
    doVal("iGiven", iGiven,mNegPluScoreMult, "increase slider, increase the score for number of economies traded.");
    doVal("wLiveWorthScore", wLiveWorthScore,mNegPluScoreMult, "increase slider, increase the winning score for the clan final worth.");
    doVal("iLiveWorthScore", iLiveWorthScore,mNegPluScoreMult, "increase slider, increase the winning score for the clan final count of planets and ships");
    doVal("iBothCreateScore", iBothCreateScore,mNegPluScoreMult, "increase slider, increase the winning score for the number of this clan in ever created");
    doVal("wYearTradeV", wYearTradeV,mNegPluScoreMult, "increase slider, increase the winning score for the increase in the year trade increase");
    doVal("wYearTradeI", wYearTradeI,mNegPluScoreMult, "increase slider, increase the winning score for thee increase in the number of economies with at least one trade that year");
    doVal("years To Win", winDif, mwinDif, "increase slider, increase the years before a winner is declared");
    doVal("iNumberDiedI", iNumberDied,mNegPluScoreMult, "increase slider, decrease the winning score for the number of dead economies for this clan this year");
    doVal("resourceCosts", mab1, mmab1, "raise the cost of resources planet and ship, makes game harder");
    doVal("staffCosts", mac1, mmac1, "raise the costs of staff for planets and ships, makes planets and ships die more often");
    doVal("Threads", maxThreads, mmaxThreads, "Increase the number of possible threads. If your computer supports more than 1 cpu, more threads may decrease the total time per year.");
    doVal("haveColors", haveColors, mHaveColors, "Above slider 50 the tab display will show the color of the current economy. The changing of colors helps understand how fast the game is going.  Some persons have trouble with blinking colors, so setting this slider below 50 will stop the color changes.");
     doVal("haveCash", haveCash, mHaveCash, "Above slider 50 the tab display Economies will start with some cash, cash is just another resource that can be traded, think of it as bars of gold, not as paper money");
    doVal("tradeReservFrac", tradeReservFrac, mTradeReservFrac, "raise the amount of resource or staff to reserve during a trade, higher reduces risk and reduces gain");
    doVal("Max LY  ", maxLY, mMaxLY, "adjust the max Light Years distance of planets for trades");
    doVal("Add LY  ", addLY, mAddLY, "adjust addition per round to the max Light Years distance of planets for traded");
    doVal("SearchPlanetsCnt", wildCursCnt, mWildCursCnt, "adjust the number of planets listed to be judged for the next trade");
    doVal("SearchYearlyBias  ", searchYearBias, mSearchYearBias, "increase,decrease value of prospective trade for earlier years");
    // doVal("econLimits1  ", econLimits1, mEconLimits1, "Increase the max number of econs (planets+ships) in this game");
    //  doVal("econLimits2  ", econLimits2, mEconLimits2, "Increase the max number of econs (planets+ships) in this game");
    //  doVal("maxEcons", econLimits3, mEconLimits3, "Increase the max number of econs (planets+ships) in this game");
    doVal("Clan Ships per planets", clanShipFrac, mClanShipFrac, "increase faction of ships per planets for this clan only, limited by All ships per planets and game ships per planets");
    doVal("All ships per planets", clanAllShipFrac, mClanAllShipFrac, "for this clan increase the fraction of ships per all planets, limited by Clan Ships per planets  and game Ships per planets ");
    doVal("Ships per planets", gameShipFrac, mGameShipFrac, "increase the fraction of ships in the game. bit for each clan, limited by clanShipFrac and clanAllShipFrac");
    doVal("resourceGrowth", resourceGrowth, mResourceGrowth, "increase amount of resource growth per year, dependent on units of staff");
    doVal("cargoGrowth", cargoGrowth, mCargoGrowth, "increase amount of cargo growth per year dependent of units of staff");
    doVal("staffGrowth", staffGrowth, mStaffGrowth, "increase amount of staff growth per year, dependent on units of staff");
    doVal("guestGrowth", guestsGrowth, mGuestsGrowth, "increase amount of guest growth per year, dependent on units of guests");
    doVal("maxGrowth", maxGrowth, mMaxGrowth, "increase the largest possible size, growths will slow to prevent reaching this size");
    doVal("InitYrsTraveled", initTravelYears, mInitTravelYears, "Increase initial travel cost");
    doVal("favr", fav0, mfavs, "how much your clan favors clan red by giving a better barter");
    doVal("favo", fav1, mfavs, "how much your clan favors clan orange by giving a better barter");
    doVal("favy", fav2, mfavs, "how much your clan favors clan yellow by giving a better barter");
    doVal("favg", fav3, mfavs, "how much your clan favors clan green by giving a better barter");
    doVal("favb", fav4, mfavs, "how much your clan favors clan blue by giving a better barter");
    doVal("ClanFutureFundDues", clanStartFutureFundDues, mClanStartFutureFundDues, "increase the value at which staff,resources are converted to cash for future economies decreases building new ships or planets, increases growths");
    doVal("futureFundTransferFrac", futureFundTransferFrac, mFutureFundTransferFrac, "increase the amount transfered to futureFund per n at emergencies and dues. increases building new economies, decreases growth may increase deaths, inrease growth, decrease new economies");
    doVal("FutureFundFrac", futureFundFrac, mFutureFundFrac, "iincrease the amount transfered to futureFund  at emergencies and dues. increases building new economies, decreases growth may increase deaths, inrease growth, decrease new economies");
    doVal("FutureFEmerg1", clanFutureFundEmerg1, mClanFutureFundEmerg, "adjust first level trigger when staff  or resources are out of bound,divert max staff/resource sectors to futureFund, larger than 2 to have 2 triggers");
    doVal("clanFutureFEmerg2", clanFutureFundEmerg2, mClanFutureFundEmerg, "adjust second level trigger when staff and resources are out of bound,divert staff/resource sectors to futureFund");
    doVal("TradeCriticalBias",tradeCriticalFrac,mTradeCriticalFrac,"clan increase the trade value of resources or staff critically needed and decrease the trade value of resources or staff least needed");
    doVal("swapDif", swapDif, mSwapDif, "decrease the difference when a resource sum, staff sum difference will permit only swap transmuts/repurposing to keep this ship or planet alive");
    doVal("staffSizeRestrictions", gameStartSizeRestrictions, mGameStartSizeRestrictions, "raise the value at which staff increases are restricted");
    doVal("Offer Request Bias", moreOfferBias, mMoreOfferBias, "adjust the bias for requests in the first round adjust of the bid at each turn of bidding");
    doVal("Maint Min Efficiency", effBias, mEffBias, "adjust the size of the efficiency fraction");
    doVal("Trade Reserve Inc Frac", tradeReserveIncFrac, mTradeReserveIncFrac, "adjust the increase of trade offerings");
    doVal("sectorDifficultyFromDifficulty", difficultyByPriorityMin, mDifficultyByPriorityMin, "Adjust the difficulty factor in sector priority");
    doVal("rsefficiencyMMin", rsefficiencyMMin, mRsefficiencyMMin, "increase the smallest maintenance efficiency, decreases the cost of maintence type costs");
    doVal("rsefficiencyMMax", rsefficiencyMMax, mRsefficiencyMMax, "increase the largest maintenance efficiency, decreases the cost of maintence type costs");
    doVal("rsefficiencyGMin", rsefficiencyGMin, mRsefficiencyGMin, "increase the smallest Growth  efficiency, decreases the cost of growth type costs");
    doVal("rsefficiencyGMax", rsefficiencyGMax, mRsefficiencyGMax, "increase the highest growth efficiency by decreasing the growth type costs");
    doVal("minFertility", minFertility, mMinFertility, "adjust the minimum value for fertility, the multiplier of staff for a sector, to calculate the amount of growth");
    doVal("maxFertility", maxFertility, mMaxFertility, "adjust the maximum value for fertility, the multiplier of staff for a sector, to calculate the amount of growth");
    doVal("poorHealthPenalty", poorHealthPenalty, mPoorHealthPenalty, "increase maintenance, travel, growth costs as health = (units - requiredHealthCosts) / required Health Costs");
    doVal("poorHealthEffectLowLim", poorHealthEffectLimitsL, mPoorHealthEffectLimitsL, "increase the lower limit of poorHealthEffect higher is higher costs");
    doVal("poorHealthEffectHighLim", poorHealthEffectLimitsH, mPoorHealthEffectLimitsH, "increase the higher limit of poorHealthEffect higher is higher costs");
    /* double futGrowthFrac[][] = {{.7, .8, .7, .8, .7}, {.8, .8, .7, .4, .3}};
  static double mFutGrowthFrac[][] = {{.2,2.2},{.2,2.2}};
  double futGrowthYrMult[][] = {{5., 5., 5., 4., 5.}, {8., 5., 3., 5., 5.}};
  static double[][] mFutGrowthYrMult = {{1.5,11.5},{1.5,11.5}};*/
    doVal("futureGrowthFrac", futGrowthFrac, mFutGrowthFrac, "increase years of costs lookahead");
    doVal("futureGrowthYears", futGrowthYrMult, mFutGrowthYrMult, "increase the growth multiplier");

    doVal("tradeEmergency", tradeEmergFrac, mTradeEmergFrac, "adjust the level  causing a planet or ship to shift to emergency trading");
    doVal("sectorDifficultyByPriority", difficultyByPriorityMult, mDifficultyByPriorityMult, "Adjust difficulty by the sector priority");

    doVal("initialWorth", initialWorth, mInitialWorth, "adjust the initial worth of a new planet or ship");
    doVal("initialColonists", initialColonists, mInitialColonists, "adjust the minimum  of colonists");
    doVal("initialColonistFrac", initialColonistFrac, mInitialColonistFrac, "increase the initial colonists fraction of initial worth");
    doVal("initialResources", initialResources, mInitialResources, "adjust the minimum h of resources");
    doVal("initialResourceFrac", initialResourceFrac, mInitialResourceFrac, "adjust the initial resources fraction of initial worth");
    doVal("initialReserve", initialReserve, mInitialReserve, "adjust the initial cargo or guests as a fraction of resources or staff");
    doVal("initialCash", initialWealth, mInitialWealth, "adjust the minimum of initial cash");
    doVal("initialWealthFrac", initialWealthFrac, mInitialWealthFrac, "adjust the initial cash fraction of initial worth");
    doVal("Knowledge worth", nominalWealthPerCommonKnowledge, mNominalWealthPerCommonKnowledge, "adjust the worth of knowledge");
    doVal("Frac New Knowledge", fracNewKnowledge, mFracNewKnowledge, "adjust the fraction of new knowledge in relation to knowledge");
    doVal("Frac Common Knowledge", initialCommonKnowledgeFrac, mInitialCommonKnowledgeFrac, "adjust the initial fraction of Initial Common Knowledge in Initial Worth");
    doVal("Manuals Worth", manualFracKnowledge, mManualFracKnowledge, "adjust the worth of manuals in relation to knowledge");

    doVal("tradeStrtAvail", startAvail, mStartAvail, "increase the number of staff&resource sectors available for trade");
    doVal("tradeAvailFrac", availFrac, mAvailFrac, "increase the amount of staff&resource available for trade");
    doVal("tradeAvailMin", availMin, mAvailMin, "increase the minimum value below which a sector is not available for trade");
    /* double offerAddlFrac[] = {.001,.2};
  static double[][]mOfferAddlFrac = {{.0001,.1},{.001,.4}}; */
    doVal("tradeAddlSVFrac", offerAddlFrac, mOfferAddlFrac, "increase the process excessOffers in a barter");
    doVal("userCatastrophyFreq  ", userCatastrophyFreq, mUserCatastrophyFreq, "increase catastrophies in each decade. Catastrophies reduce resources or staff, but then increases the possibility of growth, or ship manuals to trade");
    doVal("catastrophyMultKnow", catastrophyManualsMultSumKnowledge, mCatastrophyManualsMultSumKnowledge, "increase gain in manuals for ships in a Catastrophy");
    doVal("CatastrophicUnitsDestroyed", catastrophyUnitReduction, mCatastrophyUnitReduction, "increase the destruction of resources and staff in a catastrophy");
    doVal("CatastrophyBonusYears", catastrophyBonusYears, mCatastrophyBonusYears, "increase years of additional growth for a catastrophy");
    doVal("PriorityRandomAddition", priorityRandAdditions, mPriorityRandAdditions, "adjust possible size of random additons to sector priorities");
    doVal("CatastrophiesFrequency", userCatastrophyFreq, mUserCatastrophyFreq, "increase frequesncy of catastrophies");
    doVal("Catastrophy Size", gameUserCatastrophyMult, mGameUserCatastrophyMult, "increase size and frequency of catastrophies");
    // doVal("CWorthBias", cargoWorthBias, mCargoWorthBias, "increase the worth of cargo in relation to the worth of resources"); cargo worth matches resoruce worth, costs are however less
    //  doVal("GWorthBias", guestWorthBias, mGuestWorthBias, "increase the worth of guests in relation to the worth of staff");  guest worth matches staff worth, costs are less

    doVal("growthGoal", goalGrowth, mRegGoals, "set normal, non-emergency growth goal, may increase growth");
    doVal("ShipsTradeFraction", ssFrac, mSsFrac, "Increase the desired trade profit with other ships (received/given) in a trade, this may reduce the number of successful trades");
     doVal("tradeFraction", tradeFrac, mTradeFrac, "Increase the desired trade profit (received/given) in a trade, this may reduce the number of successful trades");
   //   doVal("tradeGrowthGoal", tradeGrowth, mAllGoals, "adjust growth goals while trading, increases the level of requests to meet goals");
    doVal("tradeGrowthGoal", tradeGrowth, mAllGoals, "adjust growth goals while trading, increases the level of requests to meet goals");
    doVal("healthGoal", goalHealth, mRegGoals, "set normal, non-emergency health goal, may increase health and reduce costs");
    doVal("emergHealthGoal", emergHealth, mAllGoals, "set emergency health goals for when economies are weak more might help or might may make them worse");
    doVal("tradeHealthGoal", tradeHealth, mAllGoals, "adjust health goals while trading, increases the level of requests to meet goals");
    /* mMtgWEmergency */
    doVal("mtgWEmergency", mtgWEmergency, mMtgWEmergency, "adjust the end of health emergencies based on prospects of life");
    doVal("futtMTGCostsMult", futtMTGCostsMult, mFuttMTGCostsMult, "unused, probably a bad idea. adjust the growth");

    vvend = vv;
    // now settings adjustments

  }

  // values  for doRes opr
  static final long getP = 00L;
  static final long getS = 01L;
  static final long psmask = 01L;
  static final long PSMASK = psmask;
  static final long sum = 0000002L;  // prlong at the round sum P + S
  static final long SUM = sum;
  static final long pns = sum;  // p and s are sum
  static final long PNS = sum;
  static final long both = 0000004L; // prlong both P & S this round
  static final long BOTH = both;
  static final long thisYr = 0000010L; // sum of r0 thisYr values
  static final long THISYR = thisYr;
  static final long TYVALUES = thisYr;
  static final long THISYEAR = thisYr;
  static final long thisYearUnits = 0100000L; // units this year
  static final long THISYEARUNITS = thisYearUnits;
  static final long THISYRUNITS = thisYearUnits;
  static final long TYUNITS = thisYearUnits;
  static final long thisYrUnits = thisYearUnits;
  static final long units = thisYearUnits; // Also this years units
  static final long UNITS = units;
  static final long skipUnset = 0000020L; // skip listing anything if value unset
  static final long SKIPUNSET = skipUnset;
  static final long curUnitAve = 0000040L; // each year sum/units
  static final long CURUNITAVE = curUnitAve;
  static final long curAve = curUnitAve;
  static final long CURAVE = curUnitAve;
  static final long cumUnitAve = 0000100L;  // cum sum values div by cum units
  static final long CUMUNITAVE = cumUnitAve;
  static final long cumAve = cumUnitAve;
  static final long CUMAVE = cumUnitAve;
  static final long thisYearUnitAve = 0000200L; // sum of This Year div by units
  static final long THISYEARAVE = thisYearUnitAve; // sum of This Year div by units
  static final long THISYEARUNITAVE = thisYearUnitAve;
  static final long thisYrUnitAve = thisYearUnitAve;
  static final long thisYrAve = thisYrUnitAve;
  static final long THISYRAVE = thisYrUnitAve;
  static final long TYAVE = thisYrUnitAve;
  static final long valuesDivByUnits = thisYearUnitAve; // sum this year div by units
  static final long cur = 0000400L;  // sums of values a listing for each saved year
  static final long CUR = cur;
  // static final long curAveUnits = 0200000; // units for each year
  static final long curUnits = 0001000L; // total units that divide curUnitAve values
  static final long CURUNITS = curUnits;
  static final long cumUnits = 0002000L; // cum sum of  units
  static final long CUMUNITS = cumUnits;
  static final long zeroUnset = 0004000L; // show unset as zero's
  static final long ZEROUNSET = zeroUnset;
  static final long tstring = 0010000L; //  use descriptor as a title
  static final long divByAve = 0020000L; // divide by other val/units
  static final long cum = 0040000L; // cum sum of values both without sum
  static final long CUM = cum;
  static final long dmask = 0777770L; // mask for at least one type
  static final long d1mask = 0777776L; // opr mask to determine boolean values

  static final long list0 = 00000100000000L; // usually part of 0 table view
  static final long LIST0 = list0;
  static final long list1 = 00000200000000L; // usually part of 1 table view etc
  static final long LIST1 = list1;
  static final long list2 = 00000400000000L;
  static final long LIST2 = list2;
  static final long list3 = 00001000000000L;
  static final long list4 = 00002000000000L;
  static final long list5 = 00004000000000L;
  static final long list6 = 00010000000000L;
  static final long list7 = 00020000000000L;
  static final long list8 = 00040000000000L;
  static final long LIST3 = list3;
  static final long LIST4 = list4;
  static final long LIST5 = list5;
  // Long LIST3a = LIST3;
  static final long LIST6 = list6;
  static final long LIST7 = list7;
  static final long LIST8 = list8;
  static final long list9 = 00100000000000L;
  static final long LIST9 = list9;
  static final long list10 = 00200000000000L;
  static final long LIST10 = list10;
  static final long list11 = 00400000000000L;
  static final long LIST11 = list11;
  static final long list12 = 01000000000000L;
  static final long LIST12 = list12;
  static final long list13 = 02000000000000L;
  static final long LIST13 = list13;
  static final long LIST14 = 04000000000000L;
  static final long LIST15 = 010000000000000L;
  static final long LIST16 = 020000000000000L;
  static final long LIST17 = 040000000000000L;
  static final long LIST18 = 0100000000000000L;
  static final long LIST19 = 0200000000000000L;
  static final long LIST20 = 0400000000000000L;
  static final long lmask = 0777777700000000L;
  static final long ROWS1 = 01000000000000000L;
  static final long ROWS2 = 02000000000000000L;
  static final long ROWS3 = 04000000000000000L;
  static final long DUP = 010000000000000000L;//BIT 49
  static final long SKIPDUP = 020000000000000000L;//BIT 50
  static final long ROWS123 = ROWS1 | ROWS2 | ROWS3;
  static final long ROWSMASK = 07000000000000000L;
  static final long LMASK = lmask;
  static final long LISTYRS = LIST10 | LIST11 | LIST12 | LIST13 | LIST14;
  //static final long LISTYRS = LISTYRS;
  static final long LIST1YRS = list1 | LISTYRS;
  static final long LIST3YRS = list3 | LISTYRS;  // missed deaths
  static final long LIST4YRS = list4 | LISTYRS;  // trades, accepted rej deaths
  static final long LIST5YRS = list5 | LISTYRS;  // other deaths
  static final long LIST41 = list4 | LIST1;
  static final long LIST40 = list4 | LIST0;   // trades accepted, rej deaths
  static final long LIST410 = list4 | LIST1 | LIST0;   // trades accepted, rej deaths
  static final long LIST51 = LIST5 | LIST1;   // other deaths
  static final long LIST52 = LIST5 | LIST2;
  static final long LIST52YRS = LIST52 | LISTYRS;
  static final long LIST41YRS = list4 | LIST1YRS;
  static final long LIST431YRS = list3 | LIST41YRS;
  static final long LIST2YRS = list2 | LISTYRS;
  static final long LIST29YRS = LIST2 | LIST9 | LISTYRS;
  static final long LIST29 = LIST2 | LIST9;
  static final long LIST42YRS = list4 | LIST2YRS;
  static final long LIST432YRS = list3 | LIST42YRS;
  static final long LIST43YRS = list3 | LIST4YRS;
  static final long LIST432 = LIST4 | LIST3 | LIST2;
  static final long LIST4321 = LIST4 | LIST3 | LIST2 | LIST1;
  static final long LIST94321 = LIST4 | LIST3 | LIST2 | LIST9 | LIST1;
  static final long LIST4321YRS = LIST1 | LIST432YRS;
  static final long LIST4320YRS = list0 | LIST432YRS;
  static final long LIST43210YRS = list1 | LIST4320YRS;
  static final long LIST32YRS = list2 | LIST3YRS;
  static final long LIST320YRS = list0 | LIST32YRS;
  static final long LTRADE = LIST1YRS | LIST4;
  static final long LDEATHS = LIST2YRS | LIST3;
  static final long LIST0YRS = list0 | LISTYRS;
  static final long LCURWORTH = LIST0YRS;
  static final long LTRADNFAVR = LIST1YRS;
  static final long LCASTFFRAND = LIST2YRS;
  static final long LRESOURSTAF = LIST0YRS | LIST7;
  static final long LGRONCSTS = LIST0YRS | LIST8;
  static final long LXFR = LIST2YRS | LIST14;
  static final long LDECR = LIST2YRS | LIST13;
  static final long LINCR = LIST2YRS | LIST12;
  static final long LFORFUND = LIST2YRS | LIST19;
  static final long LSWAPSA = LISTYRS | LIST17;
  static final long CURSMASK = CUR | CURAVE | CURUNITS | THISYEAR | THISYEARAVE | THISYEARUNITS;
  static final long CUMSMASK = CUM | CUMAVE |CUMUNITS;
  static final long LISTALL = lmask;
  static final long listall = lmask;

  static final public String gameTextFieldText = "This is to be filled with descriptions of the field over which the mouse hovers";

  //  values for doRes
  private boolean doSum = false;
  private boolean didSum = false; //a previous lock offered sum, so without both do sum
  private boolean doBoth = false;
  private long doPower = -5;
  private String powers = "";  // the power append to name and desc string
  private boolean doSkipUnset = false;
  private boolean doZeroUnset = false;
  private boolean didUnset = false;
  private boolean doCum = false;
  private boolean doValues = false;
  private boolean doUnits = false;
  private boolean doUnitsDivide = false;  // units ave
  private boolean doValidDivide = false; // divide sum by valid years  ave
  private boolean divBy = false;
  private static boolean tstr = false;   // a tstring line
  private static long lStart = 0;
  private static long lEnd = 1;  // o-1
  private static String suffix = "";
  private static String nextCol = "";
  private static boolean ctlFnd = false;
  private static boolean inNum = false;
  private static int colCnt = 0, colMax = 10, lTit = 41, lCol = 15;
  private static int lMax = 0, lCnt = 0;
  private static final int colAdd = 0;
  private static final int colAddBrk = 1;
  private static final int colBrkAdd = 2;
  private static final int colHlfAddBrk = 3;
  private static final int colHlfBrkAdd = 4;
  private static final int colBrkEnd = 5;
  TreeMap<String, Integer> valMap = new TreeMap<String, Integer>();
  TreeMap<String, Integer> resMap = new TreeMap<String, Integer>();
//                                    -1         0           1            2             3
  static final double[] MAX_PRINT = {0., 100000000., 1000000000., 10000000000., 100000000000., 1000000000000., 10000000000000., 100000000000000., 1000000000000000., 10000000000000000., 100000000000000000., 1000000000000000000., 10000000000000000000., 100000000000000000000., 1000000000000000000000., 10000000000000000000000., 100000000000000000000000., 1.E99};
  static int abc;
  String[][] resS;  // [RN][rDesc,rDetail] result string values
  static int rDesc = 0;
  static int rDetail = 1; // detail or tip text
  static final int CURMAXDEPTH = 7;

  /**
   * resV [resNum][cum,cur0-6,7-13,14-20,21-27,28-34,35-41][[p0-4],[s0-4]]
   *
   */
  static final int DCUM = 0;
  // years starting all,<=3,<=7,<=15,<=31,32++
  static final int DCUR0 = 1;
  static final int DCUR1 = DCUR0 + CURMAXDEPTH * 1;
  static final int DCUR2 = DCUR0 + CURMAXDEPTH * 2;
  static final int DCUR3 = DCUR0 + CURMAXDEPTH * 3;
  static final int DCUR4 = DCUR0 + CURMAXDEPTH * 4;
  static final int DCUR5 = DCUR0 + CURMAXDEPTH * 5;
  static final int DCUR6 = DCUR0 + CURMAXDEPTH * 6;  // not used

  static final int D3CUR0 = DCUR0 + 3 * DCUR6; // 1+3*7 = 22
  static final int D7CUR0 = 45; //starts 1, 8 4=15 8=22 16=29,32=36,end=41+2=43,
  static final int DVECTOR2L = 1 + 7 * 6;// 43
  // any row vector less than 9 does not have separate age entries
  static final int DVECTOR2MAX = 9;
  static final int DVECTOR2A = 8; //
  static final int DVECTOR3L = 2;  // P,S
  // extra start next age group cur0-6 entries in vector2
  static final int[] AGEBREAKS = {0, 4, 8, 16, 32, 999999}; // + over 31+ group
  static final String[] AGESTR = {"", "0-3", "4-7", "8-15", "16-31", "32+"};
  static final long[] AGELISTS = {list0 | list1 | list2, LIST10, LIST11, LIST12, LIST13, LIST14};
  static final long[] SHORTAGELIST = {LIST0 | LIST1 | LIST2};
  static final int shortLength = SHORTAGELIST.length;
  static final int longLength = AGELISTS.length;
  static final long AGEMASK = LIST10 | LIST11 | LIST12 | LIST13 | LIST14;
  static final int minDepth = 1; // set min number of output for allYears
  static final int maxDepth = 7; // 0 1 2 3 4 5 6
  static final int minYDepth = 1; // min number of output in year groups
  static final int maxYDepth = 2;
  // vector 4 is LCLANS
  double[][][][] resV;
  /**
   * resI [resNum][ICUM,ICUR0,...ICUR6(7*6rounds+2] [PCNTS[LCLANS],SCNTS[LCLANS]
   * ,CCONTROLD[ISSET,IVALID,IPOWER, (ICUM only),LOCKS0...3,IFRACS,IDEPTH]]
   * IDEPTH:1-7 max valid number of rows per age
   * IVALID:0-7,0=unset,1=row0..7=row6, highest set row valid entries
   * 0=unset,1=cur0,2=cur1,7=cur6
   */

  static final int ICUM = 0; // continue vector 2
  static final int ICUR0 = 1;
  static final int ICUR1 = 8;
  static final int ICUR2 = 15;
  static final int ICUR3 = 22;
  static final int ICUR4 = 29;
  static final int ICUR5 = 36;
  static final int ICUR6 = 43;
  static final int IVECTOR2L = DVECTOR2L;//43 ages < 4,8,16,31,31+
  static final int IVECTOR2A = DVECTOR2A;
  // end of second vector definition
  // start definitions for ICUM thru ICUR6, total 6 iterations
  static final int PCNTS = 0; // start 3rd vector on cum thru cur6
  static final int SCNTS = 1;
  static final int CCONTROLD = 2; // in level 3
  static final int IVECTOR3L = 3; // PP
  static final int ISSET = 0;
  static final int IVALID = 1;
  static final int IPOWER = 2;
  static final int IVECTOR4A = 3;
  static final int LOCKS0 = 3;
  static final int LOCKS1 = 4;
  static final int LOCKS2 = 5;
  static final int LOCKS3 = 6;
  static final int IFRACS = 7;   // IN FIRST ONE ONLY, FRACTION DIGITS
  static final int IDEPTH = 8; // 1 == only cur0, 3 = cur0,1,2, 7=cur0-6
  static final int IYDEPTH = 9; // depth year groups
  static final int IVECTOR4C = 10;
  // definitions for PCNTS and SCNTS

  // third vector for pCnts and sCnts
  static final int LCLANS = E.lclans;
  // third vector

  int inputClan = 5; // set in game inputs, 5=game
  int inputPorS = 0; // in inputs 0=P

  volatile long[][][][] resI;
  int winner = -1;

  // double gameV[][]; //[gameCnt][pval[],sval[],more[sngl,pmin,pmax,smin,smax]]
  // String[][] gameS; //[gameCnt][desc,detail]
 private  boolean unset = true;  // value in this rn nerver been set
 private  boolean myUnset = false;
  private boolean myCumUnset = false;
  private long valid = 0; // number of cur in this rn valid 2 = 0,1 etc.
  private long myAop = 0;
  private int myRn = 0;
  private String myDetail = "";
  private String dds = description + myDetail;
  private String isPercent = "";
  static int rende4;
  static int e4 = -1;
  static final int SCORE = ++e4;
  static final int LIVEWORTH = ++e4;
  static final int TRADELASTGAVE = ++e4;
  static final int TRADENOMINALGAVE = ++e4;
  //static final int TRADESTRATEGICGAVE = ++e4;
  static final int STARTWORTH = ++e4;
  // static final int TESTWORTH3 = ++e4;
  static final int WORTHIFRAC = ++e4;
  static final int WORTHINCR = ++e4;
  static final int WTRADEDINCRMULT = ++e4; // "WTRADEDINCRMULT" WTRADEDINCRSOS
  static final int WTRADEDINCRSOS = ++e4;
  static final int YEARCREATE = ++e4;
  static final int FUTURECREATE = ++e4;
  static final int BOTHCREATE = ++e4;
  static final int SGMTGC = ++e4;
  static final int RCMTGC = ++e4;
  static final int SGREQMC = ++e4;
  static final int RCREQMC = ++e4;
  static final int SGREQGC = ++e4;
  static final int RCREQGC = ++e4;
  static final int RRAWMC = ++e4;
  static final int CRAWMC = ++e4;
  static final int RCRAWMC = ++e4;
  static final int SRAWMC = ++e4;
  static final int GRAWMC = ++e4;
  static final int SGRAWMC = ++e4;
  static final int KNOWLEDGEB = ++e4;
  static final int KNOWLEDGEFRAC = ++e4;
  static final int POORKNOWLEDGEEFFECT = ++e4;
  static final int POORHEALTHEFFECT = ++e4;
  static final int MANUALSFRAC = ++e4;
  static final int NEWKNOWLEDGEFRAC = ++e4;
  static final int COMMONKNOWLEDGEFRAC = ++e4;
  static final int KNOWLEDGEINCR = ++e4;
  static final int NEWKNOWLEDGEINCR = ++e4;
  static final int MANUALSINCR = ++e4;
  static final int COMMONKNOWLEDGEINCR = ++e4;
  static final int RCfrac = ++e4;
  static final int SGfrac = ++e4;
  static final int MISSINGNAME = ++e4;
  //static final int DEADSWAPSNCOUNT = ++e4;
  static final int SWAPRINCRCOST = ++e4;
  static final int SWAPSINCRCOST = ++e4;
  static final int SWAPRDECRCOST = ++e4;
  static final int SWAPSDECRCOST = ++e4;
  static final int SWAPRXFERCOST = ++e4;
  static final int SWAPSXFERCOST = ++e4;
  static final int INCRAVAILFRAC5 = ++e4;
  static final int INCRAVAILFRAC4 = ++e4;
  static final int INCRAVAILFRAC3 = ++e4;
  static final int INCRAVAILFRAC2 = ++e4;
  static final int INCRAVAILFRAC1 = ++e4;
  static final int INCRAVAILFRAC0 = ++e4;
  static final int INCRAVAILFRAC = ++e4;  // sll svvrpyrf
  static final int INCRAVAILFRACa = ++e4;
  static final int INCRAVAILFRACb = ++e4;
  static final int DIED = ++e4;
  static final int DIEDPERCENT = ++e4;
  static final int DIEDCATASTROPHY = ++e4;
  static final int DEADRATIO = ++e4;
  static final int DEADHEALTH = ++e4;
  static final int DEADFERTILITY = ++e4;
  static final int DEADSWAPSMOVED = ++e4;
  static final int DEADSWAPSCOSTS = ++e4;
  static final int DEADTRADED = ++e4;
  static final int DTRADEACC = ++e4;
  static final int DTRADEOSOSR1 = ++e4;
  static final int DTRADEOSOSR2 = ++e4;
  static final int DTRADEOSOSR3 = ++e4;
  static final int DLOSTOSOSR1 = ++e4;
  static final int DLOSTOSOSR2 = ++e4;
  static final int DLOSTOSOSR3 = ++e4;
  static final int DLOSTSOSR1 = ++e4;
  static final int DTRADESOSR1 = ++e4;

  static final int DIEDSN4 = ++e4;
  static final int DIEDRN4 = ++e4;
  static final int DIEDSN4RM3X5 = ++e4;
  static final int DIEDSN4RM3X4 = ++e4;
  static final int DIEDSM3X5 = ++e4;
  static final int DIEDRM3X4 = ++e4; // END NOT IN IF ELSE CHAIN
  static final int DIEDSN4RN4 = ++e4;
  static final int DIEDSN3RN3 = ++e4;
  static final int DIEDSN3RN2 = ++e4;
  static final int DIEDSN3RM3X4 = ++e4;
  static final int DIEDSN3RM3X3 = ++e4;
  static final int DIEDSN3RN1 = ++e4;
  static final int DIEDSN3RM3X2 = ++e4;
  static final int DIEDSN3RM3X1 = ++e4;
  static final int DIEDSN3RM2X4 = ++e4;
  static final int DIEDSN3RM2X3 = ++e4;
  static final int DIEDSN3RM2X2 = ++e4;
  static final int DIEDSN3RM2X1 = ++e4;
  static final int DIEDSN3RM1X4 = ++e4;
  static final int DIEDSN3RM1X3 = ++e4;
  static final int DIEDSN3RM1X2 = ++e4;
  static final int DIEDSN3RM1X1 = ++e4;
  static final int DIEDSN2RN3 = ++e4;
  static final int DIEDSN2RN2 = ++e4;
  static final int DIEDSN2RM3X4 = ++e4;
  static final int DIEDSN2RM3X3 = ++e4;
  static final int DIEDSN2RN1 = ++e4;
  static final int DIEDSN2RM3X2 = ++e4;
  static final int DIEDSN2RM3X1 = ++e4;
  static final int DIEDSN2RM2X4 = ++e4;
  static final int DIEDSN2RM2X3 = ++e4;
  static final int DIEDSN2RM2X2 = ++e4;
  static final int DIEDSN2RM2X1 = ++e4;
  static final int DIEDSN2RM1X4 = ++e4;
  static final int DIEDSN2RM1X3 = ++e4;
  static final int DIEDSN2RM1X2 = ++e4;
  static final int DIEDSN2RM1X1 = ++e4;
  static final int DIEDSN1RN3 = ++e4;
  static final int DIEDSN1RN2 = ++e4;
  static final int DIEDSN1RM3X4 = ++e4;
  static final int DIEDSN1RM3X3 = ++e4;
  static final int DIEDSN1RN1 = ++e4;
  static final int DIEDSN1RM3X2 = ++e4;
  static final int DIEDSN1RM3X1 = ++e4;
  static final int DIEDSN1RM2X4 = ++e4;
  static final int DIEDSN1RM2X3 = ++e4;
  static final int DIEDSN1RM2X2 = ++e4;
  static final int DIEDSN1RM2X1 = ++e4;
  static final int DIEDSN1RM1X4 = ++e4;
  static final int DIEDSN1RM1X3 = ++e4;
  static final int DIEDSN1RM1X2 = ++e4;
  static final int DIEDSM3X4RN3 = ++e4;
  static final int DIEDSM3X3RN3 = ++e4;
  static final int DIEDSM3X2RN3 = ++e4;
  static final int DIEDSM3X1RN3 = ++e4;
  static final int DIEDSM2X4RN3 = ++e4;
  static final int DIEDSM2X3RN3 = ++e4;
  static final int DIEDSM2X2RN3 = ++e4;
  static final int DIEDSM2X1RN3 = ++e4;
  static final int DIEDSM1X4RN3 = ++e4;
  static final int DIEDSM1X3RN3 = ++e4;
  static final int DIEDSM1X2RN3 = ++e4;
  static final int DIEDSM1X1RN3 = ++e4;
  static final int DIEDSM3X4RN2 = ++e4;
  static final int DIEDSM3X3RN2 = ++e4;
  static final int DIEDSM3X2RN2 = ++e4;
  static final int DIEDSM3X1RN2 = ++e4;
  static final int DIEDSM2X4RN2 = ++e4;
  static final int DIEDSM2X3RN2 = ++e4;
  static final int DIEDSM2X2RN2 = ++e4;
  static final int DIEDSM2X1RN2 = ++e4;
  static final int DIEDSM1X4RN2 = ++e4;
  static final int DIEDSM1X3RN2 = ++e4;
  static final int DIEDSM1X2RN2 = ++e4;
  static final int DIEDSM1X1RN1 = ++e4;
  static final int DIEDSM3X4RN1 = ++e4;
  static final int DIEDSM3X3RN1 = ++e4;
  static final int DIEDSM3X2RN1 = ++e4;
  static final int DIEDSM3X1RN1 = ++e4;
  static final int DIEDSM2X4RN1 = ++e4;
  static final int DIEDSM2X3RN1 = ++e4;
  static final int DIEDSM2X2RN1 = ++e4;
  static final int DIEDSM2X1RN1 = ++e4;
  static final int DIEDSM1X4RN1 = ++e4;
  static final int DIEDSM1X3RN1 = ++e4;
  static final int DIEDSM1X2RN1 = ++e4;
  static final int DIEDSN1RM1X1 = ++e4;
  static final int TRADEFIRSTRECEIVE = ++e4;
  static final int TRADELASTRECEIVE = ++e4;
  static final int TRADERECEIVELASTPERCENTFIRST = ++e4;
  static final int TRADEFIRSTGAVE = ++e4;
  static final int TRADESTRATFIRSTRECEIVE = ++e4;
  static final int TRADESTRATLASTRECEIVE = ++e4;
  static final int TRADESTRATFIRSTGAVE = ++e4;
  static final int TRADESTRATLASTGAVE = ++e4;
  static final int TradeNominalReceivePercentNominalOffer = ++e4;
  static final int MaxNominalReceivePercentNominalOffer = ++e4;
  static final int MinNominalReceivePercentNominalOffer = ++e4;
  static final int TradeStrategicReceivePercentStrategicOffer = ++e4;
  static final int MaxStrategicReceivePercentStrategicOffer= ++e4;
  static final int MinStrategicReceivePercentStrategicOffer = ++e4;
  static final int YearTradeNominalReceivePercentNominalOffer = ++e4;
  static final int YearMaxNominalReceivePercentNominalOffer = ++e4;
  static final int YearMinNominalReceivePercentNominalOffer = ++e4;
  static final int YearTradeStrategicReceivePercentStrategicOffer = ++e4;
  static final int YearMaxStrategicReceivePercentStrategicOffer= ++e4;
  static final int YearMinStrategicReceivePercentStrategicOffer = ++e4;
  /*
   setStat(EM.TradeNominalReceivePercentNominalOffer,pors,clan,100*totalStrategicRequests/nominalOffers);
          setNaxEM.MaxNominalReceivePercentNominalOffer,pors,clan,100*totalStrategicRequests/nominalOffers);
          setMin(EM.MinNominalReceivePercentNominalOffer,pors,clan,100*totalStrategicRequests/nominalOffers);
          setStat(EM.TradeStrategicReceivePercentStrategicOffer,pors,clan,100*totalStrategicRequests/nominalOffers);
          setMax(EM.MaxStrategicReceivePercentStrategicOffer,pors,clan,100*totalStrategicRequests/nominalOffers);
          setMin(EM.MinStrategicReceivePercentStrategicOffer,pors,clan,100*totalStrategicRequests/nominalOffers);
  */
  static final int TRADESOS1 = ++e4;
  static final int TRADESOS2 = ++e4;
  static final int TRADESOS3 = ++e4;
  static final int TRADEOSOS1 = ++e4;
  static final int TRADEOSOS2 = ++e4;
  static final int TRADEOSOS3 = ++e4;
  static final int TRADESOSR1 = ++e4;
  static final int TRADESOSR2 = ++e4;
  static final int TRADESOSR3 = ++e4;
  static final int TRADEOSOSR1 = ++e4;
  static final int TRADEOSOSR2 = ++e4;
  static final int TRADEOSOSR3 = ++e4;
  static final int BEFORETRADEWORTH = ++e4;
  static final int AFTERTRADEWORTH = ++e4;
  static final int TRADEWORTHINCRPERCENT = ++e4;
  static final int TradeAcceptValuePerGoal = ++e4;
  static final int TradeRejectValuePerGoal = ++e4;
  static final int TradeLostValuePerGoal = ++e4;
  static final int TradeFirstStrategicGoal = ++e4;
  static final int TradeLastStrategicGoal = ++e4;
  static final int TradeFirstStrategicValue = ++e4;
  static final int TradeLastStrategicValue = ++e4;
  static final int TradeStrategicValueLastPercentFirst = ++e4;
  static final int TradeRejectedStrategicGoal = ++e4;
  static final int TradeLostStrategicGoal = ++e4;
  static final int TradeRejectedStrategicValue = ++e4;
  static final int TradeLostStrategicValue = ++e4;
  static final int TradeMissedStrategicGoal = ++e4;
  static final int TradeDeadLostStrategicGoal = ++e4;
  static final int TradeDeadLostStrategicValue = ++e4;
  static final int TradeDeadRejectedStrategicGoal = ++e4;
  static final int TradeDeadStrategicGoal = ++e4;
  static final int TradeDeadRejectedStrategicValue = ++e4;
  static final int TradeDeadStrategicValue = ++e4;
  static final int TradeDeadMissedStrategicGoal = ++e4;
  static final int RCTWORTH = ++e4;
  static final int RCWORTH = ++e4;
  static final int RCTBAL = ++e4;
  static final int RCBAL = ++e4;
  static final int RCTGROWTHPERCENT = ++e4;
  static final int RCWORTHGROWTHPERCENT = ++e4;
  static final int RCGLT10PERCENT = ++e4;
  static final int RCWGLT10PERCENT = ++e4;
  static final int RCGLT100PERCENT = ++e4;
  static final int CRISISINCR = ++e4;
  static final int CRISIS2INCR = ++e4;
  static final int CRISIS3INCR = ++e4;
  static final int NOCRISISINCR = ++e4;
  static final int NOCRISIS2INCR = ++e4;
  static final int NOCRISIS3INCR = ++e4;
  static final int CRISISRESREDUCEPERCENT = ++e4;
  static final int CRISISSTAFFREDUCEPERCENT = ++e4;
  static final int CRISISRESREDUCESURPLUSPERCENT = ++e4;
  static final int CRISISSTAFFREDUCESURPLUSPERCENT = ++e4;
  static final int CRISISSTAFFGROWTHPERCENTINCR = ++e4;
  static final int CRISISSTAFFGROWTHYEARSINCR = ++e4;
  static final int CRISISRESGROWTHPERCENTINCR = ++e4;
  static final int CRISISRESGROWTHYEARSINCR = ++e4;
 
  static final int CRISISMANUALSPERCENTINCR = ++e4;
  // static final int TESTWORTH4 = ++e4;
  /// static final int TESTWORTH5 = ++e4;
//  static final int TESTWORTH6 = ++e4;
  // static final int TESTWORTH7 = ++e4;
  // static final int TESTWORTH8 = ++e4;
  static final int rendae4 = e4;

  void defRes() {

    doRes(SCORE, "Score", "Winner has the highest score the result of combining the different priorities set by several value entries which increase the score", 3, 4, 3, LIST7 | LIST8 | LIST9 | LIST43210YRS | THISYEAR  | SUM ,0, 0,0);
    doRes(LIVEWORTH, "Live Worth", "Live Worth Value including year end working, reserve: resource, staff, knowledge", 2, 2, 0, LIST0 | LIST7 | LIST8 | LIST9 | thisYr | SUM, ROWS1 | LIST0 | LIST7 | LIST8 | LIST9  | THISYEAR | THISYEARUNITS | thisYrAve | BOTH, ROWS3 | LIST43210YRS | LIST5 | CUMUNITS | BOTH | SKIPUNSET, 0L);
    doRes(STARTWORTH, "Starting Worth", "Starting Worth Value including working, reserve: resource, staff, knowledge", 2, 2, 0, LIST7 | LIST8 | LIST9  | thisYr | SUM, ROWS1 | LIST7 | LIST8 | LIST9  | THISYEAR |  thisYrAve | BOTH, 0L, 0L);
    doRes(WORTHIFRAC, "PercInitWorth ", "Percent of Initial Worth Value including working, reserve: resource, staff, knowledge", 2, 2, 0, LIST7 | LIST8 | THISYEARAVE | BOTH, ROWS2 |  LIST8 | LIST9 | CUMAVE | BOTH, 0, 0);
    doRes(WORTHINCR, "PercIncWorth", "Percent worth increase per year", 2, 2, 0, LIST0 | LIST7 | LIST8 | THISYEARAVE | BOTH, ROWS2 | LIST0YRS | LIST8 | LIST9 | CUMAVE | BOTH, 0, 0);
    doRes(YEARCREATE, "yearCreations", "new Econs ceated from game funds", 3, 2, 0, LIST8 | LISTYRS | THISYEARUNITS | SKIPUNSET | BOTH, ROWS2 | LIST8 | LIST0YRS | CUMUNITS | SKIPUNSET | BOTH, 0L, 0L);
    doRes(FUTURECREATE, "FutureFund Create", "Econs created from Future Funds");
    doRes(BOTHCREATE, "bothCreations", "new Econs ceated from  game funds and future funds");
    doRes(DIED, "DIED", "planets or ships died for any cause Worth increase", 2, 2, 3, LIST3 | CUM | SKIPUNSET, ROWS1 | LIST0 | LIST3 | THISYEARUNITS | BOTH | SKIPUNSET, ROWS2 | LIST0 | LIST2 | LIST3 | CUMUNITS | BOTH | SKIPUNSET, LISTYRS | CURUNITS | BOTH | SKIPUNSET);
    doRes("swapRIncr", "swapRIncr", "Uses of R Incr Swap percent of RC", 3, 2, 0, list8 | cumUnits | curUnits | both, 0, 0, 0);
    doRes("swapSIncr", "swapSIncr", "Uses of S Incr Swap percent of SG", 3, 2, 0, list8 | cumUnits | curUnits | both, 0, 0, 0);
    doRes("swapSDecr", "swapSDecr", "Uses of S Decr Swap percent of SG", 3, 2, 0, list8 | cumUnits | curUnits | both, 0, 0, 0);
    doRes("swapRDecr", "swapRDecr", "Uses of R Decr Swap percent of RC", 3, 2, 0, list8 | cumUnits | curUnits | both, 0, 0, 0);
    doRes("swapRRXchg", "swapRRXchg", "Uses of R Xchg Rcost Swap percent of RC", 3, 2, 0, list8 | cumUnits | curUnits | both, 0, 0, 0);
    doRes("swapRSXchg", "swapRSXchg", "Uses of R Xchg Scost Swap percent of RC", 3, 2, 0, list8 | cumUnits | curUnits | both, 0, 0, 0);
    doRes("swapSSXchg", "swapSSXchg", "Uses of S Xchg Scost Swap percent of RC", 3, 2, 0, list8 | cumUnits | curUnits | both, 0, 0, 0);
    doRes("swapSRXchg", "swapSRXchg", "Uses of S Xchg Rcost Swap percent of RC", 3, 2, 0, list8 | cumUnits | curUnits | both, 0, 0, 0);
    doRes(MISSINGNAME, "missing name", "tried an unknown name", 6, 0, list0 | cumUnits | curUnits | curAve | cumAve | both, 0, 0, 0);
    // doRes(DIED, "died", "planets or ships died for any cause",2,2,3,0L,ROWS1 | LIST0 | LIST3 | LISTYRS | THISYEARUNITS | BOTH | SKIPUNSET, ROWS2 |  LIST0 | LIST2 | LIST3  | CUMUNITS | BOTH | SKIPUNSET,0L);
    doRes(DIEDPERCENT, "DIED %", "Percent planets or ships died", 2, 2, 3, ROWS1 | LIST3 | LISTYRS | THISYEARAVE | BOTH | SKIPUNSET, ROWS2 | LIST3 | CUMAVE | BOTH | SKIPUNSET, 0L, 0L);
    doRes(DIEDCATASTROPHY, "DiedAfterCrisis", "Died after catastrophy percent worth would have increased", 2, 2, 3, ROWS1 | LIST3 | CUMUNITS | BOTH | SKIPUNSET, ROWS2 |  LIST3 | BOTH | THISYEARUNITS | SKIPUNSET, 0L, 0L);
    doRes(DIEDSN4, "DIEDSN4", "died s min(3) or 4 staff lt 0");
    doRes(DIEDRN4, "DIEDRN3", "died 4 resource lt 0");
    doRes(DIEDSN4RM3X5, " DIEDSN4RM3X5", "died 4 r lt 0, && max3 r gt 5 times max3 s");
    doRes(DIEDSN4RM3X4, "DIEDSN4RM3X4", "died 4 r lt 0, && max3 r gt 4 Times max3 s");
    doRes(DIEDSM3X5, "DIEDSM3X5", "died max3 s gt 5 times max3 r");
    doRes(DIEDSN4RN4, "DIEDSN4RN4", "died 4s lt 0 and 4r lt 0");
    doRes(DIEDRM3X4, "DIEDRM3X4", "died max3 of r gt 4 times max3 of s");
    doRes(DIEDSN3RN3, "diedSN3RN3", "died 3 s lt 0 and 3 r lt 0");
    doRes(DIEDSN3RN2, "DIEDSN3RN2", "died 3 s lt 0 and 2 r lt 0");
    doRes(DIEDSN3RM3X4, "DIEDSN3RM3X4", "died 3 s lt 0 and r max3 gt 4 times s max3");
    doRes(DIEDSN3RM3X3, "DIEDSN3RM3X3", "died 3 S lt 0 & r max3 gt 3 times s max3");
    doRes(DIEDSN3RN1, "DIEDSN3RN1", "died");
    doRes(DIEDSN3RM3X2, "DIEDSN3RM3X2", "died");
    doRes(DIEDSN3RM3X1, "DIEDSN3RM3X1", "died");
    doRes(DIEDSN3RM2X4, "DIEDSN3RM2X4", "died");
    doRes(DIEDSN3RM2X3, "DIEDSN3RM2X3", "died");
    doRes(DIEDSN3RM2X2, "DIEDSN3RM2X2", "died");
    doRes(DIEDSN3RM2X1, "DIEDSN3RM2X1", "died");
    doRes(DIEDSN3RM1X4, "DIEDSN3RM1X4", "died");
    doRes(DIEDSN3RM1X3, "DIEDSN3RM1X3", "died");
    doRes(DIEDSN3RM1X2, "DIEDSN3RM1X2", "died");
    doRes(DIEDSN3RM1X1, "DIEDSN3RM1X1", "died");
    doRes(DIEDSN2RN3, "DIEDSN2RN3", "died");
    doRes(DIEDSN2RN2, "DIEDSN2RN2", "died");
    doRes(DIEDSN2RM3X4, "DIEDSN2RM3X4", "died");
    doRes(DIEDSN2RM3X3, "DIEDSN2RM3X3", "died");
    doRes(DIEDSN2RN1, "DIEDSN2RN1", "died");
    doRes(DIEDSN2RM3X2, "DIEDSN2RM3X2", "died");
    doRes(DIEDSN2RM3X1, "DIEDSN2RM3X1", "died");
    doRes(DIEDSN2RM2X4, "DIEDSN2RM2X4", "died");
    doRes(DIEDSN2RM2X3, "DIEDSN2RM2X3", "died");
    doRes(DIEDSN2RM2X2, "DIEDSN2RM2X2", "died");
    doRes(DIEDSN2RM2X1, "DIEDSN2RM2X1", "died");
    doRes(DIEDSN2RM1X4, "DIEDSN2RM1X4", "died");
    doRes(DIEDSN2RM1X3, "DIEDSN2RM1X3", "died");
    doRes(DIEDSN2RM1X2, "DIEDSN2RM1X2", "died");
    doRes(DIEDSN2RM1X1, "DIEDSN2RM1X1", "died");
    doRes(DIEDSN1RN3, "DIEDSN1RN3", "died");
    doRes(DIEDSN1RN2, "DIEDSN1RN2", "died");
    doRes(DIEDSN1RM3X4, "DIEDSN1RM3X4", "died");
    doRes(DIEDSN1RM3X3, "DIEDSN1RM3X3", "died");
    doRes(DIEDSN1RN1, "DIEDSN1RN1", "died");
    doRes(DIEDSN1RM3X2, "DIEDSN1RM3X2", "died");
    doRes(DIEDSN1RM3X1, "DIEDSN1RM3X1", "died");
    doRes(DIEDSN1RM2X4, "DIEDSN1RM2X4", "died");
    doRes(DIEDSN1RM2X3, "DIEDSN1RM2X3", "died");
    doRes(DIEDSN1RM2X2, "DIEDSN1RM2X2", "died");
    doRes(DIEDSN1RM2X1, "DIEDSN1RM2X1", "died");
    doRes(DIEDSN1RM1X4, "DIEDSN1RM1X4", "died");
    doRes(DIEDSN1RM1X3, "DIEDSN1RM1X3", "died");
    doRes(DIEDSN1RM1X2, "DIEDSN1RM1X2", "died");
    doRes(DIEDSM3X4RN3, "DIEDSM3X4RN3", "died");
    doRes(DIEDSM3X3RN3, "DIEDSM3X3RN3", "died");
    doRes(DIEDSM3X2RN3, "DIEDSM3X2RN3", "died");
    doRes(DIEDSM3X1RN3, "DIEDSM3X1RN3", "died");
    doRes(DIEDSM2X4RN3, "DIEDSM2X4RN3", "died");
    doRes(DIEDSM2X3RN3, "DIEDSM2X3RN3", "died");
    doRes(DIEDSM2X2RN3, "DIEDSM2X2RN3", "died");
    doRes(DIEDSM2X1RN3, "DIEDSM2X1RN3", "died");
    doRes(DIEDSM1X4RN3, "DIEDSM1X4RN3", "died");
    doRes(DIEDSM1X3RN3, "DIEDSM1X3RN3", "died");
    doRes(DIEDSM1X2RN3, "DIEDSM1X2RN3", "died");
    doRes(DIEDSM1X1RN3, "DIEDSM1X1RN3", "died");
    doRes(DIEDSM3X4RN2, "DIEDSM3X4RN2", "died");
    doRes(DIEDSM3X3RN2, "DIEDSM3X3RN2", "died");
    doRes(DIEDSM3X2RN2, "DIEDSM3X2RN2", "died");
    doRes(DIEDSM3X1RN2, "DIEDSM3X1RN2", "died");
    doRes(DIEDSM2X4RN2, "DIEDSM2X4RN2", "died");
    doRes(DIEDSM2X3RN2, "DIEDSM2X3RN2", "died");
    doRes(DIEDSM2X2RN2, "DIEDSM2X2RN2", "died");
    doRes(DIEDSM2X1RN2, "DIEDSM2X1RN2", "died");
    doRes(DIEDSM1X4RN2, "DIEDSM1X4RN2", "died s max > 4 * r max, r min2 is neg try reduce difficulty or staff or resource costs.");
    doRes(DIEDSM1X3RN2, "DIEDSM1X3RN2", "died s max > 3 * r max, r min2 is neg try reduce difficulty or staff or resource costs.");
    doRes(DIEDSM1X2RN2, "DIEDSM1X2RN2", "died s max > 2 * r max, r min2 is neg try reduce difficulty or staff or resource costs.");
    doRes(DIEDSM1X1RN1, "DIEDSM1X1RN1", "died s max > 1 * r max, r min1 is neg try reduce difficulty or staff or resource costs.");
    doRes(DIEDSM3X4RN1, "DIEDSM3X4RN1", "died s max3 > 4 * r max, r min1 is neg try reduce difficulty or staff or resource costs.");
    doRes(DIEDSM3X3RN1, "DIEDSM3X3RN1", "died s max3 > 3 * r max, r min1 is neg try reduce difficulty or staff or resource costs.");
    doRes(DIEDSM3X2RN1, "DIEDSM3X2RN1", "died s max3 > 2 * r max, r min1 is neg try reduce difficulty or staff or resource costs.");
    doRes(DIEDSM3X1RN1, "DIEDSM3X1RN1", "died s max3 > 1 * r max, r min1 is neg try reduce difficulty or staff or resource costs.");
    doRes(DIEDSM2X4RN1, "DIEDSM2X4RN1", "died s max3 > 4 * r max, r min1 is neg try reduce difficulty or staff or resource costs.");
    doRes(DIEDSM2X3RN1, "DIEDSM2X3RN1", "died s max3 > 4 * r max, r min1 is neg try reduce difficulty or staff or resource costs.");
    doRes(DIEDSM2X2RN1, "DIEDSM2X2RN1","died s max3 > 4 * r max, r min1 is neg try reduce difficulty or staff or resource costs.");
    doRes(DIEDSM2X1RN1, "DIEDSM2X1RN1", "died s max3 > 4 * r max, r min1 is neg try reduce difficulty or staff or resource costs.");
    doRes(DIEDSM1X4RN1, "DIEDSM1X4RN1",  "died s max1 > 4 * r max, r min1 is neg try reduce difficulty or staff or resource costs.");
    doRes(DIEDSM1X3RN1, "DIEDSM1X3RN1", "died s max1 > 3 * r max, r min1 is neg try reduce difficulty or staff or resource costs.");
    doRes(DIEDSM1X2RN1, "DIEDSM1X2RN1",  "died s max1 > 2 * r max, r min1 is neg try reduce difficulty or staff or resource costs.");
    doRes(DIEDSN1RM1X1, "DIEDSN1RM1X1",  "died s max1 > 1 * r max, r min1 is neg try reduce difficulty or staff or resource costs.");
    doRes("DeadNegN", "DeadNegSwapN", "Dead Swaps never entered", 2, 2, 3, ROWS1 |  LIST3 | CUMUNITS | CUMAVE | BOTH | SKIPUNSET, ROWS3 | LIST3 | THISYEARUNITS | BOTH | SKIPUNSET, 0L, 0L);
    doRes("DeadLt5", "DeadLt5", "no more than 15 swaps");
    doRes("DeadLt10", "DeadLt10", "no more than 10 swaps");
    doRes("DeadLt20", "DeadLt20", "no more than 20 swaps");
    doRes("DeadNegProsp", "DeadNegProsp", "Died either R or S had a negative");
    doRes("DeadRatioS", "DeadRatioS", "Resource  S values simply too small");
    doRes("DeadRatioR", "DeadRatioR", "R values simply too small");
    doRes(DEADRATIO, "diedRatio", "died,average mult year last/initial worth death");
    doRes(DEADHEALTH, "died health", "died,average negative minimum health at death");
    doRes(DEADFERTILITY, "died fertility", "died,average negative minimum fertility at death");
    doRes(DEADSWAPSMOVED, "diedSwapMoves", "died,average Swap Moves at death");
    doRes(DEADSWAPSCOSTS, "diedSwapCosts", "died,average SwapCosts at death");
    doRes(DEADTRADED, "diedTraded", "died,even after trading");

    doRes(SGMTGC, "SGmtgCosts", "SG Maintenance,Travel,Growth Costs / RCSGBal", 1, 1, 1, LIST8 | ROWS2 | curAve | both | SKIPUNSET, 0, 0, 0);
    doRes(RCMTGC, "RCmtgCosts", "RC Maintenance,Travel,Growth Costs / RCSGBal");
    doRes(SGREQGC, "SGREQGCosts", "SG REQ G Costs / RCSGBal");
    doRes(RCREQGC, "RCREQGCosts", "RC REQ M Costs / RCSGBal");
    doRes(SGREQMC, "SGREQMCosts", "SG REQ M Costs / RCSGBal");
    doRes(RCREQMC, "RCREQMCosts", "RC REQ G Costs / RCSGBal");

    doRes(RRAWMC, "RMaintCosts", "R Maintenance Costs/ RCSGBal");
    doRes(CRAWMC, "CMaintCosts", "C Maintenance Costs/ RCSGbal");
    doRes(RCRAWMC, "RCMaintCosts", "RC Maintenance Costs/ RCSGBal");
    doRes(SRAWMC, "SMaintCosts", "S Maintenance Costs/ RCSGBal");
    doRes(GRAWMC, "GMaintCosts", "G Maintenance Costs/ RCSGBal");
    doRes(SGRAWMC, "SGMaintCosts", "SG Maintenance Costs/ RCSGBal");
    //   doRes(RCRAWMC, "RCRawMaintCosts", "RC Maintenance Costs/ RCSGBal");
    doRes(RCfrac, "RC/yr Worth", "RC / yr Worth");
    doRes(SGfrac, "SG/yr Worth", "SG / yr Worth");
    //chgd KNOWLEDGEB MANUALSfrac NEWKNOWLEDGEfrac COMMONKNOWLEDGEfrac KNOWLEDGEINCR NEWKNOWLEDGEINCR MANUALSINCR COMMONKNOWLEDGEINCR
    doRes(POORKNOWLEDGEEFFECT, "Dumb csts", "frac Increase in costs due to limited knowledge(ignorance)");
    doRes(POORHEALTHEFFECT, "Poor Health Cost", "Increase in costs due to insufficient required resources and staff");
    doRes(NEWKNOWLEDGEFRAC, "New KnowledgeFrac", "New knowledge / Knowledge ", 2, 3, 1, (LIST7 | LIST0YRS | curAve | both | SKIPUNSET), 0, 0, 0);
    doRes(KNOWLEDGEFRAC, "Knowledge Frac", "Knowledge worth / year worth");
    doRes(COMMONKNOWLEDGEFRAC, "Common Knowledge", "Common knowledge/knowledge");
    doRes(MANUALSFRAC, "Manualsfrac", "Manuals /knowledge, you get manuals in trades, faculty and researcher turn manuals into knowledge");

    doRes(KNOWLEDGEB, "BalanceKnowledge", "Knowledge balance");
    doRes(KNOWLEDGEINCR, "PercIncrKnowledge", "Percent Knowledge Increase per year");
    doRes(NEWKNOWLEDGEINCR, "incNewKnowledge", "Percent New Knowledge Incr/Year");
    doRes(COMMONKNOWLEDGEINCR, "incCommonKnowledge", "Percent Common Knowledge increase by year");
    doRes(MANUALSINCR, "PercIncrManuals", "Percent Manuals increase by years");
    doRes(INCRAVAILFRAC5, "IncrAvailFrac5", "Percent increase in avail frac after trade at favor 5", 2, 3, 2, THISYEARAVE | both | SKIPUNSET, ROWS1 | LIST41 | THISYEAR | BOTH | SKIPUNSET, ROWS2 | THISYEARUNITS | BOTH | SKIPUNSET, ROWS3 | CUMUNITS | BOTH | SKIPUNSET);
    doRes(INCRAVAILFRAC4, "IncrAvailFrac4", "Percent increase in avail frac after trade at favor 4", 2, 3, 2, DUP, 0, 0, 0);
    doRes(INCRAVAILFRAC3, "IncrAvailFrac3", "Percent increase in avail frac after trade at favor 3", 2, 3, 2, DUP, 0, 0, 0);
    doRes(INCRAVAILFRAC2, "IncrAvailFrac2", "Percent increase in avail frac after trade at favor 2", 2, 3, 2, DUP, 0, 0, 0);
    doRes(INCRAVAILFRAC1, "IncrAvailFrac1", "Percent increase in avail frac after trade at favor 1", 2, 3, 2, DUP, 0, 0, 0);
    doRes(INCRAVAILFRAC0, "IncrAvailFrac0", "Percent increase in avail frac after trade at favor 0", 2, 3, 2, DUP, 0, 0, 0);
    doRes(INCRAVAILFRAC, "IncrAvailFrac", "Percent increase in avail frac after trade  at any trade", 2, 3, 2, DUP, 0, 0, 0);
    doRes(INCRAVAILFRACa, "IncrAvailFracRej", "Percent increase in avail frac trade rejected", 2, 3, 2, DUP, 0, 0, 0);
    doRes(INCRAVAILFRACb, "IncrAvailFracb", "Percent increase in avail frac after trade rejected at trade failure", 2, 3, 2, DUP, 0, 0, 0);
    doRes(TRADEFIRSTRECEIVE, "First Received", "First amount received in a trade", 2, 3, 2, LIST41 | THISYEARAVE | BOTH | SKIPUNSET, ROWS2 | LIST20| CUMUNITS, 0L, 0L);
    doRes(TRADELASTRECEIVE, "Last Received", "Final amount received in a trade", 2, 3, 2, LIST41 | THISYEARAVE | BOTH | SKIPUNSET, ROWS1 | LIST20 | CURAVE | BOTH | SKIPUNSET, ROWS2 | LIST4YRS | CUMAVE | CUM | BOTH | SKIPUNSET, 0L);
    doRes(TRADERECEIVELASTPERCENTFIRST, "percent final/first requested amount", "Final percent of First  amount requested in a trade");
    doRes(TRADEFIRSTGAVE, "TradeFirstGiven", "First amount given in trade percent per sumrcsg", 2, 2, 2, 0, ROWS1 | LIST4 | THISYEARAVE | BOTH | SKIPUNSET, 0L, 0L);
    doRes(TRADELASTGAVE, "TradeNominalGiven", "Percent nominal amount given in trade per sumrcsg may be used for scoreing", 2, 2, 2, LIST40  | THISYEAR | BOTH | SKIPUNSET, ROWS1 | LIST4  | CUM| CUMAVE |  BOTH | SKIPUNSET, 0L, 0L);
     doRes(TRADESTRATLASTGAVE, "TradeStrategicLastGave", "Percent nominal amount given in trade per sumrcsg may be used for scoreing");
    doRes(TRADENOMINALGAVE, "TradeNominalGiven", "Nominal not strategic amount given in trade ", 2, 2, 2, LIST0 | CUM  | BOTH | SKIPUNSET, ROWS1 | LIST4 | CUMAVE | BOTH | SKIPUNSET, 0L, 0L);
    doRes(TRADESTRATFIRSTRECEIVE, "StrategicFirstReceived", "First strategic amount received in trade", 2, 3, 2, LIST41  | THISYEARAVE | BOTH | SKIPUNSET, ROWS1 | LIST20| BOTH | SKIPUNSET, ROWS2 | LIST4 | CUMAVE | BOTH | SKIPUNSET, 0L);
    doRes(TRADESTRATLASTRECEIVE, "StrategicLastReceived", "Final strategic amount eeceived in trade");
    doRes(TRADESTRATFIRSTGAVE, "TradeStrategicFirstGave", "First amount given in trade");
    doRes(TradeNominalReceivePercentNominalOffer, "NomReceive%NomOffer", "% of Nominal Received Per Nominal  Given", 1, 3, 2, LIST4YRS |  BOTH | SKIPUNSET,  ROWS2 | LIST4 | CUMAVE | BOTH | SKIPUNSET, 0L, 0L);
    doRes(MaxNominalReceivePercentNominalOffer, "MaxNomReceive%NomOffer", "Max % of Nominal Received Per Nominal  Given");
    doRes(MinNominalReceivePercentNominalOffer, "MinNomReceive%NomOffer", "Min % of Nominal Received Per Nominal  Given");
    doRes(TradeStrategicReceivePercentStrategicOffer, "StratReceive%StratGiven", "% of Strategic Received Per Strategic  Given");
    doRes(MaxStrategicReceivePercentStrategicOffer, "MaxStratReceive%StratGiven","Max % of Strategic Received Per Strategic  Given");
    doRes(MinStrategicReceivePercentStrategicOffer, "MinStratReceive%StratGiven" ,"Min % of Strategic Received Per Strategic  Given");
    doRes(YearTradeNominalReceivePercentNominalOffer, "YearNomReceive%NomOffer", "Year % of Nominal Received Per Nominal  Given");
    doRes(YearMaxNominalReceivePercentNominalOffer,"YearNomReceive%NomOffer", "Year Max % of Nominal Received Per Nominal  Given");
    doRes(YearMinNominalReceivePercentNominalOffer, "YearNomReceive%NomOffer","Year Min % of Nominal Received Per Nominal  Given");
    doRes(YearTradeStrategicReceivePercentStrategicOffer, "YearStratReceive%StratGiven", "Year % of Strategic Received Per Strategic  Given");
    doRes(YearMaxStrategicReceivePercentStrategicOffer, "YearMaxStratReceive%StratGiven","Year Max % of Strategic Received Per Strategic  Given");
    doRes(YearMinStrategicReceivePercentStrategicOffer, "YearMinStratReceive%StratGiven" ,"Year Min % of Strategic Received Per Strategic  Given"); 
    /*
   setStat(EM.TradeNominalReceivePercentNominalOffer,pors,clan,100*totalStrategicRequests/nominalOffers);
          setNaxEM.MaxNominalReceivePercentNominalOffer,pors,clan,100*totalStrategicRequests/nominalOffers);
          setMin(EM.MinNominalReceivePercentNominalOffer,pors,clan,100*totalStrategicRequests/nominalOffers);
          setStat(EM.TradeStrategicReceivePercentStrategicOffer,pors,clan,100*totalStrategicRequests/nominalOffers);
          setMax(EM.MaxStrategicReceivePercentStrategicOffer,pors,clan,100*totalStrategicRequests/nominalOffers);
          setMin(EM.MinStrategicReceivePercentStrategicOffer,pors,clan,100*totalStrategicRequests/nominalOffers);
  */
    doRes(BEFORETRADEWORTH, "BeforeTradeWorth", "Worth before A trade", 2, 3, 2, DUP, 0, 0, 0);
    doRes(AFTERTRADEWORTH, "AfterTradeWorth", "Worth after a trade", 2, 3, 2, DUP, 0, 0, 0);
    doRes(TRADEWORTHINCRPERCENT, "TradeWorthIncrPercent", "Percent increase in Worth after trade", 2, 3, 2, SKIPDUP | LIST4 | THISYEARAVE, 0, 0, LIST41 | ROWS3 | CUMUNITS);
    doRes(TradeAcceptValuePerGoal, "AcceptValuePercentGoal", "Accepted value percent of goal", 2, 3, 2, LIST41 | THISYEARAVE | BOTH | SKIPUNSET, ROWS1 | LIST4YRS | CURAVE | BOTH | SKIPUNSET, ROWS2 | LIST41 | CUMAVE | BOTH | SKIPUNSET, 0L);
    doRes(TradeRejectValuePerGoal, "RejectValuePercentGoal", "Rejected percent value per goal");
    doRes(TradeLostValuePerGoal, "LostValuePercentGoal", "Lost percent value per goal");
    doRes(TradeFirstStrategicGoal, "FirstStrategicGoal", "First Strategic Goal", 2, 3, 2, LIST41 | THISYEARAVE | BOTH | SKIPUNSET, ROWS1 | LIST4YRS | CURAVE | BOTH | SKIPUNSET, ROWS2 | LIST4YRS | CUMAVE | BOTH | SKIPUNSET, 0L);
    doRes(TradeLastStrategicGoal, "LastStrategicGoal", "Strategic Goal after trade");
    doRes(TradeFirstStrategicValue, "FirstStrategicValue", "First Strategic Value");
    doRes(TradeLastStrategicValue, "LastStrategicValue", "Last strategic Value strategic receive/strategic gave at trade");
    doRes(TradeStrategicValueLastPercentFirst, "LastPercentFirstStrategicValue", "LastStrategic Value percent of First Strategic Value just before trade");
    doRes(TradeRejectedStrategicGoal, "RejectedStrategicGoal", "Trade rejected Strategic Goal");
    doRes(TradeLostStrategicGoal, "LostStrategicGoal", "Strategic Goal after trade lost");
    doRes(TradeRejectedStrategicValue, "RejectedtStrategicValue", "Strategic Value after Trade rejected", 2, 3, 2, DUP, 0, 0, 0);
    doRes(TradeLostStrategicValue, "LostStrategicValue", "Strategic Value after trade lost");
    doRes(TradeMissedStrategicGoal, "MissedStrategicGoal", "Trade Missed no value");
    doRes(TradeDeadMissedStrategicGoal, "DeadMissedStrategicGoal", "Dead No Strategic Goal no trade", 2, 3, 2, THISYEARAVE | LIST94321 | both | SKIPUNSET, ROWS1 | LIST4 | LIST9 | CURAVE | BOTH | SKIPUNSET, ROWS2 | LIST9 | LIST4 | THISYEARUNITS | BOTH | SKIPUNSET, ROWS3 | LIST9 | LIST4 | CUMUNITS | BOTH | SKIPUNSET);
    doRes(TradeDeadLostStrategicGoal, "DeadLostStrategicGoal", "Strategic Goal after trade lost and dead");
    doRes(TradeDeadLostStrategicValue, "DeadLostStrategicValue", "Strategic Value after trade lost and dead");
    doRes(TradeDeadRejectedStrategicGoal, "DeadRejectedStrategicGoal", "Strategic Goal after trade rejected and dead");
    doRes(TradeDeadStrategicGoal, "DeadStrategicGoal", "Strategic Goal after trade then died");
    doRes(TradeDeadRejectedStrategicValue, "DeadRejectedStrategicValue", "Strategic Value after trade rejected then died");
    doRes(TradeDeadStrategicValue, "DeadStrategicValue", "Strategic Value after trade then died");
    doRes(TRADESOS1, "SOS1tradePercentIncWorth", "Successful trade percent incr worth after starting with SOS1", 2, 3, 2, LIST41 | THISYEARAVE | BOTH | SKIPUNSET, ROWS1 | LIST4YRS | CURAVE | BOTH | SKIPUNSET, 0L, 0L);
    doRes(TRADESOS2, "SOS2tradePercentIncWorth", "Successful trade percent incr worth after starting with SOS2");
    doRes(TRADESOS3, "SOS3tradePercentIncWorth", "Successful trade percent incr worth after starting with SOS3");
    doRes(TRADEOSOS1, "HlptdS1AccPercentIncWorth", "Helped Successful trade percent incr worth after starting with SOS1", 2, 3, 2, LIST41 | THISYEARAVE | BOTH | SKIPUNSET, ROWS1 | LISTYRS | CUR | BOTH | SKIPUNSET, ROWS2 | LIST4 | CUR | CUM | BOTH | SKIPUNSET, 0L);
    doRes(TRADEOSOS2, "HlptdS2AccPercentIncWorth", "Helped Successful trade percent incr worth after starting with SOS2");
    doRes(TRADEOSOS3, "HlptdS3AccPercentIncWorth", "Helped Successful trade percent incr worth after starting with SOS3");
    doRes(TRADESOSR1, "TrdSOS1PercentIncWorth", " trade percent incr worth with SOS1");
    doRes(TRADESOSR2, "TrdSOS2PercentIncWorth", " trade percent incr worth with SOS2");
    doRes(TRADESOSR3, "TrdSOS3PercentIncWorth", " trade percent incr worth with SOS3");
    doRes(TRADEOSOSR1, "LstS1PercentIncWorth", "Other Percent worth increase when lost for a trade with SOS1");
    doRes(TRADEOSOSR2, "LstS2PercentIncWorth", "Other Percent worth increase when lost for a trade with SOS2");
    doRes(TRADEOSOSR3, "LstS3PercentIncWorth", "Other Percent worth increase when lost for a trade with SOS3");

    doRes(DTRADEACC, "DTrade AccPercentIncWorth", "Percent worth inc/decrease when Dead after trade accepted", 2, 2, 2, LIST3 | CUMUNITS | BOTH | SKIPUNSET, ROWS1 | LIST3 | CUMAVE | BOTH | SKIPUNSET, ROWS2 | LIST3 | THISYEARUNITS | BOTH | SKIPUNSET, 0L);
    doRes(DTRADEOSOSR1, "DHLPS1PercentIncWorth", "Percent worth inc/decrease when Dead after help with SOS1");

    doRes(DLOSTSOSR1, "DLOSTSOS1PercentIncWorth", "Dead after lost trade value with SOS5, Percent Value Incr");
    doRes(DTRADEOSOSR2, "DHLPS2PercentIncWorth", "Percent worth inc/decrease when Dead after help with SOS2");
    doRes(DTRADEOSOSR3, "DHLPS3PercentIncWorth", "Percent worth inc/decrease when Dead after help with SOS3");
    doRes(DLOSTOSOSR1, "DLostoS1PercentIncWorth", "Other Dead after no trade with SOS1, Percent Value Incr", 2, 2, 2, LIST5 | LIST4 | LIST2 | LIST1 | CUMUNITS | BOTH | SKIPUNSET, ROWS1 | LIST3 | CUMAVE | BOTH | SKIPUNSET, ROWS2 | LIST3 | THISYEARUNITS | BOTH | SKIPUNSET, 0L);
    doRes(DLOSTOSOSR2, "DLostoS2PercentIncWorth", "Other Dead after no trade with SOS2, Percent Value Incr");
    doRes(DLOSTOSOSR3, "DLostoS3PercentIncWorth", "Other Dead after no trade with SOS3, Percent Value Incr");

    doRes(DTRADESOSR1, "DTradeSOS1RPercentIncWorth", "Percent worth inc/decrease when Dead after Reject with SOS1");

    // repeatlists at "W..." at a later point rn 
    doRes("WTRADEDINCRF5", "Fav5TrdPercentIncWorth", "Percent Years worth increase at Favor5/start year worth", 2, 3, 2, both | SKIPUNSET, ROWS1 | LIST41 | CURAVE | BOTH | SKIPUNSET, ROWS2 | THISYEARUNITS | BOTH | SKIPUNSET, ROWS3 | THISYEARAVE | BOTH | SKIPUNSET);
    doRes("WTRADEDINCRF4", "Fav4TrdPercentIncWorth", "Percent Years worth increase at Favor4/start year worth", 2, 3, 2, DUP, 0, 0, 0);
    doRes("WTRADEDINCRF3", "Fav3TrdPercentIncWorth", "Percent Years worth increase at Favor3/start year worth", 2, 3, 2, DUP, 0, 0, 0);
    doRes("WTRADEDINCRF2", "Fav2TrdPercentIncWorth", "Percent Years worth increase at Favor2/start year worth", 2, 3, 2, DUP, 0, 0, 0);
    doRes("WTRADEDINCRF1", "Fav1TrdPercentIncWorth", "Years worth increase at Favor1/start year worth", 2, 3, 2, DUP, 0, 0, 0);
    doRes("WTRADEDINCRF0", "Fav0TrdPercentIncWorth", "Percent Years worth increase at Favor0/start year worth", 2, 3, 2, DUP, 0, 0, 0);
    doRes(WTRADEDINCRMULT, "TrdPercentIncWorth", "Percent Years worth increase at total trades this year/start year worth part of scoring ", 2, 3, 2, LIST41 | CURAVE | BOTH | SKIPUNSET, 0, 0, 0);
    doRes(WTRADEDINCRSOS, "incrWSOSTrade", "Percent Years worth increase at an planet SOS flag trade this year/start year worth", 2, 3, 2, LIST41 | CURAVE | BOTH | SKIPUNSET, 0, 0, 0);
    doRes("WREJTRADEDPINCR", "incrWRejectedTrade", "Percent Worth incr if the other had notrejected the trade/start yr worth", 2, 3, 2, DUP, 0, 0, 0);
    doRes("WLOSTTRADEDINCR", "incrWLostTrade", "Percent Worth incr if other rejected the trade/start yr worth", 2, 3, 2, DUP, 0, 0, 0);
    doRes("UNTRADEDWINCR", "DEAD no trade Incr", "Percent no trade offered yearly growth including working, reserve: resource, staff, knowledge", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRAC", "CritReceiptsFrac", "Fraction of Critical receipts Worth/start totWorth ", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRACSYFAV5", "CritReceipts%YF5", "Percent of critical receipts worth favor5 Trades/start year worth", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRACSYFAV4", "CritReceipts%YF4", "Percent of critical receipts worth favor4 Trades/start year worth", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRACSYFAV3", "CritReceipts%YF3", "Percent of critical receipts worth favor3 Trades/start year worth", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRACSYFAV2", "CritReceipts%YF2", "Percent of critical receipts worth favor2 Trades/start year worth", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRACSYFAV1", "CritReceipts%YF1", "Percent of critical receipts worth favor1 Trades/start year worth", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRACSYFAV0", "CritReceipts%YF0", "Percent of critical receipts worth favor0 Trades/start year worth", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRACSYFAVA", "CritReceipts%YF0", "Percent of critical receipts worth favor0-5 Trades/start year worth", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRADROPT5", "CritTradeFracDropF5", "Percent of traded critical receipts worth favor5 Trades/start barter C receipts  ", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRADROPT4", "CritTradeFracDropF4", "Percent of traded critical receipts worth favor4 Trades/start barter C receipts  ", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRADROPT3", "CritTradeFracDropF3", "Percent of traded critical receipts worth favor3 Trades/start barter C receipts  ", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRADROPT2", "CritTradeFracDropF2", "Percent of traded critical receipts worth favor2 Trades/start barter C receipts  ", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRADROPT1", "CritTradeFracDropF1", "Percent of traded critical receipts worth favor1 Trades/start barter C receipts  ", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRADROPT0", "CritTradeFracDropF0", "Percent of traded critical receipts worth favor0 Trades/start barter C receipts  ", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRACWORTHF3", "CritTradeFracF3", "Percent of critical receipts worth favor3 Trades/start totWorth  ", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRACWORTHF2", "CritTradeFracF2", "Percent of critical receipts worth favor2 Trades/start totWorth  ", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRACWORTHF1", "CritTradeFracF1", "Percent of critical receipts worth favor1 Trades/start totWorth  ", 2, 3, 2, DUP, 0, 0, 0);
    doRes("WTRADEDINCR", "WTradedIncr", "Percent Years worth increase/start year worth", 2, 2, 1, LIST41 | CURAVE | BOTH | SKIPUNSET, 0, 0, 0);
    doRes("WORTHAYRNOTRADEINCR", "IncWorth aYr NoTrade", "Percent Year increase Worrth/worth if no trades this year", 2, 3, 2, DUP, 0, 0, 0);
    doRes("WORTH2YRNOTRADEINCR", "IncWorth 2Yr No Trade", "Percent Year increase Worrth/worth if  2 years of no trades", 2, 3, 2, DUP, 0, 0, 0);
    doRes("WORTH3YRNOTRADEINCR", "IncWorth 3Yrs No Trade", "Percent Year increase Worth/worth if 3 or more years of no trades", 2, 3, 2, DUP, 0, 0, 0);
    doRes("WORTHAYRTRADEINCR", "IncWorth aYr Trade", "Percent Year increase Worrth/worth if a year of trades", 2, 3, 2, DUP, 0, 0, 0);
    doRes("WORTH2YRTRADEINCR", "IncWorth 2Yr Trade", "Percent Year increase Worrth/worth if  2 succesive year of trades", 2, 3, 2, DUP, 0, 0, 0);
    doRes("WORTH3YRTRADEINCR", "IncWorth 3Yr Trade", "Percent Year increase Worrth/worth if 3 years of trades", 2, 3, 2, DUP, 0, 0, 0);
    //  doRes("WORTH1YRNOTRADEINCR", "IncWorth 1Yr No Trade", "Percent Year increase Worrth/worth if at least 1 succesive year of trades",2,2, 1, LIST2 | THISYEAR | THISYEARAVE| BOTH | SKIPUNSET , LISTYRS | SKIPUNSET | BOTH | CUR  | CURAVE, ROWS3 | LIST4 | THISYEARUNITS , 0);
    doRes(SWAPRINCRCOST, "Swap RIncr Cost", "Fraction of R INCR swap cost/sum of R units", 2, 2, 1, list8 | curAve | curUnits | both | skipUnset, 0, 0, 0);
    doRes(SWAPSINCRCOST, "Swap SIncr Cost", "Fraction of SR INCR swap cost/sum of S units", 2, 2, 1);
    doRes(SWAPRDECRCOST, "Swap RDECR Cost", "Fraction of R DECR swap cost/sum of R units", 2, 2, 1);
    doRes(SWAPSDECRCOST, "Swap SDecr Cost", "Fraction of S Decr swap cost/sum of S units", 2, 2, 1);
    doRes(SWAPRXFERCOST, "Swap RXfer Cost", "Fraction of R XFER swap cost/sum of R units", 2, 2, 1);
    doRes(SWAPSXFERCOST, "Swap SXfer Cost", "Fraction of S XFER swap cost/sum of R units", 2, 2, 1);
    doRes("EmergFF", "EmergFF", "emergency resource/staff sums tranfer resource to FutureFund", 2, 2, 1,  LIST2 | LIST6 | THISYEAR | BOTH | SKIPUNSET, ROWS1 | LISTYRS | SKIPUNSET  | BOTH | CUR  , ROWS2|  LIST6 | CUM |  CUMUNITS | BOTH | SKIPUNSET,0);
    doRes("SizeFF", "SizeFF", "Size resource/staff sums tranfer resource to FutureFund");
    doRes("FutureFundSaved", "FutureFundsSaved", "Total resource/staff sums tranfered to FutureFund", 2, 2, 1,  LIST2 | THISYEAR | BOTH | SKIPUNSET,ROWS1  | SKIPUNSET  | BOTH   , ROWS2|  LIST6 | CUM | BOTH | SKIPUNSET, 0);
    doRes("FutureFund", "Redo FutureFund", "At emergency1 level of resource/staff back out of a saved future fund");
    doRes("EmergFF1", "Emerg FutureFund1", "At emergency1 level of resource/staff neg prospects val to FutureFund");
    doRes("REmergFF1", "R Emerg FutureFund1", "At emergency1 level of resource/staff sums tranfser val to FutureFund");;
    doRes("SEmergFF1", "S Emerg FutureFund1", "At emergency1 level of staff/resource sums transfer val to FutureFund");
    doRes("REmergFF2", "R Emerg FutureFund2", "At emergency2 level of resource/staff sums transfer val to FutureFund");
    doRes("SEmergFF2", "S Emerg FutureFund2", "At emergency2 level of staff/resource sums transfer val to FutureFund");
    doRes("RcEmergFF1", "Rc Emerg FutureFund1", "At emergency1 level of resource/staff sums transfer val to FutureFund");
    doRes("SgEmergFF1", "Sg Emerg FutureFund1", "At emergency1 level of staff/resource sums transfer val to FutureFund");
    doRes("RcEmergFF2", "Rc Emerg FutureFund2", "At emergency2 level of resource/staff sums tranfser val to FutureFund");
    doRes("SgEmergFF2", "Sg Emerg FutureFund2", "At emergency2 level of staff/resource sums tranfer val to FutureFund");
    doRes("SizeFFr", "R SizeFutureFund", "At size level of resource/staff sums tranfer val  to FutureFund");
    doRes("SizeFFs", "S SizeFutureFund", "At size level of resource/staff sums tranfer val  to FutureFund");
     doRes("SizeFFEr", "RE SizeFutureFund", "At emergency prospects level of resource/staff sums tranfer val to FutureFund");
    doRes("SizeFFEs", "SE SizeFutureFund", "At size level of resource/staff sums tranfer val  to FutureFund");
    doRes("RSwapFF", "R SwapEmergFF", "At emergency level of resource/staff sums during swaps tranfer val to FutureFund");
    doRes("SSwapFF", "S SwapEmergFF", "At emergency level of staff/resource sums during swaps tranfer val to FutureFund");
    doRes(CRISISINCR, "IncWorthCrisis", "Percent Year increase Worrth/worth a year with a catastrophy", 2, 2, 1,  LIST2 |  THISYEARUNITS | BOTH | SKIPUNSET,LIST9 | LISTYRS | SKIPUNSET | BOTH | THISYEAR | THISYEARUNITS , ROWS3 | LIST9 |  CUM | CUMUNITS | BOTH | SKIPUNSET, 0);
    doRes(CRISIS2INCR, "IncWorthCrisis", "Percent Year increase Worrth/worth a second year after a  catastrophy");
    doRes(CRISIS3INCR, "IncWorthCrisis", "Percent Year increase Worrth/worth a third year after a  catastrophy");
    doRes(NOCRISISINCR, "IncWorth 1Yr No Crisis", "Percent Year increase Worrth/Year initial worth if at least 1  year of no catastrophy");
    doRes(NOCRISIS2INCR, "IncWorth 2Yr No Crisis", "Percent Year increase Worrth/Year initial worth if 2 years of No catastrophy");
    doRes(NOCRISIS3INCR, "IncWorth 3Yr No Crisis", "Percent Year increase Worrth/Year initial worth if 3 years of No catastrophy");
    doRes(CRISISRESREDUCEPERCENT, "PercentReduceResourceCrisis", "Crisis reduce resource by percent of balance");
    doRes(CRISISSTAFFREDUCEPERCENT, "PercentReduceStaffCrisis", "Crisis reduce staff by percent of balance");
    doRes(CRISISRESREDUCESURPLUSPERCENT, "PercentSurplusReduceResourceCrisis", "Crisis surplus reduce resource by percent of balance");
    doRes(CRISISSTAFFREDUCESURPLUSPERCENT, "PercentReduceStaffCrisis", "Crisis surplus reduce staff by percent of balance");
    doRes(CRISISSTAFFGROWTHPERCENTINCR, "Crisis%IncreaseStGrow", "Crisis percent increase by staff growth for a few years");
    doRes(CRISISSTAFFGROWTHYEARSINCR, "CrisisIncrStGrowYrs", "Crisis set Years of increased staff growth");
    doRes(CRISISRESGROWTHPERCENTINCR, "Crisis%IncreaseResGrow", "Crisis percent increase by resource growth for a few years");
    doRes(CRISISRESGROWTHYEARSINCR, "CrisisIncrResGrowYrs", "Crisis set Years of increased resource growth");
    doRes(CRISISMANUALSPERCENTINCR, "Crisis%IncrManals", "Crisis percent increase by manuals growth");
    doRes("rCatCosts", "r Catast Cst ", "resource catastrophy costs ave per catastrophy");
    doRes("sCatCosts", "s Catast Cst ", "staff catastrophy costs ave per catastrophy");
    doRes("rCatBonusY", "r Catast B Yr ", "resource catastrophy years added ave per catastrophy");
    doRes("sCatBonusY", "s Catast B Yr ", "staff catastrophy years added ave per catastrophy");
    doRes("rCatBonusVal", "r Catast B Val", "resource catastrophy bonus unit value added ave per catastrophy");
    doRes("sCatBonusVal", "s Catast B Yr ", "staff catastrophy bonus unit value added ave per catastrophy");
    doRes("rCatNegDecay", "r Catast ReduceDecay", "resource catastrophy bonus neg unit value ave per catastrophy");
    doRes("sCatNegDecay", "s Catast ReduceDecay", "staff catastrophy bonus neg unit value  ave per catastrophy");
    doRes("sCatBonusManuals", "s Catast Bonus Manuals", "catastrophy bonus manuals add value ave per catastrophy");
    doRes("sCatBonusNewKnowledge", "s Catast Bonus New Knowledge", "catastrophy bonus newKnowledge add value ave per catastrophy");
    doRes("DEADWTRADEDINCR", "DEAD trade Incr", "DEAD Percent Years worth increase/start year worth", 2, 2, 3, LIST3 | LIST2 | LIST5 | CUMUNITS | BOTH | SKIPUNSET, ROWS3 | LIST3 | SKIPUNSET | THISYEARAVE, ROWS2 | LIST3 | SKIPUNSET | CUMAVE | SKIPUNSET | BOTH, 0);
    doRes("DEADWTRADEDINCRF5", "DEAD fav5 trade Incr", "DEAD Percent Years worth increase at Favor5/start year worth");
    doRes("DEADWTRADEDINCRF4", "DEAD fav4 trade Incr", "Percent Years worth increase at Favor4/start year worth");
    doRes("DEADWTRADEDINCRF3", "DEAD fav3 trade Incr", "Percent Years worth increase at Favor3/start year worth");
    doRes("DEADWTRADEDINCRF2", "DEAD fav2 trade Incr", "Percent Years worth increase at Favor2/start year worth");
    doRes("DEADWTRADEDINCRF1", "DEAD fav1 trade Incr", "Years worth increase at Favor1/start year worth");
    doRes("DEADWTRADEDINCRF0", "DEAD fav0 trade Incr", "Percent Years worth increase at Favor0/start year worth");
    doRes("DEADWTRADEDINCRMULT", "DEAD TradeWIncr", "Percent Years worth increase at trades this year/start year worth");
    doRes("DEADWTRADEDINCRSOS", "DEAD SOS trade incr Worth", "Percent Years worth increase at an planet SOS flag trade this year/start year worth");
    doRes("DEADWREJTRADEDINCR", "DEAD Trade Rejected %Incr", "Percent Worth incr of a rejected trade trade/start yr worth when dead", 2, 2, 3, LIST2 | LIST4 | LIST5 | CUMUNITS | BOTH | SKIPUNSET, ROWS2 | LIST3 | SKIPUNSET | CUMAVE, ROWS3 | LIST4 | LIST5 | SKIPUNSET | THISYEARAVE | SKIPUNSET | BOTH, 0);
    doRes("DEADWLOSTTRADEDINCR", "DEAD Trade Incr Lost", "Percent Worth incr if other rejected the trade/start yr worth when dead");
    doRes("DEADUNTRADEDWINCR", "DEAD incrWorthNoTrade", "no trade offered percent yearly growth when dead including working, reserve: resource, staff, knowledge");
    doRes("WTRADEDF5", "fav5 received/init worth", "Percent received at favor 5 trade/initial worth", 2, 2, 2, (LIST1 | LIST5 | CUMUNITS | BOTH | SKIPUNSET), ROWS2 | LIST5 | SKIPUNSET | THISYEARUNITS | THISYEARAVE | SKIPUNSET | BOTH, 0, 0);
    doRes("WTRADEDF4", "fav4 received/init worth", "Percent received at favor 4 trade/initial worth");
    doRes("WTRADEDF3", "fav3 received/init worth", "Percent received at favor 3 trade/initial worth");
    doRes("WTRADEDF2", "fav2 received/init worth", "Percent received at favor 2 trade/initial worth");
    doRes("WTRADEDF1", "fav1 received/init worth", "Percent received at favor 1 trade/initial worth");
    doRes("WTRADED0", "fav0 received/init worth", "Percent received worthAtFavr0/initial worth");
    doRes("WTRADEDOS", "SOS received/Init Worth", "Percent received worthAtSOS/initial worth");
    doRes("WTRADERCDPERCENT", "received/init worth", "Percent my clan received/initial worth");
    doRes("WTRADERECEIVED", "received worth", "received canonical worth");
    doRes("WOTRADEGAVFRAC", "other gave/initial worth", "other received canonical worth/initial");
    doRes("WOTRADEGAVE", "other gave worth", "Percent worthAtSOS/initial worth");
    doRes("TRADEDRCDF5", "W rcd fav5", "Percent Worth received when trade at fav5/initial worth");
    doRes("TRADEDRCDF4", " W rcd fav4", "Percent Worth received when trade at fav4/initial worth");
    doRes("TRADEDRCDF3", "W rcd fav3", "Percent Worth received when trade at fav3/initial worth");
    doRes("TRADEDRCDF2", "W rcd fav2", "Percent Worth received when trade at fav2/initial worth");
    doRes("TRADEDRCDF1", "W rcd fav1", "Percent Worth received when trade at fav1/initial worth");
    doRes("TRADEDRCDF0", "W rcd fav0", "Percent Worth received when trade at fav 0/initial worth");
    doRes("TRADEDRCD", "W rcd", "Percent Worth received when trade/initial worth");
    doRes("TRADES%", "TRADES %", "Percent of trades per economy");
    doRes("WREJTRADE", "wRejectedTrade", "Percent lost worth  when econ rejected the trade", 2, 2, 2, (LIST1 | LIST4 | CUMUNITS | BOTH | SKIPUNSET), ROWS2 | LIST4 | SKIPUNSET | THISYEARUNITS | THISYEARAVE | SKIPUNSET | BOTH, 0, 0);
    doRes("WLOSTTRADE", "wLostTrade", "Percent lost worth if other clan lost the trade");
    doRes("MISSEDTRADE", "wMissedTrade", "Percent missed/no trade ");

    doRes("DEADTRADES%", "Dead Trades %", "Percent of trades per dead economy", 2, 2, 2, (list1 | THISYEARAVE | BOTH | SKIPUNSET), (list1 | CURAVE | CUMAVE | BOTH | SKIPUNSET), LIST0 | SKIPUNSET | CUMAVE | BOTH, 0);
    doRes(RCTBAL, "RCBal/TBal", "Percent RC balance/tbal", 1, 1, 0, (LIST7 | skipUnset | curAve), 0, 0, 0);
    doRes(RCBAL, "RCBal", "RC balance", 1, 1, 0, (LIST7 | curAve), 0, 0, 0);
    doRes("SGTBAL", "SG Balance", "Percent SG balance/worth", 1, 1, 0, (LIST7 | curAve), 0, 0, 0);
    doRes("RBAL", "RBal", "Percent R balance/ worth", 1, 1, 0, (LIST7 | skipUnset | curAve), 0, 0, 0);
    doRes("SBAL", "S Balance", "Percent S balance/worth", 1, 1, 0, (LIST7 | skipUnset | curAve), 0, 0, 0);
    doRes("CBAL", "CBal", "Percent C balance/worth", 1, 1, 0, (LIST7 | skipUnset | curAve), 0, 0, 0);
    doRes("GBAL", "G Balance", "G balance/worth", 1, 1, 0, (LIST7 | skipUnset | curAve), 0, 0, 0);
    doRes(RCTWORTH, "RCWorth", "RC Worth/TWorth", 1, 1, 0, (LIST7 | skipUnset | curAve), 0, 0, 0);
    doRes(RCWORTH, "RCWorth", "RC Worth", 1, 1, 0, (LIST7 | curAve), 0, 0, 0);
    doRes("SGTWORTH", "SGWorth", "SG Worth", 1, 1, 0, (LIST7 | skipUnset | curAve), 0, 0, 0);
    doRes("RWORTH", "RWorth", "R Worth", 1, 1, 0, (LIST7 | skipUnset | curAve), 0, 0, 0);
    doRes("SWORTH", "AWorth", "S Worth", 1, 1, 0, (LIST7 | skipUnset | curAve), 0, 0, 0);
    doRes("RCTBINC", "RCBalInc", "RC Balance Increase", 1, 1, 0, (LIST7 | skipUnset | curAve), 0, 0, 0);
    doRes("SGBTINC", "SGBalInc", "SG Balance Increase", 1, 1, 0, (LIST7 | skipUnset | curAve), 0, 0, 0);
    doRes("RCTWINC", "RCWInc", "RC Worth Inc", 1, 1, 0, (LIST7 | skipUnset | curAve), 0, 0, 0);
    doRes("SGTWINC", "SGWInc", "SG Worth Inc", 1, 1, 0, (LIST7 | skipUnset | curAve), 0, 0, 0);
    doRes("RCTREQGROWTHCOSTS3", "RCReqGCosts ", "PercentRC Required Growth Cost/RC Bal", 1, 1, 0, (LIST7 | skipUnset | curAve), 0, 0, 0);
    doRes("SGTREQGROWTHCOSTS3", "SGReqGCosts ", "PercentSG Required Growth Cost/SG Bal", 1, 1, 0, (LIST7 | skipUnset | curAve), 0, 0, 0);
    doRes("RCTGROWTHCOSTS3", "RCGCosts ", "Percent RC Growth Cost/RC Bal", 1, 1, 0, (LIST7 | skipUnset | curAve), 0, 0, 0);
    doRes(RCTGROWTHPERCENT, "RCGrowth% ", "Percent RC Growth /year start RC Bal", 1, 1, 0, (LIST8 | skipUnset | curAve), 0, 0, 0);
    doRes(RCWORTHGROWTHPERCENT, "RCWorthGrowth% ", "Percent RC Worth Growth /year start RC Worth", 5, 1, 0, (LIST8 | skipUnset | curAve), 0, 0, 0);
    doRes("RCGLT5PERCENT", "RCGrowthLT5%", "Percent RC growth LT 5 %", 5, 1, 0, LIST8 | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes(RCGLT10PERCENT, "RCGrowthLT10%", "Percent RC growth LT 10 %", 5, 1, 0, LIST8 | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes("RCGLT25PERCENT", "RCGrowthLT25%", "Percent RC growth LT 25 %", 5, 1, 0, LIST8 | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes("RCGLT50PERCENT", "RCGrowthLT50%", "Percent RC growth LT 50 %", 5, 1, 0, LIST8 | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes(RCGLT100PERCENT, "RCGrowthLT100%", "Percent RC growth LT 100 %", 5, 1, 0, LIST8 | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes("RCGGT100PERCENT", "RCGrowthGE100%", "Percent RC growth GE 100 %", 5, 1, 0, LIST8 | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes("RCWGLT5PERCENT", "RCWorthGrowthLT5%", "Percent RC Worth growth LT 5 %", 5, 1, 0, LIST8 | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes(RCWGLT10PERCENT, "RCGrowthLT10%", "Percent RC Worth growth GT 5 LT 10 %", 5, 1, 0, LIST8 | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes("RCWGLT25PERCENT", "RCWorthGrowthLT25%", "Percent Worth RC growth GT 10 LT 25 %", 5, 1, 0, LIST8 | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes("RCWGLT50PERCENT", "RCWorthGrowthLT50%", "Percent RC Worth growth GT 25 LT 50 %", 5, 1, 0, LIST8 | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes("RCWGLT100PERCENT", "RCWorthGrowthLT100%", "Percent RC Worth growth GT 50 LT 100 %", 5, 1, 0, LIST8 | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes("RCWGGT100PERCENT", "RCWorthGrowthGE100%", "Percent RC Worth growth GE 100 %", 5, 1, 0, LIST8 | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes("RCWGPERCENT", "RCWorthGrowth%", "Percent RC Worth growth", 5, 1, 0, LIST8 | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes("SGTGROWTHCOSTS3", "SGGCosts ", "SG Growth Cost/SG Bal", 1, 1, 0, (LIST7 | skipUnset | curAve), 0, 0, 0);
    doRes("RCTREQMAINTC3", "RcRQMCosts ", "rc req maintCsts / bal", 1, 1, 0, (LIST7 | skipUnset | curAve), 0, 0, 0);
    doRes("SGTREQMAINTC3", "SgRQMCosts ", "sg req maintCsts / bal", 1, 1, 0, (LIST7 | skipUnset | curAve), 0, 0, 0);
    doRes("RCTMAINTC3", "RCMCosts ", "rc maintCsts / bal", 2, 2, 2, (LIST11 | skipUnset | curAve), 0, 0, 0);
    doRes("SGTMAINTC3", "SGMCosts ", "sg maintCsts / bal", 1, 1, 0, (LIST7 | skipUnset | curAve), 0, 0, 0);
    doRes("RCTTRAVC3", "RCTCosts ", "rc travelCsts / bal", 1, 1, 0, (LIST7 | skipUnset | curAve), 0, 0, 0);
    doRes("SGTTRAVC3", "SGTCosts ", "sg travelCsts / bal", 1, 1, 0, (LIST7 | skipUnset | curAve), 0, 0, 0);
    doRes("RCTRAWGROWTHCOSTS3", "rcRawGrowthCsts ", "% r req growthCsts / bal", 1, 1, 0, (LIST7 | skipUnset | curAve), 0, 0, 0);
    doRes("SGTRAWGROWTHCOSTS3", "sgRawGrowthCsts ", "% s req growthCsts / bal", 1, 1, 0, (LIST7 | skipUnset | curAve), 0, 0, 0);
    doRes("RCTMTG3", "rc mtgCsts ", "r mtgCsts / bal", 1, 1, 0, (LIST7 | skipUnset | curAve), 0, 0, 0);
    doRes("SGTMTG3", "sg mtgCsts ", "s mtgCsts / bal", 1, 1, 0, (LIST7 | skipUnset | curAve), 0, 0, 0);
    doRes("INCRGSWAPCOSTSB3", "SwapBalCosts", "balance cost involved in repurposing material by Incr to help sectors with inadequate resources", 2, 6, 2, (LIST11 | curUnits | skipUnset), 0, 0, 0);
    doRes("DECRGSWAPCOSTS", "DecrSwapBalCosts", "balance cost involved in repurposing material by Decr to help sectors with inadequate resources", 2, 6, 2, (LIST11 | curUnits | skipUnset), 0, 0, 0);
    doRes("RXFERGSWAPCOSTS", "RXferSwapCosts", "balance cost involved in repurposing material by Xfer to help sectors with inadequate resources", 2, 6, 2, (LIST11 | curUnits | skipUnset), 0, 0, 0);
    doRes("SXFERGSWAPCOSTSB", "SXferSwapCosts", "balance cost involved in repurposing material by Xfer to help sectors with inadequate resources", 2, 6, 2, (LIST11 | curUnits | skipUnset), 0, 0, 0);
    doRes("GROWTHSWAP", "growthSwapBalCosts", "% growth loop costs involved in repurposing material to help sectors with inadequate resources", 2, 6, 2, (LIST11 | curUnits | skipUnset), 0, 0, 0);
    doRes("HEALTHSWAP", "HealthSwapBalCosts", "% health loop costs involved in repurposing material to help sectors with inadequate resources", 2, 6, 2, (LIST11 | curUnits | skipUnset), 0, 0, 0);
// doRes(rWORTH,"Worth", "IGNORE Value including year end working, reserve: resource, staff, knowledge", 6,2, (cur | curUnits | curAve | sum | both), (LIST14 | curUnitAve | cur | curUnits | both | sum), 0, 0);
    rende4 = e4;
    if (E.debugStats) {
      description = "***starting***";
      for (int rrr = 0; rrr < rende4; rrr++) {
        if (resI[rrr] == null) {
          throw new MyErr("missing resI rN=" + rrr + " prevDescription=" + description);
        }
        if (resV[rrr] == null) {
          throw new MyErr("missing resV rN=" + rrr + " prevDescription=" + description);
        }
        if (resS[rrr] == null) {
          throw new MyErr("missing resS rN=" + rrr + " prevDescription=" + description);
        }
        description = resS[rrr][0];
      }
    }
  }

  static int[] yrs1 = {0, 1};
  static int[] yrs2 = {0, 1, 8, 15, 22, 29, 36}; //position of age starts

  /**
   * define method calls creating output lines in the results array when some
   * bits in the locks correspond with bits in the offered key from StarTrader
   * locks are the same as that of the previous doRes
   *
   * @param dName name of setStats recording a result
   * @param desc description of result
   * @param detail detailed description available by clicking description
   * @param depth number of successive years displayed 1-6
   * @param ydepth number of successive years in results separated by years
   * @param fracs number of fraction digits
   *
   * @return rN the index of a vector of result integer index into
   * resI,resV,resS
   */
  private int doRes(String dName, String desc, String detail, int depth, int ydepth, int fracs) {
    return doRes(dName, desc, detail, depth, ydepth, fracs, DUP, 0L, 0L, 0L);
  }

  /**
   * define method calls creating output lines in the results array when some
   * bits in the locks correspond with bits in the offered key from StarTrader
   * duplicate that of the previous doRes.
   *
   *
   * @param dName name of setStats recording a result
   * @param desc description of result
   * @param detail detailed description available by clicking description
   *  set depth=1, ydepth=1
   * @param fracs number of fraction digits
   * @param lock0 0'th lock to match the offered keys
   * @param lock1 1'th lock to match offered keys may be empty
   * @param lock2 2nd lock to match offered keys, may be empty
   * @param lock3 3rd lock to match offered keys may be empty
   *
   * @return rN the index of a vector of result integer index into
   * resI,resV,resS
   */
 private  int doRes(String dName, String desc, String detail, int fracs, long lock0, long lock1, long lock2, long lock3) {
    return doRes(dName, desc, detail, 1, 1, fracs, lock0, lock1, lock2, lock3);
  }

  /**
   * define method calls creating output lines in the results array when some
   * bits in the locks correspond with bits in the offered key from StarTrader
   *
   * @param dName name of setStats recording a result
   * @param desc description of result
   * @param detail detailed description available by clicking description
   * @param depth number of successive years displayed 1-6
   * ydepth=1
   * @param fracs number of fraction digits
   * @param lock0 0'th lock to match the offered keys
   * @param lock1 1'th lock to match offered keys may be empty
   * @param lock2 2nd lock to match offered keys, may be empty
   * @param lock3 3rd lock to match offered keys may be empty
   *
   * @return rN the index of a vector of result integer index into
   * resI,resV,resS
   */
  private int doRes(String dName, String desc, String detail, int depth, int fracs, long lock0, long lock1, long lock2, long lock3) {
    return doRes(dName, desc, detail, depth, 1, fracs, lock0, lock1, lock2, lock3);
  }

  /**
   * define method calls creating output lines in the results array when some
   * bits in the locks correspond with bits in the offered key from StarTrader
   *
   * @param dName name of setStats recording a result
   * @param desc description of result
   * @param detail detailed description available by clicking description
   * @param depth number of successive years displayed 1-6
   * @param ydepth number of successive years in results separated by years
   * @param fracs number of fraction digits
   * @param lock0 0'th lock to match the offered keys
   * @param lock1 1'th lock to match offered keys may be empty
   * @param lock2 2nd lock to match offered keys, may be empty
   * @param lock3 3rd lock to match offered keys may be empty
   *
   * @return rN the index of a vector of result integer index into
   * resI,resV,resS
   */
  private int doRes(String dName, String desc, String detail, int depth, int ydepth, int fracs, long lock0, long lock1, long lock2, long lock3) {
    e4++;
    System.out.printf("in doRes a, rn%2d, dName=%5s, desc=%5s, detail=%5s,locks=%o,%o,%o,%o %n", e4, dName, desc, detail, lock0, lock1, lock2, lock3);
    if (resMap.containsKey(dName)) {
      doMyErr(">>>>>>dName=" + dName + " already exists, e4=" + e4);
    }
    int rN = e4;
    resMap.put(dName, rN);
    return doRes(rN, desc, detail, depth, ydepth, fracs, lock0, lock1, lock2, lock3);
  }

  /**
   * define method calls creating output lines in the results array when some
   * bits in the locks correspond with bits in the offered key from StarTrader
   *
   * @param rN number of the result
   * @param desc description
   * @param detail detailed description available by clicking description
   * @param fracs number of fraction digits
   * depth=1, ydepth=1
   * @param lock0 0'th lock to match the offered keys
   * @param lock1 1'th lock to match offered keys may be empty
   * @param lock2 2nd lock to match offered keys, may be empty
   * @param lock3 3rd lock to match offered keys may be empty
   *
   * @return rN the index of a vector of result integer index into
   * resI,resV,resS recall doRes with ydepth=1, depth = 1
   */
  private int doRes(int rN, String desc, String detail, int fracs, long lock0, long lock1, long lock2, long lock3) {
    return doRes(rN, desc, detail, 1, 1, fracs, lock0, lock1, lock2, lock3);
  }

  /**
   * define method calls creating output lines in the results array when some
   * bits in the locks correspond with bits in the offered key from StarTrader
   *
   * @param rN number of the result
   * @param desc description
   * @param detail detailed description available by clicking description
   * @param depth depth of reports for all years
   *  set ydepth depth for 5 year groups, LIST10,LIST11,LIST12,LIST13,LIST14
   * @param fracs number of fraction digits
   * @param lock0 0'th lock to match the offered keys
   * @param lock1 1'th lock to match offered keys may be empty
   * @param lock2 2nd lock to match offered keys, may be empty
   * @param lock3 3rd lock to match offered keys may be empty
   *
   * @return rN the index of a vextor of result lock which match call keys
   * recall doRes with ydepth=1;
   */
  int doRes(int rN, String desc, String detail, int depth, int fracs, long lock0, long lock1, long lock2, long lock3) {
    return doRes(rN, desc, detail, depth, 1, fracs, lock0, lock1, lock2, lock3);
  }

  int rDepth = 1, rYdepth = 1, rFracs = 0;
  long rLock0 = 0L, rLock1 = 0L, rLock2 = 0L, rLock3 = 0L;

  /**
   * define method calls creating output lines in the results array when some
   * bits in the locks correspond with bits in the offered key from StarTrader
   * the last 4 parameters are filled in with the values from the doRes
   * immediately before this doRes, This allows a quick definition using the
   * previous values which were the same If the immediate previous was
   * shortened, it was filled from the previous doRes Eventually a previous has
   * all the parameters which are used by the following doRes
   *
   * @param rN number of the result
   * @param desc description
   * @param detail detailed description available by clicking description
   * @param depth depth of reports for all years
   * @param ydepth depth for 5 year groups, LIST10,LIST11,LIST12,LIST13,LIST14
   * @param fracs number of fraction digits
   *
   *
   * @return rN the index of a vector of result locks which match call keys
   * recall doRes with ydepth=1;
   */
  int doRes(int rN, String desc, String detail, int depth, int ydepth, int fracs) {
    return doRes(rN, desc, detail, depth, ydepth, fracs, rLock0, rLock1, rLock2, rLock3);
  }

  /**
   * define method calls creating output lines in the results array when some
   * bits in the locks correspond with bits in the offered key from StarTrader
   *
   * @param rN number of the result
   * @param desc description
   * @param detail detailed description available by clicking description
   * @param depth depth of reports for all years
   * @param ydepth depth for 5 year groups, LIST10,LIST11,LIST12,LIST13,LIST14
   * @param fracs number of fraction digits
   * @param lock0 0'th lock to match the offered keys
   * @param lock1 1'th lock to match offered keys may be empty
   * @param lock2 2nd lock to match offered keys, may be empty
   * @param lock3 3rd lock to match offered keys may be empty
   *
   * @return rN the index of a vector of result locks which match call keys
   * recall doRes with ydepth=1;
   */
  private int doRes(int rN, String desc, String detail, int depth, int ydepth, int fracs, long lock0, long lock1, long lock2, long lock3) {
    rDepth = depth;
    rYdepth = ydepth;
    rFracs = fracs;
    rLock0 = lock0;
    rLock1 = lock1;
    rLock2 = lock2;
    rLock3 = lock3;
    long mLocks = (lock0 | lock1 | lock2 | lock3) & (LIST10 | LIST11 | LIST12 | LIST13 | LIST14);
    int bvector2l = mLocks == 0 ? DVECTOR2A : DVECTOR2L; // short if no age years option
    int[] yrs3 = mLocks == 0 ? yrs1 : yrs2;
    if (E.debugStats) {
      if (!(resV[rN] == null) && resV[rN].length > 1) {
        throw new MyErr("null doRes rN=" + rN + " curDescription=" + (resS[rN] != null && resS[rN][0] != null ? resS[rN][0] : " null cur descrip ") + " prevDescription=" + description);
      }
    }
    resV[rN] = new double[bvector2l][][]; // create the values structure
    resI[rN] = new long[bvector2l][][];
    /**
     * resI
     * [resNum][ICUM,ICUR0,...ICUR6(7*6rounds+2][PCNTS,SCNTS,CCONTROLD][LCLANS
     * :{over CCONTROLD}ISSET,IVALID,IPOWER:{only for
     * ICUM,CCONTROLD}LOCKS0,LOCKS1,L0CKS2,LOCKS3,IFRACS,IDEPTH] valid number of
     * valid entries 0=unset,1=cur0,2=cur1,7=cur6
     */
    int curIx = 10, ccntl = -5, ageIx = -6, mDepth = -7;
    int newIx = 56, cur0 = 25;
    // for (int jj = ICUR0 - 1; jj < bvector2l; jj++) { // cum,cur0-6
    // set only the first for a year, doStartYear will move entries up each yr
    for (int jj : yrs3) { // ICUM,ICUR0 if yrs2 5 more ages
      newIx = (jj - ICUR0 + 1) % 7;
      curIx = (jj - ICUR0) % 7; //index in relation to CUR0
      cur0 = jj - curIx;  // start of an age group
      ageIx = (jj - ICUR0) / 7; // index into age groups 0f years all <4,<8,<16,<32,32+

      resV[rN][jj] = new double[DVECTOR3L][]; // p,s
      resI[rN][jj] = new long[IVECTOR3L][];// p,s,ctl
      for (int kk = 0; kk < 2; kk++) {
        resV[rN][jj][kk] = new double[E.lclans];
        resI[rN][jj][kk] = new long[E.lclans]; // counts array
        for (int ii = 0; ii < E.lclans; ii++) {
          resV[rN][jj][kk][ii] = 0.0;
          resI[rN][jj][kk][ii] = 0;
        }
      }
      ccntl = ((jj == 0) ? 11 : 3);
      resI[rN][jj][CCONTROLD] = new long[ccntl];
      resI[rN][jj][CCONTROLD][ISSET] = 0;
      resI[rN][jj][CCONTROLD][IVALID] = 0;
      resI[rN][jj][CCONTROLD][IPOWER] = 0;
      if (jj == 0) {
        resI[rN][jj][CCONTROLD][LOCKS0] = lock0;
        resI[rN][jj][CCONTROLD][LOCKS1] = lock1;
        resI[rN][jj][CCONTROLD][LOCKS2] = lock2;
        resI[rN][jj][CCONTROLD][LOCKS3] = lock3;
        resI[rN][jj][CCONTROLD][IFRACS] = fracs;
        resI[rN][jj][CCONTROLD][IDEPTH] = Math.max(minDepth, Math.min(maxDepth, depth));
        resI[rN][jj][CCONTROLD][IYDEPTH] = Math.max(minYDepth, Math.min(maxYDepth, ydepth));
      }
    }
    description = desc;
    resS[rN] = new String[2];
    resS[rN][0] = desc;
    resS[rN][1] = detail;
    return rN;
  }

  /**
   * define method calls creating output lines in the results array when some
   * bits in the locks correspond with bits in the offered key from StarTrader
   *
   * @param rN number of the result
   * @param desc description
   * @param detail detailed description available by clicking description the
   * last 7 parameters are filled in with the values from the doRes immediately
   * before this doRes, This allows a quick definition using the previous values
   * which were the same If the immediate previous was shortened, it was filled
   * from the previous doRes Eventually a previous has all the parameters which
   * are used by the following doRes
   *
   * @return rN the index of a vector of result locks which match call keys
   * recall doRes with ydepth=1;
   */
 private  int doRes(int rN, String desc, String detail) {
    return doRes(rN, desc, detail, rDepth, rYdepth, rFracs, rLock0, rLock1, rLock2, rLock3);
  }

  /**
   * define method calls creating output lines in the results array when some
   * bits in the locks correspond with bits in the offered key from StarTrader
   *
   * @param rN number of the result
   * @param desc description
   * @param detail detailed description available by clicking description the
   * last 7 parameters are filled in with the values from the doRes immediately
   * before this doRes, This allows a quick definition using the previous values
   * which were the same If the immediate previous was shortened, it was filled
   * from the previous doRes Eventually a previous has all the parameters which
   * are used by the following doRes
   *
   * @return rN the index of a vector of result locks which match call keys
   * recall doRes with ydepth=1;
   */
  private int doRes(String rs, String desc, String detail) {
    return doRes(rs, desc, detail, rDepth, rYdepth, rFracs, rLock0, rLock1, rLock2, rLock3);
  }

  /**
   * move the cur values up one year from cur5->cur6 limit the moves by max of
   * depth and ydepth, limit also by valid, valid increments in doEndYear called
   * at the end of doYear
   *
   * @return number of undefined entries
   */
  int doStartYear() {
    // loop through all of the entries
    int cnt = 0, curIx = -7, newIx = -7, ccntl = -8, new0 = -12;
    int valid0 = -4, cur0 = -7, yearsGrp = 20, newValid = 10;
    int mdepth = 22, row0 = -1;
    long depth = 20, ydepth = 10;
    yearErrCnt = 0;
    for (int rN = 0; rN < rende4; rN++) {
      // skip undefined entries without error
      if (resI[rN] != null) {
        cnt++;  // count valid rN entries
        // move each age up 1 5->6,4->5...0->1 skip 6->0,
        // we are moving the Ivector2, Dvector2
        // jj iterates 6,5,4,3,2,1,6,5
        //create new array for each ICUR0,DCUR0
        /**
         * resI [resNum][ICUM,ICUR0,...ICUR6(7*6rounds
         * +2][PCNTS,SCNTS,CCONTROLD][LCLANS :{over
         * CCONTROLD}ISSET,IVALID,IPOWER:{only for
         * ICUM,CCONTROLD}LOCKS0,LOCKS1,L0CKS2,LOCKS3,IFRACS,IDEPTH] valid
         * number of valid entries 0=unset,1=cur0,2=cur1,7=cur6
         */
        int bvector2lim = resV[rN].length - 1; // length of vector 45, maxIx=44

        // start moving things up if they are the same depth
        valid = 0;
        //YEARS 0-99999   0,   4,   8,    16,    32,   999999}; // + over 31+ group
        //  CUM CUR0  CUR1 CUR2  CUR3  CUR4  CUR5
        //spots  0   1     8    15    22    29    36       43(44)<45>   
        //   LIST012 LIST10 LIST11 LIST12 LIST13 LIST14
        // LIST8-20        
        //              44                0
        for (int jj = bvector2lim; jj >= ICUM; jj--) {
          new0 = jj + 1; // 45
          newIx = new0 % 7; // if newIx==0, this is row0 of an agegrp =45%7 = 3
          yearsGrp = (jj - ICUR0) / 7; // (44-1)= 43/7=1 =6,5...0 should be depth
          curIx = (jj - ICUR0) % 7; //index in relation to 0 of yr grp 43%7 =1
          cur0 = jj - curIx;  //43 - 1 = 42
          if ((cur0 - ICUR0) % 7 != 0) { // (42 -1) = 41%7 = 
            bErr("rN=%2d,resV[Rn]=%5s, resI[rn].length=%3d, curIx=%1d, cur0=%1d, cur0 not index of 0, cur0=%2d, ICUR0=%2d, curIx=%2d,jj=%3d\n", rN, resS[rN][0], resI[rN].length, curIx, cur0, cur0, ICUR0, curIx, jj);
          }
          // choose depth = YDEPTH if yearsGrp !=0 , IDEPTH if == 0 using ICUM
          depth = resI[rN][ICUM][CCONTROLD][(yearsGrp == 0 ? IDEPTH : IYDEPTH)];
          if (resI[rN][jj] != null) { // don't try to mov null up one
            // each yearAge has its own isset and valid and power

            // copy only if the higher cur is valid for this age
            // depth must be 2 for a copy
            if ((curIx + 1) <= (depth)) {
              // copy if source is not null and isset
              // #7 in agegrp is always null
              if (resI[rN][jj] != null
                      && resI[rN][jj][CCONTROLD][ISSET] > 0) {
                // isset and within depth, keep larger val or set depth nexIx
                valid = Math.max(valid, (jj + 1) % 7); // highest destination value
                //copy reference up, including the current values
                // overwrite any previous reference 
                // ensure that each reference is in one ICURO+JJ
                resI[rN][jj + 1] = resI[rN][jj];  //moving up by 1 to depth
                resV[rN][jj + 1] = resV[rN][jj];
                if (E.debugStatsOut && ((rN == -96 || rN <= -3) && jj < 10 && resI[rN] != null && resI[rN][ICUR0] != null)) {
                  System.out.printf("In doStartYear move at %s rN%d, jj%d, idepth%d, ydepth%d, valid%d, isseta%d, issetb%d\n", resS[rN][0], rN, jj, resI[rN][ICUM][CCONTROLD][IDEPTH], resI[rN][ICUM][CCONTROLD][IYDEPTH], resI[rN][ICUR0][CCONTROLD][IVALID], resI[rN][ICUR0][CCONTROLD][ISSET], resI[rN][ICUR0 + 1] == null ? -2 : resI[rN][ICUR0 + 1][CCONTROLD][ISSET]);

                }// end print if
              } else {  // put null there at the next one
                resI[rN][jj + 1] = null;
                resV[rN][jj + 1] = null;
              }
            }
            if (curIx == 0) {// is this the 0 element of a ageGrp
              // create a new 0'th element, overwrite old reference
              // jj still at 0 element of age
              resV[rN][jj] = new double[2][]; // p,s
              resI[rN][jj] = new long[3][];
              for (int kk = 0; kk < 2; kk++) {
                resV[rN][jj][kk] = new double[E.lclans];// make the clans
                resI[rN][jj][kk] = new long[E.lclans]; // make array for i
                for (int ii = 0; ii < E.lclans; ii++) {
                  // zero the elements of the clan
                  resV[rN][jj][kk][ii] = 0.0;
                  resI[rN][jj][kk][ii] = 0;
                }
              }
              // int ri1[][][] = resI[rN];
              //  int ri2[][] = resI[rN][jj];
              //  int ri3[] = resI[rN][jj][CCONTROLD];
              //    resI[rN][ICUR0 + jj][CCONTROLD][IVALID] = valid;
              //    ccntl = jj == 0 ? IVECTOR4C : IVECTOR4A;
              resI[rN][jj][CCONTROLD] = new long[3];
              resI[rN][jj][CCONTROLD][ISSET] = 0;
              resI[rN][jj][CCONTROLD][IVALID] = valid;
              valid = 0; // reset for the next row
              resI[rN][jj][CCONTROLD][IPOWER] = 0;
              jj--; // skip copy up to cur0
            }
          }

        } // end loop on jj

        int rn = rN;
        boolean doYears = bvector2lim > ICUR0 + 6;
        if (E.debugStatsOut && ((rN == 96 || rN <= 1) && resI[rN] != null && resI[rN][ICUR0] != null)) {
          System.out.printf("In doStartYear at %s rN%d, idepth%d, ydepth%d, valid%d, isseta%d, issetb%d\n", resS[rN][0], rN, resI[rN][ICUM][CCONTROLD][IDEPTH], resI[rN][ICUM][CCONTROLD][IYDEPTH], resI[rN][ICUR0][CCONTROLD][IVALID], resI[rN][ICUR0][CCONTROLD][ISSET], resI[rN][ICUR0 + 1] == null ? -2 : resI[rN][ICUR0 + 1][CCONTROLD][ISSET]);
          if (doYears) {
            // System.out.print(+ ", " + resI[rN][lockC][0][rValid2] + ", " + resI[rN][lockC][0][rValid3] + ", rSets=" + resI[rN][lockC][0][rSet] + ", " + resI[rN][lockC][0][rSet1] + ", " + resI[rN][lockC][0][rSet2] + ", " + resI[rN][lockC][0][rSet3] + ", rcnts=" + resI[rN][lockC][0][rcnt] + ", " + resI[rN][lockC][0][rcnt1] + ", " + resI[rN][lockC][0][rcnt2] + ", " + resI[rN][lockC][0][rcnt3]);
          }
        }// end print if
      }// end if not null
      //      if (((resI[rN][ICUM][CCONTROLD][IDEPTH] > 1) && resI[rN][ICUM][CCONTROLD][IVALID] > resI[rN][ICUM][CCONTROLD][IDEPTH]) || resI[rN][rcur0 + resI[rN][lockC][0][rValid] - 1] == null) {
      //     E.myTest(true, "doStartYear rcur" + (resI[rN][lockC][0][rValid] - 1) + " is null" + " rValid=" + resI[rN][lockC][0][rValid] + (resI[rN][rcur0] == null ? " !!!" : " " + "rcur0") + (resI[rN][rcur0 + 1] == null ? " !!!" : " " + "rcur1") + (resI[rN][rcur0 + 2] == null ? " !!!" : " " + "rcur2") + (resI[rN][rcur0 + 3] == null ? " !!!" : " " + "rcur3") + (resI[rN][rcur0 + 4] == null ? " !!!" : " " + "rcur4") + (resI[rN][rcur0 + 5] == null ? " !!!" : " " + "rcur5"));
      //        }
    }// end rN loop

    return rende4 - cnt;
  }// end doStartYear

  static int didEndYear = 0;

  /**
   * do end of year processing, determine if values need to be divided by power
   * of 10 and then shown in the display of the result process the score
   * processor getWinner() before the rest of doEndYear
   *
   * @return
   */
  int doEndYear() {
    doResSpecial();
    getWinner();
    int cnt = 0, curIx = -7, newIx = -7, ccntl = -8;
    int valid0 = -4, cur0 = -7, yearsGrp = 20, valid = 10;
    int mdepth = 22;
    long depth = 20, ydepth = 10;
    yearErrCnt = 0;
    for (int rN = 0; rN < rende4; rN++) {
      // skip undefined entries without error
      if (resI[rN] != null) {
        cnt++;

        /**
         * resI [resNum][ICUM,ICUR0,...ICUR6(7*6rounds
         * +2][PCNTS,SCNTS,CCONTROLD][LCLANS :{over
         * CCONTROLD}ISSET,IVALID,IPOWER:{only for
         * ICUM,CCONTROLD}LOCKS0,LOCKS1,L0CKS2,LOCKS3,IFRACS,IDEPTH] valid
         * number of valid entries 0=unset,1=cur0,2=cur1,7=cur6
         */
        int bvector2lim = resI[rN].length; // length of jj vector I think
        int[] spots = {0, 1};  // ICUM,ICUR0 short spots no LISTYRS
        // 8 for LIST10 
        int[] spotsl = {0, 1, 8, 15, 22, 29, 36};// long cur0 index
        spots = bvector2lim > 8 ? spotsl : spots; // pick the right spots
        boolean doYears = bvector2lim > 8;
        boolean didPower = false;
        int maxCumP = -10;

        //YEARS 0-99999   0,   4,   8,    16,    32,   999999}; // + over 31+ group
        //  CUM CUR0  CUR1 CUR2  CUR3  CUR4  CUR5
        //spots  0   1     8    15    22    29    36       43(44)    
        //   LIST012 LIST10 LIST11 LIST12 LIST13 LIST14
        // LIST8-20
        for (int jj : spots) {
          newIx = (jj + 1) % 7;
          yearsGrp = jj / CURMAXDEPTH;
          depth = resI[rN][ICUM][CCONTROLD][(yearsGrp == 0 ? IDEPTH : IYDEPTH)];
          curIx = (jj) % CURMAXDEPTH; //index in relation to CUR0
          cur0 = jj - curIx;

          // find the max value in cum and cur
          // all entries 
          if (!didPower) {
            double maxCum = -99999999.;
            maxCumP = -10;
            int maxCumKK = 0, m;
            // find the largest abs value in all values in resV
            for (int kk = 0; kk < 2; kk++) {
              for (int ll = 0; ll < E.lclans; ll++) {
                maxCum = Math.max(maxCum, Math.abs(resV[rN][jj][kk][ll]));
              }
            }

            m = maxCumP = -12;
            if (maxCum > E.PZERO) {
              for (m = MAX_PRINT.length - 1, maxCumP = -10; m > -1 && maxCumP < -1; m--) {
                if (MAX_PRINT[m] < maxCum) {
                  maxCumP = m;   // -1,0 for no reduction in  value
                }
              }
              if (maxCumP < 0) {  // -10 bigger than the largest print divisor
                //     System.out.print("in doEndYear:" + resS[rN][0] + ", maxCumP=" + maxCumP + " choose largest = ");
                maxCumP = 0;
              }

            } else {
              maxCumP = 0;
            }
            if (maxCumP > 0) {
              didPower = true;
              if (E.debugStatsOut) {
                System.out.printf("doEndYear %s, m=%d,MAX_PRINT[%1d]=%e, maxCum=%e\n", resS[rN][0], m, maxCumP, MAX_PRINT[maxCumP], maxCum);
              }
            }
          }
          //     if(E.debugStatsOut)System.out.println( "Max_PRINT[" + maxCumP + "]=" + MAX_PRINT[maxCumP] + " maxCum=" + maxCum );
          resI[rN][jj][CCONTROLD][IPOWER] = maxCumP;

          // now compute the valid rows for stats for this age
          long isset1 = resI[rN][jj][CCONTROLD][ISSET];
          int maxjjj = 6;
          valid = 0;
          long maxd = Math.min(depth, maxjjj);
          for (int jjj = 1; jjj <= maxd && jj > 0; jjj++) {
            valid = resI[rN][jj + jjj - 1] != null && (isset1 = resI[rN][jj + jjj - 1][CCONTROLD][ISSET]) > 0 ? jjj : valid;
            if (maxd > 1 && rN < 0) {
              if (E.debugStatsOut) {
                System.out.printf("EM.doEndYear %s rN%d, valid%d,isseta%d,issetb%d, issetc%d depth%d, maxd%d, jjj%d,jj%d\n", resS[rN][0], rN, valid, isset1, (jj + jjj < resI[rN].length ? resI[rN][jj + jjj] != null ? resI[rN][jj + jjj][CCONTROLD][ISSET] : -1 : -2), (jj + CURMAXDEPTH + jjj < resI[rN].length ? resI[rN][jj + CURMAXDEPTH + jjj] != null ? resI[rN][jj + CURMAXDEPTH + jjj][CCONTROLD][ISSET] : -1 : -2), depth, maxd, jjj, jj);
              }
            }

          }// end of jjj
          //  int[][][] resii = resI[rN];
          // int[][] resi2 = resii[ICUR0+jj];
          // int[] resi3 = resi2[2];
          // int resValid = resi3[IVALID];
          resI[rN][jj][CCONTROLD][IVALID] = valid;

          if (didEndYear < 3) {
            // if(E.debugStatsOut)System.out.printf("in doEndYear econ rN=%3d, desc=%5s, MAX_PRINT[0]=%6f,%8.2e,%e,%e, (MAX_PRINT[m=%d]= < maxCum=%5f   < MAX_PRINT[m+1=%3d]=\n", rN, resS[rN][0], 729845219.331, 729845219.331, 8729845219.331, 57729845219.331, m, maxCum, m + 1);
          }
        }// end jj

        int rn = rN;
        if ((rN == 96 || rN < 1) && resI[rN] != null && resI[rN][ICUR0] != null) {
          if (E.debugStatsOut) {
            System.out.printf("In doEndYear at %s rN%d, idepth%d, ydepth%d, valid%d, isseta%d, issetb%d\n", resS[rN][0], rN, resI[rN][ICUM][CCONTROLD][IDEPTH], resI[rN][ICUM][CCONTROLD][IYDEPTH], resI[rN][ICUR0][CCONTROLD][IVALID], resI[rN][ICUR0][CCONTROLD][ISSET], resI[rN][ICUR0 + 1] == null ? -2 : resI[rN][ICUR0 + 1][CCONTROLD][ISSET]);
          }
        }// end print if
      }
    }
    didEndYear++;
    return rende4 - cnt; // number of slots left
  }

  /**
   * set a statistic value and possibly a count
   *
   * @param rn the name of this statistic
   * @param pors planet=0 ship=1
   * @param clan clan of the request
   * @param v the value to be set
   * @return v
   */
  double setStat(int rn, int pors, int clan, double v) {
    int age = curEcon.age;
    return setStat(rn, pors, clan, v, 1, age);
  }

  /**
   * set a statistic value and possibly a count
   *
   * @param rn the name of this statistic
   * @param pors planet=0 ship=1
   * @param clan clan of the request
   * @param v the value to be set
   * @param cnt cnt of occurances usually 0 or 1
   * @return v
   */
  double setStat(int rn, int pors, int clan, double v, int cnt) {
    int age = curEcon.age;
    return setStat(rn, pors, clan, v, cnt, age);
  }

  /**
   * set a statistic value and a count
   *
   * @param rn the name of this statistic
   * @param pors planet=0 ship=1
   * @param clan clan of the request
   * @param v the value to be set
   * @param cnt greater than 0 if this set is to be counted
   * @param age years since creation of the Econ for this stat
   * @return v
   */
  int cntStatsPrints = 0;
  double setStat(int rn, int pors, int clan, double v, int cnt, int age
  ) {
    int le = lStatsWaitList;
    int prevIx = ixStatsWaitList;
    ixStatsWaitList = (++ixStatsWaitList) % lStatsWaitList;
    prevIx = prevIx >= lStatsWaitList ? 0: prevIx;// I don't know why there was out of bounds sometimes
    int atCnt = 0;
    long nTime = (new Date()).getTime();
    long moreT = nTime - doYearTime;
    if (E.debugStatsOut) {
      statsWaitList[prevIx] = "setStat in thread " + Thread.currentThread().getName() + " sinceDoYear " + moreT + " at ";
      StackTraceElement[] prevCalls = new StackTraceElement[le];
      int lstk = Thread.currentThread().getStackTrace().length - 1;
      for (int ste = 1; ste < le && atCnt < 5 && ste < lstk; ste++) {
        prevCalls[ste] = Thread.currentThread().getStackTrace()[ste + 1];
        if (!prevCalls[ste].getMethodName().contentEquals("setStat")) {
          if (atCnt == 0) {
            statsWaitList[prevIx] += prevCalls[ste].getMethodName() + " ";
          }
          statsWaitList[prevIx] += " at " + prevCalls[ste].getFileName() + "." + prevCalls[ste].getLineNumber();
        }
        atCnt++;
      }//for
    }//if

    //  int sClan = curEcon.clan;
    // int pors = curEcon.pors;
    addlErr = "inSetStat a rn=" + rn + " pors=" + pors + " clan=" + clan + " ";
    String desc = resS[rn][0];
    wasHere = "inSetStat b rn=" + rn + " desc=" + desc + " pors=" + pors + " clan=" + clan + " ";
    addlErr = "";
    int a = -5, b = -5, curm = 0;
    if (resI[rn].length > DVECTOR2A) {
      for (a = 1; a < 6 && b < 0; a++) {
        //AGEBREAKS   0,   4,   8,    16,    32,   999999}; 
        //  CUM CUR0  CUR1 CUR2  CUR3  CUR4  CUR5
        //   LIST0-LIST9 LIST10 LIST11 LIST12 LIST13 LIST14 LIST15-LIST20
        //   CURMAXDEPTH = 7
        if (age < AGEBREAKS[a]) {
          b = a;
        }
      }
      curm = ICUR0 + CURMAXDEPTH * b;
    } // end if
    long  resLock[][][] = resI[rn];
    double resL[][][] = resV[rn];
    int ycnt = cnt > -2 ? cnt : 0;

    double[] resVCum = resV[rn][ICUM][pors]; //array object 
    double resVCumC = resVCum[clan]; // value in 
    resVCumC += v; // won't work
    long[] resICum = resI[rn][ICUM][pors];
    double[] resVCur = resV[rn][ICUR0][pors];
    long[] resICur = resI[rn][ICUR0][pors];
    long[] resICurCC = resI[rn][ICUR0][CCONTROLD];
    long[] resICumCC = resI[rn][ICUM][CCONTROLD];
    /* now set the values in the appropriate age group */
    double[] resVCurm = resV[rn][curm][pors];
    long[] resICurm = resI[rn][curm][pors];
    long[] resICurmCC = resI[rn][curm][CCONTROLD];
    //wasHere = "inSetStat rn=" + rn + " desc=" + desc + " pors=" + pors + " clan=" + clan + " ";
    synchronized (resLock) {
      resVCum[clan] += v;
      resICum[clan] += ycnt;
      resVCur[clan] += v;
      resICur[clan] += ycnt;
      resICurCC[ISSET] = 1;
      resICumCC[ISSET] = 1;
      if (curm > 0) {
        /* now set the values in the appropriate age group */
        resVCurm[clan] += v;
        resICurm[clan] += ycnt;
        resICurmCC[ISSET] = 1;
      }
    } // end of lock on res..[rn]

    statsWaitList[prevIx] = "";
    if (E.debugStatsOut) {
      if (rn > 0) {
        long endSt = (new Date()).getTime();
        long moreTT = endSt - doYearTime;
        int rN = rn;
        int jj = 1;
        int jjj = 1;
        long resICumClan = resI[rn][ICUM][pors][clan];
        long resIcur0Clan = resI[rn][ICUR0][pors][clan];
        double resVcur0Clan = resV[rn][ICUR0][pors][clan];
        long resICurmClan = resI[rn][curm][pors][clan];
        double resVCurmClan = resV[rn][curm][pors][clan];
        long resIcur0Isset = resI[rn][ICUR0][CCONTROLD][ISSET];
        long resICumIsset = resI[rn][ICUM][CCONTROLD][ISSET];

        long isset1 = (jj - 1 + jjj < resI[rN].length ? resI[rN][jj - 1 + jjj] != null ? resI[rN][jj - 1 + jjj][CCONTROLD][ISSET] : -1 : -2);

        if (E.debugStatsOut1) {
          if(cntStatsPrints < E.ssMax) { 
          cntStatsPrints += 1;
          System.out.println(
                  "EM.setStat " + Econ.nowName + " " + Econ.threadCnt[0] + " since doYear" + year + "=" + moreT + "=>" + moreTT + " " + resS[rN][0] + " rN" + rN + ", valid" + valid + ", " + " resIcum=" + resICumClan + ", age" + age + ", curEcon.age" + curEcon.age + ", pors=" + pors + ", clan=" + clan + ", resIcur0Isset=" + resIcur0Isset + ", resICumIsset=" + resICumIsset + ", resVCur0Clan=" + mf(resVcur0Clan) + ", resVCurmClan=" + mf(resVCurmClan));
          System.out.flush();
          } //ssMax
        }

      };
    }
    long[][][] resii = resI[rn];  //for values if using debug
    double[][][] resvv = resV[rn];
    return v;
  }

   /**
   * set a maxStatistic value and a count
   *
   * @param rn the name of this statistic
   * @param pors planet=0 ship=1
   * @param clan clan of the request
   * @param v the value to be set
   * @param cnt greater than 0 if this set is to be counted
   * @param age years since creation of the Econ for this stat
   * @return v
   */
 // int cntStatsPrints = 0;
  double setMaxStat(int rn, int pors, int clan, double v, int cnt, int age
  ) {
    int le = 10;
    int prevIx = ixStatsWaitList;
    ixStatsWaitList = (++ixStatsWaitList) % lStatsWaitList;
    int atCnt = 0;
    long nTime = (new Date()).getTime();
    long moreT = nTime - doYearTime;
    if (E.debugStatsOut) {
      statsWaitList[prevIx] = "setMaxStat in thread " + Thread.currentThread().getName() + " sinceDoYear " + moreT + " at ";
      StackTraceElement[] prevCalls = new StackTraceElement[le];
      int lstk = Thread.currentThread().getStackTrace().length - 1;
      for (int ste = 1; ste < le && atCnt < 5 && ste < lstk; ste++) {
        prevCalls[ste] = Thread.currentThread().getStackTrace()[ste + 1];
        if (!prevCalls[ste].getMethodName().contentEquals("setMaxStat")) {
          if (atCnt == 0) {
            statsWaitList[prevIx] += prevCalls[ste].getMethodName() + " ";
          }
          statsWaitList[prevIx] += " at " + prevCalls[ste].getFileName() + "." + prevCalls[ste].getLineNumber();
        }
        atCnt++;
      }//for
    }//if out

    //  int sClan = curEcon.clan;
    // int pors = curEcon.pors;
    addlErr = "inSetMaxStat a rn=" + rn + " pors=" + pors + " clan=" + clan + " ";
    String desc = resS[rn][0];
    wasHere = "inSetMaxStat b rn=" + rn + " desc=" + desc + " pors=" + pors + " clan=" + clan + " ";
    addlErr = "";
    int a = -5, b = -5, curm = 0;
    // check if we do agelist
    if (resI[rn].length > DVECTOR2A) {
      for (a = 1; a < 6 && b < 0; a++) {
        //AGEBREAKS   0,   4,   8,    16,    32,   999999}; 
        //  CUM CUR0  CUR1 CUR2  CUR3  CUR4  CUR5
        //   LIST0-LIST9 LIST10 LIST11 LIST12 LIST13 LIST14 LIST15-LIST20
        //   CURMAXDEPTH = 7
        if (age < AGEBREAKS[a]) {
          b = a;
        }
      }
      curm = ICUR0 + CURMAXDEPTH * b;
    } // end if
    long  resLock[][][] = resI[rn];
    double resL[][][] = resV[rn];
    int ycnt = cnt > 0 ? cnt : 0;

    double[] resVCum = resV[rn][ICUM][pors]; //array object 
    double resVCumC = resVCum[clan]; // value in 
    resVCumC += v; // won't work
    long[] resICum = resI[rn][ICUM][pors];
    double[] resVCur = resV[rn][ICUR0][pors];
    long[] resICur = resI[rn][ICUR0][pors];
    long[] resICurCC = resI[rn][ICUR0][CCONTROLD];
    long[] resICumCC = resI[rn][ICUM][CCONTROLD];
    /* now set the values in the appropriate age group */
    double[] resVCurm = resV[rn][curm][pors];
    long[] resICurm = resI[rn][curm][pors];
    long[] resICurmCC = resI[rn][curm][CCONTROLD];
    //wasHere = "inSetStat rn=" + rn + " desc=" + desc + " pors=" + pors + " clan=" + clan + " ";
    synchronized (resLock) {
        if(resICumCC[ISSET]  < 1){
            for(int m=0;m< 2; m++) {
                double aresVC[] = resV[rn][ICUM][m];
            for(int n=0; n < E.LCLANS;n++){
                aresVC[n] = -999999999999.;
            }
            }
        }
      resVCum[clan] = v > resVCum[clan] ? v : resVCum[clan] ;
      resICum[clan] += ycnt;
       if(resICurCC[ISSET]  < 1){
            for(int m=0;m< 2; m++) {
                double aresVC[] = resV[rn][ICUR0][m];
            for(int n=0; n < E.LCLANS;n++){
                aresVC[n] = -999999999999.;
            }
            }
        }
      resVCur[clan] = v > resVCur[clan] ? v : resVCur[clan];
      resICur[clan] += ycnt;
      resICurCC[ISSET] = 1;
      resICumCC[ISSET] = 1;
      if (curm > 0) {
          if(resICurmCC[ISSET]  < 1){
            for(int m=0;m< 2; m++) {
                double aresVC[] = resV[rn][curm][m];
            for(int n=0; n < E.LCLANS;n++){
                aresVC[n] = -999999999999.;
            }
            }
        }
        /* now set the values in the appropriate age group */
        resVCurm[clan] = v  > resVCurm[clan] ? v : resVCurm[clan];
        resICurm[clan] += ycnt;
        resICurmCC[ISSET] = 1;
      }
    } // end of lock on res..[rn]

    statsWaitList[prevIx] = "";
    if (E.debugStatsOut) {
      if (rn > 0) {
        long endSt = (new Date()).getTime();
        long moreTT = endSt - doYearTime;
        int rN = rn;
        int jj = 1;
        int jjj = 1;
        long resICumClan = resI[rn][ICUM][pors][clan];
        long resIcur0Clan = resI[rn][ICUR0][pors][clan];
        double resVcur0Clan = resV[rn][ICUR0][pors][clan];
        long resICurmClan = resI[rn][curm][pors][clan];
        double resVCurmClan = resV[rn][curm][pors][clan];
        long resIcur0Isset = resI[rn][ICUR0][CCONTROLD][ISSET];
        long resICumIsset = resI[rn][ICUM][CCONTROLD][ISSET];

        long isset1 = (jj - 1 + jjj < resI[rN].length ? resI[rN][jj - 1 + jjj] != null ? resI[rN][jj - 1 + jjj][CCONTROLD][ISSET] : -1 : -2);

        if (E.debugStatsOut1) {
          if(cntStatsPrints < E.ssMax) { 
          cntStatsPrints += 1;
          System.out.println(
                  "EM.setStat " + Econ.nowName + " " + Econ.threadCnt[0] + " since doYear" + year + "=" + moreT + "=>" + moreTT + " " + resS[rN][0] + " rN" + rN + ", valid" + valid + ", " + " resIcum=" + resICumClan + ", age" + age + ", curEcon.age" + curEcon.age + ", pors=" + pors + ", clan=" + clan + ", resIcur0Isset=" + resIcur0Isset + ", resICumIsset=" + resICumIsset + ", resVCur0Clan=" + mf(resVcur0Clan) + ", resVCurmClan=" + mf(resVCurmClan));
          System.out.flush();
          } //ssMax
        }

      };
    }
    long[][][] resii = resI[rn];  //for values if using debug
    double[][][] resvv = resV[rn];
    return v;
  }
  
   /**
   * set a min Statistic value and a count
   *
   * @param rn the name of this statistic
   * @param pors planet=0 ship=1
   * @param clan clan of the request
   * @param v the value to be set
   * @param cnt greater than 0 if this set is to be counted
   * @param age years since creation of the Econ for this stat
   * @return v
   */
 // int cntStatsPrints = 0;
  double setMinStat(int rn, int pors, int clan, double v, int cnt, int age
  ) {
    int le = 10;
    int prevIx = ixStatsWaitList;
    ixStatsWaitList = (++ixStatsWaitList) % lStatsWaitList;
    int atCnt = 0;
    long nTime = (new Date()).getTime();
    long moreT = nTime - doYearTime;
    if (E.debugStatsOut) {
      statsWaitList[prevIx] = "setMinStat in thread " + Thread.currentThread().getName() + " sinceDoYear " + moreT + " at ";
      StackTraceElement[] prevCalls = new StackTraceElement[le];
      int lstk = Thread.currentThread().getStackTrace().length - 1;
      for (int ste = 1; ste < le && atCnt < 5 && ste < lstk; ste++) {
        prevCalls[ste] = Thread.currentThread().getStackTrace()[ste + 1];
        if (!prevCalls[ste].getMethodName().contentEquals("setMinStat")) {
          if (atCnt == 0) {
            statsWaitList[prevIx] += prevCalls[ste].getMethodName() + " ";
          }
          statsWaitList[prevIx] += " at " + prevCalls[ste].getFileName() + "." + prevCalls[ste].getLineNumber();
        }
        atCnt++;
      }//for
    }//if out

    //  int sClan = curEcon.clan;
    // int pors = curEcon.pors;
    addlErr = "inSetMinStat a rn=" + rn + " pors=" + pors + " clan=" + clan + " ";
    String desc = resS[rn][0];
    wasHere = "inSetMinStat b rn=" + rn + " desc=" + desc + " pors=" + pors + " clan=" + clan + " ";
    addlErr = "";
    int a = -5, b = -5, curm = 0;
    // check if we do agelist
    if (resI[rn].length > DVECTOR2A) {
      for (a = 1; a < 6 && b < 0; a++) {
        //AGEBREAKS   0,   4,   8,    16,    32,   999999}; 
        //  CUM CUR0  CUR1 CUR2  CUR3  CUR4  CUR5
        //   LIST0-LIST9 LIST10 LIST11 LIST12 LIST13 LIST14 LIST15-LIST20
        //   CURMAXDEPTH = 7
        if (age < AGEBREAKS[a]) {
          b = a;
        }
      }
      curm = ICUR0 + CURMAXDEPTH * b;
    } // end if
    long  resLock[][][] = resI[rn];
    double resL[][][] = resV[rn];
    int ycnt = cnt > 0 ? cnt : 0;

    double[] resVCum = resV[rn][ICUM][pors]; //array object 
    double resVCumC = resVCum[clan]; // value in 
    resVCumC += v; // won't work
    long[] resICum = resI[rn][ICUM][pors];
    double[] resVCur = resV[rn][ICUR0][pors];
    long[] resICur = resI[rn][ICUR0][pors];
    long[] resICurCC = resI[rn][ICUR0][CCONTROLD];
    long[] resICumCC = resI[rn][ICUM][CCONTROLD];
    /* now set the values in the appropriate age group */
    double[] resVCurm = resV[rn][curm][pors];
    long[] resICurm = resI[rn][curm][pors];
    long[] resICurmCC = resI[rn][curm][CCONTROLD];
    //wasHere = "inSetStat rn=" + rn + " desc=" + desc + " pors=" + pors + " clan=" + clan + " ";
    synchronized (resLock) {
         if(resICumCC[ISSET] < 1){
            for(int m=0;m< 2; m++) {
                double aresVC[] = resV[rn][ICUM][m];
            for(int n=0; n < E.LCLANS;n++){
                aresVC[n] = 999999999999999999999999.;
            }
            }
        }
      resVCum[clan] = v < resVCum[clan] ? v : resVCum[clan] ;
      resICum[clan] += ycnt;
       if(resICurCC[ISSET]   < 1){
            for(int m=0;m< 2; m++) {
                double aresVC[] = resV[rn][ICUR0][m];
            for(int n=0; n < E.LCLANS;n++){
                aresVC[n] = 999999999999999999999999.;
            }
            }
        }
      resVCur[clan] = v < resVCur[clan]  ? v : resVCur[clan];
      resICur[clan] += ycnt;
      resICurCC[ISSET] = 1;
      resICumCC[ISSET] = 1;
      if (curm > 0) {
        /* now set the values in the appropriate age group */
         if(resICurmCC[ISSET]  < 1){
            for(int m=0;m< 2; m++) {
                double aresVC[] = resV[rn][curm][m];
            for(int n=0; n < E.LCLANS;n++){
                aresVC[n] = 999999999999999999999999.;
            }
            }
        }
        resVCurm[clan] = v  < resVCurm[clan] || resICurmCC[ISSET]  < 1 ? v : resVCurm[clan];
        resICurm[clan] += ycnt;
        resICurmCC[ISSET] = 1;
      }
    } // end of lock on res..[rn]

    statsWaitList[prevIx] = "";
    if (E.debugStatsOut) {
      if (rn > 0) {
        long endSt = (new Date()).getTime();
        long moreTT = endSt - doYearTime;
        int rN = rn;
        int jj = 1;
        int jjj = 1;
        long resICumClan = resI[rn][ICUM][pors][clan];
        long resIcur0Clan = resI[rn][ICUR0][pors][clan];
        double resVcur0Clan = resV[rn][ICUR0][pors][clan];
        long resICurmClan = resI[rn][curm][pors][clan];
        double resVCurmClan = resV[rn][curm][pors][clan];
        long resIcur0Isset = resI[rn][ICUR0][CCONTROLD][ISSET];
        long resICumIsset = resI[rn][ICUM][CCONTROLD][ISSET];

        long isset1 = (jj - 1 + jjj < resI[rN].length ? resI[rN][jj - 1 + jjj] != null ? resI[rN][jj - 1 + jjj][CCONTROLD][ISSET] : -1 : -2);

        if (E.debugStatsOut1) {
          if(cntStatsPrints < E.ssMax) { 
          cntStatsPrints += 1;
          System.out.println(
                  "EM.setStat " + Econ.nowName + " " + Econ.threadCnt[0] + " since doYear" + year + "=" + moreT + "=>" + moreTT + " " + resS[rN][0] + " rN" + rN + ", valid" + valid + ", " + " resIcum=" + resICumClan + ", age" + age + ", curEcon.age" + curEcon.age + ", pors=" + pors + ", clan=" + clan + ", resIcur0Isset=" + resIcur0Isset + ", resICumIsset=" + resICumIsset + ", resVCur0Clan=" + mf(resVcur0Clan) + ", resVCurmClan=" + mf(resVCurmClan));
          System.out.flush();
          } //ssMax
        }

      };
    }
    long[][][] resii = resI[rn];  //for values if using debug
    double[][][] resvv = resV[rn];
    return v;
  }
  
  /**
   * set a statistic value and possibly a count
   *
   * @param rn the name of this statistic
   * @param v the value to be set
   * @param cnt greater than 0 if this set is to be counted
   * @return v
   */
  double setStat(int rn, double v, int cnt) {
    int age = curEcon.age;
    int clan = curEcon.clan;
    int pors = curEcon.pors;
    return setStat(rn, pors, clan, v, cnt, age);
  }

  static int putRowsPrint1Count = 0;
  static int putRowsPrint2Count = 0;
  static int putRowsPrint3Count = 0;
  static int putRowsPrint4Count = 0;
  static int putRowsPrint5Count = 0;
  static int putRowsPrint6Count = 0;
  static int putRowsPrint6aCount = 0;
  static int putRowsPrint7Count = 0;
  static int putRowsPrint8Count = 0;
  static int putRowsPrint9Count = 0;
  static int prpc1 = 0;
  static int prpc2 = 0;
  static int prpc3 = 0;
  private long[][][] res2;
  private long[][][] res3;

  /**
   * possibly put a row into table if the key aop matches a lock in rn Called
   * from StarTrader
   *
   * @param table table in Stats
   * @param resExt this is the detail and (tip text) accessed by the title in the table 
   * @param row next row in the display table
   * @param aop the key to fit locks in resI, describes which page(LIST..) and types of output, rows
   * @return next row
   */
  public int putRows(JTable table, String[] resExt, int row, long aop) {
    if (putRowsPrint1Count++ < 10) {
      if (E.debugPutRowsOut) {
        System.out.println(">>>>>>putRows1 rende4=" + rende4 + "," + rendae4 + ", count=" + putRowsPrint1Count + "<<<<<<");
      }

    }
    int rn = 0;
    String prevDesc = "none";
    long depth = -2, listMatch = 0, prevListMatch = 0, hLMp = 0, myValid = 0;
    for (rn = 0; rn < rende4; rn++) { // traverse all doRes entries
      if (putRowsPrint2Count++ < 10) {
        if (E.debugPutRowsOut) {
          System.out.println(">>>>>>putRows2 count=" + putRowsPrint2Count + " rn=" + rn + " row=" + row + " <<<<<<");
        }
      }
      if (resI[rn] == null) {
        System.out.println("null resI[" + rn + "] prev desc=" + prevDesc);
      } else { // not null
        prevDesc = resS[rn][0];
        boolean myUnset;
        long[][][] resii = resI[rn];
        long[][] resiii = resI[rn][ICUM];
        long[] resiic = resI[rn][ICUM][CCONTROLD];
        int c = 0, ageIx = 0;
        // duplication is also in doRes with short versions doing duplication of previous values
        if (res3 != null) { //use prev locks by default
          res2 = res3;
          res3 = null;
        }
        // check for SKIPDUP in locks0, save res2 in res3 don't use dup, use this rn
        if ((resI[rn][ICUM][CCONTROLD][LOCKS0 + 0] & SKIPDUP) > 0L) {
          res3 = res2;  // save prev value for use above
          res2 = resI[rn];
        } else // use the previous locks at res2 if DUP is in the first lock this rn
        // there can be a long string of DUP, always use the last no DUP
        if ((resI[rn][ICUM][CCONTROLD][LOCKS0 + 0] & DUP) == 0L) { // if no DUP
          res2 = resI[rn];  //save this rn locks as current if no dup, then use previous locks
        }

        // now see if this rn has an acceptable lock,
        // if the governing locks contain
      long allLocks = resiic[LOCKS0] | resiic[LOCKS1] | resiic[LOCKS2] | resiic[LOCKS3];
        // pick ages  again in putRows2
        int maxc = resI[rn].length < DVECTOR2MAX ? shortLength : longLength;
        for (c = 3, ageIx = 0; c < maxc && ageIx == 0; c++) {
          if (((aop & allLocks) & AGELISTS[c]) > 0) { // pick the lfirst age
            ageIx = c - shortLength + 1; // LIST10 = 1
          }
        }
        // short look only at ICUR0 unset, long look at ICUR0 for the first 3 than at thee rest
        // so change ageIx to do the above
        ageIx = resI[rn].length < DVECTOR2MAX ? 0 : ageIx <= shortLength ? 0 : ageIx - shortLength + 1;
        myUnset = unset = resI[rn][ICUR0 + ageIx * CURMAXDEPTH][CCONTROLD][ISSET] < 1; // flag for age
        myValid = valid = resI[rn][ICUR0 + ageIx * CURMAXDEPTH][CCONTROLD][IVALID];
        depth = resI[rn][ICUR0 + ageIx * CURMAXDEPTH][CCONTROLD][IVALID];
        // set lla true to do a print below
        boolean lla = (rn > (rende4 - 2) ? true
                : (rn == RCGLT10PERCENT) ? true
                        : (rn == RCTGROWTHPERCENT) ? true
                                : ((aop & (LIST8 | LIST0 | LIST3)) > 0l)
                                        ? (prpc2++ > 6) ? (prpc2 = 0) == 0 : false : false);
        if (E.debugPutRowsOut) {
          if ((lla || ((putRowsPrint6aCount % 75) == 0)) && (putRowsPrint6aCount++ < 100)) {
            System.out.flush();
            System.out.printf("EM.putrow6a rn=%d %s, %s, list%d, depth%d, valid%d, cum%d, rende4=%d,%d putRowsPrint6aCount= " + putRowsPrint6aCount + " \n", rn, (unset ? "UNSET" : "ISSET"), resS[rn][0], ((aop & list0) > 0 ? 0 : (aop & list1) > 0 ? 1 : (aop & LIST8) > 10 ? 2 : (aop & LIST3) > 0 ? 17 : aop), depth, valid, resI[rn][ICUM][0][0], rende4, rendae4);
          }
        }
        row = putRows2(table, resExt, rn, row, aop, allLocks, ageIx);
      } // end not null
    }
    return row;
  }
   private long haveOpsMatch;
   private long haveAllOpsMatch;
   private long haveListsMatch;
   private long haveRows;  //StarTrader rows key
   private long haveRowsMatch;
   private long haveCmdMatch ;
   
   private String getOpsNames(Long ops){
     String rtn="";
     rtn += ((ops & CUR) > 0? " cur":"");
     rtn += ((ops & CURAVE ) > 0 ? " curAve":"");
     rtn += ((ops & CURUNITS)  > 0 ? " curUnits":"");
     rtn += ((ops & CUM) > 0 ? " cum":"");
     rtn += ((ops & CUMAVE) > 0 ? " cumAve":"");
     rtn += ((ops & CUMUNITS) > 0 ? " cumUnits":"");
     rtn += ((ops & THISYEAR) > 0 ? " thisYear":"");
     rtn += ((ops & THISYEARUNITS) > 0 ? " thisYearUnits":"");
     rtn += ((ops & THISYEARAVE) > 0 ? " thisYearAve":"");
     return rtn;
   }
     int c = 0,myRow=0,myLock=0,myAgeIx=0;
      long depth = -2, listMatch = 0, prevListMatch = 0, hLMp = 0, myValid = 0;
      //long haveRows = 0L;
      /** test whether to output this special line of output
       * 
       * @param myCmd the cmd being proessed
       * @return true if a print done
       */
      private boolean ifPutRow6(String myCmd){
        if( (resS[c][rDesc].contentEquals("EmergFF")  ) && ( (haveListsMatch & LIST6) > 0 ) ){
            if (putRowsPrint6Count  < 20 || 
                (((putRowsPrint6Count % 25) == 0)) && (putRowsPrint6Count < 200)) {
              System.out.flush();
               System.out.printf("EM.putrow6" + myCmd + "  rn=" + myRn + ", " + resS[myRn][0] + ", row=" + myRow + 
                      ", isset=" + (myUnset ? myCumUnset ? "CumUnset" : "unset" : "isSet") + 
                      ", lock#" + myLock + ", list" + ((haveListsMatch & list0) > 0 ? 0 : (haveListsMatch & list1) > 0 ? 1 : (haveListsMatch & LIST2) > 0 ? 2 : (haveListsMatch & LIST6) > 0 ? 6: "??") + 
                      ", ops=" +  getOpsNames(haveCmdMatch) + 
                      ", depth=" + depth  +  ", valid" + valid + ", ageIx" + myAgeIx + 
                              ", cum00=" + resI[myRn][ICUM][0][0] + 
                              ", rende4=" + rende4 + "," + rendae4 + " putRowsPrint6Count=" + putRowsPrint6Count  + " \n");
               return true;
            }
             
            }
        
        return false;
      }
              
  /**
   * possibly put a row into table if the key aop matches a lock in rn
   *
   * @param table table in Stats
   * @param resExt this is the detail and (tip text)
   * @param rn defined number of the stat called in order
   * @param row next row in the display table
   * @param aop the key to fit the locks in resI
   * @param allLocks the or of all the locks
   * @param ageIx the age category of the item must be recalculated
   * @return next row
   */
  private int putRows2(JTable table, String[] resExt, int rn, int row, long aop, long allLocks,int ageIx) {
    myRn = rn;
    myRow = row;
    myAop = aop;
    myAgeIx = ageIx;
    if (E.debugPutRowsOut) {
      if (putRowsPrint3Count++ < 12) {
        System.out.println(">>>>>>putRows3 count=" + putRowsPrint3Count + " rn=" + rn + " row=" + row + ", rende4=" + rende4 + "," + rendae4 + " <<<<<<");
      }
    }
    if (resV[rn] == null) { // skip undefined rows
      return row;
    }
    try {
      int tend = table.getSize().height;
      long opr = 0;
      //int opx = 0;
      long[][][] resii = res2;
      long[][] resiii = res2[ICUM];
      long[] resiic = res2[ICUM][CCONTROLD];
    
      haveAllOpsMatch = (aop & allLocks) & dmask;
      haveListsMatch = (aop & allLocks) & lmask;
      if (E.debugPutRowsOut) {
        if (putRowsPrint4Count++ < 12) {
          System.out.println(">>>>>>putRows4 count=" + putRowsPrint4Count + " haveOpsMatch=" + haveOpsMatch + " haveListsMatch=" + haveListsMatch + " rn=" + rn + " row=" + row + " <<<<<<");
        }
      }
      if (haveAllOpsMatch == 0L || haveListsMatch == 0L) { // check if EM rn missing list or do (command)
        return row;
      }

      didSum = false; // initialize didSum, remember sum across locks
      // process each LOCKS0-3 thru each command and list not zero
      // use previous hlMp (LISTs) if this lock has no list
      if (E.debugPutRowsOut) {
        if (putRowsPrint5Count++ < 12) {
          System.out.println(">>>>>>putRows5 count=" + putRowsPrint5Count + " haveOpsMatch=" + haveOpsMatch + " haveListsMatch=" + haveListsMatch + " rn=" + rn + " <<<<<<");
        }
      }
      int rowAtStart = row;
      // printing done now move through the 4 locks
      for (int d = 0; d < 4; d++) {
        myLock = d;
        // check for a LISTx match put in prevListMatch
        prevListMatch = (long) (aop & res2[ICUM][CCONTROLD][LOCKS0 + d]) & lmask;
        //look for ROWS1 ROWS2 ROWS3
        haveRows = aop & ROWSMASK;  //StarTrader rows key
        // any ROWSx in this lock
        haveRowsMatch = res2[ICUM][CCONTROLD][LOCKS0 + d] & ROWSMASK;
        // match either no ROWS or some ROWS
        boolean okRows = (haveRows == 0L && haveRowsMatch == 0L) || ((haveRows & haveRowsMatch) > 0L);
        //use current  LISTx match if present else use previous listMatch 
        listMatch = (res2[ICUM][CCONTROLD][LOCKS0 + d] & lmask) > 0L ? prevListMatch : listMatch;
        // is there a command match
        haveCmdMatch = (aop & res2[ICUM][CCONTROLD][LOCKS0 + d]) & d1mask;
        opr = haveCmdMatch;

        // must have at least 1 matching list and 1 matching do type and okRows
        if (listMatch > 0L && haveCmdMatch > 0L && okRows) {
          hLMp = listMatch; // save a good list option
          // now process the selected age group by the first match
          // aop should containt no more than 1 age list
          // pick ages  again in putRows2
        int maxc = resI[rn].length < DVECTOR2MAX ? shortLength : longLength;
        for (c = 3, ageIx = 0; c < maxc && ageIx == 0; c++) {
          if (((listMatch) & AGELISTS[c]) > 0) { // pick the first matching age start LIST10
            ageIx = c - shortLength + 1; // LIST10 = 1
          }
        }

          myUnset = unset = resI[rn][ICUR0 + ageIx * CURMAXDEPTH][CCONTROLD][ISSET] < 1; // flag for age
          myCumUnset = myUnset && resI[rn][ICUM][CCONTROLD][ISSET] < 1; 
          myValid = valid = resI[rn][ICUR0 + ageIx * CURMAXDEPTH][CCONTROLD][IVALID];
          depth = resI[rn][ICUR0 + ageIx * CURMAXDEPTH][CCONTROLD][IVALID];

          doUnits = false;
          // set didSum if ever doSum and not both
          didSum |= doSum = (opr & sum) > 0;
          doBoth = (opr & both) > 0 || !didSum;  // no default both if didsum
          didSum &= !doBoth;  // doBoth clears didSum
          doSkipUnset = (haveAllOpsMatch & skipUnset) > 0;
          doZeroUnset = (haveAllOpsMatch & zeroUnset) > 0;
          tstr = (opr & tstring) > 0;
          didUnset = false;
          // now determine type to pick a suffix and set boolean flags
          //  int cop = (int)(aop & opx & 017);
          // lla true if rn>rend4-1 
          boolean lla = (rn > (rende4 - 2)
                  && ((aop & (LIST1 | LIST0 | LIST2 | LIST3)) > 0l) ?  false : false);

          //   if ((resS[rn][rDesc].contains("WORTH") || resS[rn][rDesc].contains("KNOWLEDGE") || resS[rn][rDesc].contains("Create") || lla || ((putRowsPrint6Count % 75) == 0 )) && (putRowsPrint6Count < 200)) {
          if (E.debugPutRowsOut6) {
            if(ifPutRow6("start")) { putRowsPrint6Count++; }
          }
          if (unset) {
            suffix = " curYr:";
          }

          if (!myUnset && (opr & cur) > 0L) {
            suffix = " curYr:";
           
            for (int m = 0; m < valid; m++) {
              suffix = " curYr:" + (m + 1) + "/" + valid;
              lStart = m;
              lEnd = m + 1;
              row = putRowInTable(table, resExt, rn, cur, row, suffix, ageIx);
            }
            if (E.debugPutRowsOut6) {
              ifPutRow6("cur");
          }
          } 
          // do not process thisYear if cur was already processed
          if (!myUnset && ((opr & cur) == 0) && ((opr & thisYr) > 0L)) {
            suffix = " thisYr";
            lStart = 0;
            lEnd = 1;
            row = putRowInTable(table, resExt, rn, thisYr, row, suffix, ageIx);
                        if (E.debugPutRowsOut6) {
                         ifPutRow6("thisYr");
          }
          }

          if (tstr) { // tstring is not used
            //        row = getT(table, rn, resExt, row);
            //  return row;
          }

          if (!myUnset && (opr & curUnitAve) > 0L) {
            for (int m = 0; m < valid; m++) {
              suffix = " curAve yr:" + wh(m + 1) + "/" + wh(valid);
              lStart = m;
              lEnd = m + 1;
              row = putRowInTable(table, resExt, rn, curUnitAve, row, suffix, ageIx);
            }
                          if (E.debugPutRowsOut6) {
                            if( (resS[rn][rDesc].contentEquals("EmergFF")  ) && ( (haveListsMatch & LIST6) > 0 ) ){
            if (putRowsPrint6Count  < 20 || 
                (((putRowsPrint6Count % 25) == 0)) && (putRowsPrint6Count < 200)) {
              System.out.flush();
               System.out.printf("EM.putrow6Start rn=" + rn + ", " + resS[rn][0] + ", row=" + row + 
                      ", isset=" + (myUnset ? myCumUnset ? "CumUnset" : "unset" : "isSet") + 
                      ", lock#" + d + ", list" + ((haveListsMatch & list0) > 0 ? 0 : (haveListsMatch & list1) > 0 ? 1 : (haveListsMatch & LIST2) > 0 ? 2 : (haveListsMatch & LIST6) > 0 ? 6: "??") + 
                      ", ops=" +  getOpsNames(haveCmdMatch) + 
                      ", depth=" + depth  +  ", valid" + valid + ", ageIx" + ageIx + 
                              ", cum00=" + resI[rn][ICUM][0][0] + 
                              ", rende4=" + rende4 + "," + rendae4 + " putRowsPrint6Count=" + putRowsPrint6Count  + " \n");
            }
             
            }
          }

            // break;
          }

          // do not process thisYear if cur was already processed
          if (!myUnset && ((opr & curUnitAve) == 0L) && ((opr & thisYearUnitAve) > 0L)) {
            suffix = " ThisYrAve";
            lStart = 0;
            lEnd = 1;
            row = putRowInTable(table, resExt, rn, thisYearUnitAve, row, suffix, ageIx);
                       if (E.debugPutRowsOut6) {
                         if( (resS[rn][rDesc].contentEquals("EmergFF")  ) && ( (haveListsMatch & LIST6) > 0 ) ){
            if (putRowsPrint6Count < 20 || 
                (((putRowsPrint6Count % 25) == 0)) && (putRowsPrint6Count < 200)) {
              System.out.flush();
              System.out.printf("EM.putrow6Start rn=" + rn + ", " + resS[rn][0] + ", row=" + row + 
                      ", isset=" + (myUnset ? myCumUnset ? "CumUnset" : "unset" : "isSet") + 
                      ", lock#" + d + ", list" + ((haveListsMatch & list0) > 0 ? 0 : (haveListsMatch & list1) > 0 ? 1 : (haveListsMatch & LIST2) > 0 ? 2 : (haveListsMatch & LIST6) > 0 ? 6: "??") + 
                      ", ops=" +  getOpsNames(haveCmdMatch) + 
                      ", depth=" + depth  +  ", valid" + valid + ", ageIx" + ageIx + 
                              ", cum00=" + resI[rn][ICUM][0][0] + 
                              ", rende4=" + rende4 + "," + rendae4 + " putRowsPrint6Count=" + putRowsPrint6Count  + " \n");
            }
             
            }
          }
          }

            if (E.debugPutRowsOut6) {
                          if( (resS[rn][rDesc].contentEquals("EmergFF")  ) && ( (haveListsMatch & LIST6) > 0 ) ){
            if (putRowsPrint6Count < 20 || 
                (((putRowsPrint6Count % 25) == 0)) && (putRowsPrint6Count < 200)) {
              System.out.flush();
               System.out.printf("EM.putrow6Start rn=" + rn + ", " + resS[rn][0] + ", row=" + row + 
                      ", isset=" + (myUnset ? myCumUnset ? "CumUnset" : "unset" : "isSet") + 
                      ", lock#" + d + ", list" + ((haveListsMatch & list0) > 0 ? 0 : (haveListsMatch & list1) > 0 ? 1 : (haveListsMatch & LIST2) > 0 ? 2 : (haveListsMatch & LIST6) > 0 ? 6: "??") + 
                      ", ops=" +  getOpsNames(haveCmdMatch) + 
                      ", depth=" + depth  +  ", valid" + valid + ", ageIx" + ageIx + 
                              ", cum00=" + resI[rn][ICUM][0][0] + 
                              ", rende4=" + rende4 + "," + rendae4 + " putRowsPrint6Count=" + putRowsPrint6Count  + " \n");
            }
             
            }
          }
          if(!myCumUnset && (haveCmdMatch & cum) > 0L) {
            suffix = " Cum";
            lStart = 0;
            lEnd = 1;
            row = putRowInTable(table, resExt, rn, cum, row, suffix, ageIx);
                        if (E.debugPutRowsOut6) {
                          if( (resS[rn][rDesc].contentEquals("EmergFF")  ) && ( (haveListsMatch & LIST6) > 0 ) ){
            if (putRowsPrint6Count < 20 || 
                (((putRowsPrint6Count % 25) == 0)) && (putRowsPrint6Count < 200)) {
              System.out.flush();
               System.out.printf("EM.putrow6Start rn=" + rn + ", " + resS[rn][0] + ", row=" + row + 
                      ", isset=" + (myUnset ? myCumUnset ? "CumUnset" : "unset" : "isSet") + 
                      ", lock#" + d + ", list" + ((haveListsMatch & list0) > 0 ? 0 : (haveListsMatch & list1) > 0 ? 1 : (haveListsMatch & LIST2) > 0 ? 2 : (haveListsMatch & LIST6) > 0 ? 6: "??") + 
                      ", ops=" +  getOpsNames(haveCmdMatch) + 
                      ", depth=" + depth  +  ", valid" + valid + ", ageIx" + ageIx + 
                              ", cum00=" + resI[rn][ICUM][0][0] + 
                              ", rende4=" + rende4 + "," + rendae4 + " putRowsPrint6Count=" + putRowsPrint6Count  + " \n");
            }
             
            }
          }
          }
          
            if (E.debugPutRowsOut6) {
                          if( (resS[rn][rDesc].contentEquals("EmergFF")  ) && ( (haveListsMatch & LIST6) > 0 ) ){
            if (putRowsPrint6Count < 20 || 
                (((putRowsPrint6Count % 25) == 0)) && (putRowsPrint6Count < 200)) {
              System.out.flush();
              System.out.printf("EM.putrow6Start rn=" + rn + ", " + resS[rn][0] + ", row=" + row + 
                      ", isset=" + (myUnset ? myCumUnset ? "CumUnset" : "unset" : "isSet") + 
                      ", lock#" + d + ", list" + ((haveListsMatch & list0) > 0 ? 0 : (haveListsMatch & list1) > 0 ? 1 : (haveListsMatch & LIST2) > 0 ? 2 : (haveListsMatch & LIST6) > 0 ? 6: "??") + 
                      ", ops=" +  getOpsNames(haveCmdMatch) + 
                      ", depth=" + depth  +  ", valid" + valid + ", ageIx" + ageIx + 
                              ", cum00=" + resI[rn][ICUM][0][0] + 
                              ", rende4=" + rende4 + "," + rendae4 + " putRowsPrint6Count=" + putRowsPrint6Count  + " \n");
            }
             
            }
          }
          if (!myCumUnset && (haveCmdMatch & cumUnits) > 0) {
            suffix = " CumU";
            doUnits = true;
            lStart = 0;
            lEnd = 1;
            row = putRowInTable(table, resExt, rn, cumUnits, row, suffix, ageIx);
                        if (E.debugPutRowsOut6) {
                          if( (resS[rn][rDesc].contentEquals("EmergFF")  ) && ( (haveListsMatch & LIST6) > 0 ) ){
            if (putRowsPrint6Count < 20 || 
                (((putRowsPrint6Count % 25) == 0)) && (putRowsPrint6Count < 200)) {
              System.out.flush();
               System.out.printf("EM.putrow6Start rn=" + rn + ", " + resS[rn][0] + ", row=" + row + 
                      ", isset=" + (myUnset ? myCumUnset ? "CumUnset" : "unset" : "isSet") + 
                      ", lock#" + d + ", list" + ((haveListsMatch & list0) > 0 ? 0 : (haveListsMatch & list1) > 0 ? 1 : (haveListsMatch & LIST2) > 0 ? 2 : (haveListsMatch & LIST6) > 0 ? 6: "??") + 
                      ", ops=" +  getOpsNames(haveCmdMatch) + 
                      ", depth=" + depth  +  ", valid" + valid + ", ageIx" + ageIx + 
                              ", cum00=" + resI[rn][ICUM][0][0] + 
                              ", rende4=" + rende4 + "," + rendae4 + " putRowsPrint6Count=" + putRowsPrint6Count  + " \n");
            }
             
            }
          }
          }
          if (!myCumUnset && (haveCmdMatch & cumUnitAve) > 0) {
            suffix = " CumAve";
            lStart = 0;
            lEnd = 1;
            row = putRowInTable(table, resExt, rn, cumUnitAve, row, suffix, ageIx);
                        if (E.debugPutRowsOut6) {
                         if( (resS[rn][rDesc].contentEquals("EmergFF")  ) && ( (haveListsMatch & LIST6) > 0 ) ){
            if (putRowsPrint6Count < 20 || 
                (((putRowsPrint6Count % 25) == 0)) && (putRowsPrint6Count < 200)) {
              System.out.flush();
               System.out.printf("EM.putrow6Start rn=" + rn + ", " + resS[rn][0] + ", row=" + row + 
                      ", isset=" + (myUnset ? myCumUnset ? "CumUnset" : "unset" : "isSet") + 
                      ", lock#" + d + ", list" + ((haveListsMatch & list0) > 0 ? 0 : (haveListsMatch & list1) > 0 ? 1 : (haveListsMatch & LIST2) > 0 ? 2 : (haveListsMatch & LIST6) > 0 ? 6: "??") + 
                      ", ops=" +  getOpsNames(haveCmdMatch) + 
                      ", depth=" + depth  +  ", valid" + valid + ", ageIx" + ageIx + 
                              ", cum00=" + resI[rn][ICUM][0][0] + 
                              ", rende4=" + rende4 + "," + rendae4 + " putRowsPrint6Count=" + putRowsPrint6Count  + " \n");
            }
             
            }
          }
          }
          if (!myUnset && (haveCmdMatch & curUnits) > 0) {
            for (int m = 0; m < valid; m++) {
              suffix = " curU yr:" + wh(m + 1) + "/" + wh(valid);
              doUnits = true;
              lStart = m;
              lEnd = m + 1;
              row = putRowInTable(table, resExt, rn, curUnits, row, suffix, ageIx);
                          if (E.debugPutRowsOut6) {
                           if( (resS[rn][rDesc].contentEquals("EmergFF")  ) && ( (haveListsMatch & LIST6) > 0 ) ){
            if (putRowsPrint6Count < 20 || 
                (((putRowsPrint6Count % 25) == 0)) && (putRowsPrint6Count < 200)) {
              System.out.flush();
               System.out.printf("EM.putrow6Start rn=" + rn + ", " + resS[rn][0] + ", row=" + row + 
                      ", isset=" + (myUnset ? myCumUnset ? "CumUnset" : "unset" : "isSet") + 
                      ", lock#" + d + ", list" + ((haveListsMatch & list0) > 0 ? 0 : (haveListsMatch & list1) > 0 ? 1 : (haveListsMatch & LIST2) > 0 ? 2 : (haveListsMatch & LIST6) > 0 ? 6: "??") + 
                      ", ops=" +  getOpsNames(haveCmdMatch) + 
                      ", depth=" + depth  +  ", valid" + valid + ", ageIx" + ageIx + 
                              ", cum00=" + resI[rn][ICUM][0][0] + 
                              ", rende4=" + rende4 + "," + rendae4 + " putRowsPrint6Count=" + putRowsPrint6Count  + " \n");
            }
             
            }
          }
            }
          }
          // do not process thisYear if cur was already processed
          // if ((opr & (myOp = thisYearUnits)) > 0 && !unset) {
          if (!myUnset && ((haveCmdMatch & curUnits) == 0) && ((opr & thisYearUnits) > 0)) {
            suffix = " thisyrU";
            doUnits = true;
            lStart = 0;
            lEnd = 1;
            row = putRowInTable(table, resExt, rn, thisYearUnits, row, suffix, ageIx);
                        if (E.debugPutRowsOut6) {
                          if( (resS[rn][rDesc].contentEquals("EmergFF")  ) && ( (haveListsMatch & LIST6) > 0 ) ){
            if (putRowsPrint6Count < 20 || 
                (((putRowsPrint6Count % 25) == 0)) && (putRowsPrint6Count < 200)) {
     
              System.out.flush();
               System.out.printf("EM.putrow6Start rn=" + rn + ", " + resS[rn][0] + ", row=" + row + 
                      ", isset=" + (myUnset ? myCumUnset ? "CumUnset" : "unset" : "isSet") + 
                      ", lock#" + d + ", list" + ((haveListsMatch & list0) > 0 ? 0 : (haveListsMatch & list1) > 0 ? 1 : (haveListsMatch & LIST2) > 0 ? 2 : (haveListsMatch & LIST6) > 0 ? 6: "??") + 
                      ", ops=" +  getOpsNames(haveCmdMatch) + 
                      ", depth=" + depth  +  ", valid" + valid + ", ageIx" + ageIx + 
                              ", cum00=" + resI[rn][ICUM][0][0] + 
                              ", rende4=" + rende4 + "," + rendae4 + " putRowsPrint6Count=" + putRowsPrint6Count  + " \n");
            }
             
            }
          }
          }
                    if (E.debugPutRowsOut6) {
                      if( (resS[rn][rDesc].contentEquals("EmergFF")  ) && ( (haveListsMatch & LIST6) > 0 ) ){
            if (putRowsPrint6Count < 20 || 
                (((putRowsPrint6Count % 25) == 0)) && (putRowsPrint6Count < 200)) {
          
              System.out.flush();
               System.out.printf("EM.putrow6Start rn=" + rn + ", " + resS[rn][0] + ", row=" + row + 
                      ", isset=" + (myUnset ? myCumUnset ? "CumUnset" : "unset" : "isSet") + 
                      ", lock#" + d + ", list" + ((haveListsMatch & list0) > 0 ? 0 : (haveListsMatch & list1) > 0 ? 1 : (haveListsMatch & LIST2) > 0 ? 2 : (haveListsMatch & LIST6) > 0 ? 6: "??") + 
                      ", ops=" +  getOpsNames(haveCmdMatch) + 
                      ", depth=" + depth  +  ", valid" + valid + ", ageIx" + ageIx + 
                              ", cum00=" + resI[rn][ICUM][0][0] + 
                              ", rende4=" + rende4 + "," + rendae4 + " putRowsPrint6Count=" + putRowsPrint6Count  + " \n");
            }
        }
                    }
                    if (E.debugPutRowsOut6) { // after last process
                      if( (resS[rn][rDesc].contentEquals("EmergFF")  ) && ( (haveListsMatch & LIST6) > 0 ) ){
            if (putRowsPrint6Count < 20 || 
                (((putRowsPrint6Count % 25) == 0)) && (putRowsPrint6Count < 200)) {       
              System.out.flush();
               System.out.printf("EM.putrow6Start rn=" + rn + ", " + resS[rn][0] + ", row=" + row + 
                      ", isset=" + (myUnset ? myCumUnset ? "CumUnset" : "unset" : "isSet") + 
                      ", lock#" + d + ", list" + ((haveListsMatch & list0) > 0 ? 0 : (haveListsMatch & list1) > 0 ? 1 : (haveListsMatch & LIST2) > 0 ? 2 : (haveListsMatch & LIST6) > 0 ? 6: "??") + 
                      ", ops=" +  getOpsNames(haveCmdMatch) + 
                      ", depth=" + depth  +  ", valid" + valid + ", ageIx" + ageIx + 
                              ", cum00=" + resI[rn][ICUM][0][0] + 
                              ", rende4=" + rende4 + "," + rendae4 + " putRowsPrint6Count=" + putRowsPrint6Count  + " \n");
            } 
            }
          }
                            }// end of match
        
      } // end of loop on doRes locks0-3
      if (E.debugPutRowsOut) {
        if (putRowsPrint9Count < 12) {
          System.out.println("xit rn=" + rn + " row=" + row + ", desc=" + resS[rn][0] + " suffix=" + suffix + " putRowsPrint9Count" + putRowsPrint9Count++);
        }
      }
      return row;
    } catch (java.lang.Error ex) {
      System.out.flush();
      System.err.flush();
      System.err.println("Caught Err cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + andMore());
      System.err.println("rn=" + rn + ", desc=" + resS[rn][0]);

      ex.printStackTrace(System.err);
      System.err.flush();
    } catch (RuntimeException ex) {
      System.out.flush();
      System.err.flush();
      System.err.println(Econ.nowName  + " " + Econ.nowThread + "Caught RuntimeException cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + andMore());
      System.err.println("rn=" + rn + ", desc=" + resS[rn][0]);
      ex.printStackTrace(System.err);
      System.out.flush();
    } catch (Exception ex) {
      flushes();
      System.err.println(Econ.nowName  + " " + Econ.nowThread + "Caught Exception cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + andMore());
      System.err.println("rn=" + rn + ", desc=" + resS[rn][0]);
      System.err.flush();
      ex.printStackTrace(System.err);
      System.err.flush();
    }
    return row;
  }

  /**
   * set values into a row of the table
   *
   * @param table where rows are stored
   * @param resExit reference for the detail string
   * @param rn the index of the value being written
   * @param aop part of the key being offered for the type of line element
   * @param row the row in the table to receive a line
   * @param desc the description line for the start of the line
   * @param ageIx the index to the cur agegroup to use
   * @return the row
   */
  private int putRowInTable(JTable table, String resExt[], int rn, long aop1, int row, String suffix, int ageIx) {
    // String s[] = {"planets ", "ships ", "sum "};
    String ss[] = {"999999.", "ave of the", "P and S", "sums", ">>>>>>>>>>"};
    String ww[] = {"color", "Winner", "999999", "99.0%", ">>>>>>>>>>"};
    long d[] = {getP, getS};
    long dd[] = {getP, getS};
    description = resS[rn][rDesc];
    myRn = rn;
 //   myAop = aop;
    String detail = myDetail = resS[rn][rDetail];
    String dds = description + detail;
    String isPercent = (dds.contains("%") || dds.contains("Percen") || dds.contains("percen")) && ((aop1 & (THISYEARAVE | CURAVE | CUMAVE | THISYEAR | CUR | CUM)) > 0L) ? "%" : "";

    double aVal;
    double sums;
    dFrac.setMaximumFractionDigits(doUnits || doPower > 0 ? 0 : (int) resI[rn][ICUM][CCONTROLD][IFRACS]);
    dd[1] = doSum ? sum : getS; // force D1 request to sum for second round
    detail += ":: " + suffix;
    if (doPower > 0 && ((aop1 & (THISYEARAVE | CURAVE | CUMAVE | THISYEAR | CUR | CUM)) > 0L) ) {
      detail += "/n powers mean the number display should have " + doPower + " zero digits added before the period";

    }
    if (unset && E.debugPutRowsOutUnset && !doSkipUnset ) {
      suffix = ">>>UNSET<<<<";
    } else if (row > 96) {
      suffix = "full";
    }
    if (row < 98) {
      table.setValueAt(description + suffix, row, 0);
      resExt[row] = detail;
   
        if(E.debugPutRowsOut6){
          if (resS[rn][rDesc].contains("Score") ) {
          System.out.println("in gamRes. do Score " + Thread.currentThread().getName() + " .putRowInTable" + (doSum ? " doSum" :  doBoth ? " doBoth" : "")  + ", suffix=" + suffix + " aop1=" + Long.toOctalString(aop1) +  " lStart=" + lStart + " lEnd=" + lEnd + ", valid=" + valid +  (myUnset?" myUnset":"") + (myCumUnset?" myCumUnset":""));
          }
      //   System.out.println("in EM.gameRes." + toString() + ".putRowInTable" + dFrac.format(values[0][0]) + " " + dFrac.format(values[0][6]));
        }
      // process values never set, particularly skip adding row if unset
      if ((myCumUnset || myUnset || unset) && (doSkipUnset || didUnset || !E.debugPutRowsOutUnset )) {
        return row;  // do not update row, do not write row
      } // or set a zero row if stat is unset
      else if (myCumUnset && myUnset && doZeroUnset) {
        for (int mm = 1; mm < 11; mm++) {
          table.setValueAt("0.0", row, mm);
        }
        row++;
        didUnset = true;
      } // or set row values to ---
      else if (myCumUnset && myUnset) {
        for (int mm = 1; mm < 11; mm++) {
          table.setValueAt("---", row, mm);
        }
        row++;
        didUnset = true;
      } else {
        if (resS[rn][rDesc].contains("Score") && row == 0) {
          int i = E.S;
          // String ww[] = {"color","Winner","999999","99.0%",">>>>>>>>>>" }; `
          //  Set second half of the row in ships 5
          if(isWinner)table.setBackground(new java.awt.Color(E.backGroundColors[winner]));
          aVal = sums = 0;
          double myWin = 0;
          for (int m = 0; m < E.lclans; m++) {
            table.setValueAt(((aVal = getRowEntryValue(rn, dd[(int) i] + aop1, m, ageIx)) < -93456789.0 ? "------" : mf(aVal)), row, (int) (i * E.lclans + m + 1));
            sums += aVal;
            if (m == winner && isWinner) {
              table.setValueAt(aVal, row, 3);
              myWin = aVal;
            }
          }
          if (isWinner) {
            table.setValueAt(E.groupNames[winner], row, 1);
            table.setValueAt("Winner", row, 2);
            table.setValueAt("   by   ",row,3);
            table.setValueAt((mf(aVal * 100 / sums * .2)) + "%", row, 4); // qubbwe
            table.setValueAt(">>>>>>>>>>", row, 5);
          } else { // winner pending
            table.setValueAt(mf(curDif) + "%", row, 1);
            table.setValueAt("yet till", row, 2);
            table.setValueAt("a Winner", row, 3);
            table.setValueAt("for the", row, 4);
            table.setValueAt("game", row, 5);
          }

          table.setValueAt(description + suffix + powers, row, 0);
          resExt[row] = detail + powers;
          row++;
        } else if (doSum) {
          sums = 0.;
          for (long i : d) {
            if (doSum && (i == getP)) { // only do the first half of sums
              for (int mm = 1; mm < E.lclans; mm++) {
                //String ss[] = {">>>This", "row sums", "planets", "and ships", ">>>>>>>>>>"};
                table.setValueAt(ss[mm], row, mm + 1);
              }
            } else { // second half of sum
              for (int m = 0; m < E.lclans; m++) {
                table.setValueAt(((sums += aVal = getRowEntryValue(rn, dd[(int) i] + aop1, m, ageIx)) < -93456789.0 ? "------" : dFrac.format(aVal) + isPercent), row, (int) i * E.lclans + m + 1);
              }
              table.setValueAt(dFrac.format(sums * .2) + isPercent, row, 1);
            }
          }
          table.setValueAt(description + suffix + powers, row, 0);
          resExt[row] = detail + powers;
          row++;
        }
        if (doBoth) {
          boolean didSum = doSum;
          doSum = false; // prevent getRowEntryValue from suming values
          table.setValueAt(description + suffix + " both", row, 0);
          resExt[row] = detail;
          for (long ij : d) {
            for (int m = 0; m < E.lclans; m++) {
              table.setValueAt((((aVal = getRowEntryValue(rn, (int) dd[(int) ij] + aop1, m, ageIx)) < -93456789.
                      ? aVal < -94567895.
                              ? "--------"
                              : "-----"
                      : mf(aVal) + isPercent)),
                      row, (int) ij * E.lclans + m + 1);
            }
          }
          table.setValueAt(description + suffix + powers, row, 0);
          resExt[row] = detail + powers;
          row++;
          doSum = didSum;  // restore doSum
        }
      }
    }
    
    return row;
  }

  /**
   * return value of one value, value designated by opr
   *
   * @param rn the index of the statistic being listed
   * @param opr key flags for pors
   * @param dClan the dClan to be processed
   * @param ageIx index of age in request
   * @return value a value sumsV, sumsI or sumsV/sumsI
   */
  private double getRowEntryValue(int rn, long opr, int dClan, int ageIx) {
    double sum = 0.;
    double cnts = 0;
    // doSum doBoth global variables
    String ops = "unset";
    String doingSum = (doSum ? "doingSum" : "notDoingSum");

    int pors = (int) (opr & psmask);
    int ii = 0;
    try {
      doPower = resI[rn][ICUM][CCONTROLD][IPOWER];
      powers = "";
      if (((cum | cumUnitAve | cumUnits) & opr) > 0) {
        if ((cum & opr) > 0) {
          ops = "cum";
          //              sum                                            both
          sum = doSum ? resV[rn][ICUM][0][dClan] + resV[rn][ICUM][1][dClan] : resV[rn][ICUM][pors][dClan];
        } else if ((cumUnits & opr) > 0) {
          sum = doSum ? resI[rn][ICUM][0][dClan] + resI[rn][ICUM][1][dClan] : resI[rn][ICUM][pors][dClan];
          ops = "cumUnits";

          if (false && doPower > 0) {
            sum = sum / Math.pow(10., doPower);
            powers = " *10**" + doPower + " ";
          }
        } else if ((cumUnitAve & opr) > 0) {
          sum = (doSum ? resV[rn][ICUM][0][dClan] + resV[rn][ICUM][1][dClan] : resV[rn][ICUM][pors][dClan]) / (doSum ? resI[rn][ICUM][0][dClan] + resI[rn][ICUM][1][dClan] : resI[rn][ICUM][pors][dClan]);
          ops = "cumUnitAve";
        }
        if (doPower > 0) {
          sum = sum / Math.pow(10., doPower);
          powers = " *10**" + doPower + " ";
        }
        return sum;
      } else if (!(resI[rn][ICUR0 + (int) lStart] == null
              || resI[rn][ICUR0 + (int) lStart][CCONTROLD] == null
              || resI[rn][ICUR0 + (int) lStart][CCONTROLD][IPOWER] < 0)) {  // cur current values for up to 6 successive years
        sum = 0.;
        cnts = 0.;
        if (resI[rn] == null) {
          cErr(">>>>>>in Getd1 null at resI[" + rn + "]=" + description);
          return -88888888.;
        }
        if (resI[rn][ICUR0 + (int) lStart] != null && resI[rn][ICUR0 + (int) lStart][CCONTROLD] != null && resI[rn][ICUR0 + (int) lStart][CCONTROLD][IPOWER] < 0) {
          cErr(">>>>>>in Getd1 null at resI[rn][ICUR0 + (int) lStart=" + lStart + "] resI[" + rn + "]=" + description);
          return -9999998888.;
        }
        long resia[][] = resI[rn][ICUR0 + (int) lStart];
        if (resia[CCONTROLD] == null) {
          cErr(">>>>>>in Getd1 null at resI[rn][ICUR0 + (int) lStart=" + lStart + "][CCONTROLD] resI[" + rn + "]=" + description);
          return -99999999.;
        }
        long resib[] = resia[CCONTROLD];
        long dp = resib[IPOWER];
        doPower = resI[rn][ICUR0 + (int) lStart][CCONTROLD][IPOWER];
        powers = "";
        lEnd = Math.min(lEnd, valid); //restrict year 0 to 1 year,1 prior reset
        ops = "some Cur";
        for (ii = (int) lStart + ageIx * 7; ii < lEnd + ageIx * CURMAXDEPTH; ii++) {
          if (resV[rn] == null) {
            if (pors == 0 && dClan == 0) { //complain only once
              cErr(">>>>>>in Getd1 null at resV[" + rn + "] desc=" + resS[rn][0]);
            }
            return -98.;
          } else if (resV[rn][ICUR0 + ii] == null) {
            if (pors == 0 && dClan == 0) {
              cErr(">>>>>>in Getd1 null at resV[" + rn + "] [cur0+ " + ii + "] desc=" + resS[rn][0]);
            }
            return -97.;
          } else if (resV[rn][ICUR0 + ii][0] == null) {
            cErr(">>>>>>in Getd1 null at resV[" + rn + "] [cur0 + " + ii + "][0] desc=" + resS[rn][0]);
            return -98.78;
          }
          if ((rn == 96 || rn == 0 || rn == 2 || rn == 3) && pors == 0 && dClan == 0) {
            //  System.out.println(">>>>>>in Getd1  at resV[" + rn + "][rcur0 +" + ii + "]" + ", desc=" + resS[rn][0]);
          }
          sum += doSum ? resV[rn][ICUR0 + ii][0][dClan] + resV[rn][ICUR0 + ii][1][dClan] : resV[rn][ICUR0 + ii][pors][dClan];
          cnts += doSum ? resI[rn][ICUR0 + ii][0][dClan] + resI[rn][ICUR0 + ii][1][dClan] : resI[rn][ICUR0 + ii][pors][dClan];
        }
        if (((thisYr | cur) & opr) > 0 && sum != 0.0) {   // values/yrs
          ops = "cur";
          sum = sum / (lEnd - lStart);
          if (doPower > 0) {
            sum = sum / Math.pow(10., doPower);
            powers = " *10**" + doPower + " ";
          }
          return sum;
        } else if ((((thisYearUnitAve | curUnitAve) & opr) > 0) && (sum != 0.0) && (cnts > 0)) {
          ops = "curUnitAve";
          sum = sum / cnts; // values / units  for whatever years
          if (doPower > 0) {
            sum = sum / Math.pow(10., doPower);
            powers = " *10**" + doPower + " ";
          }
          return sum;
        } else if (((curUnits | thisYearUnits) & opr) > 0) {
          ops = "curUnits";
          return cnts;
        } else if (((thisYr | curUnits | thisYearUnitAve | curUnitAve) & opr) > 0) {
          ops = "sum or cnts 0";
          if (doPower > 0) {
            sum = sum / Math.pow(10., doPower);
            powers = " *10**" + doPower + " ";
          }
          // return sum > 0 ? sum : -7788.66; // sum or cnts 0
          return sum;
        }

      }
      return -93456789.;  // if a strange option
    } catch (Exception ex) {
      System.err.println(Econ.nowName  + " " + Econ.nowThread + "Caught Exception cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + andMore());
      System.err.println("rn=" + rn + ", desc=" + resS[rn][0]);
      ex.printStackTrace(System.err);
      return -94567895.;
    }
  }
  
  int rememberYear=-1;
  int rememberList = -1;
  int rememberRow = -1;
  int detailYear = -1;
  String rememberDetail= "xx";
  
  /** write to the keep file if rememberFromPage is set
   * rememberFromPage is cleared at each new page
   * @param list
   * @param table
   * @param theRow
   * @param theDetail 
   */
  void doRememberValues(int list,JTable table, int theRow,String theDetail){
    try {
  String ll = " ";
  if(rememberFromPage) {
    if(year != rememberYear || !st.statsRememberWhy.getText().matches(prevRememberCmt)){ // need another year comment page
      rememberYear = year;
      prevRememberCmt = st.statsRememberWhy.getText() + ""; // force a copy
      String dateString = MYDATEFORMAT.format(new Date());
  //    String rOut = "New Game " + dateString + "\r\n";
      ll = "renenberYear" + year +" version " + st.versionText  +  " " + dateString + " " + prevRememberCmt + "\r\n";
      bKeep.write(ll, 0, ll.length());
    }
    if(!theDetail.matches(rememberDetail) || year != detailYear){
      rememberDetail = theDetail + "";
    ll = "title " + theDetail + "\r\n"; // the detail description of the remember
    bKeep.write(ll,0,ll.length());
    ll = "remember ";
    // add the string value of the row entries
    for(int i=0;i < 11; i++){ ll += table.getValueAt(theRow,i).toString() + " ";}
    ll += "\r\n";
    bKeep.write(ll,0,ll.length());
    keepBuffered = true;
    }//rememberDetail
    
  }//keep from page
    } catch (Exception ex) {
      flushes();
      System.err.println(Econ.nowName  + " " + Econ.nowThread + "Ignore this error " + new Date().toString() + " " + (new Date().getTime() - startTime) + " cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + ", addlErr=" + eM.addlErr + addMore());
      flushes();

    }
}//doWriteKeepVals

  /**
   * set the columns of a title row, and end the row as needed commands colAdd
   * colAddBrk colBrkAdd colHlfAddBrk colHlfBrkAdd colBrkEnd String nextCol =
   * ""; static int colCnt = 0, colMax = 10, lTit = 41, lCol = 15; static int
   * lMax=0,lCnt=0;
   *
   * @param cmd command for processing the chr
   * @param table table for the columns
   * @param chr
   * @param resExit
   * @param row
   * @return
   */
  int savT(int cmd, JTable table, String chr, String[] resExit, int row) {
    switch (cmd) {
      case colAdd:
        nextCol += chr;
        break;

      default:
    }
    return row;
  }

  /**
   * loop through the description for row titles
   *
   * @calls savT static int colCnt = 0, colMax = 10, lTit = 41, lCol = 15;
   * static int lMax=0,lCnt=0;
   */
  int getT(JTable table, int rn, String[] resExit, int row) {
    String ctlChr = "&", ctlBrk = "-", ctlHBrk = "_", numBrk = ",.$", oBrk = ":;<>";
    String nextCol = "", chunk1 = "", chunk2 = "";
    int lNextCol = 0, lChunk1 = 0, lChunk2 = 0, lNextNChunk1 = 0;
    colCnt = 0;
    int maxNextCol = 41, lCols = 15;
    boolean ctlFnd = false;
    boolean inNum = false;
    int lMax = colCnt == 0 ? lTit : lCol;
    String next = "";
    String description = resS[rn][rDesc];
    int maxM = description.length();
    for (int m = 0; m < maxM; m++) {
      next = description.substring(m, m + 1);
      if (ctlFnd) {
        if (ctlBrk.contains(next)) {

        }
      } else if (inNum) {
      } else if (ctlChr.contains(next)) {
        ctlFnd = true;
      } else {
      }
    }
    return row++;
  }

  /**
   * get the detail string
   *
   * @return
   */
  //   public String getDetail() {
  //    return detail;
  //  }
  /**
   * enum to hold and sum result values, flags indicate at which rounds a given
   * enum will produce a row in the table. (title, 5 clans)
   *
   *
   */
  private static boolean game = true;
  private static boolean clan = false;
  private static boolean printVal = true;

  /**
   * Throw a MyErrException error, list the message sent with the error
   *
   * @param form format of the error message
   * @param oargs arguments of the error
   */
  void aErr(String form, Object... oargs) {
    // StringBuffer m = "Exception";
    //throw MyTestException()
    Object v[] = new Object[21];
    for (int i = 0; i < v.length; i++) {
      v[i] = 0.;
    }
    for (int i = 0; i < oargs.length && i < v.length; i++) {
      v[i] = oargs[i];
    }

    System.out.flush();
    System.err.flush();
    System.err.format("name=" + (curEcon == null ? "" : curEcon.name) + ":" + form + "%n", v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11], v[12], v[13], v[14], v[15], v[16], v[17], v[18], v[19], v[20]);
    System.err.format((curEcon == null ? "" : curEcon.name) + ":" + form + "%n", v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11], v[12], v[13], v[14], v[15], v[16], v[17], v[18], v[19], v[20]);
    String vvv = "".format((curEcon == null ? "" : curEcon.name) + ":" + form + "%n", v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11], v[12], v[13], v[14], v[15], v[16], v[17], v[18], v[19], v[20]);
    new Throwable().printStackTrace(System.err);
    System.err.flush();
    System.out.flush();

    myTestDone = true;
    throw new MyErrException(vvv); // throw as part of enclosing if statment
    //System.exit(5);
    //return 0.;
  }

  /**
   * send a set of error messages, only throw a MyErrException if too many
   * errors
   *
   * @param form format for the messages
   * @param oargs arguments for the format
   */
  void bErr(String form, Object... oargs) {
    // StringBuffer m = "Exception";
    //throw MyTestException()
    Object v[] = new Object[21];
    for (int i = 0; i < v.length; i++) {
      v[i] = 0.;
    }
    for (int i = 0; i < oargs.length && i < v.length; i++) {
      v[i] = oargs[i];
    }

    System.out.flush();
    System.err.flush();
    System.err.format((curEcon == null ? "" : curEcon.name) + ":" + form + "%n", v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11], v[12], v[13], v[14], v[15], v[16], v[17], v[18], v[19], v[20]);
    String vvv = "".format((curEcon == null ? "" : curEcon.name) + ":" + form + "%n", v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11], v[12], v[13], v[14], v[15], v[16], v[17], v[18], v[19], v[20]);
    System.out.print(vv);
    new Throwable().printStackTrace(System.err);
    System.err.flush();
    System.out.flush();

    if (++allGameErrCnt >= allGameErrMax || ++gameErrCnt >= gameErrMax || ++yearErrCnt > yearErrMax) {
      System.err.print(">>>>> fatal Error " + (allGameErrCnt >= allGameErrMax ? "allGameErr" : gameErrCnt >= gameErrMax ? "gameErr" : yearErrCnt >= yearErrMax ? "yearErr" : "unknown") + " count exceeded=" + "allGameErrCnt=" + allGameErrCnt + ", gameErrCnt=" + gameErrCnt + ", yearErrCnt=" + yearErrCnt + "<<<<<<<<<");
      myTestDone = true;
      throw new MyErrException(vvv); // throw as part of enclosing if statment
    }
    //System.exit(5);
    //return 0.;
  }

  void cErr(String form) {
    // StringBuffer m = "Exception";
    //throw MyTestException()

    System.out.flush();
    System.err.flush();
    System.err.println(form);
    new Throwable().printStackTrace(System.err);
    System.err.flush();
    System.out.flush();

    if (++allGameErrCnt >= allGameErrMax || ++gameErrCnt >= gameErrMax || ++yearErrCnt > yearErrMax) {
      System.err.print(">>>>> fatal Error " + (allGameErrCnt >= allGameErrMax ? "allGameErr" : gameErrCnt >= gameErrMax ? "gameErr" : yearErrCnt >= yearErrMax ? "yearErr" : "unknown") + " count exceeded=" + "allGameErrCnt=" + allGameErrCnt + ", gameErrCnt=" + gameErrCnt + ", yearErrCnt=" + yearErrCnt + "<<<<<<<<<");
      myTestDone = true;
      throw new MyErrException("cErr=" + form); // throw as part of enclosing if statment
    }
    //System.exit(5);
    //return 0.;
  }
  
  /** generate a string with names of Econ, Thread, file.line.method, file.line.method why
   * 
   * @param ec  the Econ of the caller
   * @param why  the message
   * @return filled out message line
   */
  static String here(Econ ec,String why){
    String isHere = "++" + ec.name +"  " + Thread.currentThread().getName();
    StackTraceElement aa = Thread.currentThread().getStackTrace()[2];
    int stLen = Thread.currentThread().getStackTrace().length;
    if(stLen == 2){
      isHere += " " + aa.getFileName() + "." + aa.getLineNumber()  + "." + aa.getMethodName()  + " " + why ;
    } else if(stLen >= 3){
    StackTraceElement ab = Thread.currentThread().getStackTrace()[3];
   // StackTraceElement ac = Thread.currentThread().getStackTrace()[5];
     isHere += " " + aa.getFileName() + "." + aa.getLineNumber()  + "." + aa.getMethodName() + " from " + ab.getFileName()  + "." + ab.getLineNumber() + " " + ab.getMethodName() + " " + why ;
    return isHere;
    }
    return isHere;
  }
  
   /** generate a string with names of Econ, Thread, file.line.method, file.line.method why
   *   Place the string at variable wasHere
   * 
   * @param ec  the Econ of the caller
   * @param why  the message
   * @return filled out message line
   */
  static String isHere1(Econ ec, String why){ return wasHere = here(ec,why);}

   /** generate a string with names of Econ, Thread, file.line.method, file.line.method why
   *   Place the string at variable wasHere2
   * 
   * @param ec  the Econ of the caller
   * @param why  the message
   * @return filled out message line
   */
  static String isHere2(Econ ec, String why){ return wasHere2 = here(ec,why);}
  
   /** generate a string with names of Econ, Thread, file.line.method, file.line.method why
   *   Place the string at variable wasHere
   * 
   * @param ec  the Econ of the caller
   * @param why  the message
   * @return filled out message line
   */
  static String isHere3(Econ ec, String why){ return wasHere3 = here(ec,why);}
  
  /**  write to System.err a message using form with oargs as arguments
   * 
   * @param form  the form of the message
   * @param oargs a set of up to 20 arguments 
   */
  void aMsg(String form, Object... oargs) {
    // StringBuffer m = "Exception";
    //throw MyTestException()
    Object v[] = new Object[21];
    for (int i = 0; i < v.length; i++) {
      v[i] = 0.;
    }
    for (int i = 0; i < oargs.length && i < v.length; i++) {
      v[i] = oargs[i];
    }

    System.out.flush();
    System.err.flush();
    // StringBuffer m = "Exception";
    //throw MyTestException()
    StackTraceElement aa = Thread.currentThread().getStackTrace()[3];
    StackTraceElement ab = Thread.currentThread().getStackTrace()[4];
    StackTraceElement ac = Thread.currentThread().getStackTrace()[5];

    String Fname = aa.getFileName();
    int Fline = aa.getLineNumber();
    String Fname2 = ab.getFileName();
    int Fline2 = ab.getLineNumber();
    String Mname2 = ab.getMethodName();
    String Fname3 = ac.getFileName();
    int Fline3 = ac.getLineNumber();
    String Mname3 = ac.getMethodName();
    String Cname = aa.getClassName();
    String Mname = aa.getMethodName();
    //System.err.format("name=" + (curEcon == null ? "" : curEcon.name) + ":" + form + "%n", v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11], v[12], v[13], v[14], v[15], v[16], v[17], v[18], v[19], v[20]);
    System.err.format((curEcon == null ? "" : curEcon.name) + "." + Mname3 + "." + Mname2 + "." + Mname + ":" + form + "%n", v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11], v[12], v[13], v[14], v[15], v[16], v[17], v[18], v[19], v[20]);

    new Throwable().printStackTrace(System.err);
    System.err.flush();
    System.out.flush();
  }

  /** pending
   * 
   */
  void doResSpecial() {

  }

  /**
   * save the percent of rN2*100./rN1 runs inside doResSpecial before getWinner
   * inside doEndYear The results go into ICUR0 of the result The units for rN1
   * are placed in ICUR0 of the result unless rn2==result rN The ISSET of ICUR0
   * is set in result The results are added to ICUM The units of rN1 are added
   * to ICUM unless rN1 == result rN
   *
   * @param rNPer The doRes result of the percent
   * @param rN1 the first doRes generally earliest value
   * @param rN2 the second doRes generally last value
   */
  void doResVPercent(int rNPer, int rN1, int rN2) {
    if (!(resI[rN1] == null || resI[rN1][ICUR0] == null || resI[rN1][ICUR0][CCONTROLD] == null || resI[rN1][ICUR0][CCONTROLD][ISSET] != 1 || resI[rN2] == null || resI[rN2][ICUR0] == null || resI[rN2][ICUR0][CCONTROLD] == null || resI[rN2][ICUR0][CCONTROLD][ISSET] != 1)) {
      for (int m = 0; m < 2; m++) {
        for (int n = 0; n < 5; n++) {
          resV[rNPer][ICUM][m][n] += resV[rNPer][ICUR0][m][n] = (resV[rN2][ICUR0][m][n] - resV[rN1][ICUR0][m][n]) * 100. / (resV[rN1][ICUR0][m][n] + .00001);
          if (rNPer != rN1) {
            resI[rNPer][ICUM][m][n] += resI[rNPer][ICUR0][m][n] = resI[rN1][ICUR0][m][n];
          }//if
        }//n
      }//m
      resI[rNPer][ICUR0][CCONTROLD][ISSET] = 1;
    }
  }

  /**
   * save the percent increase of (rN2 -rN1)*100./rN1 runs inside doResSpecial
   * before getWinner inside doEndYear The results go into ICUR0 of the result
   * The units for rN2 are placed in ICUR0 of the result unless rn2==result rN
   * The ISSET of ICUR0 is set The results are added to ICUM The units of rN1
   * are added to ICUM unless rN1 == result rN
   *
   * @param rNPer The doRes result of the incr percent
   * @param rN1 the first doRes generally earliest value
   * @param rN2 the second doRes generally last value
   */
  void doResVIncrPercent(int rNPer, int rN1, int rN2) {
    if (!(resI[rN1] == null || resI[rN1][ICUR0] == null || resI[rN1][ICUR0][CCONTROLD] == null || resI[rN1][ICUR0][CCONTROLD][ISSET] != 1 || resI[rN2] == null || resI[rN2][ICUR0] == null || resI[rN2][ICUR0][CCONTROLD] == null || resI[rN2][ICUR0][CCONTROLD][ISSET] != 1)) {
      if (!(resI[rN1] == null || resI[rN1][ICUR0] == null || resI[rN1][ICUR0][CCONTROLD] == null || resI[rN1][ICUR0][CCONTROLD][ISSET] != 1 || resI[rN2] == null || resI[rN2][ICUR0] == null || resI[rN2][ICUR0][CCONTROLD] == null || resI[rN2][ICUR0][CCONTROLD][ISSET] != 1)) {
        for (int m = 0; m < 2; m++) {
          for (int n = 0; n < 5; n++) {
            resV[rNPer][ICUM][m][n] += resV[rNPer][ICUR0][m][n] = (resV[rN2][ICUR0][m][n] - resV[rN1][ICUR0][m][n]) * 100. / resV[rN1][ICUR0][m][n];
            if (rNPer != rN1) {
              resI[rNPer][ICUM][m][n] += resI[rNPer][ICUR0][m][n] = resI[rN1][ICUR0][m][n];
            }//if
          }//n
        }//m
        resI[rNPer][ICUR0][CCONTROLD][ISSET] = 1;
      }
    }
  }

  /**
   * save the sum of rNAdd = (rN2 + rN1) runs inside doResSpecial before
   * getWinner inside doEndYear The results go into ICUR0 of the result The
   * units for rN2 are placed in ICUR0 of the result unless rn2==result rN The
   * ISSET of ICUR0 is set The results are added to ICUM The units of rN1 are
   * added to ICUM unless rN2 == result rN
   *
   * @param rNAdd The doRes result of the add
   * @param rN1 the first doRes generally earliest value
   * @param rN2 the second doRes generally last value
   */
  void doResAdd(int rNAdd, int rN1, int rN2) {
    if (!(resI[rN1] == null || resI[rN1][ICUR0] == null || resI[rN1][ICUR0][CCONTROLD] == null || resI[rN1][ICUR0][CCONTROLD][ISSET] != 1 || resI[rN2] == null || resI[rN2][ICUR0] == null || resI[rN2][ICUR0][CCONTROLD] == null || resI[rN2][ICUR0][CCONTROLD][ISSET] != 1)) {
      for (int m = 0; m < 2; m++) {
        for (int n = 0; n < 5; n++) {
          resV[rNAdd][ICUM][m][n] += resV[rNAdd][ICUR0][m][n] = (resV[rN2][ICUR0][m][n] + resV[rN1][ICUR0][m][n]);
          if (rNAdd != rN2) {
            resI[rNAdd][ICUM][m][n] += resI[rNAdd][ICUR0][m][n] = resI[rN2][ICUR0][m][n];
          }// if
        }//n
      }//m
      resI[rNAdd][ICUR0][CCONTROLD][ISSET] = 1;
    }
  }

  /**
   * save the difference of rNAdd = (rN2 - rN1) runs inside doResSpecial before
   * getWinner inside doEndYea The results go into ICUR0 of the result The units
   * for rN2 are placed in ICUR0 of the result unless rn2==result rN The ISSET
   * of ICUR0 is set The results are added to ICUM The units of rN1 are added to
   * ICUM unless rN2 == result rN
   *
   * @param rNSub The doRes result of the add
   * @param rN1 the first doRes generally earliest value
   * @param rN2 the second doRes generally last value
   */
  void doResSub(int rNSub, int rN1, int rN2) {
    if (!(resI[rN1] == null || resI[rN1][ICUR0] == null || resI[rN1][ICUR0][CCONTROLD] == null || resI[rN1][ICUR0][CCONTROLD][ISSET] != 1 || resI[rN2] == null || resI[rN2][ICUR0] == null || resI[rN2][ICUR0][CCONTROLD] == null || resI[rN2][ICUR0][CCONTROLD][ISSET] != 1)) {
      for (int m = 0; m < 2; m++) {
        for (int n = 0; n < 5; n++) {
          resV[rNSub][ICUM][m][n] += resV[rNSub][ICUR0][m][n] = (resV[rN2][ICUR0][m][n] - resV[rN1][ICUR0][m][n]);
          if (rN2 != rNSub) {
            resI[rNSub][ICUM][m][n] += resI[rNSub][ICUR0][m][n] = resI[rN2][ICUR0][m][n];
          }// if
        }//n
      }//m
      resI[rNSub][ICUR0][CCONTROLD][ISSET] = 1;
    }
  }

  /**
   * save the division of rN2 by rN1 of rNDivUnits = rN1 / rN2 runs inside
   * doResSpecial before getWinner inside doEndYea The results go into ICUR0 of
   * the result The units for rN2 are placed in ICUR0 of the result unless
   * rn1==result rN The ISSET of ICUR0 is set in result The results are added to
   * ICUM The units of rN1 are added to ICUM unless rN1 == result rN
   *
   * @param rNDivUnits The doRes result of the division
   * @param rN1 the first doRes generally earliest value
   * @param rN2 the second doRes generally last value units
   */
  void doResVDivUnits(int rNDivUnits, int rN1, int rN2) {
    if (!(resI[rN1] == null || resI[rN1][ICUR0] == null || resI[rN1][ICUR0][CCONTROLD] == null || resI[rN1][ICUR0][CCONTROLD][ISSET] != 1 || resI[rN2] == null || resI[rN2][ICUR0] == null || resI[rN2][ICUR0][CCONTROLD] == null || resI[rN2][ICUR0][CCONTROLD][ISSET] != 1)) {
      for (int m = 0; m < 2; m++) {
        for (int n = 0; n < 5; n++) {
          resV[rNDivUnits][ICUM][m][n] += resV[rNDivUnits][ICUR0][m][n] = (resV[rN1][ICUR0][m][n] / (resI[rN2][ICUR0][m][n] + .00001));
          if (rN1 != rNDivUnits) {
            resI[rNDivUnits][ICUM][m][n] += resI[rNDivUnits][ICUR0][m][n] = resI[rN1][ICUR0][m][n];
          }// if
        }//n
      }//m
      resI[rNDivUnits][ICUR0][CCONTROLD][ISSET] = 1;
    }
  }

  /**
   * save the percent of rN2 by rN1 of rNDivUnits = rN1 * 100. / rN2 runs inside
   * doResSpecial before getWinner inside doEndYea The results go into ICUR0 of
   * the result The units for rN2 are placed in ICUR0 of the result unless
   * rn1==result rN The ISSET of ICUR0 is set in result The results are added to
   * ICUM The units of rN1 are added to ICUM unless rN1 == result rN
   *
   * @param rNDivUnits The doRes result of the division
   * @param rN1 the first doRes units generally earliest value
   * @param rN2 the second doRes generally last value units
   */
  void doResIPercemtUnits(int rNDivUnits, int rN1, int rN2) {
    if (!(resI[rN1] == null || resI[rN1][ICUR0] == null || resI[rN1][ICUR0][CCONTROLD] == null || resI[rN1][ICUR0][CCONTROLD][ISSET] != 1 || resI[rN2] == null || resI[rN2][ICUR0] == null || resI[rN2][ICUR0][CCONTROLD] == null || resI[rN2][ICUR0][CCONTROLD][ISSET] != 1)) {
      for (int m = 0; m < 2; m++) {
        for (int n = 0; n < 5; n++) {
          resV[rNDivUnits][ICUM][m][n] += resV[rNDivUnits][ICUR0][m][n] = (resI[rN1][ICUR0][m][n] * 100. / (resI[rN2][ICUR0][m][n] + .00001));
          if (rN1 != rNDivUnits) {
            resI[rNDivUnits][ICUM][m][n] += resI[rNDivUnits][ICUR0][m][n] = resI[rN1][ICUR0][m][n];
          }// if
        }//n
      }//m
      resI[rNDivUnits][ICUR0][CCONTROLD][ISSET] = 1;
    }
  }

  boolean isWinner = false;
  double myScore[] = {400., 400., 400., 400., 400.};
  double myScoreSum = 0.0;
  int isV = 0;
  int isI = 1;
  int isAve = 2; 
  int sValsCnt = -1;
  double difPercent = 0.0;
  double difMult = 0.0;
  double winDif[][] = {{2.000}};
  static double mwinDif[][] = {{0.2, 4.0}, {0.2, 4.0}};
  double curDif = 0.0;

  int getWinner() {
    // initialize curDif,difMult if year < 2
    curDif = year < 2 ? winDif[0][0] : curDif;
    difMult = year < 2 ? 1.0 / winDif[0][0] : difMult;
    for (int n = 0; n < 5; n++) {
      myScore[n] = 400.;  // allow negatives to reduce it
    }
    winner = scoreVals(TRADELASTGAVE, iGiven, ICUM, isI);
    winner = scoreVals(TRADELASTGAVE, wGiven, ICUM, isV);
    winner = scoreVals(TRADENOMINALGAVE, wGiven2, ICUM, isV);
    winner = scoreVals(TRADESTRATLASTGAVE, wGenerous, ICUM, isV);//%given
    winner = scoreVals(LIVEWORTH, wLiveWorthScore, ICUM, isV);
    winner = scoreVals(LIVEWORTH, iLiveWorthScore, ICUR0, isI); 
    winner = scoreVals(WTRADEDINCRMULT, wYearTradeV, ICUR0, isV);
   // winner = scoreVals(WTRADEDINCRMULT, wYearTradeI, ICUR0, isI);
    winner = scoreVals(DIED, iNumberDied, ICUM, isI);
    winner = scoreVals(BOTHCREATE, iBothCreateScore, ICUM, isI);

    // winner = scoreVals(getStatrN("WTRADEDINCRMULT"), wYearTradeI, ICUR0, isI);
    resI[SCORE][ICUR0][CCONTROLD][ISSET] = 1;
    myScoreSum = 0.0;
    for (int n = 0; n < 5; n++) {
      myScoreSum = myScore[n] * 2;
      resV[SCORE][ICUR0][0][n] = myScore[n];
      resV[SCORE][ICUR0][1][n] = myScore[n];
      resI[SCORE][ICUR0][1][n] = 1;
      resI[SCORE][ICUR0][0][n] = 1;  
    }
    double dif = 0.0, wDif = 0.0;
    // dif = max - myScore.ave
    // wDif = dif/myScore.av - curDif
    // isWinner if wDif > 0.0 that is max  is enough greater than ave
    int prevWinner = winner;
    boolean badbad = winner < 0 || winner > 4 ? true: false; // set legal winner
    winner = badbad? 0:winner; // set winner legal
    difPercent = (wDif = (dif = (myScore[winner] - myScoreSum * .1) / myScoreSum * .1) - curDif) * 100.0;
    if (wDif > 0.0) {  // wDif is frac (max-ave)/ave - curDif
      isWinner = true;
    } else {  // wDif < 0.0 reduce curDif
      // reduce curDif the amt needed max >= ave
      curDif += (wDif * difMult);
      // reduce curDif each year but don't go negative
      // redices amt max/ave >  curDif
      curDif -= (curDif - curDif * difMult) > 0.0 ? curDif * difMult: 0.0;
    }
    System.out.println("getWinner " + resS[SCORE][0] + " =" + mf(resV[SCORE][ICUR0][0][0]) + " , " + mf(resV[SCORE][ICUR0][0][1]) + " , " + mf(resV[SCORE][ICUR0][0][2]) + "," + mf(resV[SCORE][ICUR0][0][3]) + "," + mf(resV[SCORE][ICUR0][0][4]) + " : " + mf(resV[SCORE][ICUR0][1][0]) + " , " + mf(resV[SCORE][ICUR0][1][1]) + " , " + mf(resV[SCORE][ICUR0][1][2]) + "," + mf(resV[SCORE][ICUR0][1][3]) + "," + mf(resV[SCORE][ICUR0][1][4]) + ", myScore=" + mf(myScore[0]) + " , " + mf(myScore[1]) + " , " + mf(myScore[2]) + "," + mf(myScore[3]) + "," + mf(myScore[4]) + ", winner=" + winner + (badbad ? " illegal winner=" + prevWinner: ""));
    return winner;
  }

  /**
   * score a doRes into myScore and set winner
   *
   * @param dRn count of the stats
   * @param mult pointer to multiplier entry, neg mult, neg to score
   * @param cumCur ICUM or ICUR0
   * @param isN isV =0,isI=1,isAve = 2
   * @return 0-4 number of winner
   */
  int scoreVals(int dRn, double[][] mult, int cumCur, int isN) {
    double inScore[] = {0., 0., 0., 0., 0.};
    int winner = -1; 
    double mm = mult[0][0];  // bottom value of limits
    double min = 9999999999.E+20; //reduce to mins
    double max = -min; // increase to max
    double max1 = max - 1.;// an almost max amount
    int ii=0;
    int m = 0, n = 0;
    for (m = 0; m < 2; m++) {
      for (n = 0; n < 5; n++) {
        
        if (m == 0) {
          // initialize with planet values for each clan
          if (isN == isI) {
            inScore[n] = resI[dRn][cumCur][m][n] * mm;
          } else if(isN == isV){
            inScore[n] = resV[dRn][cumCur][m][n] * mm;
          } else { // isAve
            ii = (int)resI[dRn][cumCur][m][n];
            inScore[n] = mm * (ii == 0? 1.: resV[dRn][cumCur][m][n]/ii);
          }
        } else { // Than add ship values per clan
          if (isN == isI) {
            inScore[n] += resI[dRn][cumCur][m][n] * mm;
          } else if(isN == isV){
            inScore[n] += resV[dRn][cumCur][m][n] * mm;
           } else {
            ii = (int)resI[dRn][cumCur][m][n];
            inScore[n] += mm * (ii == 0? 1.: resV[dRn][cumCur][m][n]/ii);
          }
          // than find the min value of all clans
          if (inScore[n] < min) {
            min = inScore[n];  // update min for all clanSums
          }
          // find the max value of all clans
          if (inScore[n] > max) {
            max1 = max; // update the previous max
            max = inScore[n];  // update max for all clanSums
          }
        }
      }//n
    }//m
    double smax = -9999999999.E+10;// ?? reset max
    // increase myScore for each clan by clanSum/min
    // neg inScore reduces myScore
    // neg min reduces myScore
    // neg min neg inScore increase myScore
    // plus min plus inScore increases myScore
    // plus inScore large min small increase
    // plus large inScore small plus min large increase
    // plus large inScore small neg min large decrease
    for (n = 0; n < 5; n++) {
      myScore[n] += (inScore[n] + .00001) / (min * .3 + .00001); //update to max greater than max1
      if (myScore[n] > smax) {
        smax = myScore[n];
        winner = n; // current winner
      }
    }
    System.out.println("scoreVals " + resS[dRn][0] + ", mult=" + mm + ", inScore=" + mf(inScore[0]) + " , " + mf(inScore[1]) + " , " + mf(inScore[2]) + "," + mf(inScore[3]) + "," + mf(inScore[4]) + ", myScore=" + mf(myScore[0]) + " , " + mf(myScore[1]) + " , " + mf(myScore[2]) + "," + mf(myScore[3]) + "," + mf(myScore[4]) + ", winner=" + winner);
    return winner;
  }

  int scoreVals(String rName, double[][] mult, int cumCur, int isN) {
    return scoreVals(getStatrN(rName), mult, cumCur, isN);
  }

  int getCurrentShipUnits(String dname) {
    return getCurCumPorsClanUnitSum(getStatrN(dname), ICUR0, E.S, E.S + 1, 0, 5);
  }

  int getCurrentShipUnits(int rN) {
    return getCurCumPorsClanUnitSum(rN, ICUR0, E.S, E.S + 1, 0, 5);
  }

  int getCurrentPlanetUnits(String dname) {
    return getCurCumPorsClanUnitSum(getStatrN(dname), ICUR0, E.P, E.P + 1, 0, 5);
  }

  int getCurrentPlanetUnits(int rN) {
    return getCurCumPorsClanUnitSum(rN, ICUR0, E.P, E.S + 1, 0, 5);
  }

  int getCurrentSumUnits(String dname) {
    return getCurCumPorsClanUnitSum(getStatrN(dname), ICUR0, E.P, E.P + 1, 0, 5);
  }

  int getCurrentSumUnits(int dname) {
    return getCurCumPorsClanUnitSum(dname, ICUR0, E.P, E.S + 1, 0, 5);
  }

  int getCurrentClanUnits(int rN, int cl) {
    return getCurCumPorsClanUnitSum(rN, ICUR0, E.P, E.S + 1, cl, cl + 1);
  }

  int getCurrentClanPlanets(String rN, int cl) {
    return getCurCumPorsClanUnitSum(getStatrN(rN), ICUR0, E.P, E.P + 1, cl, cl + 1);
  }

  /**
   * get the rN for the stats name
   *
   * @param dname string stats name to be found
   * @return the integer related to that name
   */
  int getStatrN(String dname) {
    addlErr = "setStat dname=" + dname;
    Object o1 = resMap.get(dname);
    if (o1 == null) {
      System.out.printf("setStat a cannot find \"%s\" \n", dname);
      o1 = resMap.get("missing name");
    }
    return (int) o1;
  }

  /**
   * get a units sum from the stats database, it could be for the current year
   * or for the cunulative sum of all the years
   *
   * @param rN the index into the stats database<br>
   * use getStatrN(name) as rN to select by string name
   * @param curCum either ICUM or ICUR0 from EM
   * @param porsStart 0:start with planets, 1 start with ships
   * @param porsEnd 1. 0:1 sum just planets, 2. 0:2 sum planets & ships
   * @param clanStart 0-4 sum of the clan to start with
   * @param clanEnd 1-5 end of clan sum, 0:1,1:2 etc. 1 clan<br>
   * 0:5 sum all of the clans
   * @return the sum of units as filtered by the selectors
   */
  int getCurCumPorsClanUnitSum(int rn, int curCum, int porsStart, int porsEnd, int clanStart, int clanEnd) {
    String anErr = "";
    if(E.PAINTDISPLAYOUT){
        anErr = (curCum < ICUM ? "curCum=" + curCum + " is less than ICUM"
            : curCum > ICUR0 ? "curCum " + curCum + " is greater than ICUR0"
                    : porsStart < 0 ? "porsStart=" + porsStart + " is less than 0 E.P"
                            : porsStart > 1 ? "porsStart=" + porsStart + " is greater than 1 E.S"
                                    : porsEnd < 1 ? "porsEnd=" + porsEnd + " is less than 1 E.P+1"
                                            : porsEnd > 2 ? "porsEnd=" + porsEnd + " is greater than 2 E.S+1"
                                                    : clanStart < 0 ? "clanStart=" + clanStart + " is less than 0"
                                                            : clanStart > 4 ? "clanStart=" + clanStart + " is greater than 4"
                                                                    : clanEnd < 1 ? "clanEnd=" + clanEnd + " is less than 1"
                                                                            : clanEnd > 5 ? "clanEnd=" + clanEnd + " is greater than 5"
                                                                                    : "");
    if (anErr.length() > 0) {
      throw (new MyErr("ERR: " + anErr + " stats:" + rn + ":" + resS[rn][0]
      ));
    }
    }
    int mPors = 0, nClan = 0, iSum = 0;
    for (mPors = porsStart; mPors < porsEnd; mPors++) {
      for (nClan = clanStart; nClan < clanEnd; nClan++) {
        iSum += (int) (long) resI[rn][curCum][mPors][nClan];
      }
    }
    return iSum;
  }


/**
   * get a val sum from the stats database, it could be for the current year
   * or for the cunulative sum of all the years
   *
   * @param rN the index into the stats database<br>
   * use getStatrN(name) as rN to select by string name
   * @param curCum either ICUM or ICUR0 from EM
   * @param porsStart 0:start with planets, 1 start with ships
   * @param porsEnd 1. 0:1 sum just planets, 2. 0:2 sum planets & ships
   * @param clanStart 0-4 sum of the clan to start with
   * @param clanEnd 1-5 end of clan sum, 0:1,1:2 etc. 1 clan<br>
   * 0:5 sum all of the clans
   * @return the sum of values as filtered by the selectors
   */
  double getCurCumPorsClanValSum(int rn, int curCum, int porsStart, int porsEnd, int clanStart, int clanEnd) {
    String anErr = "";
    if(E.PAINTDISPLAYOUT){
    anErr = (curCum < ICUM ? "curCum=" + curCum + " is less than ICUM"
            : curCum > ICUR0 ? "curCum " + curCum + " is greater than ICUR0"
                    : porsStart < 0 ? "porsStart=" + porsStart + " is less than 0 E.P"
                            : porsStart > 1 ? "porsStart=" + porsStart + " is greater than 1 E.S"
                                    : porsEnd < 1 ? "porsEnd=" + porsEnd + " is less than 1 E.P+1"
                                            : porsEnd > 2 ? "porsEnd=" + porsEnd + " is greater than 2 E.S+1"
                                                    : clanStart < 0 ? "clanStart=" + clanStart + " is less than 0"
                                                            : clanStart > 4 ? "clanStart=" + clanStart + " is greater than 4"
                                                                    : clanEnd < 1 ? "clanEnd=" + clanEnd + " is less than 1"
                                                                            : clanEnd > 5 ? "clanEnd=" + clanEnd + " is greater than 5"
                                                                                    : "");
    if (anErr.length() > 0) {
      throw (new MyErr("ERR: " + anErr + " stats#:" + rn + ":" + resS[rn][0]
      ));
    }
    } // if DEBUG
    double vSum=0.;
    int mPors = 0, nClan = 0, iSum = 0;
    for (mPors = porsStart; mPors < porsEnd; mPors++) {
      for (nClan = clanStart; nClan < clanEnd; nClan++) {
        vSum += resV[rn][curCum][mPors][nClan];
      }
    }
    return vSum;
  }
  
  /**
   * get a val min from the stats database, it could be for the current year
   * or for the cunulative mins of all the years
   *
   * @param rN the index into the stats database<br>
   * use getStatrN(name) as rN to select by string name
   * @param curCum either ICUM or ICUR0 from EM
   * @param porsStart 0:start with planets, 1 start with ships
   * @param porsEnd 1. 0:1 sum just planets, 2. 0:2 sum planets & ships
   * @param clanStart 0-4 sum of the clan to start with
   * @param clanEnd 1-5 end of clan sum, 0:1,1:2 etc. 1 clan<br>
   * 0:5 sum all of the clans
   * @return the min of values as filtered by the selectors
   */
  double getCurCumPorsClanValMin(int rn, int curCum, int porsStart, int porsEnd, int clanStart, int clanEnd) {
    String anErr = "";
    if(E.PAINTDISPLAYOUT){
    anErr = (curCum < ICUM ? "curCum=" + curCum + " is less than ICUM"
            : curCum > ICUR0 ? "curCum " + curCum + " is greater than ICUR0"
                    : porsStart < 0 ? "porsStart=" + porsStart + " is less than 0 E.P"
                            : porsStart > 1 ? "porsStart=" + porsStart + " is greater than 1 E.S"
                                    : porsEnd < 1 ? "porsEnd=" + porsEnd + " is less than 1 E.P+1"
                                            : porsEnd > 2 ? "porsEnd=" + porsEnd + " is greater than 2 E.S+1"
                                                    : clanStart < 0 ? "clanStart=" + clanStart + " is less than 0"
                                                            : clanStart > 4 ? "clanStart=" + clanStart + " is greater than 4"
                                                                    : clanEnd < 1 ? "clanEnd=" + clanEnd + " is less than 1"
                                                                            : clanEnd > 5 ? "clanEnd=" + clanEnd + " is greater than 5"
                                                                                    : "");
    if (anErr.length() > 0) {
      throw (new MyErr("ERR: " + anErr + " stats#:" + rn + ":" + resS[rn][0]
      ));
    }
    } // if DEBUG
    double vMin = 999999999999999999.;
    int mPors = 0, nClan = 0, iSum = 0;
    for (mPors = porsStart; mPors < porsEnd; mPors++) {
      for (nClan = clanStart; nClan < clanEnd; nClan++) {
        vMin += resV[rn][curCum][mPors][nClan] < vMin ?  resV[rn][curCum][mPors][nClan] :vMin;
      }
    }
    return vMin;
  }
} // Class EM
