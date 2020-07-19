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

 /*
 * StarTrader.java
 *
 * Created on Dec 15, 2011, 9:51:37 PM
 * the game has up to 5 players, some of which are robots, using preset values.
 * the object of the game is to have something to tinker with, and apply different
 * schemes for growing star planets, and spaceships that carry tradable things.
 * Ships travel between stars with cargo of the 7 resources and guests (colonists)
 * In addition both planets and ships use resources, some of which are not available
 * on the given planet.  Ships must grow by profits in transportation, birth rate is usually
 * lower than death rate so that they must accept colonists as staff.
 *
 * The planets and ships are created with varying difficulty.  Difficulty is overcome by
 * staff creating and using knowledge.  So colonist become engineer which require also
 * faculty and researchers.  Only researchers create knowledge, although ships carry
 * knowledge between planet, and planets can buy knowledge.  Each player has sets of
 * slides to adjust the choices made by stars and ships, players do not make detailed
 * moves, only adjust slides and than run from 1 to 5 years to see how well planets
 * and ships are doing in terms of health, wealth, worth, staff, knowledge.  For example,
 * if population outstrips resources than the health of the planet falls, more things
 * wear out, and more colonists die.  Resources get used up, and only new knowledge can
 * help increase efficiency and thus keep growing resources.
 * Normall
 */
package trade;

// import java.desktop/javax.swing.plaf.synth.SynthGraphicsUtils.paintText;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author albert steiner
 */
public class StarTrader extends javax.swing.JFrame {

  static boolean run1 = false;
  static boolean run2 = false;
  static boolean run5 = false;
  static boolean run10 = false;  // run 10 years of dorun
// There are two contexts

  static protected enum ContextNames {

    PLANET, SHIP, COUNT
  }
  static final protected int P = ContextNames.PLANET.ordinal();
  static final protected int S = ContextNames.SHIP.ordinal();
  static protected String[] contextName = {"planet", "ship"};
  static String stringTemp = "";
  // static int contextV= 0;
  // there are 7 resources and 5 groups
  //static protected int[] logLevel={5,5};
  /**
   * pointers into the hist table initially
   */
  //static protected int[] logStartMVal={0,0};
  /**
   * pointers into the hist table after a display
   */
  //static protected int[] logStartM={0,0};
  static final protected int[] logLengthDisplay = {60, 25, 50};
  static final protected int logRowCount = 50;
  static protected int logSelectedRow = 5;
  static protected Color bg1 = new java.awt.Color(140, 255, 140);

  /**
   * for each row in the display, index to a row in the hist table
   */
  static protected int[] logRowToM;
  /**
   * a pointer to the logHistory table being displayed
   */
  static protected ArrayList<History> logHistoryHist;
  /**
   * a list of the last m(hist table index)
   */
  static protected ArrayList<Integer> logMHist = new ArrayList<Integer>();
  /**
   * pointer to the display table in the log tab
   */
  static protected javax.swing.JTable pLogDisplayTable;
  static protected ArrayList<History> logHist1;
  static protected int[] displayHistoryRowToM;
  static protected ArrayList<Integer> displayHistoryMHist = new ArrayList<Integer>();
  /**
   * the last value for m(the hist table index)
   */
  static protected int[] logLastM = {0, 0};
  /**
   * the direction of movement through the hist table
   */
  static protected int[] logDirection = {1, 1};
  static final protected String[] groupNames = {"red", "orange", "yellow", "green", "blue"};
  static final protected int red = 0;
  static final protected int orange = 1;
  static final protected int yellow = 2;
  static final protected int green = 3;
  static final protected int blue = 4;
  static protected E eE;
  static EM eM;
  int nn = 0; // needed for event processors
  static protected ArrayList<String> planetsDisplay;
  static protected ArrayList<String> starsDisplay;

  static public int namesListRow = 0;
  /**
   * StarTrader E  EM contain the used set of stats descriptors 
   *
   */
  static final public String statsButton0Tip = "0: Current Game Worths";
  static final public String statsButton1Tip = "1: Favors and trade effects";
  static final public String statsButton2Tip = "2: Catastrophies, deaths, randoms, forwardfund";
  static final public String statsButton17Tip = "3: Deaths";
  static final public String statsButton18Tip = "4: Trades";
  static final public String statsButton19Tip = "5: Creates";
  static final public String statsButton20Tip = "6: ForwardFund";
  static final public String statsButton9Tip = "7: Resource, staff, knowledge values";
  static final public String statsButton10Tip = "8: growth and costs details";
  static final public String statsButton11Tip = "9: Fertility, health and effects";
  static final public String statsButton3Tip = "10: years 0,1,2,3 worth inc, costs, efficiency,knowledge,phe";
  static final public String statsButton4Tip = "11: years 4,5,6,7 worth inc, costs, efficiency,knowledge,phe ";
  static final public String statsButton5Tip = "12: years 8->15 worth inc, costs, efficiency,knowledge,phe ";
  static final public String statsButton6Tip = "13: years 16->31 worth inc, costs, efficiency,knowledge,phe ";
  static final public String statsButton7Tip = "14: years 32+ worth inc, costs, efficiency,knowledge,phe ";
  static final public String statsButton8Tip = "15: swap factors";
  static final public String statsButton12Tip = "16: Swaps years incr skips, redos and dos";
  static final public String statsButton13Tip = "17: Swaps years decr skips, redos and dos";
  static final public String statsButton14Tip = "18: Swaps years xfer skips, redos and dos";
  static final public String statsButton15Tip = "19: Swaps years Forward Fund imbalance or save";
  static final public String statsButton16Tip = "20: Swaps cumulative values";

  static final public String gameTextFieldText = "This is to be filled with descriptions of the field over which the mouse hovers";
  /* 0:worths,1:trade favor,2:random,crisis,deaths,forward,34567 ages,8:swap,9 rcsg bal,10:growth,cost,11:fertility health effect
   */

  static final public String[] statsButtonsTips = {statsButton0Tip, statsButton1Tip, statsButton2Tip, statsButton3Tip, statsButton4Tip, statsButton5Tip, statsButton6Tip, statsButton7Tip, statsButton8Tip, statsButton9Tip, statsButton10Tip, statsButton11Tip, statsButton12Tip, statsButton13Tip, statsButton14Tip, statsButton15Tip, statsButton16Tip, statsButton17Tip, statsButton18Tip, statsButton19Tip, statsButton20Tip, gameTextFieldText};
  static final public String versionText = "     Version 19.02";
  static final public String storyText = "This game is about trading not fighting. Trading is done between planets and Starships which move between planets.  There are 5 clans and a gamemaster, all of which can change some priorities or values for the game.You can choose one of several winning goals, highest worth, highest trades received, highest trades given, highest number of planets and ships etc.  Planets and ships are each economies with 7 sectors.  Each sector has working resources, reserve resources (cargo), working staff and reserve staff (guests).\n\nThere is also knowledge.  As knowledge in a sector increases, the annual cost per unit in each sector decreases and in some sectors new units of resource, staff, and knowledge increase. As in any game, random factors influence many aspects of the game.\n\nPlanets mine resources and grow staff, but deliberately have surpluses in some financial sectors, and famines in some other sectors.  Ships move resources between planets, often trading the resources that are lacking at a given planet in exchange for other resources and staff and knowledge.  But ships generally cannot increase staff or mine/find resources, they must receive significant resources and staff in each trade to grow at a rate that allows them to be productive trade partners.\n\nThe game proceeds either 1 year at a time or 5 years at a time depending on the choice by the gamemaster.  At the end of each 1 year or 5 years, clan masters may look at their statistices and decide to change some clan priorities.  The gamemaster can also make changes, but probably should not.  It is possible in some systems to have multiple games running, each game with different gamemaster priorities.  The initial priorities are set to make the game interesting with possibilities of growth.  Some priority changes by gamemaster or clanmaster may increase growth, but may also decrease growth causing a death of all or most ships and planets.  The balancing of economies is not simple or easy.  It is more possible to crash an economy than to grow it, so don\'t be greedy, make small changes until you understand the game better.\n\n "
  + "Click on the tab labled \"game\" to change game parameters.  Click the first gray radio button master, to change overall game parameters as the \"gamemaster\". "
      + "The next 5 radio buttons are for the 5 clans, \"red\", \"orange\", \"yellow\", \"green\" and \"blue\".  When you click on the parameter name," 
  + " the green field on the right side is set to a description of what that parameter does.  You can then move the slides to change the parameter.  Careful, economies are touchy, and  changes can make stop working, all the planets and ships start dieing.  At the initial difficulty, about 1 in 10 ships or plaets should die each year.  The forward fund taken from the other ships and planets is used to finance new ships or planets.\n\n Each year creates costs of all sectors of both resources and staff.  Costs increase as the game difficulty increases. Costs decrease as the knowledge increases.  Knowledge increases with each years growth, also manuals are studied and some become knowledge each year.  When trades occur, knowledge is also traded between ships and planets as manuals, depending on the higher grade staff of each ship or planet and the amount of knowledge with that economy.  Each of the 7 financial sectors have their own knowledge. their own priority, their own resources and staff, but costs that as each sector uses every sector each year.  Random factors occur with costs, growths and trading.\n\n"
          + "As each year begins some ships and planets encounter catastrophies.  Clanmasters that choose higher random activity encountor more catastrophies.  Catastrophies remove some resources and some staff, but they also reveal additional resources that planets can mine, they can increase resources that ships can find while traveling between planets, they can ships to uncover caches of  manuals for techniques in one or more sectors.  In each case, a trade is very useful to get back resources and/or staff in crippled sectors.\n\nAfter catastrophies ships and planets do trading.  For planets the goal is to even out resources and staff as much as possible, and to gain tech manuals to be turned into knowledge.  For ships the goal is to pick friendly planets which will do reasonable trades, and to try to grow in all resources and staff, whle also getting cargo and guests that can be traded to some other planet, the ship also gets manuals from the planet of new technologies that have not been found elsewhere.  The gamemaster sets the amount of new knowledge that can be turned into manuals for trade.\n\nAfter zero to 15 ships trade with a planet, and ships trade with up to 5 ships at the same planet.\n\nThe next activity is swapping resources to prevent death, reduce costs and to enhance growth.  Units of resource or staff can be moved between working and reserve status at a modest cost.  Moving resorces between sectors has a much higher cost, you can call the move transmuting a resource or repurposing a resource depending on you view of magic and economic theory.  The gamemaster can change the costs of movement.\n\nAfter the swapping the planet dies if some sector does not have enough working staff or resources.  Each year has a requirment for there to be enough buildings to survive winter, enough food production to not starve etc.  The required working units are higher than the cost of units for a given year, and growth is done before taking costs.<p>The stregth of sectors may influence some operations, E.G. a strong defense may reduce the size of catastrophies, a strong, strong lifeneed may aid staff growth and decreas staff deaths, strong transportation may decrease cost of moving staff and resources."
          + "\n\nClanmasters can change priorities that influence how the robots runing each clan ship and clan planet make decisions.  The gamemaster sets overall parameters that can change the nature of the game in some way for all of the clans.\n\nThe window for the game has a number of named tabs.\n\nThe \"game\" tab displays ways for the gamemaster and each clanmaster to change priorities. \n\n Planets and ships make increasing contributions to the forward fund as their worth increases.  At the beginning of each yaar, new planets and ships are created from the forward fund and/or from the game initialization.  As the game reaches the maximum number of plaets and ships, contributions to the forward fund are reduced.  The memory assigned to the game limits the number of planets and ships.\n\nThe \"stats\" tab shows many different values about the position of each clan.  The stats can be used to decide how to change input values at the \"game\" tab.<p>The \"display\" tab will change as the game progresses each year.\n\n" +
      "To start game, go to the game tab, click 1 year or 5 years of activity";

  static int iii = 0;
  static final int STARTING = 0; // start list of constants
  static String sn0 = "Starting";
  static final int CREATING = 1;
  static String sn1 = "Creating";
  static final int FUTUREFUNDCREATE = 2;
  static String sn2 = "CreatingFutureF";
  static final int STARTYR = 3;
  static String sn3 = "StartYear";
  static final int SEARCH = 4;
  static String sn4 = "Search to Trade";
  static final int TRADING = 5;
  static String sn5 = "Trading";
  static final int ENDYR = 6;
  static String sn6 = "EndYear";
  static final int STATS = 7;
  static String sn7 = "Stats";
  static final int SWAPS = 8;
  static String sn8 = "Swaping";
  static final int WAITING = 9;
  static String sn9 = "Waiting for action";
  static final int STOPPED = 10;
  static String sn10 = "Stopped";
  static final int FATALERR = 11;
  static String sn11 = "Fatal Error";
  static String[] stateStringNames = {sn0, sn1, sn2, sn3, sn4, sn5, sn6, sn7, sn8, sn9, sn10, sn11};
  static int stateConst = WAITING;  // constant set to stated
  static int prevState = WAITING;
  static Econ curEc = EM.curEcon;
  static String prevEconName = "nnnnn";
  static String curEconName = "mmmmmm";
  static boolean doStop = false; // set by game or stats stop execution
  static boolean fatalError = false;
  static int stateCnt = 0;
  static int yearsToRun = 0;
  static int econCnt = -5;
  static int sameEconState = 0;
  static int blip = 1000 / 60;  // shortest animation interval 60/ second
  static int blip2 = blip * 2;  //  30/second
  static int blip5 = blip * 5;
  static int blip10 = blip * 10; // 6/second
  static int blip30 = blip * 30; // 2/second
  static public long startTime = (new Date()).getTime();
  static public long startYear = startTime;
  static public long startEconState = startTime;
  static public long totMem, freeMem, usedMem;
  static public String stNi = "stNi";
  static public String ecNi = "ecNi";
  static public String asNi = "asNi";
  static public String cfNi = "cfNi";  // CashFlow method
  static public String cfNi2 = "cfNi2"; // within CashFlow method

  // public Star(int group,int contextV,String xname, int xpos, int ypos, int wealth, int colonists, double difficulty,
  //         int xknowledge,PriorityInput[] pri)
  // static protected Econ env  = new Econ("able",0,0,5,5,5,1000,1000,100,50.,
  //   "struct",30.,"energy",25.,"life",5.,"defense",10.,"colonist",25.);
  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    logButtonGroup1or2 = new javax.swing.ButtonGroup();
    initButtonGroupPorS = new javax.swing.ButtonGroup();
    clanButtonGroupActiveClan = new javax.swing.ButtonGroup();
    statsButtonGroupReportNumber = new javax.swing.ButtonGroup();
    statsButtonGroupClans = new javax.swing.ButtonGroup();
    logBGactions = new javax.swing.ButtonGroup();
    gameButtonGroup = new javax.swing.ButtonGroup();
    gameButtonUp = new java.awt.Button();
    controlPanels = new javax.swing.JTabbedPane();
    story = new javax.swing.JPanel();
    storyVersionField = new javax.swing.JTextField();
    storyTextPane = new javax.swing.JScrollPane();
    storyTextField1 = new javax.swing.JTextArea();
    game = new javax.swing.JPanel();
    gameMaster = new javax.swing.JRadioButton();
    clanRed = new javax.swing.JRadioButton();
    clanOrange = new javax.swing.JRadioButton();
    clanYellow = new javax.swing.JRadioButton();
    clanGreen = new javax.swing.JRadioButton();
    clanBlue = new javax.swing.JRadioButton();
    gameXtraPanel1 = new javax.swing.JPanel();
    gameCtlButtonRun1Year1 = new javax.swing.JButton();
    gameCtlButtonRun5Years1 = new javax.swing.JButton();
    gameCtlButtonRun1Yr2 = new javax.swing.JButton();
    gameToLabelPlanet = new javax.swing.JLabel();
    gameTopLabelShip = new javax.swing.JLabel();
    gameTopRightFill = new javax.swing.JTextField();
    gamePanel0 = new javax.swing.JPanel();
    gameTextField0 = new javax.swing.JTextField();
    gameSliderP0 = new javax.swing.JSlider();
    jSeparator1 = new javax.swing.JSeparator();
    gameSliderS0 = new javax.swing.JSlider();
    gameButtonUp1 = new java.awt.Button();
    gamePanel1 = new javax.swing.JPanel();
    gameTextField1 = new javax.swing.JTextField();
    gameSliderP1 = new javax.swing.JSlider();
    jSeparator2 = new javax.swing.JSeparator();
    gameSliderS1 = new javax.swing.JSlider();
    gamePanel2 = new javax.swing.JPanel();
    gameTextField2 = new javax.swing.JTextField();
    gameSliderP2 = new javax.swing.JSlider();
    jSeparator3 = new javax.swing.JSeparator();
    gameSliderS2 = new javax.swing.JSlider();
    gamePanel3 = new javax.swing.JPanel();
    gameTextField3 = new javax.swing.JTextField();
    gameSliderP3 = new javax.swing.JSlider();
    jSeparator4 = new javax.swing.JSeparator();
    gameSliderS3 = new javax.swing.JSlider();
    gamePanel4 = new javax.swing.JPanel();
    gameTextField4 = new javax.swing.JTextField();
    gameSliderP4 = new javax.swing.JSlider();
    jSeparator5 = new javax.swing.JSeparator();
    gameSliderS4 = new javax.swing.JSlider();
    gamePanel5 = new javax.swing.JPanel();
    gameTextField5 = new javax.swing.JTextField();
    gameSliderP5 = new javax.swing.JSlider();
    jSeparator11 = new javax.swing.JSeparator();
    gameSliderS5 = new javax.swing.JSlider();
    gamePanel6 = new javax.swing.JPanel();
    gameTextField6 = new javax.swing.JTextField();
    gameSliderP6 = new javax.swing.JSlider();
    jSeparator13 = new javax.swing.JSeparator();
    gameSliderS6 = new javax.swing.JSlider();
    gamePanel7 = new javax.swing.JPanel();
    gameTextField7 = new javax.swing.JTextField();
    gameSliderP7 = new javax.swing.JSlider();
    jSeparator14 = new javax.swing.JSeparator();
    gameSliderS7 = new javax.swing.JSlider();
    gamePanel8 = new javax.swing.JPanel();
    gameTextField8 = new javax.swing.JTextField();
    gameSliderP8 = new javax.swing.JSlider();
    jSeparator15 = new javax.swing.JSeparator();
    gameSliderS8 = new javax.swing.JSlider();
    gamePanel9 = new javax.swing.JPanel();
    gameTextField9 = new javax.swing.JTextField();
    gameSliderP9 = new javax.swing.JSlider();
    jSeparator16 = new javax.swing.JSeparator();
    gameSliderS9 = new javax.swing.JSlider();
    gameButtonDown = new java.awt.Button();
    gamePanelRearPanel = new javax.swing.JPanel();
    gamePanelBottomPanel = new javax.swing.JPanel();
    gameTextPane = new javax.swing.JScrollPane();
    gameTextField = new javax.swing.JTextArea();
    javax.swing.JPanel log = new javax.swing.JPanel();
    logTableScrollPanel = new javax.swing.JScrollPane();
    logDisplayTable = new javax.swing.JTable();
    logDlevel2 = new javax.swing.JLabel();
    LogDlen1Slider = new javax.swing.JSlider();
    logDlen1 = new javax.swing.JLabel();
    logDLevel1Slider = new javax.swing.JSlider();
    SpinnerModel startModel1 = new SpinnerNumberModel(10,
      0, //min
      2000000, //max
      10);
    logM1Spinner = new javax.swing.JSpinner(startModel1);
    LogDLen2Slider = new javax.swing.JSlider();
    logDLevel2Slider = new javax.swing.JSlider();
    logDlen2 = new javax.swing.JLabel();
    logDlevel1 = new javax.swing.JLabel();
    SpinnerModel startModel2 = new SpinnerNumberModel(10,
      0, //min
      2000000, //max
      10);
    logM2Spinner = new javax.swing.JSpinner(startModel2);
    logNamesScrollPanel = new javax.swing.JScrollPane();
    namesList = new DefaultListModel();
    logEnvirnNamesList = new javax.swing.JList(namesList);
    Start1Name = new javax.swing.JLabel();
    Start2Name = new javax.swing.JLabel();
    logRadioButtonStart1 = new javax.swing.JRadioButton();
    logRadioButtonStart2 = new javax.swing.JRadioButton();
    logActionJump = new javax.swing.JRadioButton();
    logActionAdd = new javax.swing.JRadioButton();
    logActionDel = new javax.swing.JRadioButton();
    clan = new javax.swing.JPanel();
    clanTextPane = new javax.swing.JScrollPane();
    clanTextField = new javax.swing.JTextArea();
    clanPanel0 = new javax.swing.JPanel();
    clanTextField0 = new javax.swing.JTextField();
    gameLabelP5 = new javax.swing.JLabel();
    clanSliderP0 = new javax.swing.JSlider();
    jSeparator6 = new javax.swing.JSeparator();
    gameLabelS5 = new javax.swing.JLabel();
    clanSliderS0 = new javax.swing.JSlider();
    clanPanel1 = new javax.swing.JPanel();
    clanTextField1 = new javax.swing.JTextField();
    gameLabelP6 = new javax.swing.JLabel();
    clanSliderP1 = new javax.swing.JSlider();
    jSeparator7 = new javax.swing.JSeparator();
    gameLabelS6 = new javax.swing.JLabel();
    clanSliderS1 = new javax.swing.JSlider();
    clanPanel2 = new javax.swing.JPanel();
    clanTextField2 = new javax.swing.JTextField();
    gameLabelP7 = new javax.swing.JLabel();
    clanSliderP2 = new javax.swing.JSlider();
    jSeparator8 = new javax.swing.JSeparator();
    gameLabelS7 = new javax.swing.JLabel();
    clanSliderS2 = new javax.swing.JSlider();
    clanPanel3 = new javax.swing.JPanel();
    clanTextField3 = new javax.swing.JTextField();
    clanLabelP3 = new javax.swing.JLabel();
    clanSliderP3 = new javax.swing.JSlider();
    jSeparator9 = new javax.swing.JSeparator();
    gameLabelS8 = new javax.swing.JLabel();
    clanSliderS3 = new javax.swing.JSlider();
    clanPanel4 = new javax.swing.JPanel();
    clanTextField4 = new javax.swing.JTextField();
    clanLabelP4 = new javax.swing.JLabel();
    clanSliderP4 = new javax.swing.JSlider();
    jSeparator10 = new javax.swing.JSeparator();
    clanLabelS4 = new javax.swing.JLabel();
    clanSliderS4 = new javax.swing.JSlider();
    stats = new javax.swing.JPanel();
    statsScrollPanel = new javax.swing.JScrollPane();
    statsTable1 = new javax.swing.JTable();
    statsButton0 = new javax.swing.JRadioButton();
    statsButton1 = new javax.swing.JRadioButton();
    statsButton2 = new javax.swing.JRadioButton();
    statsButton3 = new javax.swing.JRadioButton();
    statsButton4 = new javax.swing.JRadioButton();
    statsButton5 = new javax.swing.JRadioButton();
    statsButton6 = new javax.swing.JRadioButton();
    statsButton7 = new javax.swing.JRadioButton();
    statsButton8 = new javax.swing.JRadioButton();
    statsButton9 = new javax.swing.JRadioButton();
    statsButton10 = new javax.swing.JRadioButton();
    statsCtlButtonRun1Yr = new javax.swing.JButton();
    statsField = new javax.swing.JTextField();
    statsButton11 = new javax.swing.JRadioButton();
    statsCtlButtonRun5Yr = new javax.swing.JButton();
    statsButton12 = new javax.swing.JRadioButton();
    statsButton13 = new javax.swing.JRadioButton();
    statsButton14 = new javax.swing.JRadioButton();
    statsButton15 = new javax.swing.JRadioButton();
    statsButton16 = new javax.swing.JRadioButton();
    statsButton17 = new javax.swing.JRadioButton();
    statsButton18 = new javax.swing.JRadioButton();
    statsButton19 = new javax.swing.JRadioButton();
    statsButton20 = new javax.swing.JRadioButton();
    statsField2 = new javax.swing.JTextField();
    display = new javax.swing.JPanel();
    displayPanel0 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    displayPanel0Text = new javax.swing.JTextArea();
    displayPanel0Text1 = new javax.swing.JTextField();
    displayPanel1 = new javax.swing.JPanel();
    displayPanel1SinceYearStart = new javax.swing.JTextField();
    displayPanel1EconName = new javax.swing.JTextField();
    displayPanel1Operation = new javax.swing.JTextField();
    displayPanel2 = new javax.swing.JPanel();
    displayPanel2EconName = new javax.swing.JTextField();
    displayPanel2Operation = new javax.swing.JTextField();
    displayPanel2SinceYearStart = new javax.swing.JTextField();

    gameButtonUp.setLabel("up");
    gameButtonUp.setMaximumSize(new java.awt.Dimension(70, 55));
    gameButtonUp.setMinimumSize(new java.awt.Dimension(30, 45));

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setBounds(new java.awt.Rectangle(0, 0, 1200, 1200));
    setMaximizedBounds(new java.awt.Rectangle(0, 0, 1, 0));
    setMinimumSize(new java.awt.Dimension(800, 600));
    setResizable(false);
    addInputMethodListener(new java.awt.event.InputMethodListener() {
      public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
        formCaretPositionChanged(evt);
      }
      public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
        formInputMethodTextChanged(evt);
      }
    });
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosed(java.awt.event.WindowEvent evt) {
        formWindowClosed(evt);
      }
    });
    getContentPane().setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

    controlPanels.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
    controlPanels.setAutoscrolls(true);
    controlPanels.setDebugGraphicsOptions(javax.swing.DebugGraphics.BUFFERED_OPTION);
    controlPanels.setDoubleBuffered(true);
    controlPanels.setMaximumSize(new java.awt.Dimension(1700, 1400));
    controlPanels.setMinimumSize(new java.awt.Dimension(500, 550));
    controlPanels.setPreferredSize(new java.awt.Dimension(1200, 1250));
    controlPanels.setBackground(bg1);
    controlPanels.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        controlPanelsStateChanged(evt);
      }
    });
    controlPanels.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
      public void propertyChange(java.beans.PropertyChangeEvent evt) {
        controlPanelsPropertyChange(evt);
      }
    });

    story.setAutoscrolls(true);
    story.setMaximumSize(new java.awt.Dimension(1000, 800));
    story.setMinimumSize(new java.awt.Dimension(800, 700));
    story.setName(""); // NOI18N
    story.setPreferredSize(new java.awt.Dimension(800, 700));

    storyVersionField.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
    storyVersionField.setText("jTextField1");
    storyVersionField.setBorder(null);
    storyVersionField.setMaximumSize(new java.awt.Dimension(400, 50));
    storyVersionField.setMinimumSize(new java.awt.Dimension(100, 30));
    storyVersionField.setPreferredSize(new java.awt.Dimension(200, 40));
    story.add(storyVersionField);

    storyTextPane.setAutoscrolls(true);
    storyTextPane.setMaximumSize(new java.awt.Dimension(1200, 1000));
    storyTextPane.setMinimumSize(new java.awt.Dimension(400, 500));
    storyTextPane.setPreferredSize(new java.awt.Dimension(1200, 600));

    storyTextField1.setEditable(false);
    storyTextField1.setColumns(200);
    storyTextField1.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
    storyTextField1.setLineWrap(true);
    storyTextField1.setRows(30);
    storyTextField1.setWrapStyleWord(true);
    storyTextField1.setMargin(new java.awt.Insets(0, 0, 0, 0));
    storyTextField1.setMaximumSize(new java.awt.Dimension(1200, 1000));
    storyTextField1.setMinimumSize(new java.awt.Dimension(600, 400));
    storyTextField1.setPreferredSize(new java.awt.Dimension(900, 800));
    storyTextPane.setViewportView(storyTextField1);
    storyTextField1.getAccessibleContext().setAccessibleParent(storyTextPane);

    story.add(storyTextPane);

    controlPanels.addTab("story", story);

    game.setBackground(new java.awt.Color(255, 255, 255));
    game.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
    game.setAlignmentX(0.0F);
    game.setAlignmentY(0.0F);
    game.setAutoscrolls(true);
    game.setMaximumSize(new java.awt.Dimension(1200, 1200));
    game.setMinimumSize(new java.awt.Dimension(300, 100));
    game.setName("Settings"); // NOI18N
    game.setPreferredSize(new java.awt.Dimension(900, 900));
    game.addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        gameComponentShown(evt);
      }
    });
    game.setLayout(new java.awt.GridBagLayout());

    gameMaster.setBackground(new java.awt.Color(204, 204, 204));
    gameButtonGroup.add(gameMaster);
    gameMaster.setForeground(new java.awt.Color(102, 102, 102));
    gameMaster.setText("master");
    gameMaster.setToolTipText("");
    gameMaster.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    gameMaster.setMaximumSize(new java.awt.Dimension(150, 21));
    gameMaster.setMinimumSize(new java.awt.Dimension(90, 20));
    gameMaster.setPreferredSize(new java.awt.Dimension(125, 21));
    gameMaster.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        gameMasterItemStateChanged(evt);
      }
    });
    gameMaster.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameMasterMouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameMasterMouseExited(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    game.add(gameMaster, gridBagConstraints);

    clanRed.setBackground(new java.awt.Color(255, 153, 153));
    gameButtonGroup.add(clanRed);
    clanRed.setForeground(new java.awt.Color(153, 0, 0));
    clanRed.setText("red");
    clanRed.setMaximumSize(new java.awt.Dimension(70, 50));
    clanRed.setPreferredSize(new java.awt.Dimension(70, 21));
    clanRed.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        clanRedItemStateChanged(evt);
      }
    });
    clanRed.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        clanRedMouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        clanRedMouseExited(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridy = 0;
    game.add(clanRed, gridBagConstraints);

    clanOrange.setBackground(new java.awt.Color(204, 153, 0));
    gameButtonGroup.add(clanOrange);
    clanOrange.setForeground(new java.awt.Color(102, 51, 0));
    clanOrange.setText("orange");
    clanOrange.setMaximumSize(new java.awt.Dimension(120, 45));
    clanOrange.setMinimumSize(new java.awt.Dimension(69, 21));
    clanOrange.setPreferredSize(new java.awt.Dimension(120, 21));
    clanOrange.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        clanOrangeItemStateChanged(evt);
      }
    });
    clanOrange.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        clanOrangeMouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        clanOrangeMouseExited(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridy = 0;
    game.add(clanOrange, gridBagConstraints);

    clanYellow.setBackground(new java.awt.Color(255, 255, 51));
    gameButtonGroup.add(clanYellow);
    clanYellow.setText("yellow");
    clanYellow.setMaximumSize(new java.awt.Dimension(100, 25));
    clanYellow.setPreferredSize(new java.awt.Dimension(100, 23));
    clanYellow.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        clanYellowItemStateChanged(evt);
      }
    });
    clanYellow.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        clanYellowMouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        clanYellowMouseExited(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridy = 0;
    game.add(clanYellow, gridBagConstraints);

    clanGreen.setBackground(new java.awt.Color(0, 255, 0));
    gameButtonGroup.add(clanGreen);
    clanGreen.setText("green");
    clanGreen.setMaximumSize(new java.awt.Dimension(90, 23));
    clanGreen.setPreferredSize(new java.awt.Dimension(90, 23));
    clanGreen.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        clanGreenItemStateChanged(evt);
      }
    });
    clanGreen.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        clanGreenMouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        clanGreenMouseExited(evt);
      }
    });
    game.add(clanGreen, new java.awt.GridBagConstraints());

    clanBlue.setBackground(new java.awt.Color(51, 51, 255));
    gameButtonGroup.add(clanBlue);
    clanBlue.setText("blue");
    clanBlue.setMaximumSize(new java.awt.Dimension(80, 23));
    clanBlue.setPreferredSize(new java.awt.Dimension(80, 23));
    clanBlue.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        clanBlueItemStateChanged(evt);
      }
    });
    clanBlue.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseExited(java.awt.event.MouseEvent evt) {
        clanBlueMouseExited(evt);
      }
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        clanBlueMouseEntered(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    game.add(clanBlue, gridBagConstraints);

    gameXtraPanel1.setBackground(new java.awt.Color(153, 255, 255));
    gameXtraPanel1.setMaximumSize(new java.awt.Dimension(55, 21));
    gameXtraPanel1.setMinimumSize(new java.awt.Dimension(0, 21));
    gameXtraPanel1.setPreferredSize(new java.awt.Dimension(0, 21));

    javax.swing.GroupLayout gameXtraPanel1Layout = new javax.swing.GroupLayout(gameXtraPanel1);
    gameXtraPanel1.setLayout(gameXtraPanel1Layout);
    gameXtraPanel1Layout.setHorizontalGroup(
      gameXtraPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 0, Short.MAX_VALUE)
    );
    gameXtraPanel1Layout.setVerticalGroup(
      gameXtraPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 0, Short.MAX_VALUE)
    );

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    game.add(gameXtraPanel1, gridBagConstraints);

    gameCtlButtonRun1Year1.setText(" 1 yr");
    gameCtlButtonRun1Year1.setAlignmentY(0.0F);
    gameCtlButtonRun1Year1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.black, java.awt.Color.black, null, null));
    gameCtlButtonRun1Year1.setContentAreaFilled(false);
    gameCtlButtonRun1Year1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    gameCtlButtonRun1Year1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    gameCtlButtonRun1Year1.setIconTextGap(0);
    gameCtlButtonRun1Year1.setMargin(new java.awt.Insets(1, 1, 1, 1));
    gameCtlButtonRun1Year1.setMaximumSize(new java.awt.Dimension(100, 25));
    gameCtlButtonRun1Year1.setMinimumSize(new java.awt.Dimension(50, 21));
    gameCtlButtonRun1Year1.setName("  3 Yr"); // NOI18N
    gameCtlButtonRun1Year1.setPreferredSize(new java.awt.Dimension(50, 21));
    gameCtlButtonRun1Year1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
    gameCtlButtonRun1Year1.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
    gameCtlButtonRun1Year1.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        gameCtlButtonRun1Year1MouseClicked(evt);
      }
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameCtlButtonRun1Year1MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameCtlButtonRun1Year1MouseExited(evt);
      }
    });
    gameCtlButtonRun1Year1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        gameCtlButtonRun1Year1ActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    game.add(gameCtlButtonRun1Year1, gridBagConstraints);

    gameCtlButtonRun5Years1.setText(" 5 yr");
    gameCtlButtonRun5Years1.setAlignmentY(0.0F);
    gameCtlButtonRun5Years1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.red, java.awt.Color.black, null, null));
    gameCtlButtonRun5Years1.setContentAreaFilled(false);
    gameCtlButtonRun5Years1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    gameCtlButtonRun5Years1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    gameCtlButtonRun5Years1.setMargin(new java.awt.Insets(0, 3, 0, 3));
    gameCtlButtonRun5Years1.setMaximumSize(new java.awt.Dimension(100, 25));
    gameCtlButtonRun5Years1.setMinimumSize(new java.awt.Dimension(40, 22));
    gameCtlButtonRun5Years1.setPreferredSize(new java.awt.Dimension(65, 22));
    gameCtlButtonRun5Years1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
    gameCtlButtonRun5Years1.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        gameCtlButtonRun5Years1MouseClicked(evt);
      }
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameCtlButtonRun5Years1MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameCtlButtonRun5Years1MouseExited(evt);
      }
    });
    game.add(gameCtlButtonRun5Years1, new java.awt.GridBagConstraints());

    gameCtlButtonRun1Yr2.setText(" abc");
    gameCtlButtonRun1Yr2.setBorder(new javax.swing.border.MatteBorder(null));
    gameCtlButtonRun1Yr2.setContentAreaFilled(false);
    gameCtlButtonRun1Yr2.setDisplayedMnemonicIndex(2);
    gameCtlButtonRun1Yr2.setMargin(new java.awt.Insets(2, 0, 2, 0));
    gameCtlButtonRun1Yr2.setMaximumSize(new java.awt.Dimension(100, 25));
    gameCtlButtonRun1Yr2.setMinimumSize(new java.awt.Dimension(40, 22));
    gameCtlButtonRun1Yr2.setName("1 M"); // NOI18N
    gameCtlButtonRun1Yr2.setPreferredSize(new java.awt.Dimension(40, 22));
    gameCtlButtonRun1Yr2.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        gameCtlButtonRun1Yr2MouseClicked(evt);
      }
    });
    gameCtlButtonRun1Yr2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        gameCtlButtonRun1Yr2ActionPerformed(evt);
      }
    });
    game.add(gameCtlButtonRun1Yr2, new java.awt.GridBagConstraints());

    gameToLabelPlanet.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
    gameToLabelPlanet.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    gameToLabelPlanet.setText("Planet");
    gameToLabelPlanet.setMaximumSize(new java.awt.Dimension(500, 40));
    gameToLabelPlanet.setMinimumSize(new java.awt.Dimension(300, 20));
    gameToLabelPlanet.setPreferredSize(new java.awt.Dimension(440, 21));
    game.add(gameToLabelPlanet, new java.awt.GridBagConstraints());

    gameTopLabelShip.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
    gameTopLabelShip.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    gameTopLabelShip.setText("ship");
    gameTopLabelShip.setMaximumSize(new java.awt.Dimension(500, 40));
    gameTopLabelShip.setMinimumSize(new java.awt.Dimension(300, 15));
    gameTopLabelShip.setPreferredSize(new java.awt.Dimension(350, 25));
    game.add(gameTopLabelShip, new java.awt.GridBagConstraints());

    gameTopRightFill.setEditable(false);
    gameTopRightFill.setBackground(new java.awt.Color(255, 255, 153));
    gameTopRightFill.setHorizontalAlignment(javax.swing.JTextField.LEFT);
    gameTopRightFill.setAlignmentX(0.0F);
    gameTopRightFill.setAlignmentY(0.0F);
    gameTopRightFill.setMaximumSize(new java.awt.Dimension(600, 25));
    gameTopRightFill.setMinimumSize(new java.awt.Dimension(60, 20));
    gameTopRightFill.setName("gameTopMt"); // NOI18N
    gameTopRightFill.setPreferredSize(new java.awt.Dimension(200, 20));
    gameTopRightFill.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        gameTopRightFillActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 0.5;
    game.add(gameTopRightFill, gridBagConstraints);

    gamePanel0.setAlignmentX(0.1F);
    gamePanel0.setAlignmentY(0.1F);
    gamePanel0.setMaximumSize(new java.awt.Dimension(800, 65));
    gamePanel0.setMinimumSize(new java.awt.Dimension(700, 45));
    gamePanel0.setPreferredSize(new java.awt.Dimension(700, 55));
    gamePanel0.setRequestFocusEnabled(false);
    gamePanel0.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gamePanel0MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gamePanel0MouseExited(evt);
      }
    });
    gamePanel0.setLayout(new javax.swing.BoxLayout(gamePanel0, javax.swing.BoxLayout.LINE_AXIS));

    gameTextField0.setEditable(false);
    gameTextField0.setText("tb set");
    gameTextField0.setMaximumSize(new java.awt.Dimension(300, 45));
    gameTextField0.setMinimumSize(new java.awt.Dimension(150, 35));
    gameTextField0.setPreferredSize(new java.awt.Dimension(200, 35));
    gameTextField0.setRequestFocusEnabled(false);
    gameTextField0.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameTextField0MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameTextField0MouseExited(evt);
      }
    });
    gamePanel0.add(gameTextField0);

    gameSliderP0.setMajorTickSpacing(10);
    gameSliderP0.setMinorTickSpacing(5);
    gameSliderP0.setPaintLabels(true);
    gameSliderP0.setPaintTicks(true);
    gameSliderP0.setSnapToTicks(true);
    gameSliderP0.setToolTipText("Slider1");
    gameSliderP0.setMaximumSize(new java.awt.Dimension(400, 55));
    gameSliderP0.setMinimumSize(new java.awt.Dimension(150, 35));
    gameSliderP0.setName("Slider1"); // NOI18N
    gameSliderP0.setPreferredSize(new java.awt.Dimension(300, 35));
    gameSliderP0.setValueIsAdjusting(true);
    gameSliderP0.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderP0MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderP0MouseExited(evt);
      }
    });
    gamePanel0.add(gameSliderP0);
    gameSliderP0.getAccessibleContext().setAccessibleName("Slider1");

    jSeparator1.setMaximumSize(new java.awt.Dimension(50, 40));
    jSeparator1.setMinimumSize(new java.awt.Dimension(20, 30));
    jSeparator1.setPreferredSize(new java.awt.Dimension(20, 40));
    gamePanel0.add(jSeparator1);

    gameSliderS0.setMajorTickSpacing(10);
    gameSliderS0.setMinorTickSpacing(5);
    gameSliderS0.setPaintLabels(true);
    gameSliderS0.setPaintTicks(true);
    gameSliderS0.setSnapToTicks(true);
    gameSliderS0.setToolTipText("hello1");
    gameSliderS0.setMaximumSize(new java.awt.Dimension(450, 45));
    gameSliderS0.setMinimumSize(new java.awt.Dimension(150, 45));
    gameSliderS0.setOpaque(false);
    gameSliderS0.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderS0.setValueIsAdjusting(true);
    gameSliderS0.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderS0MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderS0MouseExited(evt);
      }
    });
    gamePanel0.add(gameSliderS0);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 23;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    game.add(gamePanel0, gridBagConstraints);

    gameButtonUp1.setActionCommand("up");
    gameButtonUp1.setLabel("up");
    gameButtonUp1.setMaximumSize(new java.awt.Dimension(60, 55));
    gameButtonUp1.setMinimumSize(new java.awt.Dimension(30, 45));
    gameButtonUp1.setPreferredSize(new java.awt.Dimension(50, 55));
    gameButtonUp1.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        gameButtonUp1MouseClicked(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    game.add(gameButtonUp1, gridBagConstraints);

    gamePanel1.setAlignmentX(0.1F);
    gamePanel1.setAlignmentY(0.1F);
    gamePanel1.setMaximumSize(new java.awt.Dimension(800, 65));
    gamePanel1.setMinimumSize(new java.awt.Dimension(100, 35));
    gamePanel1.setPreferredSize(new java.awt.Dimension(700, 55));
    gamePanel1.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gamePanel1MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gamePanel1MouseExited(evt);
      }
    });
    gamePanel1.setLayout(new javax.swing.BoxLayout(gamePanel1, javax.swing.BoxLayout.LINE_AXIS));

    gameTextField1.setEditable(false);
    gameTextField1.setHorizontalAlignment(javax.swing.JTextField.LEFT);
    gameTextField1.setText("tb set");
    gameTextField1.setMargin(new java.awt.Insets(0, 0, 0, 0));
    gameTextField1.setMaximumSize(new java.awt.Dimension(300, 45));
    gameTextField1.setMinimumSize(new java.awt.Dimension(150, 35));
    gameTextField1.setPreferredSize(new java.awt.Dimension(200, 35));
    gameTextField1.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameTextField1MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameTextField1MouseExited(evt);
      }
    });
    gameTextField1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        gameTextField1ActionPerformed(evt);
      }
    });
    gamePanel1.add(gameTextField1);

    gameSliderP1.setMajorTickSpacing(10);
    gameSliderP1.setMinorTickSpacing(5);
    gameSliderP1.setPaintLabels(true);
    gameSliderP1.setPaintTicks(true);
    gameSliderP1.setSnapToTicks(true);
    gameSliderP1.setToolTipText("Slider1");
    gameSliderP1.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderP1.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderP1.setName("Slider1"); // NOI18N
    gameSliderP1.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderP1.setValueIsAdjusting(true);
    gameSliderP1.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderP1MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderP1MouseExited(evt);
      }
    });
    gamePanel1.add(gameSliderP1);

    jSeparator2.setMaximumSize(new java.awt.Dimension(50, 40));
    jSeparator2.setMinimumSize(new java.awt.Dimension(20, 30));
    jSeparator2.setPreferredSize(new java.awt.Dimension(20, 40));
    gamePanel1.add(jSeparator2);

    gameSliderS1.setMajorTickSpacing(10);
    gameSliderS1.setMinorTickSpacing(5);
    gameSliderS1.setPaintLabels(true);
    gameSliderS1.setPaintTicks(true);
    gameSliderS1.setSnapToTicks(true);
    gameSliderS1.setToolTipText("hello1");
    gameSliderS1.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderS1.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderS1.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderS1.setValueIsAdjusting(true);
    gameSliderS1.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderS1MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderS1MouseExited(evt);
      }
    });
    gamePanel1.add(gameSliderS1);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 23;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    game.add(gamePanel1, gridBagConstraints);

    gamePanel2.setMaximumSize(new java.awt.Dimension(800, 65));
    gamePanel2.setMinimumSize(new java.awt.Dimension(100, 45));
    gamePanel2.setPreferredSize(new java.awt.Dimension(750, 55));
    gamePanel2.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gamePanel2MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gamePanel2MouseExited(evt);
      }
    });
    gamePanel2.setLayout(new javax.swing.BoxLayout(gamePanel2, javax.swing.BoxLayout.LINE_AXIS));

    gameTextField2.setEditable(false);
    gameTextField2.setText("tb set");
    gameTextField2.setMaximumSize(new java.awt.Dimension(300, 45));
    gameTextField2.setMinimumSize(new java.awt.Dimension(150, 35));
    gameTextField2.setPreferredSize(new java.awt.Dimension(200, 35));
    gameTextField2.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameTextField2MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameTextField2MouseExited(evt);
      }
    });
    gamePanel2.add(gameTextField2);

    gameSliderP2.setMajorTickSpacing(10);
    gameSliderP2.setMinorTickSpacing(5);
    gameSliderP2.setPaintLabels(true);
    gameSliderP2.setPaintTicks(true);
    gameSliderP2.setSnapToTicks(true);
    gameSliderP2.setToolTipText("Slider1");
    gameSliderP2.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderP2.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderP2.setName("Slider1"); // NOI18N
    gameSliderP2.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderP2.setValueIsAdjusting(true);
    gameSliderP2.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderP2MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderP2MouseExited(evt);
      }
    });
    gamePanel2.add(gameSliderP2);

    jSeparator3.setMaximumSize(new java.awt.Dimension(50, 40));
    jSeparator3.setMinimumSize(new java.awt.Dimension(20, 30));
    jSeparator3.setPreferredSize(new java.awt.Dimension(20, 40));
    gamePanel2.add(jSeparator3);

    gameSliderS2.setMajorTickSpacing(10);
    gameSliderS2.setMinorTickSpacing(5);
    gameSliderS2.setPaintLabels(true);
    gameSliderS2.setPaintTicks(true);
    gameSliderS2.setSnapToTicks(true);
    gameSliderS2.setToolTipText("hello1");
    gameSliderS2.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderS2.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderS2.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderS2.setValueIsAdjusting(true);
    gameSliderS2.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderS2MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderS2MouseExited(evt);
      }
    });
    gamePanel2.add(gameSliderS2);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = 23;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    game.add(gamePanel2, gridBagConstraints);

    gamePanel3.setMaximumSize(new java.awt.Dimension(600, 65));
    gamePanel3.setMinimumSize(new java.awt.Dimension(100, 45));
    gamePanel3.setPreferredSize(new java.awt.Dimension(150, 55));
    gamePanel3.setLayout(new javax.swing.BoxLayout(gamePanel3, javax.swing.BoxLayout.LINE_AXIS));

    gameTextField3.setEditable(false);
    gameTextField3.setText("tb set");
    gameTextField3.setMaximumSize(new java.awt.Dimension(300, 45));
    gameTextField3.setMinimumSize(new java.awt.Dimension(150, 35));
    gameTextField3.setPreferredSize(new java.awt.Dimension(200, 35));
    gameTextField3.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameTextField3MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameTextField3MouseExited(evt);
      }
    });
    gamePanel3.add(gameTextField3);

    gameSliderP3.setMajorTickSpacing(10);
    gameSliderP3.setMinorTickSpacing(5);
    gameSliderP3.setPaintLabels(true);
    gameSliderP3.setPaintTicks(true);
    gameSliderP3.setSnapToTicks(true);
    gameSliderP3.setToolTipText("Slider1");
    gameSliderP3.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderP3.setMinimumSize(new java.awt.Dimension(150, 45));
    gameSliderP3.setName("Slider1"); // NOI18N
    gameSliderP3.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderP3.setValueIsAdjusting(true);
    gameSliderP3.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderP3MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderP3MouseExited(evt);
      }
    });
    gamePanel3.add(gameSliderP3);

    jSeparator4.setMaximumSize(new java.awt.Dimension(50, 40));
    jSeparator4.setMinimumSize(new java.awt.Dimension(20, 30));
    jSeparator4.setPreferredSize(new java.awt.Dimension(20, 40));
    gamePanel3.add(jSeparator4);

    gameSliderS3.setMajorTickSpacing(10);
    gameSliderS3.setMinorTickSpacing(5);
    gameSliderS3.setPaintLabels(true);
    gameSliderS3.setPaintTicks(true);
    gameSliderS3.setSnapToTicks(true);
    gameSliderS3.setToolTipText("hello1");
    gameSliderS3.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderS3.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderS3.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderS3.setValueIsAdjusting(true);
    gameSliderS3.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderS3MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderS3MouseExited(evt);
      }
    });
    gamePanel3.add(gameSliderS3);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.gridwidth = 23;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    game.add(gamePanel3, gridBagConstraints);

    gamePanel4.setMaximumSize(new java.awt.Dimension(600, 65));
    gamePanel4.setMinimumSize(new java.awt.Dimension(100, 45));
    gamePanel4.setPreferredSize(new java.awt.Dimension(150, 55));
    gamePanel4.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gamePanel4MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gamePanel4MouseExited(evt);
      }
    });
    gamePanel4.setLayout(new javax.swing.BoxLayout(gamePanel4, javax.swing.BoxLayout.LINE_AXIS));

    gameTextField4.setEditable(false);
    gameTextField4.setText("tb set");
    gameTextField4.setMaximumSize(new java.awt.Dimension(300, 45));
    gameTextField4.setMinimumSize(new java.awt.Dimension(150, 35));
    gameTextField4.setPreferredSize(new java.awt.Dimension(200, 35));
    gameTextField4.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameTextField4MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameTextField4MouseExited(evt);
      }
    });
    gamePanel4.add(gameTextField4);

    gameSliderP4.setMajorTickSpacing(10);
    gameSliderP4.setMinorTickSpacing(5);
    gameSliderP4.setPaintLabels(true);
    gameSliderP4.setPaintTicks(true);
    gameSliderP4.setSnapToTicks(true);
    gameSliderP4.setToolTipText("Slider1");
    gameSliderP4.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderP4.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderP4.setName("Slider1"); // NOI18N
    gameSliderP4.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderP4.setValueIsAdjusting(true);
    gameSliderP4.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderP4MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderP4MouseExited(evt);
      }
    });
    gamePanel4.add(gameSliderP4);

    jSeparator5.setMaximumSize(new java.awt.Dimension(50, 40));
    jSeparator5.setMinimumSize(new java.awt.Dimension(20, 30));
    jSeparator5.setPreferredSize(new java.awt.Dimension(20, 40));
    gamePanel4.add(jSeparator5);

    gameSliderS4.setMajorTickSpacing(10);
    gameSliderS4.setMinorTickSpacing(5);
    gameSliderS4.setPaintLabels(true);
    gameSliderS4.setPaintTicks(true);
    gameSliderS4.setSnapToTicks(true);
    gameSliderS4.setToolTipText("hello1");
    gameSliderS4.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderS4.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderS4.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderS4.setValueIsAdjusting(true);
    gameSliderS4.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderS4MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderS4MouseExited(evt);
      }
    });
    gamePanel4.add(gameSliderS4);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.gridwidth = 23;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    game.add(gamePanel4, gridBagConstraints);

    gamePanel5.setMaximumSize(new java.awt.Dimension(600, 65));
    gamePanel5.setMinimumSize(new java.awt.Dimension(100, 45));
    gamePanel5.setPreferredSize(new java.awt.Dimension(150, 55));
    gamePanel5.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gamePanel5MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gamePanel5MouseExited(evt);
      }
    });
    gamePanel5.setLayout(new javax.swing.BoxLayout(gamePanel5, javax.swing.BoxLayout.LINE_AXIS));

    gameTextField5.setEditable(false);
    gameTextField5.setText("tb set");
    gameTextField5.setMaximumSize(new java.awt.Dimension(300, 45));
    gameTextField5.setMinimumSize(new java.awt.Dimension(150, 35));
    gameTextField5.setPreferredSize(new java.awt.Dimension(200, 35));
    gameTextField5.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameTextField5MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameTextField5MouseExited(evt);
      }
    });
    gamePanel5.add(gameTextField5);

    gameSliderP5.setMajorTickSpacing(10);
    gameSliderP5.setMinorTickSpacing(5);
    gameSliderP5.setPaintLabels(true);
    gameSliderP5.setPaintTicks(true);
    gameSliderP5.setSnapToTicks(true);
    gameSliderP5.setToolTipText("Slider1");
    gameSliderP5.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderP5.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderP5.setName("Slider1"); // NOI18N
    gameSliderP5.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderP5.setValueIsAdjusting(true);
    gameSliderP5.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderP5MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderP5MouseExited(evt);
      }
    });
    gamePanel5.add(gameSliderP5);

    jSeparator11.setMaximumSize(new java.awt.Dimension(50, 40));
    jSeparator11.setMinimumSize(new java.awt.Dimension(20, 30));
    jSeparator11.setPreferredSize(new java.awt.Dimension(20, 40));
    gamePanel5.add(jSeparator11);

    gameSliderS5.setMajorTickSpacing(10);
    gameSliderS5.setMinorTickSpacing(5);
    gameSliderS5.setPaintLabels(true);
    gameSliderS5.setPaintTicks(true);
    gameSliderS5.setSnapToTicks(true);
    gameSliderS5.setToolTipText("hello1");
    gameSliderS5.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderS5.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderS5.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderS5.setValueIsAdjusting(true);
    gameSliderS5.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderS5MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderS5MouseExited(evt);
      }
    });
    gamePanel5.add(gameSliderS5);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridwidth = 23;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    game.add(gamePanel5, gridBagConstraints);

    gamePanel6.setMaximumSize(new java.awt.Dimension(600, 65));
    gamePanel6.setMinimumSize(new java.awt.Dimension(100, 45));
    gamePanel6.setPreferredSize(new java.awt.Dimension(150, 55));
    gamePanel6.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gamePanel6MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gamePanel6MouseExited(evt);
      }
    });
    gamePanel6.setLayout(new javax.swing.BoxLayout(gamePanel6, javax.swing.BoxLayout.LINE_AXIS));

    gameTextField6.setEditable(false);
    gameTextField6.setText("tb set");
    gameTextField6.setMaximumSize(new java.awt.Dimension(300, 45));
    gameTextField6.setMinimumSize(new java.awt.Dimension(150, 35));
    gameTextField6.setPreferredSize(new java.awt.Dimension(200, 35));
    gameTextField6.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameTextField6MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameTextField6MouseExited(evt);
      }
    });
    gamePanel6.add(gameTextField6);

    gameSliderP6.setMajorTickSpacing(10);
    gameSliderP6.setMinorTickSpacing(5);
    gameSliderP6.setPaintLabels(true);
    gameSliderP6.setPaintTicks(true);
    gameSliderP6.setSnapToTicks(true);
    gameSliderP6.setToolTipText("Slider1");
    gameSliderP6.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderP6.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderP6.setName("Slider1"); // NOI18N
    gameSliderP6.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderP6.setValueIsAdjusting(true);
    gameSliderP6.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderP6MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderP6MouseExited(evt);
      }
    });
    gamePanel6.add(gameSliderP6);

    jSeparator13.setMaximumSize(new java.awt.Dimension(50, 40));
    jSeparator13.setMinimumSize(new java.awt.Dimension(20, 30));
    jSeparator13.setPreferredSize(new java.awt.Dimension(20, 40));
    gamePanel6.add(jSeparator13);

    gameSliderS6.setMajorTickSpacing(10);
    gameSliderS6.setMinorTickSpacing(5);
    gameSliderS6.setPaintLabels(true);
    gameSliderS6.setPaintTicks(true);
    gameSliderS6.setSnapToTicks(true);
    gameSliderS6.setToolTipText("hello1");
    gameSliderS6.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderS6.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderS6.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderS6.setValueIsAdjusting(true);
    gameSliderS6.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderS6MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderS6MouseExited(evt);
      }
    });
    gamePanel6.add(gameSliderS6);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridwidth = 23;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    game.add(gamePanel6, gridBagConstraints);

    gamePanel7.setMaximumSize(new java.awt.Dimension(600, 65));
    gamePanel7.setMinimumSize(new java.awt.Dimension(100, 45));
    gamePanel7.setPreferredSize(new java.awt.Dimension(150, 55));
    gamePanel7.setLayout(new javax.swing.BoxLayout(gamePanel7, javax.swing.BoxLayout.LINE_AXIS));

    gameTextField7.setEditable(false);
    gameTextField7.setText("tb set");
    gameTextField7.setMaximumSize(new java.awt.Dimension(300, 45));
    gameTextField7.setMinimumSize(new java.awt.Dimension(150, 35));
    gameTextField7.setPreferredSize(new java.awt.Dimension(200, 35));
    gameTextField7.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameTextField7MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameTextField7MouseExited(evt);
      }
    });
    gamePanel7.add(gameTextField7);

    gameSliderP7.setMajorTickSpacing(10);
    gameSliderP7.setMinorTickSpacing(5);
    gameSliderP7.setPaintLabels(true);
    gameSliderP7.setPaintTicks(true);
    gameSliderP7.setSnapToTicks(true);
    gameSliderP7.setToolTipText("Slider1");
    gameSliderP7.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderP7.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderP7.setName("Slider1"); // NOI18N
    gameSliderP7.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderP7.setValueIsAdjusting(true);
    gameSliderP7.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderP7MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderP7MouseExited(evt);
      }
    });
    gamePanel7.add(gameSliderP7);

    jSeparator14.setMaximumSize(new java.awt.Dimension(50, 40));
    jSeparator14.setMinimumSize(new java.awt.Dimension(20, 30));
    jSeparator14.setPreferredSize(new java.awt.Dimension(20, 40));
    jSeparator14.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        jSeparator14MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        jSeparator14MouseExited(evt);
      }
    });
    gamePanel7.add(jSeparator14);

    gameSliderS7.setMajorTickSpacing(10);
    gameSliderS7.setMinorTickSpacing(5);
    gameSliderS7.setPaintLabels(true);
    gameSliderS7.setPaintTicks(true);
    gameSliderS7.setSnapToTicks(true);
    gameSliderS7.setToolTipText("hello1");
    gameSliderS7.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderS7.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderS7.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderS7.setValueIsAdjusting(true);
    gameSliderS7.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderS7MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderS7MouseExited(evt);
      }
    });
    gamePanel7.add(gameSliderS7);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridwidth = 23;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    game.add(gamePanel7, gridBagConstraints);

    gamePanel8.setMaximumSize(new java.awt.Dimension(600, 65));
    gamePanel8.setMinimumSize(new java.awt.Dimension(100, 45));
    gamePanel8.setPreferredSize(new java.awt.Dimension(150, 55));
    gamePanel8.setLayout(new javax.swing.BoxLayout(gamePanel8, javax.swing.BoxLayout.LINE_AXIS));

    gameTextField8.setEditable(false);
    gameTextField8.setText("tb set");
    gameTextField8.setMaximumSize(new java.awt.Dimension(300, 45));
    gameTextField8.setMinimumSize(new java.awt.Dimension(150, 35));
    gameTextField8.setPreferredSize(new java.awt.Dimension(200, 35));
    gameTextField8.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameTextField8MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameTextField8MouseExited(evt);
      }
    });
    gamePanel8.add(gameTextField8);

    gameSliderP8.setMajorTickSpacing(10);
    gameSliderP8.setMinorTickSpacing(5);
    gameSliderP8.setPaintLabels(true);
    gameSliderP8.setPaintTicks(true);
    gameSliderP8.setSnapToTicks(true);
    gameSliderP8.setToolTipText("Slider1");
    gameSliderP8.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderP8.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderP8.setName("Slider1"); // NOI18N
    gameSliderP8.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderP8.setValueIsAdjusting(true);
    gameSliderP8.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderP8MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderP8MouseExited(evt);
      }
    });
    gamePanel8.add(gameSliderP8);

    jSeparator15.setMaximumSize(new java.awt.Dimension(50, 40));
    jSeparator15.setMinimumSize(new java.awt.Dimension(20, 30));
    jSeparator15.setPreferredSize(new java.awt.Dimension(20, 40));
    jSeparator15.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        jSeparator15MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        jSeparator15MouseExited(evt);
      }
    });
    gamePanel8.add(jSeparator15);

    gameSliderS8.setMajorTickSpacing(10);
    gameSliderS8.setMinorTickSpacing(5);
    gameSliderS8.setPaintLabels(true);
    gameSliderS8.setPaintTicks(true);
    gameSliderS8.setSnapToTicks(true);
    gameSliderS8.setToolTipText("hello1");
    gameSliderS8.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderS8.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderS8.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderS8.setValueIsAdjusting(true);
    gameSliderS8.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderS8MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderS8MouseExited(evt);
      }
    });
    gamePanel8.add(gameSliderS8);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridwidth = 23;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    game.add(gamePanel8, gridBagConstraints);

    gamePanel9.setMaximumSize(new java.awt.Dimension(600, 65));
    gamePanel9.setMinimumSize(new java.awt.Dimension(100, 45));
    gamePanel9.setPreferredSize(new java.awt.Dimension(150, 55));
    gamePanel9.setLayout(new javax.swing.BoxLayout(gamePanel9, javax.swing.BoxLayout.LINE_AXIS));

    gameTextField9.setEditable(false);
    gameTextField9.setText("tb set");
    gameTextField9.setMaximumSize(new java.awt.Dimension(300, 45));
    gameTextField9.setMinimumSize(new java.awt.Dimension(150, 35));
    gameTextField9.setPreferredSize(new java.awt.Dimension(200, 35));
    gameTextField9.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameTextField9MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameTextField9MouseExited(evt);
      }
    });
    gamePanel9.add(gameTextField9);

    gameSliderP9.setMajorTickSpacing(10);
    gameSliderP9.setMinorTickSpacing(5);
    gameSliderP9.setPaintLabels(true);
    gameSliderP9.setPaintTicks(true);
    gameSliderP9.setSnapToTicks(true);
    gameSliderP9.setToolTipText("Slider1");
    gameSliderP9.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderP9.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderP9.setName("Slider1"); // NOI18N
    gameSliderP9.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderP9.setValueIsAdjusting(true);
    gameSliderP9.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderP9MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderP9MouseExited(evt);
      }
    });
    gamePanel9.add(gameSliderP9);

    jSeparator16.setMaximumSize(new java.awt.Dimension(50, 40));
    jSeparator16.setMinimumSize(new java.awt.Dimension(20, 30));
    jSeparator16.setPreferredSize(new java.awt.Dimension(20, 40));
    gamePanel9.add(jSeparator16);

    gameSliderS9.setMajorTickSpacing(10);
    gameSliderS9.setMinorTickSpacing(5);
    gameSliderS9.setPaintLabels(true);
    gameSliderS9.setPaintTicks(true);
    gameSliderS9.setSnapToTicks(true);
    gameSliderS9.setToolTipText("hello1");
    gameSliderS9.setMaximumSize(new java.awt.Dimension(400, 45));
    gameSliderS9.setMinimumSize(new java.awt.Dimension(250, 45));
    gameSliderS9.setPreferredSize(new java.awt.Dimension(300, 45));
    gameSliderS9.setValueIsAdjusting(true);
    gameSliderS9.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameSliderS9MouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameSliderS9MouseExited(evt);
      }
    });
    gamePanel9.add(gameSliderS9);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridwidth = 23;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    game.add(gamePanel9, gridBagConstraints);

    gameButtonDown.setLabel("down");
    gameButtonDown.setMaximumSize(new java.awt.Dimension(60, 55));
    gameButtonDown.setMinimumSize(new java.awt.Dimension(30, 45));
    gameButtonDown.setPreferredSize(new java.awt.Dimension(46, 55));
    gameButtonDown.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        gameButtonDownMouseClicked(evt);
      }
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        gameButtonDownMouseEntered(evt);
      }
      public void mouseExited(java.awt.event.MouseEvent evt) {
        gameButtonDownMouseExited(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 10;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    game.add(gameButtonDown, gridBagConstraints);

    gamePanelRearPanel.setPreferredSize(new java.awt.Dimension(700, 45));

    javax.swing.GroupLayout gamePanelRearPanelLayout = new javax.swing.GroupLayout(gamePanelRearPanel);
    gamePanelRearPanel.setLayout(gamePanelRearPanelLayout);
    gamePanelRearPanelLayout.setHorizontalGroup(
      gamePanelRearPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 0, Short.MAX_VALUE)
    );
    gamePanelRearPanelLayout.setVerticalGroup(
      gamePanelRearPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGap(0, 0, Short.MAX_VALUE)
    );

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridwidth = 23;
    gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weighty = 0.5;
    game.add(gamePanelRearPanel, gridBagConstraints);

    gamePanelBottomPanel.setMinimumSize(new java.awt.Dimension(100, 100));

    gameTextPane.setBackground(new java.awt.Color(255, 204, 204));
    gameTextPane.setAlignmentX(0.0F);
    gameTextPane.setAlignmentY(0.0F);
    gameTextPane.setAutoscrolls(true);
    gameTextPane.setMaximumSize(new java.awt.Dimension(220, 300));
    gameTextPane.setMinimumSize(new java.awt.Dimension(90, 200));
    gameTextPane.setPreferredSize(new java.awt.Dimension(220, 225));

    gameTextField.setBackground(new java.awt.Color(153, 255, 153));
    gameTextField.setColumns(20);
    gameTextField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
    gameTextField.setLineWrap(true);
    gameTextField.setRows(5);
    gameTextField.setText("This is to be filled with descriptions of the field over which the mouse hovers");
    gameTextField.setWrapStyleWord(true);
    gameTextField.setMargin(new java.awt.Insets(0, 0, 0, 0));
    gameTextField.setMinimumSize(new java.awt.Dimension(50, 100));
    gameTextField.setPreferredSize(new java.awt.Dimension(300, 220));
    gameTextPane.setViewportView(gameTextField);

    javax.swing.GroupLayout gamePanelBottomPanelLayout = new javax.swing.GroupLayout(gamePanelBottomPanel);
    gamePanelBottomPanel.setLayout(gamePanelBottomPanelLayout);
    gamePanelBottomPanelLayout.setHorizontalGroup(
      gamePanelBottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(gamePanelBottomPanelLayout.createSequentialGroup()
        .addComponent(gameTextPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(0, 0, Short.MAX_VALUE))
    );
    gamePanelBottomPanelLayout.setVerticalGroup(
      gamePanelBottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(gamePanelBottomPanelLayout.createSequentialGroup()
        .addComponent(gameTextPane, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(0, 0, Short.MAX_VALUE))
    );

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 25;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.weighty = 0.5;
    game.add(gamePanelBottomPanel, gridBagConstraints);

    controlPanels.addTab("Settings", game);

    log.setBackground(new java.awt.Color(255, 255, 255));
    log.setAutoscrolls(true);
    log.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    log.setMaximumSize(new java.awt.Dimension(1200, 1200));
    log.setMinimumSize(new java.awt.Dimension(500, 500));
    log.setPreferredSize(new java.awt.Dimension(1200, 1200));

    logTableScrollPanel.setAutoscrolls(true);
    logTableScrollPanel.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
    logTableScrollPanel.setMaximumSize(new java.awt.Dimension(1200, 1200));
    logTableScrollPanel.setMinimumSize(new java.awt.Dimension(800, 600));
    logTableScrollPanel.setPreferredSize(new java.awt.Dimension(1200, 1200));
    logTableScrollPanel.addInputMethodListener(new java.awt.event.InputMethodListener() {
      public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
        logTableScrollPanelCaretPositionChanged(evt);
      }
      public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
        logTableScrollPanelInputMethodTextChanged(evt);
      }
    });

    logDisplayTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    logDisplayTable.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
    logDisplayTable.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null}
      },
      new String [] {
        "Title", "col0", "col1", "col2", "col3", "col4", "col5", "col6", "col7", "col8", "col9"
      }
    ) {
      Class[] types = new Class [] {
        java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
      };
      boolean[] canEdit = new boolean [] {
        false, false, false, false, false, false, false, false, false, false, false
      };

      public Class getColumnClass(int columnIndex) {
        return types [columnIndex];
      }

      public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit [columnIndex];
      }
    });
    logDisplayTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
    logDisplayTable.setColumnSelectionAllowed(true);
    logDisplayTable.setGridColor(new java.awt.Color(153, 153, 255));
    logDisplayTable.setMaximumSize(new java.awt.Dimension(1200, 1200));
    logDisplayTable.setMinimumSize(new java.awt.Dimension(11200, 900));
    logDisplayTable.setPreferredSize(new java.awt.Dimension(1200, 1200));
    logDisplayTable.setRowHeight(13);
    logDisplayTable.getTableHeader().setResizingAllowed(false);
    logDisplayTable.getTableHeader().setReorderingAllowed(false);
    logTableScrollPanel.setViewportView(logDisplayTable);
    logDisplayTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    if (logDisplayTable.getColumnModel().getColumnCount() > 0) {
      logDisplayTable.getColumnModel().getColumn(0).setResizable(false);
      logDisplayTable.getColumnModel().getColumn(0).setPreferredWidth(110);
      logDisplayTable.getColumnModel().getColumn(1).setResizable(false);
      logDisplayTable.getColumnModel().getColumn(2).setResizable(false);
      logDisplayTable.getColumnModel().getColumn(3).setResizable(false);
      logDisplayTable.getColumnModel().getColumn(4).setResizable(false);
      logDisplayTable.getColumnModel().getColumn(5).setResizable(false);
      logDisplayTable.getColumnModel().getColumn(6).setResizable(false);
      logDisplayTable.getColumnModel().getColumn(7).setResizable(false);
      logDisplayTable.getColumnModel().getColumn(8).setResizable(false);
      logDisplayTable.getColumnModel().getColumn(9).setResizable(false);
      logDisplayTable.getColumnModel().getColumn(10).setResizable(false);
    }
    logDisplayTable.getAccessibleContext().setAccessibleName("logTable");

    logDlevel2.setText("DLevel2");

    LogDlen1Slider.setMajorTickSpacing(25);
    LogDlen1Slider.setMaximum(75);
    LogDlen1Slider.setMinorTickSpacing(5);
    LogDlen1Slider.setPaintLabels(true);
    LogDlen1Slider.setPaintTicks(true);
    LogDlen1Slider.setValue(20);
    LogDlen1Slider.setMaximumSize(new java.awt.Dimension(60, 35));
    LogDlen1Slider.setMinimumSize(new java.awt.Dimension(24, 12));
    LogDlen1Slider.setName("Length"); // NOI18N
    LogDlen1Slider.setPreferredSize(new java.awt.Dimension(60, 35));
    LogDlen1Slider.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        LogDlen1SliderStateChanged(evt);
      }
    });

    logDlen1.setText("DLen1");

    logDLevel1Slider.setMajorTickSpacing(5);
    logDLevel1Slider.setMaximum(15);
    logDLevel1Slider.setMinorTickSpacing(1);
    logDLevel1Slider.setPaintLabels(true);
    logDLevel1Slider.setPaintTicks(true);
    logDLevel1Slider.setValue(2);
    logDLevel1Slider.setMinimumSize(new java.awt.Dimension(36, 35));
    logDLevel1Slider.setPreferredSize(new java.awt.Dimension(200, 35));
    logDLevel1Slider.setValueIsAdjusting(true);
    logDLevel1Slider.setVerifyInputWhenFocusTarget(false);
    logDLevel1Slider.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        logDLevel1SliderStateChanged(evt);
      }
    });

    logM1Spinner.setName("histStartValue"); // NOI18N
    logM1Spinner.setValue(1);
    logM1Spinner.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        logM1SpinnerStateChanged(evt);
      }
    });

    LogDLen2Slider.setMajorTickSpacing(25);
    LogDLen2Slider.setMaximum(75);
    LogDLen2Slider.setMinorTickSpacing(5);
    LogDLen2Slider.setPaintLabels(true);
    LogDLen2Slider.setPaintTicks(true);
    LogDLen2Slider.setValue(20);
    LogDLen2Slider.setMaximumSize(new java.awt.Dimension(100, 24));
    LogDLen2Slider.setName("Length"); // NOI18N
    LogDLen2Slider.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        LogDLen2SliderStateChanged(evt);
      }
    });

    logDLevel2Slider.setMajorTickSpacing(5);
    logDLevel2Slider.setMaximum(15);
    logDLevel2Slider.setMinorTickSpacing(1);
    logDLevel2Slider.setPaintLabels(true);
    logDLevel2Slider.setPaintTicks(true);
    logDLevel2Slider.setValue(2);
    logDLevel2Slider.setValueIsAdjusting(true);
    logDLevel2Slider.setVerifyInputWhenFocusTarget(false);
    logDLevel2Slider.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        logDLevel2SliderStateChanged(evt);
      }
    });

    logDlen2.setText("DLen2");

    logDlevel1.setText("DLevel1");

    logM2Spinner.setName("histStartValue"); // NOI18N
    logM2Spinner.setValue(1);
    logM2Spinner.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        logM2SpinnerStateChanged(evt);
      }
    });
    logM2Spinner.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseReleased(java.awt.event.MouseEvent evt) {
        logM2SpinnerMouseReleased(evt);
      }
    });

    logNamesScrollPanel.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    logNamesScrollPanel.setPreferredSize(new java.awt.Dimension(350, 2000));

    logEnvirnNamesList.setModel(namesList
    );
    logEnvirnNamesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    logEnvirnNamesList.setMaximumSize(new java.awt.Dimension(300, 2000));
    logEnvirnNamesList.setMinimumSize(new java.awt.Dimension(50, 50));
    logEnvirnNamesList.setPreferredSize(new java.awt.Dimension(300, 2000));
    logEnvirnNamesList.setVisibleRowCount(3);
    logEnvirnNamesList.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        logEnvirnNamesListMouseClicked(evt);
      }
    });
    logNamesScrollPanel.setViewportView(logEnvirnNamesList);

    Start1Name.setBackground(new java.awt.Color(255, 102, 204));
    Start1Name.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
    Start1Name.setForeground(new java.awt.Color(204, 0, 0));
    Start1Name.setText("P000001");

    Start2Name.setBackground(new java.awt.Color(255, 102, 204));
    Start2Name.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
    Start2Name.setForeground(new java.awt.Color(204, 0, 0));
    Start2Name.setText("P00001");

    logButtonGroup1or2.add(logRadioButtonStart1);
    logRadioButtonStart1.setText("Start1");
    logRadioButtonStart1.setToolTipText("Planet");
    logRadioButtonStart1.setActionCommand("1");
    logRadioButtonStart1.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        logRadioButtonStart1ItemStateChanged(evt);
      }
    });

    logButtonGroup1or2.add(logRadioButtonStart2);
    logRadioButtonStart2.setText("Start2");
    logRadioButtonStart2.setActionCommand("2");
    logRadioButtonStart2.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        logRadioButtonStart2ItemStateChanged(evt);
      }
    });

    logBGactions.add(logActionJump);
    logActionJump.setText("Jump");
    logActionJump.setName("logActionJump22"); // NOI18N
    logActionJump.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        logActionJumpItemStateChanged(evt);
      }
    });

    logBGactions.add(logActionAdd);
    logActionAdd.setText("add");
    logActionAdd.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        logActionAddActionPerformed(evt);
      }
    });

    logBGactions.add(logActionDel);
    logActionDel.setText("del");
    logActionDel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        logActionDelActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout logLayout = new javax.swing.GroupLayout(log);
    log.setLayout(logLayout);
    logLayout.setHorizontalGroup(
      logLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(logLayout.createSequentialGroup()
        .addGap(6, 6, 6)
        .addGroup(logLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(logLayout.createSequentialGroup()
            .addGap(4, 4, 4)
            .addGroup(logLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(logRadioButtonStart1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(logRadioButtonStart2, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(6, 6, 6)
            .addComponent(logM1Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(logLayout.createSequentialGroup()
            .addComponent(logActionJump, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(3, 3, 3)
            .addComponent(logActionAdd)
            .addGap(2, 2, 2)
            .addComponent(logActionDel)))
        .addGap(4, 4, 4)
        .addGroup(logLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(Start1Name, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(Start2Name, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(10, 10, 10)
        .addGroup(logLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(logDlen1)
          .addComponent(logDlen2))
        .addGap(142, 142, 142)
        .addGroup(logLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(logDlevel1)
          .addComponent(logDlevel2))
        .addGap(12, 12, 12)
        .addGroup(logLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(logDLevel1Slider, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGroup(logLayout.createSequentialGroup()
            .addGap(10, 10, 10)
            .addComponent(logDLevel2Slider, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))))
      .addComponent(logTableScrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 1195, javax.swing.GroupLayout.PREFERRED_SIZE)
      .addGroup(logLayout.createSequentialGroup()
        .addGap(276, 276, 276)
        .addComponent(LogDlen1Slider, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
      .addGroup(logLayout.createSequentialGroup()
        .addGap(276, 276, 276)
        .addComponent(LogDLen2Slider, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
      .addGroup(logLayout.createSequentialGroup()
        .addGap(86, 86, 86)
        .addComponent(logM2Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
      .addGroup(logLayout.createSequentialGroup()
        .addGap(600, 600, 600)
        .addComponent(logNamesScrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE))
    );
    logLayout.setVerticalGroup(
      logLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(logLayout.createSequentialGroup()
        .addGroup(logLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(logLayout.createSequentialGroup()
            .addGap(1, 1, 1)
            .addGroup(logLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(logRadioButtonStart1)
              .addGroup(logLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(logRadioButtonStart2))
              .addComponent(logM1Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(4, 4, 4)
            .addGroup(logLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(logActionJump, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(logActionAdd)
              .addComponent(logActionDel)))
          .addGroup(logLayout.createSequentialGroup()
            .addGap(3, 3, 3)
            .addComponent(Start1Name)
            .addGap(7, 7, 7)
            .addComponent(Start2Name))
          .addGroup(logLayout.createSequentialGroup()
            .addGap(4, 4, 4)
            .addComponent(logDlen1)
            .addGap(27, 27, 27)
            .addComponent(logDlen2))
          .addGroup(logLayout.createSequentialGroup()
            .addGap(10, 10, 10)
            .addComponent(logDlevel1)
            .addGap(26, 26, 26)
            .addComponent(logDlevel2))
          .addGroup(logLayout.createSequentialGroup()
            .addComponent(logDLevel1Slider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(15, 15, 15)
            .addComponent(logDLevel2Slider, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addGap(19, 19, 19)
        .addComponent(logTableScrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 915, javax.swing.GroupLayout.PREFERRED_SIZE))
      .addGroup(logLayout.createSequentialGroup()
        .addGap(1, 1, 1)
        .addComponent(LogDlen1Slider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
      .addGroup(logLayout.createSequentialGroup()
        .addGap(45, 45, 45)
        .addComponent(LogDLen2Slider, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
      .addGroup(logLayout.createSequentialGroup()
        .addGap(23, 23, 23)
        .addComponent(logM2Spinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
      .addGroup(logLayout.createSequentialGroup()
        .addGap(10, 10, 10)
        .addComponent(logNamesScrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
    );

    LogDlen1Slider.getAccessibleContext().setAccessibleName("Length");
    logDLevel1Slider.getAccessibleContext().setAccessibleName("Level Slider");

    controlPanels.addTab("logs", log);

    clan.setAutoscrolls(true);
    clan.setMaximumSize(new java.awt.Dimension(1000, 800));
    clan.setMinimumSize(new java.awt.Dimension(800, 700));
    clan.setName(""); // NOI18N
    clan.setPreferredSize(new java.awt.Dimension(800, 700));
    clan.setLayout(new java.awt.GridBagLayout());

    clanTextField.setColumns(20);
    clanTextField.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
    clanTextField.setRows(5);
    clanTextField.setMargin(new java.awt.Insets(0, 0, 0, 0));
    clanTextField.setMinimumSize(new java.awt.Dimension(50, 100));
    clanTextField.setPreferredSize(new java.awt.Dimension(75, 150));
    clanTextPane.setViewportView(clanTextField);

    clan.add(clanTextPane, new java.awt.GridBagConstraints());

    clanPanel0.setMaximumSize(new java.awt.Dimension(1100, 65));
    clanPanel0.setMinimumSize(new java.awt.Dimension(700, 45));
    clanPanel0.setPreferredSize(new java.awt.Dimension(700, 55));
    clanPanel0.setLayout(new javax.swing.BoxLayout(clanPanel0, javax.swing.BoxLayout.LINE_AXIS));

    clanTextField0.setEditable(false);
    clanTextField0.setText("tb set");
    clanTextField0.setMaximumSize(new java.awt.Dimension(200, 45));
    clanTextField0.setMinimumSize(new java.awt.Dimension(100, 35));
    clanTextField0.setPreferredSize(new java.awt.Dimension(100, 35));
    clanPanel0.add(clanTextField0);

    gameLabelP5.setText("P");
    clanPanel0.add(gameLabelP5);

    clanSliderP0.setMajorTickSpacing(10);
    clanSliderP0.setMinorTickSpacing(5);
    clanSliderP0.setPaintLabels(true);
    clanSliderP0.setPaintTicks(true);
    clanSliderP0.setSnapToTicks(true);
    clanSliderP0.setToolTipText("Slider1");
    clanSliderP0.setMaximumSize(new java.awt.Dimension(400, 45));
    clanSliderP0.setMinimumSize(new java.awt.Dimension(250, 45));
    clanSliderP0.setName("Slider1"); // NOI18N
    clanSliderP0.setPreferredSize(new java.awt.Dimension(300, 45));
    clanSliderP0.setValueIsAdjusting(true);
    clanPanel0.add(clanSliderP0);

    jSeparator6.setMaximumSize(new java.awt.Dimension(50, 40));
    jSeparator6.setMinimumSize(new java.awt.Dimension(20, 30));
    jSeparator6.setPreferredSize(new java.awt.Dimension(20, 40));
    clanPanel0.add(jSeparator6);

    gameLabelS5.setText("S");
    clanPanel0.add(gameLabelS5);

    clanSliderS0.setMajorTickSpacing(10);
    clanSliderS0.setMinorTickSpacing(5);
    clanSliderS0.setPaintLabels(true);
    clanSliderS0.setPaintTicks(true);
    clanSliderS0.setSnapToTicks(true);
    clanSliderS0.setToolTipText("hello1");
    clanSliderS0.setMaximumSize(new java.awt.Dimension(400, 45));
    clanSliderS0.setMinimumSize(new java.awt.Dimension(250, 45));
    clanSliderS0.setPreferredSize(new java.awt.Dimension(300, 45));
    clanSliderS0.setValueIsAdjusting(true);
    clanPanel0.add(clanSliderS0);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = 28;
    gridBagConstraints.gridheight = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    clan.add(clanPanel0, gridBagConstraints);

    clanPanel1.setMaximumSize(new java.awt.Dimension(1100, 65));
    clanPanel1.setMinimumSize(new java.awt.Dimension(700, 45));
    clanPanel1.setPreferredSize(new java.awt.Dimension(700, 55));
    clanPanel1.setLayout(new javax.swing.BoxLayout(clanPanel1, javax.swing.BoxLayout.LINE_AXIS));

    clanTextField1.setEditable(false);
    clanTextField1.setText("tb set");
    clanTextField1.setMaximumSize(new java.awt.Dimension(200, 45));
    clanTextField1.setMinimumSize(new java.awt.Dimension(100, 35));
    clanTextField1.setPreferredSize(new java.awt.Dimension(100, 35));
    clanPanel1.add(clanTextField1);

    gameLabelP6.setText("P");
    clanPanel1.add(gameLabelP6);

    clanSliderP1.setMajorTickSpacing(10);
    clanSliderP1.setMinorTickSpacing(5);
    clanSliderP1.setPaintLabels(true);
    clanSliderP1.setPaintTicks(true);
    clanSliderP1.setSnapToTicks(true);
    clanSliderP1.setToolTipText("Slider1");
    clanSliderP1.setMaximumSize(new java.awt.Dimension(400, 45));
    clanSliderP1.setMinimumSize(new java.awt.Dimension(250, 45));
    clanSliderP1.setName("Slider1"); // NOI18N
    clanSliderP1.setPreferredSize(new java.awt.Dimension(300, 45));
    clanSliderP1.setValueIsAdjusting(true);
    clanPanel1.add(clanSliderP1);

    jSeparator7.setMaximumSize(new java.awt.Dimension(50, 40));
    jSeparator7.setMinimumSize(new java.awt.Dimension(20, 30));
    jSeparator7.setPreferredSize(new java.awt.Dimension(20, 40));
    clanPanel1.add(jSeparator7);

    gameLabelS6.setText("S");
    clanPanel1.add(gameLabelS6);

    clanSliderS1.setMajorTickSpacing(10);
    clanSliderS1.setMinorTickSpacing(5);
    clanSliderS1.setPaintLabels(true);
    clanSliderS1.setPaintTicks(true);
    clanSliderS1.setSnapToTicks(true);
    clanSliderS1.setToolTipText("hello1");
    clanSliderS1.setMaximumSize(new java.awt.Dimension(400, 45));
    clanSliderS1.setMinimumSize(new java.awt.Dimension(250, 45));
    clanSliderS1.setPreferredSize(new java.awt.Dimension(300, 45));
    clanSliderS1.setValueIsAdjusting(true);
    clanPanel1.add(clanSliderS1);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.gridwidth = 28;
    gridBagConstraints.gridheight = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    clan.add(clanPanel1, gridBagConstraints);

    clanPanel2.setMaximumSize(new java.awt.Dimension(1100, 65));
    clanPanel2.setMinimumSize(new java.awt.Dimension(700, 45));
    clanPanel2.setPreferredSize(new java.awt.Dimension(700, 55));
    clanPanel2.setLayout(new javax.swing.BoxLayout(clanPanel2, javax.swing.BoxLayout.LINE_AXIS));

    clanTextField2.setEditable(false);
    clanTextField2.setText("tb set");
    clanTextField2.setMaximumSize(new java.awt.Dimension(200, 45));
    clanTextField2.setMinimumSize(new java.awt.Dimension(100, 35));
    clanTextField2.setPreferredSize(new java.awt.Dimension(100, 35));
    clanPanel2.add(clanTextField2);

    gameLabelP7.setText("P");
    clanPanel2.add(gameLabelP7);

    clanSliderP2.setMajorTickSpacing(10);
    clanSliderP2.setMinorTickSpacing(5);
    clanSliderP2.setPaintLabels(true);
    clanSliderP2.setPaintTicks(true);
    clanSliderP2.setSnapToTicks(true);
    clanSliderP2.setToolTipText("Slider1");
    clanSliderP2.setMaximumSize(new java.awt.Dimension(400, 45));
    clanSliderP2.setMinimumSize(new java.awt.Dimension(250, 45));
    clanSliderP2.setName("Slider1"); // NOI18N
    clanSliderP2.setPreferredSize(new java.awt.Dimension(300, 45));
    clanSliderP2.setValueIsAdjusting(true);
    clanPanel2.add(clanSliderP2);

    jSeparator8.setMaximumSize(new java.awt.Dimension(50, 40));
    jSeparator8.setMinimumSize(new java.awt.Dimension(20, 30));
    jSeparator8.setPreferredSize(new java.awt.Dimension(20, 40));
    clanPanel2.add(jSeparator8);

    gameLabelS7.setText("S");
    clanPanel2.add(gameLabelS7);

    clanSliderS2.setMajorTickSpacing(10);
    clanSliderS2.setMinorTickSpacing(5);
    clanSliderS2.setPaintLabels(true);
    clanSliderS2.setPaintTicks(true);
    clanSliderS2.setSnapToTicks(true);
    clanSliderS2.setToolTipText("hello1");
    clanSliderS2.setMaximumSize(new java.awt.Dimension(400, 45));
    clanSliderS2.setMinimumSize(new java.awt.Dimension(250, 45));
    clanSliderS2.setPreferredSize(new java.awt.Dimension(300, 45));
    clanSliderS2.setValueIsAdjusting(true);
    clanPanel2.add(clanSliderS2);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 11;
    gridBagConstraints.gridwidth = 28;
    gridBagConstraints.gridheight = 3;
    clan.add(clanPanel2, gridBagConstraints);

    clanPanel3.setMaximumSize(new java.awt.Dimension(1100, 65));
    clanPanel3.setMinimumSize(new java.awt.Dimension(700, 45));
    clanPanel3.setPreferredSize(new java.awt.Dimension(700, 55));
    clanPanel3.setLayout(new javax.swing.BoxLayout(clanPanel3, javax.swing.BoxLayout.LINE_AXIS));

    clanTextField3.setEditable(false);
    clanTextField3.setText("tb set");
    clanTextField3.setMaximumSize(new java.awt.Dimension(200, 45));
    clanTextField3.setMinimumSize(new java.awt.Dimension(100, 35));
    clanTextField3.setPreferredSize(new java.awt.Dimension(100, 35));
    clanPanel3.add(clanTextField3);

    clanLabelP3.setText("P");
    clanPanel3.add(clanLabelP3);

    clanSliderP3.setMajorTickSpacing(10);
    clanSliderP3.setMinorTickSpacing(5);
    clanSliderP3.setPaintLabels(true);
    clanSliderP3.setPaintTicks(true);
    clanSliderP3.setSnapToTicks(true);
    clanSliderP3.setToolTipText("Slider1");
    clanSliderP3.setMaximumSize(new java.awt.Dimension(400, 45));
    clanSliderP3.setMinimumSize(new java.awt.Dimension(250, 45));
    clanSliderP3.setName("Slider1"); // NOI18N
    clanSliderP3.setPreferredSize(new java.awt.Dimension(300, 45));
    clanSliderP3.setValueIsAdjusting(true);
    clanPanel3.add(clanSliderP3);

    jSeparator9.setMaximumSize(new java.awt.Dimension(50, 40));
    jSeparator9.setMinimumSize(new java.awt.Dimension(20, 30));
    jSeparator9.setPreferredSize(new java.awt.Dimension(20, 40));
    clanPanel3.add(jSeparator9);

    gameLabelS8.setText("S");
    clanPanel3.add(gameLabelS8);

    clanSliderS3.setMajorTickSpacing(10);
    clanSliderS3.setMinorTickSpacing(5);
    clanSliderS3.setPaintLabels(true);
    clanSliderS3.setPaintTicks(true);
    clanSliderS3.setSnapToTicks(true);
    clanSliderS3.setToolTipText("hello1");
    clanSliderS3.setMaximumSize(new java.awt.Dimension(400, 45));
    clanSliderS3.setMinimumSize(new java.awt.Dimension(250, 45));
    clanSliderS3.setPreferredSize(new java.awt.Dimension(300, 45));
    clanSliderS3.setValueIsAdjusting(true);
    clanPanel3.add(clanSliderS3);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 15;
    gridBagConstraints.gridwidth = 28;
    gridBagConstraints.gridheight = 3;
    clan.add(clanPanel3, gridBagConstraints);

    clanPanel4.setMaximumSize(new java.awt.Dimension(1100, 65));
    clanPanel4.setMinimumSize(new java.awt.Dimension(700, 45));
    clanPanel4.setPreferredSize(new java.awt.Dimension(700, 55));
    clanPanel4.setLayout(new javax.swing.BoxLayout(clanPanel4, javax.swing.BoxLayout.LINE_AXIS));

    clanTextField4.setEditable(false);
    clanTextField4.setText("tb set");
    clanTextField4.setMaximumSize(new java.awt.Dimension(200, 45));
    clanTextField4.setMinimumSize(new java.awt.Dimension(100, 35));
    clanTextField4.setPreferredSize(new java.awt.Dimension(100, 35));
    clanPanel4.add(clanTextField4);

    clanLabelP4.setText("P");
    clanPanel4.add(clanLabelP4);

    clanSliderP4.setMajorTickSpacing(10);
    clanSliderP4.setMinorTickSpacing(5);
    clanSliderP4.setPaintLabels(true);
    clanSliderP4.setPaintTicks(true);
    clanSliderP4.setSnapToTicks(true);
    clanSliderP4.setToolTipText("Slider1");
    clanSliderP4.setMaximumSize(new java.awt.Dimension(400, 45));
    clanSliderP4.setMinimumSize(new java.awt.Dimension(250, 45));
    clanSliderP4.setName("Slider1"); // NOI18N
    clanSliderP4.setPreferredSize(new java.awt.Dimension(300, 45));
    clanSliderP4.setValueIsAdjusting(true);
    clanPanel4.add(clanSliderP4);

    jSeparator10.setMaximumSize(new java.awt.Dimension(50, 40));
    jSeparator10.setMinimumSize(new java.awt.Dimension(20, 30));
    jSeparator10.setPreferredSize(new java.awt.Dimension(20, 40));
    clanPanel4.add(jSeparator10);

    clanLabelS4.setText("S");
    clanPanel4.add(clanLabelS4);

    clanSliderS4.setMajorTickSpacing(10);
    clanSliderS4.setMinorTickSpacing(5);
    clanSliderS4.setPaintLabels(true);
    clanSliderS4.setPaintTicks(true);
    clanSliderS4.setSnapToTicks(true);
    clanSliderS4.setToolTipText("hello1");
    clanSliderS4.setMaximumSize(new java.awt.Dimension(400, 45));
    clanSliderS4.setMinimumSize(new java.awt.Dimension(250, 45));
    clanSliderS4.setPreferredSize(new java.awt.Dimension(300, 45));
    clanSliderS4.setValueIsAdjusting(true);
    clanPanel4.add(clanSliderS4);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 19;
    gridBagConstraints.gridwidth = 28;
    gridBagConstraints.gridheight = 3;
    clan.add(clanPanel4, gridBagConstraints);

    controlPanels.addTab("clan", clan);

    stats.setMaximumSize(new java.awt.Dimension(1200, 1200));
    stats.setMinimumSize(new java.awt.Dimension(800, 500));
    stats.setPreferredSize(new java.awt.Dimension(1200, 1200));

    statsScrollPanel.setMaximumSize(new java.awt.Dimension(1200, 1200));
    statsScrollPanel.setMinimumSize(new java.awt.Dimension(700, 400));
    statsScrollPanel.setName(""); // NOI18N
    statsScrollPanel.setPreferredSize(new java.awt.Dimension(1200, 500));

    statsTable1.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null},
        {null, null, null, null, null, null, null, null, null, null, null}
      },
      new String [] {
        "title", "P-red", "P-orange", "P-yellow", "P-green", "P-blue", "S-red", "S-orange", "S-yellow", "S-green", "S-blue"
      }
    ) {
      Class[] types = new Class [] {
        java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
      };
      boolean[] canEdit = new boolean [] {
        false, false, false, false, false, false, false, false, false, false, false
      };

      public Class getColumnClass(int columnIndex) {
        return types [columnIndex];
      }

      public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit [columnIndex];
      }
    });
    statsTable1.setColumnSelectionAllowed(true);
    statsTable1.setMaximumSize(new java.awt.Dimension(1200, 1200));
    statsTable1.setMinimumSize(new java.awt.Dimension(600, 400));
    statsTable1.setPreferredSize(new java.awt.Dimension(1200, 900));
    statsTable1.getTableHeader().setReorderingAllowed(false);
    statsScrollPanel.setViewportView(statsTable1);
    statsTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    if (statsTable1.getColumnModel().getColumnCount() > 0) {
      statsTable1.getColumnModel().getColumn(0).setResizable(false);
      statsTable1.getColumnModel().getColumn(0).setPreferredWidth(250);
      statsTable1.getColumnModel().getColumn(1).setResizable(false);
      statsTable1.getColumnModel().getColumn(2).setResizable(false);
      statsTable1.getColumnModel().getColumn(3).setResizable(false);
      statsTable1.getColumnModel().getColumn(4).setResizable(false);
      statsTable1.getColumnModel().getColumn(5).setResizable(false);
      statsTable1.getColumnModel().getColumn(6).setResizable(false);
      statsTable1.getColumnModel().getColumn(7).setResizable(false);
      statsTable1.getColumnModel().getColumn(8).setResizable(false);
      statsTable1.getColumnModel().getColumn(10).setResizable(false);
    }

    statsButtonGroupReportNumber.add(statsButton0);
    statsButton0.setText("0");
    statsButton0.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton0ItemStateChanged(evt);
      }
    });
    statsButton0.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton0MouseEntered(evt);
      }
    });

    statsButtonGroupReportNumber.add(statsButton1);
    statsButton1.setText("1");
    statsButton1.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton1ItemStateChanged(evt);
      }
    });
    statsButton1.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton1MouseEntered(evt);
      }
    });
    statsButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        statsButton1ActionPerformed(evt);
      }
    });

    statsButtonGroupReportNumber.add(statsButton2);
    statsButton2.setText("2");
    statsButton2.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton2ItemStateChanged(evt);
      }
    });
    statsButton2.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton2MouseEntered(evt);
      }
    });

    statsButtonGroupReportNumber.add(statsButton3);
    statsButton3.setText("3");
    statsButton3.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton3ItemStateChanged(evt);
      }
    });
    statsButton3.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton3MouseEntered(evt);
      }
    });
    statsButton3.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        statsButton3ActionPerformed(evt);
      }
    });

    statsButtonGroupReportNumber.add(statsButton4);
    statsButton4.setText("4");
    statsButton4.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton4ItemStateChanged(evt);
      }
    });
    statsButton4.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton4MouseEntered(evt);
      }
    });

    statsButtonGroupReportNumber.add(statsButton5);
    statsButton5.setText("5");
    statsButton5.setToolTipText("GameMaster");
    statsButton5.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton5ItemStateChanged(evt);
      }
    });
    statsButton5.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton5MouseEntered(evt);
      }
    });
    statsButton5.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        statsButton5ActionPerformed(evt);
      }
    });

    statsButtonGroupReportNumber.add(statsButton6);
    statsButton6.setText(" 6");
    statsButton6.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton6ItemStateChanged(evt);
      }
    });
    statsButton6.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton6MouseEntered(evt);
      }
    });

    statsButtonGroupReportNumber.add(statsButton7);
    statsButton7.setText("7");
    statsButton7.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton7ItemStateChanged(evt);
      }
    });
    statsButton7.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton7MouseEntered(evt);
      }
    });

    statsButtonGroupReportNumber.add(statsButton8);
    statsButton8.setText("8");
    statsButton8.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton8ItemStateChanged(evt);
      }
    });
    statsButton8.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton8MouseEntered(evt);
      }
    });

    statsButtonGroupReportNumber.add(statsButton9);
    statsButton9.setText("9");
    statsButton9.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton9ItemStateChanged(evt);
      }
    });
    statsButton9.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton9MouseEntered(evt);
      }
    });

    statsButtonGroupReportNumber.add(statsButton10);
    statsButton10.setText("10");
    statsButton10.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton10ItemStateChanged(evt);
      }
    });
    statsButton10.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton10MouseEntered(evt);
      }
    });
    statsButton10.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        statsButton10ActionPerformed(evt);
      }
    });

    statsCtlButtonRun1Yr.setText("5 yr");
    statsCtlButtonRun1Yr.setMargin(new java.awt.Insets(2, 0, 2, 0));
    statsCtlButtonRun1Yr.setMaximumSize(new java.awt.Dimension(20, 20));
    statsCtlButtonRun1Yr.setMinimumSize(new java.awt.Dimension(20, 20));
    statsCtlButtonRun1Yr.setName("5yr"); // NOI18N
    statsCtlButtonRun1Yr.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        statsCtlButtonRun1YrMouseClicked(evt);
      }
    });
    statsCtlButtonRun1Yr.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        statsCtlButtonRun1YrActionPerformed(evt);
      }
    });

    statsField.setText("jTextField1");

    statsButtonGroupReportNumber.add(statsButton11);
    statsButton11.setText("11");
    statsButton11.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton11ItemStateChanged(evt);
      }
    });
    statsButton11.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton11MouseEntered(evt);
      }
    });

    statsCtlButtonRun5Yr.setText("1 yr");
    statsCtlButtonRun5Yr.setMargin(new java.awt.Insets(2, 0, 2, 0));
    statsCtlButtonRun5Yr.setMaximumSize(new java.awt.Dimension(25, 40));
    statsCtlButtonRun5Yr.setMinimumSize(new java.awt.Dimension(20, 20));
    statsCtlButtonRun5Yr.setName("1 yr"); // NOI18N
    statsCtlButtonRun5Yr.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        statsCtlButtonRun5YrMouseClicked(evt);
      }
    });

    statsButtonGroupReportNumber.add(statsButton12);
    statsButton12.setText("12");
    statsButton12.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton12ItemStateChanged(evt);
      }
    });
    statsButton12.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton12MouseEntered(evt);
      }
    });

    statsButtonGroupReportNumber.add(statsButton13);
    statsButton13.setText("13");
    statsButton13.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton13ItemStateChanged(evt);
      }
    });
    statsButton13.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton13MouseEntered(evt);
      }
    });

    statsButtonGroupReportNumber.add(statsButton14);
    statsButton14.setText("14");
    statsButton14.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton14ItemStateChanged(evt);
      }
    });
    statsButton14.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton14MouseEntered(evt);
      }
    });

    statsButtonGroupReportNumber.add(statsButton15);
    statsButton15.setText("15");
    statsButton15.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton15ItemStateChanged(evt);
      }
    });
    statsButton15.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton15MouseEntered(evt);
      }
    });

    statsButtonGroupReportNumber.add(statsButton16);
    statsButton16.setText("16");
    statsButton16.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton16ItemStateChanged(evt);
      }
    });
    statsButton16.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton16MouseEntered(evt);
      }
    });

    statsButtonGroupReportNumber.add(statsButton17);
    statsButton17.setText("17");
    statsButton17.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton17ItemStateChanged(evt);
      }
    });
    statsButton17.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton17MouseEntered(evt);
      }
    });

    statsButtonGroupReportNumber.add(statsButton18);
    statsButton18.setText("18");
    statsButton18.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton18ItemStateChanged(evt);
      }
    });
    statsButton18.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton18MouseEntered(evt);
      }
    });

    statsButtonGroupReportNumber.add(statsButton19);
    statsButton19.setText("19");
    statsButton19.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton19ItemStateChanged(evt);
      }
    });
    statsButton19.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton19MouseEntered(evt);
      }
    });

    statsButtonGroupReportNumber.add(statsButton20);
    statsButton20.setText("20");
    statsButton20.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        statsButton20ItemStateChanged(evt);
      }
    });
    statsButton20.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        statsButton20MouseEntered(evt);
      }
    });

    statsField2.setText("jTextField1");
    statsField2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        statsField2ActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout statsLayout = new javax.swing.GroupLayout(stats);
    stats.setLayout(statsLayout);
    statsLayout.setHorizontalGroup(
      statsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(statsLayout.createSequentialGroup()
        .addGap(10, 10, 10)
        .addComponent(statsButton0)
        .addGap(9, 9, 9)
        .addComponent(statsButton11)
        .addGap(3, 3, 3)
        .addGroup(statsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(statsButton2)
          .addComponent(statsButton12))
        .addGap(3, 3, 3)
        .addComponent(statsButton13)
        .addGap(3, 3, 3)
        .addComponent(statsButton14)
        .addGap(123, 123, 123)
        .addGroup(statsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(statsButton18)
          .addComponent(statsButton8))
        .addGap(43, 43, 43)
        .addComponent(statsButton10)
        .addGap(3, 3, 3)
        .addComponent(statsCtlButtonRun5Yr, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(70, 70, 70)
        .addComponent(statsField, javax.swing.GroupLayout.PREFERRED_SIZE, 690, javax.swing.GroupLayout.PREFERRED_SIZE))
      .addGroup(statsLayout.createSequentialGroup()
        .addGap(50, 50, 50)
        .addComponent(statsButton1))
      .addGroup(statsLayout.createSequentialGroup()
        .addGap(290, 290, 290)
        .addComponent(statsButton7))
      .addGroup(statsLayout.createSequentialGroup()
        .addGap(170, 170, 170)
        .addComponent(statsButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
      .addComponent(statsScrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
      .addGroup(statsLayout.createSequentialGroup()
        .addGap(410, 410, 410)
        .addComponent(statsButton20))
      .addGroup(statsLayout.createSequentialGroup()
        .addGap(210, 210, 210)
        .addComponent(statsButton15))
      .addGroup(statsLayout.createSequentialGroup()
        .addGap(250, 250, 250)
        .addComponent(statsButton6))
      .addGroup(statsLayout.createSequentialGroup()
        .addGap(130, 130, 130)
        .addComponent(statsButton3))
      .addGroup(statsLayout.createSequentialGroup()
        .addGap(250, 250, 250)
        .addComponent(statsButton16))
      .addGroup(statsLayout.createSequentialGroup()
        .addGap(290, 290, 290)
        .addComponent(statsButton17))
      .addGroup(statsLayout.createSequentialGroup()
        .addGap(370, 370, 370)
        .addComponent(statsButton19))
      .addGroup(statsLayout.createSequentialGroup()
        .addGap(210, 210, 210)
        .addComponent(statsButton5))
      .addGroup(statsLayout.createSequentialGroup()
        .addGap(370, 370, 370)
        .addComponent(statsButton9))
      .addGroup(statsLayout.createSequentialGroup()
        .addGap(450, 450, 450)
        .addComponent(statsCtlButtonRun1Yr, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
      .addGroup(statsLayout.createSequentialGroup()
        .addGap(510, 510, 510)
        .addComponent(statsField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    );
    statsLayout.setVerticalGroup(
      statsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(statsButton0)
      .addGroup(statsLayout.createSequentialGroup()
        .addGap(20, 20, 20)
        .addComponent(statsButton11))
      .addComponent(statsButton2)
      .addGroup(statsLayout.createSequentialGroup()
        .addGap(20, 20, 20)
        .addComponent(statsButton12))
      .addGroup(statsLayout.createSequentialGroup()
        .addGap(20, 20, 20)
        .addComponent(statsButton13))
      .addGroup(statsLayout.createSequentialGroup()
        .addGap(20, 20, 20)
        .addComponent(statsButton14))
      .addGroup(statsLayout.createSequentialGroup()
        .addGap(20, 20, 20)
        .addComponent(statsButton18))
      .addComponent(statsButton8)
      .addComponent(statsButton10)
      .addComponent(statsCtlButtonRun5Yr, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
      .addComponent(statsField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
      .addComponent(statsButton1)
      .addComponent(statsButton7)
      .addComponent(statsButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
      .addGroup(statsLayout.createSequentialGroup()
        .addGap(40, 40, 40)
        .addComponent(statsScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
      .addGroup(statsLayout.createSequentialGroup()
        .addGap(20, 20, 20)
        .addComponent(statsButton20))
      .addGroup(statsLayout.createSequentialGroup()
        .addGap(20, 20, 20)
        .addComponent(statsButton15))
      .addComponent(statsButton6)
      .addComponent(statsButton3)
      .addGroup(statsLayout.createSequentialGroup()
        .addGap(20, 20, 20)
        .addComponent(statsButton16))
      .addGroup(statsLayout.createSequentialGroup()
        .addGap(20, 20, 20)
        .addComponent(statsButton17))
      .addGroup(statsLayout.createSequentialGroup()
        .addGap(20, 20, 20)
        .addComponent(statsButton19))
      .addComponent(statsButton5)
      .addComponent(statsButton9)
      .addGroup(statsLayout.createSequentialGroup()
        .addGap(20, 20, 20)
        .addComponent(statsCtlButtonRun1Yr, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
      .addComponent(statsField2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
    );

    controlPanels.addTab("stats", stats);

    display.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
    display.setAlignmentY(300.0F);
    display.setMaximumSize(new java.awt.Dimension(900, 700));
    display.setMinimumSize(new java.awt.Dimension(300, 400));
    display.setPreferredSize(new java.awt.Dimension(800, 600));
    display.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

    displayPanel0.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    displayPanel0.setToolTipText("primary display");
    displayPanel0.setMaximumSize(new java.awt.Dimension(1200, 500));
    displayPanel0.setMinimumSize(new java.awt.Dimension(400, 200));
    displayPanel0.setPreferredSize(new java.awt.Dimension(1200, 300));
    displayPanel0.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

    jScrollPane1.setBorder(null);
    jScrollPane1.setAutoscrolls(true);
    jScrollPane1.setMaximumSize(new java.awt.Dimension(1200, 300));
    jScrollPane1.setMinimumSize(new java.awt.Dimension(800, 80));
    jScrollPane1.setName(""); // NOI18N
    jScrollPane1.setPreferredSize(new java.awt.Dimension(1200, 100));

    displayPanel0Text.setColumns(50);
    displayPanel0Text.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N
    displayPanel0Text.setLineWrap(true);
    displayPanel0Text.setRows(5);
    displayPanel0Text.setWrapStyleWord(true);
    displayPanel0Text.setBorder(null);
    displayPanel0Text.setMaximumSize(new java.awt.Dimension(1200, 300));
    displayPanel0Text.setMinimumSize(new java.awt.Dimension(800, 30));
    displayPanel0Text.setName(""); // NOI18N
    displayPanel0Text.setOpaque(false);
    displayPanel0Text.setPreferredSize(new java.awt.Dimension(1200, 100));
    jScrollPane1.setViewportView(displayPanel0Text);

    displayPanel0.add(jScrollPane1);

    displayPanel0Text1.setText("jTextField1");
    displayPanel0Text1.setMaximumSize(new java.awt.Dimension(1200, 300));
    displayPanel0Text1.setMinimumSize(new java.awt.Dimension(600, 50));
    displayPanel0Text1.setPreferredSize(new java.awt.Dimension(800, 50));
    displayPanel0.add(displayPanel0Text1);

    display.add(displayPanel0);

    displayPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));
    displayPanel1.setMaximumSize(new java.awt.Dimension(800, 30));
    displayPanel1.setMinimumSize(new java.awt.Dimension(800, 20));
    displayPanel1.setName("CreatePannel"); // NOI18N
    displayPanel1.setPreferredSize(new java.awt.Dimension(800, 20));

    displayPanel1SinceYearStart.setText("000.000");
    displayPanel1SinceYearStart.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        displayPanel1SinceYearStartActionPerformed(evt);
      }
    });

    displayPanel1EconName.setText("econName");

    displayPanel1Operation.setText("operation");
    displayPanel1Operation.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        displayPanel1OperationActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout displayPanel1Layout = new javax.swing.GroupLayout(displayPanel1);
    displayPanel1.setLayout(displayPanel1Layout);
    displayPanel1Layout.setHorizontalGroup(
      displayPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(displayPanel1Layout.createSequentialGroup()
        .addGap(436, 436, 436)
        .addComponent(displayPanel1EconName, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(16, 16, 16)
        .addComponent(displayPanel1Operation, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(displayPanel1SinceYearStart, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(0, 0, Short.MAX_VALUE))
    );
    displayPanel1Layout.setVerticalGroup(
      displayPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(displayPanel1Layout.createSequentialGroup()
        .addGap(117, 117, 117)
        .addGroup(displayPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(displayPanel1EconName, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(displayPanel1Operation, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(displayPanel1SinceYearStart, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(26, 26, 26))
    );

    display.add(displayPanel1);

    displayPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));
    displayPanel2.setAlignmentX(0.0F);
    displayPanel2.setAlignmentY(300.0F);
    displayPanel2.setMaximumSize(new java.awt.Dimension(800, 30));
    displayPanel2.setMinimumSize(new java.awt.Dimension(800, 20));
    displayPanel2.setName("displayPanel2"); // NOI18N
    displayPanel2.setPreferredSize(new java.awt.Dimension(800, 20));

    displayPanel2EconName.setText("econName");
    displayPanel2EconName.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        displayPanel2EconNameActionPerformed(evt);
      }
    });

    displayPanel2Operation.setText("operation");
    displayPanel2Operation.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        displayPanel2OperationActionPerformed(evt);
      }
    });

    displayPanel2SinceYearStart.setText("000.000");

    javax.swing.GroupLayout displayPanel2Layout = new javax.swing.GroupLayout(displayPanel2);
    displayPanel2.setLayout(displayPanel2Layout);
    displayPanel2Layout.setHorizontalGroup(
      displayPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(displayPanel2Layout.createSequentialGroup()
        .addComponent(displayPanel2EconName, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(displayPanel2Operation, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(18, 18, 18)
        .addComponent(displayPanel2SinceYearStart, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(0, 198, Short.MAX_VALUE))
    );
    displayPanel2Layout.setVerticalGroup(
      displayPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, displayPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(displayPanel2EconName, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
      .addGroup(displayPanel2Layout.createSequentialGroup()
        .addGroup(displayPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(displayPanel2SinceYearStart, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(displayPanel2Operation, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(0, 0, Short.MAX_VALUE))
    );

    display.add(displayPanel2);

    controlPanels.addTab("display", display);

    getContentPane().add(controlPanels);
    controlPanels.getAccessibleContext().setAccessibleName("traderPanel");
  }// </editor-fold>//GEN-END:initComponents

  private void formInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_formInputMethodTextChanged
    /*
     try {
     NumberFormat whole = NumberFormat.getNumberInstance();
     whole.setMaximumFractionDigits(0);
     NumberFormat format = NumberFormat.getIntegerInstance(Locale.US);
     format.setParseIntegerOnly(true);
     String source = evt.getSource().toString();
     int M = 0;
     if (E.printlnLimit > 1) {
     System.out.println(since() + "formInputMethodTextChanged source=" + source);
     }
     if (source.equals("StaffGrowthPerYear")) {
     E.staffGrowth = ((Number) StaffGrowthPerYear.getValue()).doubleValue();
     StaffDeaths1.setValue(new Integer((int) E.staffGrowth));
     System.out.println(since() + "InputInputMethodTextChanged StaffGrowthPerYear=" + whole.format(E.staffGrowth));
     } else if (source.equals("StaffDeathsPerYear")) {
     E.staffDeathRate = ((Number) StaffDeathsPerYear.getValue()).doubleValue();
     } else if (source.equals("logDisplay1Start")) {
     M = eM.logEnvirn[0].logM[0] = format.parse(evt.getText().toString()).intValue();
     setLogM(0, M);
     System.out.println(since() + "InputInputMethodTextChanged logDisplay1Start=" + whole.format(eM.logEnvirn[0].logM[0]));
     } else if (source.equals("logDisplay2Start")) {
     M = eM.logEnvirn[1].logM[1] = format.parse(evt.getText().toString()).intValue();
     setLogM(1, M);
     System.out.println(since() + "InputInputMethodTextChanged logDisplay1Start=" + whole.format(eM.logEnvirn[1].logM[1]));

     } else if (source.equals("log")) {
     double d = ((Number) evt.getText()).doubleValue();
     System.out.println(since() + " formInputMethodTextChanged log=" + whole.format(d));
     } else {
     double d = ((Number) evt.getText()).doubleValue();
     System.out.println(since() + " formInputMethodTextChanged Unknown=" + source + ", val=" + whole.format(d));

     }
     } catch (Exception ex) {
      System.out.flush();
      System.err.flush();
      System.err.println(new Date().toString() + since() + " cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + ", addlErr=" + eM.addlErr);
      ex.printStackTrace(System.err);
      System.err.flush();
            setFatalError();
     }
     */
  }//GEN-LAST:event_formInputMethodTextChanged

  private void formCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_formCaretPositionChanged
    String sss = evt.toString();
    System.out.println(since() + " formCaretPositionChanged=" + sss);
  }//GEN-LAST:event_formCaretPositionChanged

  private void LogsInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_LogsInputMethodTextChanged
    try {
      String source = evt.getSource().toString();
      NumberFormat whole = NumberFormat.getNumberInstance();
      whole.setMaximumFractionDigits(0);
      NumberFormat format = NumberFormat.getIntegerInstance(Locale.US);
      format.setParseIntegerOnly(true);
      int M = 0;

      if (source.equals("logDisplay1Start")) {
        M = eM.logEnvirn[0].logM[0] = format.parse(evt.getText().toString()).intValue();
        setLogM(0, M);
        System.out.println(since() + "LogsInputMethodTextChanged logDisplay1Start=" + whole.format(eM.logEnvirn[0].logM[0]));
      }
      else if (source.equals("logDisplay2Start")) {
        M = eM.logEnvirn[1].logM[1] = format.parse(evt.getText().toString()).intValue();
        setLogM(1, M);
        System.out.println(since() + "LogsInputMethodTextChanged logDisplay1Start=" + whole.format(eM.logEnvirn[1].logM[1]));
      }
      else if (source.equals("StaffDeathsPerYear")) {
        //      E.staffDeathRate[0] = ((Number) StaffDeathsPerYear.getValue()).doubleValue();
      }
      else {
        M = format.parse(evt.getText().toString()).intValue();
        System.out.println(since() + "LogsInputMethodTextChanged unknown=" + source + ", value=" + M);
      }
    }
    catch (Exception ex) {
      System.out.flush();
      System.err.flush();
      System.err.println(new Date().toString() + since() + " cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + ", addlErr=" + eM.addlErr);
      ex.printStackTrace(System.err);
      System.err.flush();
      setStopExe();
    }
  }//GEN-LAST:event_LogsInputMethodTextChanged

  private void logM2SpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_logM2SpinnerStateChanged
    try {
      String sss = evt.toString();
      JSpinner source = (JSpinner) evt.getSource();
      NumberFormat format = NumberFormat.getIntegerInstance(Locale.US);
      format.setParseIntegerOnly(true);
      int m = format.parse(source.getValue().toString()).intValue();
      int start2 = format.parse(logM2Spinner.getValue().toString()).intValue();
      System.out.println(since() + " logM2SpinnerStateChanged=" + "m=" + m + "start2=" + start2 + eM.logEnvirn[1].logM[1] + " lev=" + eM.logEnvirn[1].logLev[1] + " Second bunch=" + eM.logEnvirn[1].logLen[1]);
      setLogM(1, m);
      displayLog();
      System.out.println(since() + " logM2SpinnerStateChanged=" + eM.logEnvirn[1].logM[1] + " lev=" + eM.logEnvirn[1].logLev[1] + " first bunch=" + eM.logEnvirn[1].logLen[1]);
    }
    catch (Exception ex) {
      System.out.flush();
      System.err.flush();
      System.err.println(new Date().toString() + since() + " cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + ", addlErr=" + eM.addlErr);
      ex.printStackTrace(System.err);
      System.err.flush();
      setStopExe();
    }
  }//GEN-LAST:event_logM2SpinnerStateChanged

  private void logDLevel2SliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_logDLevel2SliderStateChanged
    try {
      JSlider source = (JSlider) evt.getSource();
      if (!source.getValueIsAdjusting()) {
        int m = (int) source.getValue();
        saveLogLev(1, m);
        displayLog();
        System.out.println(since() + " levelSlider2StateChanged=" + eM.logEnvirn[1].logLev[1]);
      }
    }
    catch (Exception ex) {
      System.out.flush();
      System.err.flush();
      System.err.println(new Date().toString() + since() + " cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + ", addlErr=" + eM.addlErr);
      ex.printStackTrace(System.err);
      System.err.flush();
      setStopExe();
    }
  }//GEN-LAST:event_logDLevel2SliderStateChanged

  private void LogDLen2SliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_LogDLen2SliderStateChanged
    try {
      JSlider source = (JSlider) evt.getSource();
      if (!source.getValueIsAdjusting()) {
        int m = (int) source.getValue();
        saveLogLen(1, m);
        System.out.println(since() + " lengthSlider2StateChanged=" + eM.logEnvirn[1].logLen[1]);
        displayLog();
      }

    }
    catch (Exception ex) {
      System.out.flush();
      System.err.flush();
      System.err.println(new Date().toString() + since() + " cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + ", addlErr=" + eM.addlErr);
      ex.printStackTrace(System.err);
      System.err.flush();
      setStopExe();
    }
  }//GEN-LAST:event_LogDLen2SliderStateChanged

  private void logDisplay1StartStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_logDisplay1StartStateChanged
    try {
      String sss = evt.toString();
      JSpinner source = (JSpinner) evt.getSource();
      NumberFormat format = NumberFormat.getIntegerInstance(Locale.US);
      format.setParseIntegerOnly(true);
      int m = format.parse(source.getValue().toString()).intValue();
      saveLogM(0, m);
      //   displayHistoryFirstBunch = historyDisplay1Length.getValue();
      int start2 = format.parse(logM1Spinner.getValue().toString()).intValue();
      System.out.println(since() + " logDisplay1StartStateChanged=" + eM.logEnvirn[0].logM[0] + " lev=" + eM.logEnvirn[0].logLev[0] + " start2=" + start2);

    }
    catch (Exception ex) {
      System.out.flush();
      System.err.flush();
      System.err.println(new Date().toString() + since() + " cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + ", addlErr=" + eM.addlErr);
      ex.printStackTrace(System.err);
      System.err.flush();
      setStopExe();
    }

  }//GEN-LAST:event_logDisplay1StartStateChanged

  private void logDLevel1SliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_logDLevel1SliderStateChanged
    JSlider source = (JSlider) evt.getSource();
    if (!source.getValueIsAdjusting()) {
      int m = (int) source.getValue();
      saveLogLev(0, m);
      displayLog();
      System.out.println(since() + " levelSlider1StateChanged=" + E.logLev[0]);

    }
  }//GEN-LAST:event_logDLevel1SliderStateChanged

  private void LogDlen1SliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_LogDlen1SliderStateChanged
    try {
      JSlider source = (JSlider) evt.getSource();
      if (!source.getValueIsAdjusting()) {
        int m = (int) source.getValue();
        saveLogLen(0, m);
        displayLog();
        System.out.println(since() + " DLen1StateChanged=" + m);
      }
    }
    catch (Exception ex) {
      System.out.flush();
      System.err.flush();
      System.err.println(new Date().toString() + since() + " cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + ", addlErr=" + eM.addlErr);
      ex.printStackTrace(System.err);
      System.err.flush();
      setStopExe();
    }
  }//GEN-LAST:event_LogDlen1SliderStateChanged

  private void logTableScrollPanelCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_logTableScrollPanelCaretPositionChanged
    String source = evt.toString();
    System.out.println(since() + " jScrollPane1CaretPositionChanged" + source);
  }//GEN-LAST:event_logTableScrollPanelCaretPositionChanged

  private void logTableScrollPanelInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_logTableScrollPanelInputMethodTextChanged
    String sss = evt.toString();
    System.out.println(since() + " jScrollPane1InputMethodTextChanged=" + sss);
  }//GEN-LAST:event_logTableScrollPanelInputMethodTextChanged

  private void logEnvirnNamesListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logEnvirnNamesListMouseClicked
    // TODO add your handling code here:
  }//GEN-LAST:event_logEnvirnNamesListMouseClicked

  private void LogsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LogsMouseClicked
    displayLog();
  }//GEN-LAST:event_LogsMouseClicked

  private void logDisplay1StartMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logDisplay1StartMouseReleased
    displayLog();
  }//GEN-LAST:event_logDisplay1StartMouseReleased

  private void logM2SpinnerMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logM2SpinnerMouseReleased
    displayLog();
  }//GEN-LAST:event_logM2SpinnerMouseReleased

  private void logRadioButtonStart1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_logRadioButtonStart1ItemStateChanged
    int st = evt.getStateChange();
    int eID = evt.getID();

    boolean bid = logRadioButtonStart1.isSelected();
    if (bid) {
      E.dN = 0;
    }
    System.out.println("logButton1ItemStateChanged state=" + st + ", ID=" + eID + ", bid=" + bid + ", dn=" + E.dN);
  }//GEN-LAST:event_logRadioButtonStart1ItemStateChanged

  private void logRadioButtonStart2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_logRadioButtonStart2ItemStateChanged
    int st = evt.getStateChange();
    int eID = evt.getID();

    boolean bid = logRadioButtonStart2.isSelected();
    if (bid) {
      E.dN = 1;
    }
    System.out.println("logButton2ItemStateChanged state=" + st + ", ID=" + eID + ", bid=" + bid + ", dn=" + E.dN);
  }//GEN-LAST:event_logRadioButtonStart2ItemStateChanged

  private void logActionAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logActionAddActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_logActionAddActionPerformed

  private void logActionDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logActionDelActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_logActionDelActionPerformed

  private void logActionJumpItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_logActionJumpItemStateChanged
    // TODO add your handling code here:
  }//GEN-LAST:event_logActionJumpItemStateChanged

  private void gameTopRightFillActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gameTopRightFillActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_gameTopRightFillActionPerformed

    private void gameTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gameTextField1ActionPerformed
      // TODO add your handling code here:
    }//GEN-LAST:event_gameTextField1ActionPerformed

  private void clanRedMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clanRedMouseEntered
    setgameTextField("Display entry options for the clan Red");
  }//GEN-LAST:event_clanRedMouseEntered

  private void clanRedMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clanRedMouseExited
    revertgameTextField();
  }//GEN-LAST:event_clanRedMouseExited

  private void controlPanelsPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_controlPanelsPropertyChange
    Object source = evt.getSource();
    JTabbedPane sourceTabbedPane = (JTabbedPane) evt.getSource();
    int ix = sourceTabbedPane.getSelectedIndex();
    if (ix == 2) {
      statsButton0.setSelected(true);
      statsButton0.setToolTipText(statsButton0Tip);
    }
    else if (ix == 1) {
      gameMaster.setSelected(true);
      gameTextField.setText("Game Master set options for the overall game, note that there are many options to make planets or ships die quickly");
    }
    else if (ix == 3) {

    }

  }//GEN-LAST:event_controlPanelsPropertyChange

  private void clanRedItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_clanRedItemStateChanged
    int st = evt.getStateChange();
    int eID = evt.getID();

    boolean bid = clanRed.isSelected();
    if (bid) {
      gamePanelChange(0, -2, gamePanels, gameTextFields, gameSlidersP, gameSlidersS, fullVals, curVals);
    }
    System.out.println("clanRed=" + st + ", ID=" + eID + ", bid=" + bid + ", gameClanStatus=" + eM.gameClanStatus);
  }//GEN-LAST:event_clanRedItemStateChanged

  private void gameMasterMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameMasterMouseEntered
    setgameTextField("Display entry options for the game Master");
  }//GEN-LAST:event_gameMasterMouseEntered

  private void gameMasterMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameMasterMouseExited
    revertgameTextField();
  }//GEN-LAST:event_gameMasterMouseExited

  private void clanOrangeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clanOrangeMouseEntered
    setgameTextField("Display entry options for clan Orange");
  }//GEN-LAST:event_clanOrangeMouseEntered

  private void clanOrangeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clanOrangeMouseExited
    revertgameTextField();
  }//GEN-LAST:event_clanOrangeMouseExited

  private void clanYellowMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clanYellowMouseExited
    revertgameTextField();
  }//GEN-LAST:event_clanYellowMouseExited

  private void clanYellowMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clanYellowMouseEntered
    setgameTextField("Display entry options for clan Yellow");
  }//GEN-LAST:event_clanYellowMouseEntered

  private void clanGreenMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clanGreenMouseEntered
    setgameTextField("Display entry options for clan Green");
  }//GEN-LAST:event_clanGreenMouseEntered

  private void clanGreenMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clanGreenMouseExited
    revertgameTextField();
  }//GEN-LAST:event_clanGreenMouseExited

  private void clanBlueMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clanBlueMouseEntered
    setgameTextField("Display entry options for clan Blue");
  }//GEN-LAST:event_clanBlueMouseEntered

  private void clanBlueMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clanBlueMouseExited
    revertgameTextField();
  }//GEN-LAST:event_clanBlueMouseExited

  private void gameCtlButtonRun5Years1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameCtlButtonRun5Years1MouseEntered
    setgameTextField("Run all economies for 5 years");
  }//GEN-LAST:event_gameCtlButtonRun5Years1MouseEntered

  private void gameCtlButtonRun5Years1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameCtlButtonRun5Years1MouseExited
    revertgameTextField();
  }//GEN-LAST:event_gameCtlButtonRun5Years1MouseExited

  private void gameCtlButtonRun1Year1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameCtlButtonRun1Year1MouseEntered
    setgameTextField("Run all economies for 1 year");
  }//GEN-LAST:event_gameCtlButtonRun1Year1MouseEntered

  private void gameCtlButtonRun1Year1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameCtlButtonRun1Year1MouseExited
    revertgameTextField();
  }//GEN-LAST:event_gameCtlButtonRun1Year1MouseExited

  private void gameMasterItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_gameMasterItemStateChanged
    int st = evt.getStateChange();
    int eID = evt.getID();

    boolean bid = gameMaster.isSelected();

    if (bid) {
      setgameTextField("Player Red options");
      gamePanelChange(5, -2, gamePanels, gameTextFields, gameSlidersP, gameSlidersS, fullVals, curVals);
      setGameButtonColors();
      System.out.println("gameMaster=" + st + ", ID=" + eID + ", bid=" + bid + ", gameClanStatus=" + eM.gameClanStatus + " number=" + eM.gameDisplayNumber[eM.gameClanStatus]);
    }

  }//GEN-LAST:event_gameMasterItemStateChanged

  private void clanOrangeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_clanOrangeItemStateChanged
    int st = evt.getStateChange();
    int eID = evt.getID();

    boolean bid = clanOrange.isSelected();

    if (bid) {
      setgameTextField("Player Orange options");
      System.out.println("clanOrange=" + st + ", ID=" + eID + ", bid=" + bid + ", gameClanStatus=" + eM.gameClanStatus + "=>1" + " number=" + eM.gameDisplayNumber[1]);
      gamePanelChange(1, -2, gamePanels, gameTextFields, gameSlidersP, gameSlidersS, fullVals, curVals);
      setGameButtonColors();
    }

  }//GEN-LAST:event_clanOrangeItemStateChanged

  private void clanYellowItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_clanYellowItemStateChanged
    int st = evt.getStateChange();
    int eID = evt.getID();

    boolean bid = clanYellow.isSelected();

    if (bid) {
      setgameTextField("Player Yellow options");
      gamePanelChange(2, -2, gamePanels, gameTextFields, gameSlidersP, gameSlidersS, fullVals, curVals);
      setGameButtonColors();
    }
    System.out.println("clanYellow=" + st + ", ID=" + eID + ", bid=" + bid + ", gameClanStatus=" + eM.gameClanStatus);
  }//GEN-LAST:event_clanYellowItemStateChanged

  private void clanGreenItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_clanGreenItemStateChanged
    int st = evt.getStateChange();
    int eID = evt.getID();

    boolean bid = clanGreen.isSelected();
    if (bid) {
      setgameTextField("Player Green options");
      gamePanelChange(3, -2, gamePanels, gameTextFields, gameSlidersP, gameSlidersS, fullVals, curVals);
      setGameButtonColors();
    }
    System.out.println("clanGreene=" + st + ", ID=" + eID + ", bid=" + bid + ", gameClanStatus=" + eM.gameClanStatus);
  }//GEN-LAST:event_clanGreenItemStateChanged

  private void clanBlueItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_clanBlueItemStateChanged
    int st = evt.getStateChange();
    int eID = evt.getID();

    boolean bid = clanBlue.isSelected();

    if (bid) {
      setgameTextField("Player Blue options");
      gamePanelChange(4, -2, gamePanels, gameTextFields, gameSlidersP, gameSlidersS, fullVals, curVals);
    }
    setGameButtonColors();
    System.out.println("clanBlue =" + st + ", ID=" + eID + ", bid=" + bid + ", gameClanStatus=" + eM.gameClanStatus);
  }//GEN-LAST:event_clanBlueItemStateChanged

  private void gameComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_gameComponentShown
    boolean bid = evt.getSource().equals(game);
    if (bid) {

      setgameTextField("Start options input for Game Master");
      gamePanelChange(5, -2, gamePanels, gameTextFields, gameSlidersP, gameSlidersS, fullVals, curVals);
    }
    setGameButtonColors();
    System.out.println("game componentShown bid=" + bid + ", gameClanStatus=" + eM.gameClanStatus);
    printMem3();
  }//GEN-LAST:event_gameComponentShown

  private void gameCtlButtonRun1Year1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameCtlButtonRun1Year1MouseClicked
    System.out.println("in Run1Year Mouse Clicked");
    if (eM.fatalError) {
      setFatalError();
    }
    else {
      runYears(1);
    }
  }//GEN-LAST:event_gameCtlButtonRun1Year1MouseClicked

  private void gameCtlButtonRun5Years1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameCtlButtonRun5Years1MouseClicked
   
    if (eM.fatalError) {
      setFatalError();
    }
    else {
      runYears(5);
    }
  }//GEN-LAST:event_gameCtlButtonRun5Years1MouseClicked

  private void gamePanel0MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gamePanel0MouseEntered
    if (gamePanel0.isEnabled() && curVals[0] > -1) {
      setgameTextField(eM.getDetail(curVals[0]));
      gamePanel0.setToolTipText(eM.getDetail(curVals[0]));

    };
  }//GEN-LAST:event_gamePanel0MouseEntered

  private void gamePanel0MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gamePanel0MouseExited
    if (gamePanel0.isEnabled() && curVals[0] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gamePanel0MouseExited

  private void gamePanel1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gamePanel1MouseEntered
    if (gamePanel1.isEnabled() && curVals[1] > -1) {
      setgameTextField(eM.getDetail(curVals[1]));
      gamePanel1.setToolTipText(eM.getDetail(curVals[1]));
    };
  }//GEN-LAST:event_gamePanel1MouseEntered

  private void gamePanel1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gamePanel1MouseExited
    if (gamePanel1.isEnabled() && curVals[1] > -1) {
      revertgameTextField();
    }
  }//GEN-LAST:event_gamePanel1MouseExited

  private void gamePanel2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gamePanel2MouseEntered
    if (gamePanel2.isEnabled() && curVals[2] > -1) {
      setgameTextField(eM.getDetail(curVals[2]));
      gamePanel2.setToolTipText(eM.getDetail(curVals[2]));
    };
  }//GEN-LAST:event_gamePanel2MouseEntered

  private void gamePanel2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gamePanel2MouseExited
    if (gamePanel2.isEnabled() && curVals[2] < -1) {
      revertgameTextField();
    }
  }//GEN-LAST:event_gamePanel2MouseExited

  private void gameTextField1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField1MouseEntered
    if (gamePanel1.isEnabled() && curVals[1] > -1) {
      setgameTextField(eM.getDetail(curVals[1]));
      gamePanel1.setToolTipText(eM.getDetail(curVals[1]));
    };
  }//GEN-LAST:event_gameTextField1MouseEntered

  private void gameTextField1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField1MouseExited
    if (gamePanel1.isEnabled() && curVals[1] > -1) {
      revertgameTextField();
    }
  }//GEN-LAST:event_gameTextField1MouseExited

  private void gameTextField0MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField0MouseEntered
    if (gamePanel0.isEnabled() && curVals[0] > -1) {
      setgameTextField(eM.getDetail(curVals[0]));
      gamePanel0.setToolTipText(eM.getDetail(curVals[0]));
    };
  }//GEN-LAST:event_gameTextField0MouseEntered

  private void gameTextField0MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField0MouseExited
    if (gamePanel0.isEnabled() && curVals[0] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameTextField0MouseExited

  private void statsButton2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton2ItemStateChanged
    boolean bid = statsButton2.isSelected();
    if (bid) {
      statsButton2.setToolTipText(statsButton2Tip);
      statsField.setText(statsButton2Tip);
      listRes(2, resLoops, fullRes);
    }

  }//GEN-LAST:event_statsButton2ItemStateChanged

  private void statsButton1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton1ItemStateChanged
    boolean bid = statsButton1.isSelected();
    if (bid) {
      statsButton1.setToolTipText(statsButton1Tip);
      statsField.setText(statsButton1Tip);
      listRes(1, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton1ItemStateChanged

  private void statsButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statsButton3ActionPerformed
    boolean bid = statsButton3.isSelected();
    if (bid) {
      statsButton1.setToolTipText(statsButton3Tip);
      statsField.setText(statsButton3Tip);
      listRes(3, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton3ActionPerformed

  private void statsButton4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton4ItemStateChanged
    boolean bid = statsButton4.isSelected();
    if (bid) {
      statsButton1.setToolTipText(statsButton4Tip);
      statsField.setText(statsButton4Tip);
      listRes(4, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton4ItemStateChanged

  private void gameSliderS0MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS0MouseEntered
    if (gamePanel0.isEnabled() && curVals[0] > -1) {
      statsButton0.setToolTipText(eM.getDetail(curVals[0]));
      setgameTextField(eM.getDetail(curVals[0]));
    };
  }//GEN-LAST:event_gameSliderS0MouseEntered

  private void gameSliderS0MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS0MouseExited
    if (gamePanel0.isEnabled() && curVals[0] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderS0MouseExited

  private void gameSliderP0MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP0MouseEntered
    if (gamePanel0.isEnabled() && curVals[0] > -1) {
      setgameTextField(eM.getDetail(curVals[0]));
    };
  }//GEN-LAST:event_gameSliderP0MouseEntered

  private void gameSliderP0MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP0MouseExited
    if (gamePanel0.isEnabled() && curVals[0] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderP0MouseExited

  private void gameSliderP1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP1MouseEntered
    if (gamePanel1.isEnabled() && curVals[1] > -1) {
      setgameTextField(eM.getDetail(curVals[1]));
    };
  }//GEN-LAST:event_gameSliderP1MouseEntered

  private void gameSliderP1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP1MouseExited
    if (gamePanel1.isEnabled() && curVals[1] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderP1MouseExited

  private void gameSliderS1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS1MouseEntered
    if (gamePanel1.isEnabled() && curVals[1] > -1) {
      setgameTextField(eM.getDetail(curVals[1]));
    };
  }//GEN-LAST:event_gameSliderS1MouseEntered

  private void gameSliderS1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS1MouseExited
    if (gamePanel1.isEnabled() && curVals[1] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderS1MouseExited

  private void gameTextField2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField2MouseEntered
    if (gamePanel2.isEnabled() && curVals[2] > -1) {
      setgameTextField(eM.getDetail(curVals[2]));
    };
  }//GEN-LAST:event_gameTextField2MouseEntered

  private void gameTextField2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField2MouseExited
    if (gamePanel2.isEnabled() && curVals[2] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameTextField2MouseExited

  private void gameSliderP2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP2MouseEntered
    if (gamePanel2.isEnabled() && curVals[2] > -1) {
      setgameTextField(eM.getDetail(curVals[2]));
    };
  }//GEN-LAST:event_gameSliderP2MouseEntered

  private void gameSliderP2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP2MouseExited
    if (gamePanel2.isEnabled() && curVals[2] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderP2MouseExited

  private void gameSliderS2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS2MouseEntered
    if (gamePanel2.isEnabled() && curVals[2] > -1) {
      setgameTextField(eM.getDetail(curVals[2]));
    };
  }//GEN-LAST:event_gameSliderS2MouseEntered

  private void gameSliderS2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS2MouseExited
    if (gamePanel3.isEnabled() && curVals[3] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderS2MouseExited

  private void gameTextField3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField3MouseEntered
    if (gamePanel3.isEnabled() && curVals[3] > -1) {
      setgameTextField(eM.getDetail(curVals[3]));
    };
  }//GEN-LAST:event_gameTextField3MouseEntered

  private void gameTextField3MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField3MouseExited
    if (gamePanel3.isEnabled() && curVals[3] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameTextField3MouseExited

  private void gameSliderP3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP3MouseEntered
    if (gamePanel2.isEnabled() && curVals[3] > -1) {
      setgameTextField(eM.getDetail(curVals[3]));
    };
  }//GEN-LAST:event_gameSliderP3MouseEntered

  private void gameSliderP3MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP3MouseExited
    if (gamePanel3.isEnabled() && curVals[3] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderP3MouseExited

  private void gameSliderS3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS3MouseEntered
    if (gamePanel2.isEnabled() && curVals[3] > -1) {
      setgameTextField(eM.getDetail(curVals[3]));
    };
  }//GEN-LAST:event_gameSliderS3MouseEntered

  private void gameSliderS3MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS3MouseExited
    if (gamePanel3.isEnabled() && curVals[3] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderS3MouseExited

  private void gamePanel4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gamePanel4MouseEntered
    if (gamePanel4.isEnabled() && curVals[4] > -1) {
      setgameTextField(eM.getDetail(curVals[4]));
    };
  }//GEN-LAST:event_gamePanel4MouseEntered

  private void gamePanel4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gamePanel4MouseExited
    if (gamePanel4.isEnabled() && curVals[4] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gamePanel4MouseExited

  private void gameTextField4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField4MouseEntered
    if (gamePanel4.isEnabled() && curVals[4] > -1) {
      setgameTextField(eM.getDetail(curVals[4]));
    };
  }//GEN-LAST:event_gameTextField4MouseEntered

  private void gameTextField4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField4MouseExited
    if (gamePanel4.isEnabled() && curVals[4] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameTextField4MouseExited

  private void gameSliderP4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP4MouseEntered
    if (gamePanel4.isEnabled() && curVals[4] > -1) {
      setgameTextField(eM.getDetail(curVals[4]));
    };
  }//GEN-LAST:event_gameSliderP4MouseEntered

  private void gameSliderP4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP4MouseExited
    if (gamePanel4.isEnabled() && curVals[4] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderP4MouseExited

  private void gameSliderS4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS4MouseEntered
    if (gamePanel4.isEnabled() && curVals[4] > -1) {
      setgameTextField(eM.getDetail(curVals[4]));
    };
  }//GEN-LAST:event_gameSliderS4MouseEntered

  private void gameSliderS4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS4MouseExited
    if (gamePanel4.isEnabled() && curVals[4] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderS4MouseExited

  private void gamePanel5MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gamePanel5MouseEntered
    if (gamePanel5.isEnabled() && curVals[5] > -1) {
      setgameTextField(eM.getDetail(curVals[5]));
    };
  }//GEN-LAST:event_gamePanel5MouseEntered

  private void gamePanel5MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gamePanel5MouseExited
    if (gamePanel5.isEnabled() && curVals[5] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gamePanel5MouseExited

  private void gameTextField5MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField5MouseEntered
    if (gamePanel5.isEnabled() && curVals[5] > -1) {
      setgameTextField(eM.getDetail(curVals[5]));
    }
  }//GEN-LAST:event_gameTextField5MouseEntered

  private void gameTextField5MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField5MouseExited
    if (gamePanel5.isEnabled() && curVals[5] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameTextField5MouseExited

  private void gameSliderP5MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP5MouseEntered
    if (gamePanel5.isEnabled() && curVals[5] > -1) {
      setgameTextField(eM.getDetail(curVals[5]));
    }
  }//GEN-LAST:event_gameSliderP5MouseEntered

  private void gameSliderP5MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP5MouseExited
    if (gamePanel5.isEnabled() && curVals[5] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderP5MouseExited

  private void gameSliderS5MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS5MouseEntered
    if (gamePanel5.isEnabled() && curVals[5] > -1) {
      setgameTextField(eM.getDetail(curVals[5]));
    }
  }//GEN-LAST:event_gameSliderS5MouseEntered

  private void gameSliderS5MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS5MouseExited
    if (gamePanel5.isEnabled() && curVals[5] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderS5MouseExited

  private void gamePanel6MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gamePanel6MouseEntered
    if (gamePanel6.isEnabled() && curVals[6] > -1) {
      setgameTextField(eM.getDetail(curVals[6]));
    }
  }//GEN-LAST:event_gamePanel6MouseEntered

  private void gamePanel6MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gamePanel6MouseExited
    if (gamePanel6.isEnabled() && curVals[6] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gamePanel6MouseExited

  private void gameTextField6MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField6MouseEntered
    if (gamePanel6.isEnabled() && curVals[6] > -1) {
      setgameTextField(eM.getDetail(curVals[6]));
    }
  }//GEN-LAST:event_gameTextField6MouseEntered

  private void gameTextField6MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField6MouseExited
    if (gamePanel6.isEnabled() && curVals[6] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameTextField6MouseExited

  private void gameSliderP6MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP6MouseEntered
    if (gamePanel6.isEnabled() && curVals[6] > -1) {
      setgameTextField(eM.getDetail(curVals[6]));
    }
  }//GEN-LAST:event_gameSliderP6MouseEntered

  private void gameSliderP6MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP6MouseExited
    if (gamePanel6.isEnabled() && curVals[6] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderP6MouseExited

  private void gameSliderS6MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS6MouseEntered
    if (gamePanel6.isEnabled() && curVals[6] > -1) {
      setgameTextField(eM.getDetail(curVals[6]));
    }
  }//GEN-LAST:event_gameSliderS6MouseEntered

  private void gameSliderS6MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS6MouseExited
    if (gamePanel6.isEnabled() && curVals[6] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderS6MouseExited

  private void statsButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statsButton1ActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_statsButton1ActionPerformed

  private void statsButton0ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton0ItemStateChanged
    boolean bid = statsButton0.isSelected();
    if (bid) {
      statsField.setText(statsButton0Tip);
      listRes(0, resLoops, fullRes);
      statsButton0.setToolTipText(statsButton0Tip);
    }
  }//GEN-LAST:event_statsButton0ItemStateChanged

  private void statsButton3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton3ItemStateChanged
    boolean bid = statsButton3.isSelected();
    if (bid) {
      statsField.setText(statsButton3Tip);
      listRes(3, resLoops, fullRes);
      statsButton3.setToolTipText(statsButton3Tip);
    }
  }//GEN-LAST:event_statsButton3ItemStateChanged

  private void statsButton5ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton5ItemStateChanged
    boolean bid = statsButton5.isSelected();
    if (bid) {
      statsField.setText(statsButton5Tip);
      listRes(5, resLoops, fullRes);
      statsButton5.setToolTipText(statsButton5Tip);
    }
  }//GEN-LAST:event_statsButton5ItemStateChanged

  private void gameTextField7MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField7MouseEntered
    if (gamePanel7.isEnabled() && curVals[7] > -1) {
      setgameTextField(eM.getDetail(curVals[7]));
    }
  }//GEN-LAST:event_gameTextField7MouseEntered

  private void gameTextField7MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField7MouseExited
    if (gamePanel7.isEnabled() && curVals[7] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameTextField7MouseExited

  private void gameSliderP7MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP7MouseEntered
    if (gamePanel7.isEnabled() && curVals[7] > -1) {
      setgameTextField(eM.getDetail(curVals[7]));
    }
  }//GEN-LAST:event_gameSliderP7MouseEntered

  private void gameSliderP7MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP7MouseExited
    if (gamePanel7.isEnabled() && curVals[7] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderP7MouseExited

  private void gameSliderS7MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS7MouseEntered
    if (gamePanel7.isEnabled() && curVals[7] > -1) {
      setgameTextField(eM.getDetail(curVals[7]));
    }
  }//GEN-LAST:event_gameSliderS7MouseEntered

  private void gameSliderS7MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS7MouseExited
    if (gamePanel7.isEnabled() && curVals[7] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderS7MouseExited

  private void jSeparator14MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSeparator14MouseEntered
    if (gamePanel7.isEnabled() && curVals[7] > -1) {
      setgameTextField(eM.getDetail(curVals[7]));
    }
  }//GEN-LAST:event_jSeparator14MouseEntered

  private void jSeparator14MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSeparator14MouseExited
    if (gamePanel7.isEnabled() && curVals[7] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_jSeparator14MouseExited

  private void gameTextField8MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField8MouseEntered
    if (gamePanel8.isEnabled() && curVals[7] > -1) {
      setgameTextField(eM.getDetail(curVals[8]));
    }
  }//GEN-LAST:event_gameTextField8MouseEntered

  private void gameTextField8MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField8MouseExited
    if (gamePanel8.isEnabled() && curVals[8] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameTextField8MouseExited

  private void gameSliderP8MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP8MouseEntered
    if (gamePanel8.isEnabled() && curVals[8] > -1) {
      setgameTextField(eM.getDetail(curVals[8]));
    }
  }//GEN-LAST:event_gameSliderP8MouseEntered

  private void gameSliderP8MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP8MouseExited
    if (gamePanel8.isEnabled() && curVals[8] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderP8MouseExited

  private void jSeparator15MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSeparator15MouseEntered
    if (gamePanel8.isEnabled() && curVals[8] > -1) {
      setgameTextField(eM.getDetail(curVals[8]));
    }
  }//GEN-LAST:event_jSeparator15MouseEntered

  private void jSeparator15MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSeparator15MouseExited
    if (gamePanel8.isEnabled() && curVals[8] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_jSeparator15MouseExited

  private void gameSliderS8MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS8MouseEntered
    if (gamePanel8.isEnabled() && curVals[8] > -1) {
      setgameTextField(eM.getDetail(curVals[8]));
    }
  }//GEN-LAST:event_gameSliderS8MouseEntered

  private void gameSliderS8MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS8MouseExited
    if (gamePanel8.isEnabled() && curVals[8] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderS8MouseExited

  private void gameButtonDownMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameButtonDownMouseEntered
    // TODO add your handling code here:
  }//GEN-LAST:event_gameButtonDownMouseEntered

  private void gameButtonDownMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameButtonDownMouseExited
    // TODO add your handling code here:
  }//GEN-LAST:event_gameButtonDownMouseExited

  private void gameTextField9MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField9MouseEntered
    if (gamePanel9.isEnabled() && curVals[9] > -1) {
      setgameTextField(eM.getDetail(curVals[9]));
    }
  }//GEN-LAST:event_gameTextField9MouseEntered

  private void gameTextField9MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameTextField9MouseExited
    if (gamePanel9.isEnabled() && curVals[9] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameTextField9MouseExited

  private void gameSliderP9MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP9MouseEntered
    if (gamePanel9.isEnabled() && curVals[9] > -1) {
      setgameTextField(eM.getDetail(curVals[9]));
    }
  }//GEN-LAST:event_gameSliderP9MouseEntered

  private void gameSliderP9MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderP9MouseExited
    if (gamePanel9.isEnabled() && curVals[9] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderP9MouseExited

  private void gameSliderS9MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS9MouseEntered
    if (gamePanel9.isEnabled() && curVals[9] > -1) {
      setgameTextField(eM.getDetail(curVals[9]));
    }
  }//GEN-LAST:event_gameSliderS9MouseEntered

  private void gameSliderS9MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameSliderS9MouseExited
    if (gamePanel9.isEnabled() && curVals[9] > -1) {
      revertgameTextField();
    };
  }//GEN-LAST:event_gameSliderS9MouseExited

  private void gameButtonUp1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameButtonUp1MouseClicked
    setgameTextField("This is to be filled with descriptions of the field over which the mouse hovers");

    gamePanelChange(eM.gameClanStatus, -1, gamePanels, gameTextFields, gameSlidersP, gameSlidersS, fullVals, curVals);
  }//GEN-LAST:event_gameButtonUp1MouseClicked

  private void gameButtonDownMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameButtonDownMouseClicked
    setgameTextField("This is to be filled with descriptions of the field over which the mouse hovers");
    gamePanelChange(eM.gameClanStatus, +1, gamePanels, gameTextFields, gameSlidersP, gameSlidersS, fullVals, curVals);
    printMem3();
  }//GEN-LAST:event_gameButtonDownMouseClicked

  private void statsButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statsButton10ActionPerformed
    boolean bid = statsButton10.isSelected();
    if (bid) {
      statsField.setText(statsButton10Tip);
      listRes(10, resLoops, fullRes);
      statsButton10.setToolTipText(statsButton10Tip);
    }
  }//GEN-LAST:event_statsButton10ActionPerformed

  private void statsButton5MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton5MouseEntered
    statsField.setText(statsTips(5));
  }//GEN-LAST:event_statsButton5MouseEntered

  private void statsButton0MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton0MouseEntered
    statsButton0.setToolTipText(statsTips(0));
  }//GEN-LAST:event_statsButton0MouseEntered

  private void statsButton1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton1MouseEntered
    statsButton1.setToolTipText(statsTips(1));
  }//GEN-LAST:event_statsButton1MouseEntered

  private void statsButton2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton2MouseEntered
    statsButton2.setToolTipText(statsTips(2));
  }//GEN-LAST:event_statsButton2MouseEntered

  private void statsButton3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton3MouseEntered
    statsButton3.setToolTipText(statsButton3Tip);
  }//GEN-LAST:event_statsButton3MouseEntered

  private void statsButton4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton4MouseEntered
    statsButton4.setToolTipText(statsTips(4));
  }//GEN-LAST:event_statsButton4MouseEntered

  private void statsButton6MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton6MouseEntered
    statsButton6.setToolTipText(statsTips(6));
  }//GEN-LAST:event_statsButton6MouseEntered

  private void statsButton6ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton6ItemStateChanged
    boolean bid = statsButton6.isSelected();
    if (bid) {
      statsField.setText(statsButton6Tip);
      listRes(6, resLoops, fullRes);
      statsButton6.setToolTipText(statsButton6Tip);
    }
  }//GEN-LAST:event_statsButton6ItemStateChanged

  private void statsButton7ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton7ItemStateChanged
    boolean bid = statsButton7.isSelected();
    if (bid) {
      listRes(7, resLoops, fullRes);
      statsButton7.setToolTipText(statsButton7Tip);
      statsField.setText(statsButton7Tip);
    }
  }//GEN-LAST:event_statsButton7ItemStateChanged

  private void statsButton7MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton7MouseEntered
    statsField.setText(statsTips(7));
  }//GEN-LAST:event_statsButton7MouseEntered

  private void statsButton8ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton8ItemStateChanged
    boolean bid = statsButton8.isSelected();
    if (bid) {
      statsField.setText(statsButton8Tip);
      statsButton8.setToolTipText(statsButton8Tip);
      listRes(8, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton8ItemStateChanged

  private void statsButton8MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton8MouseEntered
    statsField.setText(statsTips(8));
  }//GEN-LAST:event_statsButton8MouseEntered

  private void statsButton9ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton9ItemStateChanged
    boolean bid = statsButton9.isSelected();
    if (bid) {
      statsField.setText(statsButton9Tip);
      statsButton9.setToolTipText(statsButton9Tip);
      listRes(9, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton9ItemStateChanged

  private void statsButton9MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton9MouseEntered
    statsField.setText(statsTips(9));
  }//GEN-LAST:event_statsButton9MouseEntered

  private void statsButton10ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton10ItemStateChanged
    boolean bid = statsButton10.isSelected();
    if (bid) {
      statsField.setText(statsButton10Tip);
      statsButton10.setToolTipText(statsButton10Tip);
      listRes(10, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton10ItemStateChanged

  private void statsButton10MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton10MouseEntered
    statsField.setText(statsTips(10));
  }//GEN-LAST:event_statsButton10MouseEntered

  private void statsButton11ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton11ItemStateChanged
    boolean bid = statsButton11.isSelected();
    if (bid) {
      statsField.setText(statsButton11Tip);
      statsButton11.setToolTipText(statsButton11Tip);
      listRes(11, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton11ItemStateChanged

  private void statsButton11MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton11MouseEntered
    statsField.setText(statsTips(11));
  }//GEN-LAST:event_statsButton11MouseEntered

  private void controlPanelsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_controlPanelsStateChanged
    JTabbedPane sourceTabbedPane = (JTabbedPane) evt.getSource();
    int ix = sourceTabbedPane.getSelectedIndex();
    if (ix == 2) {
      statsButton0.setSelected(true);
      statsButton0.setToolTipText(statsTips(0));
      statsButton1.setToolTipText(statsTips(1));
      statsButton2.setToolTipText(statsTips(2));
      statsButton3.setToolTipText(statsTips(3));
      statsButton4.setToolTipText(statsTips(4));
      statsButton5.setToolTipText(statsTips(5));
      statsButton6.setToolTipText(statsTips(6));
      statsButton7.setToolTipText(statsTips(7));
      statsButton8.setToolTipText(statsTips(8));
      statsButton9.setToolTipText(statsTips(9));
      statsButton10.setToolTipText(statsTips(10));
      statsButton11.setToolTipText(statsTips(11));
      statsButton12.setToolTipText(statsTips(12));
      statsButton13.setToolTipText(statsTips(13));
      statsButton14.setToolTipText(statsTips(14));
      statsButton15.setToolTipText(statsTips(15));
      statsButton16.setToolTipText(statsTips(16));
      statsButton17.setToolTipText(statsTips(17));
      statsButton18.setToolTipText(statsTips(18));
      statsButton19.setToolTipText(statsTips(19));
      statsButton20.setToolTipText(statsTips(20));
    }
  }//GEN-LAST:event_controlPanelsStateChanged

  private void statsCtlButtonRun1YrMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsCtlButtonRun1YrMouseClicked
    System.out.println("in stats Run 1Year Mouse Clicked");
    if (eM.fatalError) {
      setFatalError();
    }
    else {
      runYears(5);
    }
  }//GEN-LAST:event_statsCtlButtonRun1YrMouseClicked

  private void statsCtlButtonRun1YrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statsCtlButtonRun1YrActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_statsCtlButtonRun1YrActionPerformed

  private void statsCtlButtonRun5YrMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsCtlButtonRun5YrMouseClicked
    System.out.println("in stats Run 1Year Mouse Clicked");
    if (eM.fatalError) {
      setFatalError();
    }
    else {
      runYears(1);
    }
  }//GEN-LAST:event_statsCtlButtonRun5YrMouseClicked

  private void displayPanel1OperationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_displayPanel1OperationActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_displayPanel1OperationActionPerformed

  private void displayPanel2OperationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_displayPanel2OperationActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_displayPanel2OperationActionPerformed

  private void displayPanel1SinceYearStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_displayPanel1SinceYearStartActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_displayPanel1SinceYearStartActionPerformed

  private void gameCtlButtonRun1Year1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gameCtlButtonRun1Year1ActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_gameCtlButtonRun1Year1ActionPerformed

  private void gameCtlButtonRun1Yr2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gameCtlButtonRun1Yr2MouseClicked
    // TODO add your handling code here:
  }//GEN-LAST:event_gameCtlButtonRun1Yr2MouseClicked

  private void gameCtlButtonRun1Yr2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gameCtlButtonRun1Yr2ActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_gameCtlButtonRun1Yr2ActionPerformed

  private void displayPanel2EconNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_displayPanel2EconNameActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_displayPanel2EconNameActionPerformed

  private void statsButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statsButton5ActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_statsButton5ActionPerformed

  private void statsButton12ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton12ItemStateChanged
    boolean bid = statsButton12.isSelected();
    if (bid) {
      statsField.setText(statsButton11Tip);
      statsButton11.setToolTipText(statsButton12Tip);
      listRes(12, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton12ItemStateChanged

  private void statsButton12MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton12MouseEntered
    statsField.setText(statsTips(12));
  }//GEN-LAST:event_statsButton12MouseEntered

  private void statsButton13ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton13ItemStateChanged
    boolean bid = statsButton13.isSelected();
    if (bid) {
      statsField.setText(statsButton13Tip);
      statsButton11.setToolTipText(statsButton11Tip);
      listRes(13, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton13ItemStateChanged

  private void statsButton13MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton13MouseEntered
    statsField.setText(statsTips(13));
  }//GEN-LAST:event_statsButton13MouseEntered

  private void statsButton14ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton14ItemStateChanged
    boolean bid = statsButton14.isSelected();
    if (bid) {
      statsField.setText(statsButton14Tip);
      statsButton11.setToolTipText(statsButton11Tip);
      listRes(14, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton14ItemStateChanged

  private void statsButton14MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton14MouseEntered
    statsField.setText(statsTips(14));
  }//GEN-LAST:event_statsButton14MouseEntered

  private void statsButton15ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton15ItemStateChanged
    boolean bid = statsButton15.isSelected();
    if (bid) {
      statsField.setText(statsButton15Tip);
      statsButton11.setToolTipText(statsButton11Tip);
      listRes(15, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton15ItemStateChanged

  private void statsButton15MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton15MouseEntered
    statsField.setText(statsTips(15));
  }//GEN-LAST:event_statsButton15MouseEntered

  private void statsButton16ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton16ItemStateChanged
    boolean bid = statsButton16.isSelected();
    if (bid) {
      statsField.setText(statsButton16Tip);
      statsButton11.setToolTipText(statsButton11Tip);
      listRes(16, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton16ItemStateChanged

  private void statsButton16MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton16MouseEntered
    statsField.setText(statsTips(16));
  }//GEN-LAST:event_statsButton16MouseEntered

  private void statsButton17ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton17ItemStateChanged
    boolean bid = statsButton17.isSelected();
    if (bid) {
      statsField.setText(statsButton17Tip);
      statsButton11.setToolTipText(statsButton11Tip);
      listRes(17, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton17ItemStateChanged

  private void statsButton17MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton17MouseEntered
    statsField.setText(statsTips(17));
  }//GEN-LAST:event_statsButton17MouseEntered

  private void statsButton18ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton18ItemStateChanged
    boolean bid = statsButton18.isSelected();
    if (bid) {
      statsField.setText(statsButton18Tip);
      statsButton11.setToolTipText(statsButton11Tip);
      listRes(18, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton18ItemStateChanged

  private void statsButton18MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton18MouseEntered
    statsField.setText(statsTips(18));
  }//GEN-LAST:event_statsButton18MouseEntered

  private void statsButton19ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton19ItemStateChanged
    boolean bid = statsButton19.isSelected();
    if (bid) {
      statsField.setText(statsButton19Tip);
      statsButton11.setToolTipText(statsButton11Tip);
      listRes(19, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton19ItemStateChanged

  private void statsButton19MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton19MouseEntered
    statsField.setText(statsTips(19));
  }//GEN-LAST:event_statsButton19MouseEntered

  private void statsButton20ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_statsButton20ItemStateChanged
    boolean bid = statsButton20.isSelected();
    if (bid) {
      statsField.setText(statsButton11Tip);
      statsButton11.setToolTipText(statsButton20Tip);
      listRes(20, resLoops, fullRes);
    }
  }//GEN-LAST:event_statsButton20ItemStateChanged

  private void statsButton20MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statsButton20MouseEntered
    statsField.setText(statsTips(20));

  }//GEN-LAST:event_statsButton20MouseEntered

  private void statsField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_statsField2ActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_statsField2ActionPerformed

  private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
    System.exit(1);
  }//GEN-LAST:event_formWindowClosed
  static Boolean resetOut = false;

  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    /* Set the Nimbus look and feel --change to animation*/

    //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
    /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
     * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
     */
    try {

      PrintStream o, er, console, errNew;
      if(E.debugOutput){    
          o = new PrintStream(new File("C:/Users/albert/Desktop/NetbeansOut/myOut.txt"));
      
      console = System.out;
      er = new PrintStream(new File("C:/Users/albert/Desktop/NetbeansOut/MyErr.txt"));
      errNew = System.err;
      if (resetOut) {
        System.setOut(o);
        System.setErr(er);
      }}

      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {

        if ("Nimbus".equals(info.getName())) {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
      /* Create and display the form */
      java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
          new StarTrader().setVisible(true);
        }
      });
    }
    catch (ClassNotFoundException ex) {
      java.util.logging.Logger.getLogger(StarTrader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    catch (InstantiationException ex) {
      java.util.logging.Logger.getLogger(StarTrader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    catch (IllegalAccessException ex) {
      java.util.logging.Logger.getLogger(StarTrader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    catch (javax.swing.UnsupportedLookAndFeelException ex) {
      java.util.logging.Logger.getLogger(StarTrader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    catch (FileNotFoundException ex) {
      java.util.logging.Logger.getLogger(StarTrader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    //</editor-fold>

    ;
  }

  public void second() {
    java.awt.EventQueue.invokeLater(new Runnable() {
      public void run() {
        new StarTrader().setVisible(true);
      }
    });
  }


  // Variables declaration - do not modify//GEN-BEGIN:variables
  protected javax.swing.JSlider LogDLen2Slider;
  protected javax.swing.JSlider LogDlen1Slider;
  protected javax.swing.JLabel Start1Name;
  protected javax.swing.JLabel Start2Name;
  protected javax.swing.JPanel clan;
  protected javax.swing.JRadioButton clanBlue;
  protected javax.swing.ButtonGroup clanButtonGroupActiveClan;
  protected javax.swing.JRadioButton clanGreen;
  protected javax.swing.JLabel clanLabelP3;
  protected javax.swing.JLabel clanLabelP4;
  protected javax.swing.JLabel clanLabelS4;
  protected javax.swing.JRadioButton clanOrange;
  protected javax.swing.JPanel clanPanel0;
  protected javax.swing.JPanel clanPanel1;
  protected javax.swing.JPanel clanPanel2;
  protected javax.swing.JPanel clanPanel3;
  protected javax.swing.JPanel clanPanel4;
  protected javax.swing.JRadioButton clanRed;
  protected javax.swing.JSlider clanSliderP0;
  protected javax.swing.JSlider clanSliderP1;
  protected javax.swing.JSlider clanSliderP2;
  protected javax.swing.JSlider clanSliderP3;
  protected javax.swing.JSlider clanSliderP4;
  protected javax.swing.JSlider clanSliderS0;
  protected javax.swing.JSlider clanSliderS1;
  protected javax.swing.JSlider clanSliderS2;
  protected javax.swing.JSlider clanSliderS3;
  protected javax.swing.JSlider clanSliderS4;
  protected javax.swing.JTextArea clanTextField;
  protected javax.swing.JTextField clanTextField0;
  protected javax.swing.JTextField clanTextField1;
  protected javax.swing.JTextField clanTextField2;
  protected javax.swing.JTextField clanTextField3;
  protected javax.swing.JTextField clanTextField4;
  protected javax.swing.JScrollPane clanTextPane;
  protected javax.swing.JRadioButton clanYellow;
  protected javax.swing.JTabbedPane controlPanels;
  protected javax.swing.JPanel display;
  protected javax.swing.JPanel displayPanel0;
  protected javax.swing.JTextArea displayPanel0Text;
  protected javax.swing.JTextField displayPanel0Text1;
  protected javax.swing.JPanel displayPanel1;
  protected javax.swing.JTextField displayPanel1EconName;
  protected javax.swing.JTextField displayPanel1Operation;
  protected javax.swing.JTextField displayPanel1SinceYearStart;
  protected javax.swing.JPanel displayPanel2;
  protected javax.swing.JTextField displayPanel2EconName;
  protected javax.swing.JTextField displayPanel2Operation;
  protected javax.swing.JTextField displayPanel2SinceYearStart;
  protected javax.swing.JPanel game;
  protected java.awt.Button gameButtonDown;
  protected javax.swing.ButtonGroup gameButtonGroup;
  protected java.awt.Button gameButtonUp;
  protected java.awt.Button gameButtonUp1;
  protected javax.swing.JButton gameCtlButtonRun1Year1;
  protected javax.swing.JButton gameCtlButtonRun1Yr2;
  protected javax.swing.JButton gameCtlButtonRun5Years1;
  protected javax.swing.JLabel gameLabelP5;
  protected javax.swing.JLabel gameLabelP6;
  protected javax.swing.JLabel gameLabelP7;
  protected javax.swing.JLabel gameLabelS5;
  protected javax.swing.JLabel gameLabelS6;
  protected javax.swing.JLabel gameLabelS7;
  protected javax.swing.JLabel gameLabelS8;
  protected javax.swing.JRadioButton gameMaster;
  protected javax.swing.JPanel gamePanel0;
  protected javax.swing.JPanel gamePanel1;
  protected javax.swing.JPanel gamePanel2;
  protected javax.swing.JPanel gamePanel3;
  protected javax.swing.JPanel gamePanel4;
  protected javax.swing.JPanel gamePanel5;
  protected javax.swing.JPanel gamePanel6;
  protected javax.swing.JPanel gamePanel7;
  protected javax.swing.JPanel gamePanel8;
  protected javax.swing.JPanel gamePanel9;
  protected javax.swing.JPanel gamePanelBottomPanel;
  protected javax.swing.JPanel gamePanelRearPanel;
  protected javax.swing.JSlider gameSliderP0;
  protected javax.swing.JSlider gameSliderP1;
  protected javax.swing.JSlider gameSliderP2;
  protected javax.swing.JSlider gameSliderP3;
  protected javax.swing.JSlider gameSliderP4;
  protected javax.swing.JSlider gameSliderP5;
  protected javax.swing.JSlider gameSliderP6;
  protected javax.swing.JSlider gameSliderP7;
  protected javax.swing.JSlider gameSliderP8;
  protected javax.swing.JSlider gameSliderP9;
  protected javax.swing.JSlider gameSliderS0;
  protected javax.swing.JSlider gameSliderS1;
  protected javax.swing.JSlider gameSliderS2;
  protected javax.swing.JSlider gameSliderS3;
  protected javax.swing.JSlider gameSliderS4;
  protected javax.swing.JSlider gameSliderS5;
  protected javax.swing.JSlider gameSliderS6;
  protected javax.swing.JSlider gameSliderS7;
  protected javax.swing.JSlider gameSliderS8;
  protected javax.swing.JSlider gameSliderS9;
  protected javax.swing.JTextArea gameTextField;
  protected javax.swing.JTextField gameTextField0;
  protected javax.swing.JTextField gameTextField1;
  protected javax.swing.JTextField gameTextField2;
  protected javax.swing.JTextField gameTextField3;
  protected javax.swing.JTextField gameTextField4;
  protected javax.swing.JTextField gameTextField5;
  protected javax.swing.JTextField gameTextField6;
  protected javax.swing.JTextField gameTextField7;
  protected javax.swing.JTextField gameTextField8;
  protected javax.swing.JTextField gameTextField9;
  protected javax.swing.JScrollPane gameTextPane;
  protected javax.swing.JLabel gameToLabelPlanet;
  protected javax.swing.JLabel gameTopLabelShip;
  protected javax.swing.JTextField gameTopRightFill;
  protected javax.swing.JPanel gameXtraPanel1;
  protected javax.swing.ButtonGroup initButtonGroupPorS;
  protected javax.swing.JScrollPane jScrollPane1;
  protected javax.swing.JSeparator jSeparator1;
  protected javax.swing.JSeparator jSeparator10;
  protected javax.swing.JSeparator jSeparator11;
  protected javax.swing.JSeparator jSeparator13;
  protected javax.swing.JSeparator jSeparator14;
  protected javax.swing.JSeparator jSeparator15;
  protected javax.swing.JSeparator jSeparator16;
  protected javax.swing.JSeparator jSeparator2;
  protected javax.swing.JSeparator jSeparator3;
  protected javax.swing.JSeparator jSeparator4;
  protected javax.swing.JSeparator jSeparator5;
  protected javax.swing.JSeparator jSeparator6;
  protected javax.swing.JSeparator jSeparator7;
  protected javax.swing.JSeparator jSeparator8;
  protected javax.swing.JSeparator jSeparator9;
  protected javax.swing.JRadioButton logActionAdd;
  protected javax.swing.JRadioButton logActionDel;
  protected javax.swing.JRadioButton logActionJump;
  protected javax.swing.ButtonGroup logBGactions;
  protected javax.swing.ButtonGroup logButtonGroup1or2;
  protected javax.swing.JSlider logDLevel1Slider;
  protected javax.swing.JSlider logDLevel2Slider;
  protected javax.swing.JTable logDisplayTable;
  protected javax.swing.JLabel logDlen1;
  protected javax.swing.JLabel logDlen2;
  protected javax.swing.JLabel logDlevel1;
  protected javax.swing.JLabel logDlevel2;
  protected javax.swing.DefaultListModel namesList;
  protected javax.swing.JList logEnvirnNamesList;
  protected javax.swing.JSpinner logM1Spinner;
  protected javax.swing.JSpinner logM2Spinner;
  protected javax.swing.JScrollPane logNamesScrollPanel;
  protected javax.swing.JRadioButton logRadioButtonStart1;
  protected javax.swing.JRadioButton logRadioButtonStart2;
  protected javax.swing.JScrollPane logTableScrollPanel;
  protected javax.swing.JPanel stats;
  protected javax.swing.JRadioButton statsButton0;
  protected javax.swing.JRadioButton statsButton1;
  protected javax.swing.JRadioButton statsButton10;
  protected javax.swing.JRadioButton statsButton11;
  protected javax.swing.JRadioButton statsButton12;
  protected javax.swing.JRadioButton statsButton13;
  protected javax.swing.JRadioButton statsButton14;
  protected javax.swing.JRadioButton statsButton15;
  protected javax.swing.JRadioButton statsButton16;
  protected javax.swing.JRadioButton statsButton17;
  protected javax.swing.JRadioButton statsButton18;
  protected javax.swing.JRadioButton statsButton19;
  protected javax.swing.JRadioButton statsButton2;
  protected javax.swing.JRadioButton statsButton20;
  protected javax.swing.JRadioButton statsButton3;
  protected javax.swing.JRadioButton statsButton4;
  protected javax.swing.JRadioButton statsButton5;
  protected javax.swing.JRadioButton statsButton6;
  protected javax.swing.JRadioButton statsButton7;
  protected javax.swing.JRadioButton statsButton8;
  protected javax.swing.JRadioButton statsButton9;
  protected javax.swing.ButtonGroup statsButtonGroupClans;
  protected javax.swing.ButtonGroup statsButtonGroupReportNumber;
  protected javax.swing.JButton statsCtlButtonRun1Yr;
  protected javax.swing.JButton statsCtlButtonRun5Yr;
  protected javax.swing.JTextField statsField;
  protected javax.swing.JTextField statsField2;
  protected javax.swing.JScrollPane statsScrollPanel;
  protected javax.swing.JTable statsTable1;
  protected javax.swing.JPanel story;
  protected javax.swing.JTextArea storyTextField1;
  protected javax.swing.JScrollPane storyTextPane;
  protected javax.swing.JTextField storyVersionField;
  // End of variables declaration//GEN-END:variables

  private void logM1SpinnerStateChanged(javax.swing.event.ChangeEvent evt) {
    try {
      String sss = evt.toString();
      JSpinner source = (JSpinner) evt.getSource();
      NumberFormat format = NumberFormat.getIntegerInstance(Locale.US);
      format.setParseIntegerOnly(true);
      int m = format.parse(source.getValue().toString()).intValue();
      int start2 = format.parse(logM1Spinner.getValue().toString()).intValue();
      System.out.println(since() + " logM1SpinnerStateChanged=" + eM.logEnvirn[0].logM[0] + " lev=" + eM.logEnvirn[0].logLev[0] + "m=" + m + " start2=" + start2);
      saveLogM(0, m);
      displayLog();

    }
    catch (Exception ex) {
      System.out.flush();
      System.err.flush();
      System.err.println(new Date().toString() + since() + " cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + ", addlErr=" + eM.addlErr);
      ex.printStackTrace(System.err);
      System.err.flush();
      setStopExe();
    }

  }

  public void logM1MouseClicked(java.awt.event.MouseEvent evt) {
    try {
    }
    catch (Exception ex) {
      System.out.flush();
      System.err.flush();
      System.err.println(new Date().toString() + since() + " cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + ", addlErr=" + eM.addlErr);
      ex.printStackTrace(System.err);
      System.err.flush();
      setStopExe();
    }
  }

  String statsTips(int num) {
    String rtn = "Year " + eM.year + " :" + statsButtonsTips[num];
    return rtn;
  }

  Color redish = new Color(255, 153, 153);

  /**
   * stop core execution by callin setFatalError(Color);
   *
   */
  void setFatalError() {
    setFatalError(redish);
  }

  /**
   * stop core program execution but not triggers from buttons etc.
   *
   */
  void setStopExe() {
    EM.stopExe = true;
  }

  /**
   * stop all execution of interrupts and core program by setting the
   * eM.fatalError flag and eM.stopExe
   *
   * @param rrr color to set in log table and other tab views
   */
  void setFatalError(Color rrr) {
    // change the background color of log to red for fatal error
    if (eM.fatalError) {
      eM.stopExe = true;
      eM.fatalError = true;
      doStop = true;
      fatalError = true;
      System.err.println("request rejected, due to a previous fatal error");
      throw new WasFatalError();
    }
    eM.stopExe = true;
    eM.fatalError = true;
    doStop = fatalError = true;
    Color redish = new Color(255, 204, 204);
    Color r4 = new Color(255, 204, 154);
    eM.logEnvirn[0] = eM.curEcon;
    eM.logEnvirn[1] = eM.curEcon;
    eM.hists[0] = eM.logEnvirn[0].hists[0];
    eM.hists[1] = eM.logEnvirn[1].hists[0];
    eM.logEnvirn[0].logLen[0] = 90;
    LogDlen1Slider.setValue(90);
    eM.logEnvirn[0].logLen[1] = 5;
    eM.hists[0].add(new History(3, "final string", "ERROR ==============================="));
    int siz = eM.hists[0].size();
    int siz1 = siz - 87;
    siz = siz1 < 0 ? 0 : siz1;
    eM.logEnvirn[0].logM[0] = siz;
    logM1Spinner.setValue(siz);
    // change to display the log of the erring Econ
    // do not try to display a log that does not exist
    Boolean ll = eM.logEnvirn[0].logM[0] > 50;
    if (ll) {
      eM.logEnvirn[0].logLev[0] = 20;
      logDLevel1Slider.setValue(20);
      displayLog();
    }
    logDisplayTable.setBackground(rrr);
    logTableScrollPanel.setBackground(rrr);
    game.setBackground(redish);
    stats.setBackground(rrr);
    statsTable1.setBackground(redish);
    gameToLabelPlanet.setBackground(r4);
    gameTopLabelShip.setBackground(rrr);
    controlPanels.getComponent(0).setBackground(new Color(255, 204, 152));
    controlPanels.setBackground(rrr);
    display.setBackground(rrr);
    displayPanel1.setBackground(rrr);
    displayPanel2.setBackground(rrr);
    displayPanel1EconName.setBackground(rrr);
    displayPanel1Operation.setBackground(rrr);
    displayPanel2EconName.setBackground(rrr);
    displayPanel2Operation.setBackground(rrr);
    displayPanel1Operation.setText("fatalError");
    controlPanels.revalidate();
    controlPanels.repaint();

    System.err.flush();
    System.out.flush();
    System.err.println("request rejected, due to a  fatal error");
    throw new WasFatalError();
    //  controlPanels.setSelectedComponent(log);
  }

  //obsolete
  // public SwingWorker worker = new SwingWorker<Boolean,Boolean>() {
  public class Worker extends SwingWorker<Boolean, Boolean> {

    @Override
    public Boolean doInBackground() throws Exception {
      runBackgroundYears(yearsToRun);
      return true;
    }
  };  // end new SwingWorker

  /**
   * class of the animation thread call runYears2 which starts the background
   * thread the calls doYear then continues with animation depending on the
   * stateConst
   *
   */
  public class RunYrs extends Thread {

    public void run() {
      runYears2();
    }
  }

  /**
   * class of the background thread invokes runBackgroundYears which invokes the
   * proper number of runYear -> doYear which sets stateConst before each
   * different loop or routine in the year.
   */
  public class RunYrs2 extends Thread {

    public void run() {
      runBackgroundYears(yearsToRun);

    }
  }

  Worker jake;

  /**
   * called from field tab stats or game click on 1 yr or 5 yrs In
   * EventDispatchThead, so start a animation thread and end The animation
   * thread continues and starts the background thread
   *
   * @param nYears number of do years to run
   */
  void runYears(int nYears) {
    try {
      stateConst = WAITING;
      yearsToRun = nYears;
      EM.runYearsTime = (new Date()).getTime();
      System.out.println("$$$$$$$$$$$$$ runYears;" + since() + " at start stateConst=" + stateConst + " stateCnt =" + stateCnt + " stateName=" + stateStringNames[stateConst] + " year=" + eM.year);
      E.myTest(!javax.swing.SwingUtilities.isEventDispatchThread(), "not eventDispatchThread");
      RunYrs rYrs = new RunYrs();
      rYrs.setPriority(3);
      rYrs.start();  // start runYears2
    }
    catch (Exception ex) {
      if (!resetOut) {
        System.out.flush();
        System.err.flush();
      }
      System.err.println(new Date().toString() + "Exception " + ex.toString() + " found " + eM.addlErr);
      ex.printStackTrace(System.err);
      if (!resetOut) {
        System.out.flush();
        System.err.flush();
      }
      setFatalError();
    }
  }

  String prevWasHere = "";

  void setEconState(int stateConst) {
    Econ curEc = EM.curEcon;
    curEconName = (EM.curEcon == null ? "notYet" : EM.curEcon.name);
    if (stateConst == prevState && curEconName.equals(prevEconName) && eM.wasHere == prevWasHere && stateConst != STATS && stateConst != WAITING && stateConst != STOPPED && stateConst != FATALERR) {
      sameEconState++;
      if (curEc != null && curEc.name.equals(prevEconName) && sameEconState > 40) {
        E.myTest(true, "STUCK at:" + stateStringNames[stateConst] + " " + curEconName + ", cnt=" + sameEconState + " millisecs=" + (new Date().getTime() - startEconState));
      }
    }
    else {
      prevEconName = curEconName;
      prevWasHere = eM.wasHere; // move the reference
      stateCnt = 0;
      prevState = stateConst;
      sameEconState = 0;
      startEconState = (new Date()).getTime();
    }
  }

  /**
   * start of the animation thread the animation thread starts the background
   * thread RunYrs2 Then it watches stateConst and updates the display tab
   *
   */
  void runYears2() {
    try {
      Econ curEc = eM.curEcon;;
      if(E.debugThreads)E.sysmsg("$$$$$$$$$$$$$ runYears2;" + since() + " at start stateConst=" + stateConst + " stateCnt =" + stateCnt + " stateName=" + stateStringNames[stateConst] + " year=" + eM.year);
      E.myTest(javax.swing.SwingUtilities.isEventDispatchThread(), "is eventDispatchThread");
      paintCurDisplay(eM.curEcon);
      System.out.println("###################runYears2;" + since() + " stateConst=" + stateConst + " stateCnt =" + stateCnt + " stateName=" + stateStringNames[stateConst] + " year=" + eM.year);
      RunYrs2 rYrs = new RunYrs2();
      rYrs.setPriority(3);
      rYrs.start();  // start the background job
      stateConst = STARTING;
      paintCurDisplay(eM.curEcon);
      // now continue EDS thread with updating display
      Boolean done = false, did = false;
      for (stateCnt = 0; !eM.fatalError && !eM.stopExe && !done && !fatalError; stateCnt++) {
        curEc = eM.curEcon;
        if (curEc != null) {
          curEconName = curEc.name;
        }
        // System.out.println("***************runYears;" + since() + " " + stateStringNames[stateConst]+ stateCnt + " year=" + eM.year + ", econ=" + prevEconName);
        setEconState(stateConst);
        /*
          if(stateConst == prevState  && curEc != null && curEconName.equals(prevEconName)) {
            sameEconState++;
            if(curEc != null && curEc.name.equals(prevEconName)&& sameEconState > 40){
              E.myTest(true,"STUCK at:" + stateStringNames[stateConst] + " "+ curEconName + ", cnt=" + sameEconState + " millisecs=" +(new Date().getTime() - startEconState) ); 
            }
          } else {
            stateCnt = 0;
            prevState = stateConst;
            sameEconState=0;
          }
          
          if(curEc != null){prevEconName = curEc.name;}
         */
        if(E.debugThreads)E.sysmsg("$$$$$$$$$$$$$runYears2 " + since() + " " + stateStringNames[stateConst] + sameEconState + " " + EM.wasHere);
        paintCurDisplay(eM.curEcon);
        switch (stateConst) {
          case WAITING:
          case STARTING:
           // paintCurDisplay(eM.curEcon);
             {
              Thread.sleep(500);
            }
            break;
          case CREATING:
           // paintCurDisplay(eM.curEcon);
            Thread.sleep(blip10);
            break;
          case FUTUREFUNDCREATE:
           // paintCurDisplay(eM.curEcon);
            Thread.sleep(blip);
            break;
          case STARTYR:
           // paintCurDisplay(eM.curEcon);
            Thread.sleep(blip);
            did = true;
            break;
          case SEARCH:
           // paintCurDisplay(eM.curEcon);
            Thread.sleep(blip);
            did = false;
            break;
          case SWAPS:
          //  paintCurDisplay(eM.curEcon);
            Thread.sleep(blip5);
            did = false;
            break;
          case TRADING:
           // paintCurDisplay(eM.curEcon);
            Thread.sleep(blip10);
            did = false;
            break;
          case ENDYR:
          //  paintCurDisplay(eM.curEcon);
             {
              Thread.sleep(blip);
            }
            did = true;
            break;
          case STATS:
            done = true;
            if(E.debugThreads)E.sysmsg("$$$$$$$$$$$$$$$runYears2;" + since() + " STATS stateConst=" + stateConst + " stateCnt =" + stateCnt + " stateName=" + stateStringNames[stateConst] + " year=" + eM.year);
            listRes(fullRes);
            break;
          default:
            if (did) {
              done = true;
            }
            else {
              Thread.sleep(blip30);
            }
            if(E.debugThreads)E.sysmsg("$$$$$$$$$$$$$$$runYears2;" + since() + " DEFAULT; stateConst=" + stateConst + " stateCnt =" + stateCnt + " stateName=" + stateStringNames[stateConst] + " year=" + eM.year);

        }
      }
    }
    catch (Exception ex) {
      if (!resetOut) {
        System.out.flush();
        System.err.flush();
      }
      System.err.println(new Date().toString() + "runYears2 Exception " + ex.toString() + " found " + eM.addlErr);
      ex.printStackTrace(System.err);
      if (!resetOut) {
        System.out.flush();
        System.err.flush();
      }
      setFatalError();
    }
  }

  protected void runBackgroundYears(int nYears) {
    getGameValues(curVals, gamePanels, gameTextFields, gameSlidersP, gameSlidersS);
    stateConst = STARTING;
    E.myTest(javax.swing.SwingUtilities.isEventDispatchThread(), "is eventDispatchThread");
    for (int nn = 0; nn < nYears && !eM.fatalError && !eM.stopExe && !doStop && !fatalError; nn++) {
      System.out.println("&&&&&&&&&&&& runBackroundYears" + since() + "run year="
              + (eM.year + 1)
              + "##########################");
      runYear();
    }
    stateConst = STATS;
    printMem3(); // goes to doYears
  }

  int initialPlanetShip = 0;
  int nextPlanetShip = 1;

  /**
   * return a new Econ enter with preset counts
   *
   * @param cash The worth of the new Econ to be created
   * @param clan The clan of the new Econ
   * @return a new Econ reference
   */
  Econ newEcon(double[] cash, int clan) {
    // eM.porsCnt[0] = eM.planets.size();
    // eM.porsCnt[1] = eM.ships.size();
    int lEcons = eM.econCnt;// = eM.econs.size();
    int pors = S;
    int eClan = -5;

    // select planet if any of the ship fracs are exceeded
    // select ship only if all shipFracs are not met
    // game shipFracs, clanClanShipFrac, clanAllShipFrac
    pors = eM.clanCnt[clan] < 1
      ? E.P
      : // P clanCnt < 1
      eM.porsClanCnt[S][clan] < 1
        ? E.S
        : // S clanCnt[S} < 1
        eM.porsCnt[S] / eM.econCnt > eM.clanAllShipFrac[S][clan]
          ? E.P // P ships/tot > clanAllShipFrac 
          : eM.porsCnt[S] / eM.econCnt > eM.gameShipFrac[S]
            ? E.P
            : // P ships/tot > gameShipFrac
            eM.porsClanCnt[S][clan] / eM.clanCnt[clan] > eM.clanShipFrac[S][clan]
              ? E.P
              : // clan ship cnt/ clanCnt > clanShipFrac
              E.S;
    // select a ship only if all the following conditions are true

    double sFrac1 = 99.;
    double sFrac2 = 99.;
    double sFrac3 = 99.;
    // use .0001 to prevent divide by zero
    double sFrac1a = (sFrac1 = (.0001 + eM.porsCnt[S]) / (.0001 + eM.econCnt));
    double sFrac2a = (sFrac2 = (.0001 + eM.porsCnt[S]) / (.0001 + eM.econCnt));
    double sFrac3a = (sFrac3 = (.0001 + eM.porsClanCnt[S][clan]) / (.0001 + eM.clanCnt[clan]));
    pors = (eM.clanCnt[clan] > 0
            && eM.porsClanCnt[P][clan] > 0
            && sFrac1 < eM.clanAllShipFrac[P][clan]
            && sFrac2 < eM.gameShipFrac[P]
            && sFrac3 < eM.clanShipFrac[P][clan]) ? E.S : E.P;

    System.out.println("%%%%lPlanets=" + eM.porsCnt[P]
            + " lShips=" + eM.porsCnt[S]
            + " lClanPlanets=" + eM.porsClanCnt[P][clan]
            + " lEconCnt=" + eM.econCnt
            + " lClanCnt[" + clan + "]=" + eM.clanCnt[clan]
            + " lClanShips=" + eM.porsClanCnt[S][clan]
            + " lCEcons=" + eM.clanCnt[clan]
            + " clanShipsFrac=" + eM.df(sFrac3)
            + " clanShipFrac[P][clan] =" + eM.clanShipFrac[P][clan]
            + " pors=" + pors
            + "+++"
            + (eM.clanCnt[clan] == 0 ? "P0000" : "S"
                    + (eM.porsClanCnt[P][clan] == 0 ? "P000" : "S"
                            + (sFrac1 < eM.clanAllShipFrac[P][clan] ? "S" : "P")
                            + (sFrac2 < eM.gameShipFrac[P] ? "S" : "P")
                            + (sFrac3 < eM.clanShipFrac[P][clan] ? "S" : "P"))));

    double xpos = -9999.;
    eM.curEcon = null;
    // now try to find a dead economy to use instead of recreating one
    if (pors == E.P) {
      for (Econ n : eM.econs) {
        if (n.getDie() && n.getPors() == E.P && n.getDAge() > 2) {
          eM.curEcon = n;   // take a dead one
          System.out.println("found dead Planet cnt=" + n + " name=" + n.name + " dage=" + n.getDAge());
          break;
        }
      }
    }
    else if ((eM.curEcon == null) && pors == E.S) {
      for (Econ n : eM.econs) {
        if (n.getDie() && n.pors == E.S && n.getDAge() > 2) {
          eM.curEcon = n;
          System.out.println("found dead Ship cnt=" + n + " name=" + n.name + " dage=" + n.getDAge());
          break;
        }
      }
    }
    if (eM.curEcon == null) {  // no dead one found
      eM.curEcon = new Econ();
      eM.econs.add(eM.curEcon); // add to the main list
      eM.econCnt++;
    }
    NumberFormat nameF = NumberFormat.getNumberInstance();
    nameF.setMinimumIntegerDigits(3);
    String name = (pors == 0 ? "P0" : "S0") + nameF.format(eM.nameCnt++);
    eM.curEcon.init(this, eM, name, clan, eM.econCnt, pors, xpos, eM.difficultyPercent[0], cash[pors]);
    startEconState = (new Date()).getTime();
    // now update counts planets and ships
    Econ t = eM.curEcon;
    if (!t.getDie()) {
      eM.porsClanCnt[t.pors][t.clan]++;
      eM.clanCnt[t.clan]++;
      eM.porsCnt[t.pors]++;

      if (t.pors == P) {
        eM.planets.add(t);
      }
      else {
        eM.ships.add(t);
      }
    }
    return eM.curEcon;
  }

  /**
   * get a reference to Class E
   *
   * @return reference to Class E
   */
  public E getE() {
    return eE;
  }

  /**
   * return the seconds since start of StarTrader
   *
   * @return seconds nnn.mmm
   */
  public String since() {
    return since("since game start", startTime);
  }

  /**
   * return the seconds since the start of RunYear
   *
   * @return seconds nnn.mmm
   */
  public String sinceRunYear() {
    return since("", startYear);
  }

  /**
   * format the seconds since a given recorded time
   *
   * @param prefix a String to previx the number
   * @param startTime the original start time to be reported
   * @return a string of the now - startTime with miliseconds as a fraction
   */
  public String since(String prefix, long startTime) {
    long now = (new Date()).getTime();
    double nu = (now - startTime);
    String sAge = (eM.curEcon == null ? " " : " " + eM.curEcon.name + " age=" + eM.curEcon.age);
    return prefix  + " secs=" + E.mf(nu * .001)+ sAge;
  }

  protected double mapHealth(double h) {
    double[] hh = {-10., -.5, -.3, -.3, -.2, -.1, 0., .3, .5, .7, 2., 10., 100., 1000., 10000};
    double[] h2 = {1.5, 1.45, 1.4, 1.35, 1.3, 1.25, 1.2, 1.15, 1.1, 1.05, 1, .95, .90, .85, .8};
    for (int i = 0; i < hh.length; i++) {
      if (h < hh[i]) {
        return h2[i];
      }
    }
    return .75;
  }

  /**
   * return the current value of the AssetsYr loop n
   *
   * @return
   */
  int getN() {
    return eM.curEcon.getN();
  }

  /**
   * return the pointer to the hist of the current Economy (Planet or Ship)
   *
   * @return
   */
  ArrayList<History> getHist() {
    return eM.curEcon.getHist();
  }

  /**
   * display a hist file reference for a given economy using displayLog
   *
   * @param table table to be displayed with displayLog
   * @param his unused
   * @param rowToM unused
   * @param startM unused
   * @param maxs unused
   * @param levs unused
   * @return number of last row displayed
   */
  protected int displayHistory(javax.swing.JTable table, ArrayList<History> his, int[] rowToM, int startM, int[] maxs, int[] levs) {
    return displayLog(table);
  }

  ;
   public int displayLog() {
    return displayLog(logDisplayTable);
  }

  int k;

  protected int displayLog(javax.swing.JTable table) {
    //  pLogDisplayTable = table;
    //  logHistoryHist = his;
    NumberFormat dispFraction = NumberFormat.getNumberInstance();
    dispFraction.setMinimumFractionDigits(2);
    NumberFormat whole = NumberFormat.getNumberInstance();
    whole.setMaximumFractionDigits(0);
    table.getColumnModel().getColumn(0).setMinWidth(180);
    table.getColumnModel().getColumn(0).setPreferredWidth(180);
    int tableRowCount = table.getRowCount();
    E.logSizeHis[0] = eM.hists[0].size();
    E.logSizeHis[1] = eM.hists[1].size();
    E.myTest(eM.hists[0] != eM.logEnvirn[0].getHist(), " Error hists[0] not match hist for " + eM.logEnvirn[0].name);
    E.myTest(eM.hists[1] != eM.logEnvirn[1].getHist(), " Error hists[1] not match hist for " + eM.logEnvirn[1].name);
    //  E.myTest(E.logSizeHis[0] == 0, " error Empty hist " + eM.logEnvirn[0].name);
    //  E.myTest(E.logSizeHis[1] == 0, " error Empty hist " + eM.logEnvirn[1].name);
    E.logLen[0] = Math.min(eM.logEnvirn[0].logLen[0], tableRowCount - 1);
    E.logLen[1] = Math.min(eM.logEnvirn[1].logLen[1], tableRowCount);
    E.logLen[2] = Math.min(E.logLen[0] + 1 + E.logLen[1], tableRowCount);

    // M or m represent the line number in the display table= table
    E.logM[0] = eM.logEnvirn[0].logM[0];
    E.logM[1] = eM.logEnvirn[1].logM[1];
    int lead = 250; // prior numbers we look for titles
    int logLev[] = new int[2];
    logLev[0] = E.logLev[0] = eM.logEnvirn[0].logLev[0];
    logLev[1] = E.logLev[1] = eM.logEnvirn[1].logLev[1];
    int logRowStart[] = {0, E.logLen[0] + 1};
    int logRowEnd[] = {E.logLen[0], E.logLen[2]};
    int prev20 = -5;
    int row = 0, rowsStart = 0, m;
    for (k = 0; k < 2; k++) {
      if (E.logM[k] < 0) {
        E.logM[k] = 0;
      }
      if (E.logSizeHis[k] < 1) {
        System.out.printf("hist %d is empty\n", k);
      }
      else {
        int ma, mb, mc, r0, r1, rp;
        r1 = rp = r0 = -1;
        boolean showLine = false;
        int maxLev = logLev[k];
        // int rowsStart = logRowStart[k];
        rowsStart = row;
        int rowsEnd = logRowEnd[k];
        int mEnd = E.logSizeHis[k];
        //set start of look for a 20 title
        ma = (mc = ((mb = E.logM[k]) - lead)) < 0 ? 0 : mc;
        // don't go over the end of the hist
        ma = mEnd > ma ? ma : mEnd - 1;
        //     row = logDisplayStart[k];
        System.out.println(since() + " display history k=" + k + " size=" + mEnd + " max=" + maxLev + " lev=" + eM.hists[k].get(ma).level + " m=" + ma + " row=" + row);
        System.out.flush();
        int drlev = 4;
        int md = 50000;
        // int row = rowsStart;
        // row = rowsStart;
        for (m = ma; row < rowsEnd && m < mEnd; m++) {
          md = m;
          History dr = eM.hists[k].get(m);
          E.myTest(dr == null, "null dr at k=" + k + " m=" + m);
          drlev = dr.level;
          if (drlev == 20 && maxLev > 5) {
            showLine = true;
            if (m < mb) {
              row = rowsStart;
            }
          }
          else if (drlev == 1 && rp != 1 && m < mb) {
            showLine = true;
            row = rowsStart;
          }
          else if ((drlev == 1 || drlev == 2) && rp == 1 && row == rowsStart + 1 && m < mb) {
            showLine = true;
          }
          else if (drlev <= maxLev && m >= mb) {
            showLine = true;
          }
          else {
            showLine = false;
          }
          rp = drlev;
          if (showLine) {
            String tit = row + dr.pre + m + ":" + dr.level + "=" + dr.title;
            table.setValueAt(tit, row, 0);
            int i2 = 1;
            for (int ii = 0; ii < 10; ii++, i2++) {
              table.setValueAt(dr.Ss[ii], row, i2);
            }
            logRowToM[row] = m;
            row++;
            logLastM[k] = m;
          }
        }
        if (k == 0 && row == rowsEnd) {  // need separator line?
          table.setValueAt("---------------------", row, 0);
          int i2 = 1;
          for (int ii = 0; ii < 10; ii++, i2++) {
            table.setValueAt("--------", row, i2);
          }
          row++;
        }
        if (m == mEnd && row < rowsEnd) {
          table.setValueAt(">>>>>>>>END <<<<<<<<", row, 0);
          int i2 = 1;
          for (int ii = 0; ii < 10; ii++, i2++) {
            table.setValueAt(">>>>>end<<<<<", row, i2);
          }
          row++;
        }

        System.out.println("displayLog k=" + k + ", row=" + row + ", last=" + logLastM[k] + ", m=" + m + ", md=" + md + ", level=" + logLev[k] + ", logRowEnd=" + logRowEnd[k]);
        System.out.flush();
        //     logMHist.add(logLastM[k]);
      }
    }
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    ListSelectionModel rowSM = table.getSelectionModel();
    k = 0;
    System.out.println("displayLog lsm k=" + k + ", row=" + row + ", last=" + logLastM[k] + ", level=" + logLev[k] + ", logRowEnd=" + logRowEnd[k]);
    System.out.flush();
    rowSM.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        //Ignore extra messages.
        NumberFormat whole = NumberFormat.getNumberInstance();
        whole.setMaximumFractionDigits(0);
        k = 0;
        if (e.getValueIsAdjusting()) {
          return;
        }

        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        if (lsm.isSelectionEmpty()) {
          //      System.out.println(since() + " No rows are selected.");
        }
        else {
          int selectedRow = lsm.getMinSelectionIndex();
          int nrows = logRowCount;;
          logSelectedRow = selectedRow;
          int am = 0, am0 = 0, am1 = 0, am2 = 0, am3 = 0;
          // process the first Econ display
          if (logSelectedRow < E.logLen[0]) {
            k = 0;
            am = logRowToM[selectedRow];
            // do we go backword
            if (selectedRow < E.logLen[0] / 4) {
              // 12/30/15 try for a better back up
              // find  neg the number of lines from start+1 to end-1
              // get number of log lines per display line
              am0 = (logRowToM[1] - logRowToM[E.logLen[0] - 1]) / (E.logLen[0] - 2);
              // find number of display lines to go back
              int am0a = E.logLen[0] / 4 - selectedRow;
              // compute a log row am0a * am0 less than display row1
              am1 = logRowToM[1] - am0a * am0 - 40;
              am2 = am1 < 0 ? 2 : am1; // keep it positive
              am3 = am2 > eM.hists[0].size() ? eM.hists[0].size() - 20 : am2;
              // am = am - E.logLen[0] * 3 / 4;
            }
            else {
              am3 = am2 = am1 = am;
            }
            setLogM(0, am3);
          }
          else { // second section
            k = 1;
            int logStart = E.logLen[0] + 1; // go past the -- -- --
            am = logRowToM[selectedRow];
            if (selectedRow - logStart < E.logLen[1] / 4) {
              am1 = logRowToM[logStart + 1] - (logRowToM[logStart + E.logLen[1] - 1] - am);
              am2 = am1 < 0 ? 10 : am1; // keep positive
              am3 = am2 > eM.hists[1].size() ? eM.hists[1].size() - 20 : am2;
              // am = am - E.logLen[0] - E.logLen[1] * 3 / 4;
            }
            else {
              am3 = am2 = am1 = am;
            }
            setLogM(1, am3);
          }
          System.out.println(since() + " selectedRow=" + selectedRow + " k:am=" + k + ":" + am + "=>" + am1 + "=>" + am2 + "am3 " + am3);
          System.out.println(since() + " from" + logLastM[k] + " selectRow=" + whole.format(k) + ":" + whole.format(am));
          displayLog(logDisplayTable);
        }
      }
    });

    return logSelectedRow;
  }

  static int envsLoop = 0;
  static int planetsLoop = 0;
  static int shipsLoop = 0;
  static int envsLoop2 = 0;
  // static int[] envsPerYear = {10, 20, 30, 40, 40, 40, 40, 40, 40, 40, 40, 40};
  static int[] envsPerYear = {10, 20, 30, 40};
  JSlider[] gameSlidersP = {gameSliderP0, gameSliderP1, gameSliderP2, gameSliderP3, gameSliderP4, gameSliderP5, gameSliderP6, gameSliderP7, gameSliderP8, gameSliderP9};
  JSlider[] gameSlidersS = {gameSliderS0, gameSliderS1, gameSliderS2, gameSliderS3, gameSliderS4, gameSliderS5, gameSliderS6, gameSliderS7, gameSliderS8, gameSliderS9};
  JSlider[] clanSlidersP = {clanSliderP0, clanSliderP1, clanSliderP2, clanSliderP3, clanSliderP4};
  JSlider[] clanSlidersS = {clanSliderS0, clanSliderS1, clanSliderS2, clanSliderS3, clanSliderS4};
  JTextField[] gameTextFields = {gameTextField0, gameTextField1, gameTextField2, gameTextField3, gameTextField4, gameTextField5, gameTextField6, gameTextField7, gameTextField8, gameTextField9};
  JTextField[] clanTextFields = {clanTextField0, clanTextField1, clanTextField2, clanTextField3, clanTextField4};
  JPanel gamePanels[] = {gamePanel0, gamePanel1, gamePanel2, gamePanel3, gamePanel4, gamePanel5, gamePanel6, gamePanel7, gamePanel8, gamePanel9};
  JPanel clanPanels[] = {clanPanel0, clanPanel1, clanPanel2, clanPanel3, clanPanel4};
  JLabel clanLabelsP[] = {gameLabelP5, gameLabelP6, gameLabelP7, clanLabelP3, clanLabelP4};
  JLabel clanLabelsS[] = {gameLabelS5, gameLabelS6, gameLabelS7, gameLabelS8, clanLabelS4};
  double fullRes[] = {1., 2.};
  int lGameRes = fullRes.length;

  Runtime runtime = Runtime.getRuntime();
  static final long MEGABYTE = 1024L * 1024L;

  public static double bytesToMegabytes(Long bytes) {
    return bytes / MEGABYTE;
  }

//  String[][] statsData;
  /**
   * Creates new Class/Form StarTrader
   */
  public StarTrader() {
    this.eE = new E();
    this.eM = new EM(eE, this);
    EM.startTime = startTime;
    eE.init();
    eM.init();

    //  fullRes = EM.gameRes.values();
    //  lGameRes = fullRes.length;
    // ABalRows dummy;
    //  dummy = new ABalRows();
    /**
     *
     */
    initComponents();
    Object statsData[][];

    //   statsTable = new javax.swing.JTable();
    JSlider[] gameSlidersP1 = {gameSliderP0, gameSliderP1, gameSliderP2, gameSliderP3, gameSliderP4, gameSliderP5, gameSliderP6, gameSliderP7, gameSliderP8, gameSliderP9};
    JSlider[] gameSlidersS1 = {gameSliderS0, gameSliderS1, gameSliderS2, gameSliderS3, gameSliderS4, gameSliderS5, gameSliderS6, gameSliderS7, gameSliderS8, gameSliderS9};
//  JSlider[] clanSlidersP = {clanSliderP0, clanSliderP1, clanSliderP2, clanSliderP3, clanSliderP4};
    // JSlider[] clanSlidersS = {clanSliderS0, clanSliderS1, clanSliderS2, clanSliderS3, clanSliderS4};
    JTextField[] gameTextFields1 = {gameTextField0, gameTextField1, gameTextField2, gameTextField3, gameTextField4, gameTextField5, gameTextField6, gameTextField7, gameTextField8, gameTextField9};
    // JTextField[] clanTextFields = {clanTextField0, clanTextField1, clanTextField2, clanTextField3, clanTextField4};
    JPanel gamePanels1[] = {gamePanel0, gamePanel1, gamePanel2, gamePanel3, gamePanel4, gamePanel5, gamePanel6, gamePanel7, gamePanel8, gamePanel9};
//  JPanel clanPanels[] = {clanPanel0, clanPanel1, clanPanel2, clanPanel3, clanPanel4};

//  JLabel clanLabelsP[] = {gameLabelP5, gameLabelP6, gameLabelP7, clanLabelP3, clanLabelP4};
    // JLabel clanLabelsS[] = {gameLabelS5, gameLabelS6, gameLabelS7, gameLabelS8, clanLabelS4};
    //   E.sysmsg("gameSlidersP1[0] =" + (gameSlidersP1[0] == null ? "null" : gameSlidersP1[0].isEnabled() ? "enabled" : "disabled"));
    gameSlidersP = gameSlidersP1;
    gameSlidersS = gameSlidersS1;
    gameTextFields = gameTextFields1;
    gamePanels = gamePanels1;
    double tCons = 300.26;
    double tWork = 200.33;
    double tFert = 250.45;
    NumberFormat dispFraction = NumberFormat.getNumberInstance();

    dispFraction.setMinimumFractionDigits(2);
    //   displayConsumers.setText(dispFraction.format(tCons));
    //   displayWorkers.setValue(dispFraction.format(tWork));
    //   displayFertile.setValue(dispFraction.format(tFert));
    logDisplayTable.setShowHorizontalLines(true);
    logDisplayTable.setShowVerticalLines(true);
    logDisplayTable.getColumnModel().getColumn(0).setMinWidth(150);
    logDisplayTable.getColumnModel().getColumn(0).setPreferredWidth(150);
    statsTable1.setShowHorizontalLines(true);
    statsTable1.setShowVerticalLines(true);

    // now reset the slide values
    /*    historyDisplay1Length.setMajorTickSpacing(10);
     historyDisplay1Length.setMinorTickSpacing(5);
     historyDisplay1Length.setPaintTicks(true);
     historyDisplay1Length.setPaintLabels(true);
     historyDisplay1Length.setValue(20);
     historyDisplay1Length.setMaximumSize(new java.awt.Dimension(100, 24));
     historyDisplay1Length.setName("Length");
     *
     */
    logRowToM = new int[100];

    //   TreeMap<Double, Econ> runOrder = new TreeMap<Double, Econ>();
    putInitValues();
    storyTextField1.setText(storyText);
    storyVersionField.setText(versionText);

    //  EM.clanVals.TEST.show();
    //  EM.clanVals.TEST2.show();
    //   EM.clanVals.TEST1.show();
    // resetRes(fullRes);
    //   setFatalError();
    // set the following gamePanelChange if wanted before first year
    // gamePanelChange(5, -2, gamePanels, gameTextFields, gameSlidersP, gameSlidersS, fullVals, curVals);
    //  runYear();  // do if a year execution before game request
    gamePanelChange(5, -2, gamePanels, gameTextFields, gameSlidersP, gameSlidersS, fullVals, curVals);
    //eM.curEcon.runYear(.0);
    //eM.curEcon.runYear(.0);
    //eM.curEcon.runYear(.0);
    //eM.curEcon.runYear(.0);
    //eM.curEcon.runYear(.0);
    printMem3();
  }

  public void printMem3() {
    runtime.gc(); // garbage collect
    totMem = runtime.totalMemory();
    freeMem = runtime.freeMemory();
    usedMem = totMem - freeMem;
    double tmem = (double) totMem, fmem = (double) freeMem, umem = (double) usedMem;
    //System.out.println("");
    System.out.printf("%n====================================================%n %s totMemory=%7.2g, usedMemory=%7.2g freeMemory=%7.2g%n^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n", since(), tmem, fmem, umem);
  }

  void printMem() {
    System.out.println();
  }

  int[] clanShipsDone = {0,0,0,0,0};
  /**
   * get Wild Current Planets for possible selection by a ship
   * There will be duplicates if needed to fill the array
   *
   * @param n number of planets in the return Econ array
   * @param shipsLoop position in the ships loop 0 -> max ships
   * 
   * @return new Econ array
   */
 ArrayList<Econ> getWildCurs(int shipsDone,int psize) {
    int lPlanets = eM.planets.size();
    int lShips = eM.ships.size();
    ArrayList<Econ> tradablePlanets = new ArrayList();
    int lTradablePlanets = psize;
    double lsel, maxsel;
    int rtns = -1; // counter for planets in ret;
    clanShipsDone[eM.curEcon.clan]++;
    Econ planet = eM.planets.get(0);

       
    double shipsVisitedPerPlanetVisited = EM.porsVisited[E.P] == 0? 0.:EM.porsVisited[E.S]/EM.porsVisited[E.P];
    double planetsGameGoalFrac = 1.0 - eM.gameShipFrac[E.P];
    double goalGameShipsPerPlanet = planetsGameGoalFrac == 0.0? 0.0:eM.gameShipFrac[E.P] / planetsGameGoalFrac;
    
    double clanPlanetFrac[] = {1.0 - eM.clanShipFrac[E.P][0],1.0 - eM.clanShipFrac[E.P][1],1.0 - eM.clanShipFrac[E.P][2],1.0 - eM.clanShipFrac[E.P][3],1.0 - eM.clanShipFrac[E.P][4]};
    double clanGoalShipsPerPlanet[] = {eM.clanShipFrac[E.P][0]/clanPlanetFrac[0],eM.clanShipFrac[E.P][1]/clanPlanetFrac[1],eM.clanShipFrac[E.P][2]/clanPlanetFrac[2],eM.clanShipFrac[E.P][3]/clanPlanetFrac[3],eM.clanShipFrac[E.P][4]/clanPlanetFrac[4]};
//    double clanCurShipsPerPlanet[] = {eM.porsClanVisited[E.S][0]/eM.porsClanVisited[E.P][0],eM.porsClanVisited[E.S][1]/eM.porsClanVisited[E.P][1],eM.porsClanVisited[E.S][2]/eM.porsClanVisited[E.P][2],eM.porsClanVisited[E.S][3]/eM.porsClanVisited[E.P][3],eM.porsClanVisited[E.S][4]/eM.porsClanVisited[E.P][4]};
    
        ;
    int loops=0;
    double lse1 =0.,lse2=0.;
    boolean lla=true,llb=true;
    // find planets new to old
      for(loops = 0;loops<4;loops++){
        boolean okClanSovrP[] = {
        };
      for (int i=lPlanets-1; i >= 0 && rtns < lTradablePlanets ; i--) {
        planet = eM.planets.get(i);
        if(!planet.getDie()){
          // if shipsDone/EconsDone  <= eM.gameShipFrac[E.P] + eM.addGoal[loops]
          // always allow trade to planets < age 3 with no ship visited
          int jjj = planet.as.shipsVisited; // == null ? 0:planet.as.shipsVisited;
          jjj = shipsDone;
          jjj = EM.porsVisited[E.P];
          jjj = planet.getAge();
          jjj = planet.getAge() < 3? 5:7;
          jjj = (int)(shipsDone + 
              EM.porsVisited[E.P]+.0001);
          // jjj = 0;
          jjj = (int)(shipsDone
              /(jjj +.0001) );
           jjj =(int)(shipsDone
              /(shipsDone + 
              EM.porsVisited[E.P] + .0001));
          jjj = planet.getTradedShipsTried();
          
          // check new planet always can trade
          boolean q0 = planet.getTradedShipsTried() < 1; // no visit 
          boolean q1 = loops < 2 && planet.getAge() < 3; //age 0,1,2 first loop
          boolean t0 = q0 && q1; // force accept planet
          
          //test 1 the limit clan planets per clan ships 
          double r1 = (1.-eM.gameShipFrac[E.P])/eM.gameShipFrac[E.P];//goal P/s
          double r2 = (eM.porsClanVisited[E.P][planet.clan] + .0001)/(clanShipsDone[planet.clan] +.0001);
          boolean t1 = r2 <= r1; //ok more planet
          
          // test2 
          double p2 = (shipsDone/5.)/(1. - eM.clanShipFrac[E.P][planet.clan]); // planets
          boolean t2 = (p2 + .0001 + eM.addGoal[loops]) >= EM.porsClanVisited[E.P][planet.clan];
       
          double p3 = (shipsDone);
          double j1 = (shipsDone + EM.porsVisited[E.P]+.0001);
          double j2 = shipsDone/j1; // cur gameShipFrac
          double j3 = eM.gameShipFrac[E.P] + eM.addGoal[loops];
          boolean t3 = j2 <= j3; // cur <= gameShipFrac+addGoal planet ok
          
          //test2 clan ships per planet 
         
          
          if(t0 || (t1)){
            if((lla = loops < 2 && planet.getAge() < 3 && planet.getTradedShipsTried() < 1) 
              || ( llb = loops > 1 && planet.getTradedShipsTried()<  1  ) ) {
              if((lsel = planet.calcLY(planet, eM.curEcon )) 
              < (lse2 = eM.maxLY[0] 
              + eM.addLY[0]*eM.multLYM[loops]))  {
            boolean goPrev = true;   
            for(int prev=0; prev < rtns && goPrev == true;prev++){
              if(planet == tradablePlanets.get(prev) ) goPrev = false;
            }
            if(goPrev){
          if (rtns < lTradablePlanets) {
            tradablePlanets.add(planet);
            rtns++;
          }
            //    System.out.println(eM.curEcon.getName() + " build select list=" + planet.getName());
            E.sysmsg("build planets list #%d for %s, dist=%5.2f < max=%5.2f planet %s\n", rtns, eM.curEcon.getName(), lsel, eM.maxLY[0] + eM.addLY[0]*loops, planet.getName());
          
              }}}
              }  
        }
          }
        }// loops
     
    
    return tradablePlanets;
  }// getWildCurs

  boolean clearHist(Econ myCur) {
    if (myCur.econCnt >= eM.keepHistsByYear[eM.year > eM.keepHistsByYear.length - 1 ? eM.keepHistsByYear.length - 1 : eM.year] && myCur.hist.size() > 20) {
      myCur.hist.clear();
      return true;
    }
    return false;
  }

  void runYearsInBackground(int years) {
    for (int yy = 0; yy < years; yy++) {
      runYear();
    }
    stateConst = STATS;
  }

  /**
   * run one year of planets than ships ships must first select a next plaet to
   * trade, then run a year than a startShipTrade is done to the ship
   */
  void runYear() {
    if (eM.fatalError) {
      setFatalError();
      return;
    }

    //initialize the yearly variabes
    // I may want to allow intrupts to change values on the fly, call doYear
    envsLoop = 0;
    planetsLoop = 0;
    shipsLoop = eM.ships.size() - 1;
    envsLoop2 = 0;
    E.msgs = E.dmsgs;   // reset messages for each year
    E.msgcnt = 0;
    System.out.println("in runYear, year=" + (eM.year + 1) + " now doYear");
    // all restarts after user input go to doYear keeping yearly variables
    if (!doStop && !eM.stopExe && !fatalError) {
      doYear();
      stateConst = STATS;
    }
  }

  Date dnow = new Date();
  //int clanShipsDone[] = {0,0,0,0,0}; // new every doYear

  /** process another year for each of the ships and planets
   * settings may have been changed before this year
   * do any initial creations and then forward fund creations
   * start planets presetting some variables to 0
   * start ships tradings starting with the newest ships and planets
   * limit number planet trades to the goal of ships for the clan
   * allow multiple ships to trade with a planet and each other if 
   * more ships than planets
   * after all ships done, do endYear of all economies
   * some economies experience catastrophies, losses and gains
   * economies without enough infrastructure die
   * planets with enough infrastructure grow, 
   * the more surplus the more growth
   * statisics are gathered through out the year, 
   * but most statistics at end of endYear
   * statistics are prepared for display, then end of year for all
   * mouse clicks are enabled and statistics can be read and
   * priorities changed.
   */
  public synchronized void doYear() {
 
    try {
       NumberFormat df = NumberFormat.getNumberInstance();
    df.setMinimumFractionDigits(2);
    df.setMaximumFractionDigits(5);
    NumberFormat whole = NumberFormat.getNumberInstance();
    whole.setMaximumFractionDigits(0);
    double curWorth = 1.;
      // years is a -1 origin,
      EM.doYearTime = startYear = new Date().getTime();
      stateConst = STARTING;
      eM.year++;
      E.resetMsgs();
      Thread.yield();
      System.out.println("in doYear year=" + eM.year + " econs=" + eM.econs.size() + " new:" + envsPerYear[(int) ((eM.year + 1) > (envsPerYear.length - 1) ? (envsPerYear.length - 1) : (eM.year + 1))] + " envs.length=" + envsPerYear.length);
      //     resetRes(fullRes);  // move years up cur, leave 0 ready for new statRes
      paintWaiting();
      if (!doStop && !fatalError) {
        eM.doStartYear();  //move stats up for the next year
      }
      else {
        stateConst = STOPPED;
      }
      //    eE.newRpt();  // zero the report lines for a new sum from each economy
      // add more planets or ships for each new year to the limit of defined Envirns
      // envsCreate

      // set up counts to be used later
      int lNamesList = namesList.getSize();
      int clanBias = 2;
      // eM.porsCnt[0] = eM.planets.size();
      // eM.porsCnt[1] = eM.ships.size();
      int econClan = -5;
      int lEcons = eM.econs.size();

      // preset counts to zero, they will be counted next
      // preset traded to zero
      eM.econCnt = 0;
      eM.planets.clear();
      eM.ships.clear();
      EM.tradedCnt = 0;
      EM.visitedCnt=0;
      for (int m = 0; m < 2; m++) {
        EM.porsCnt[m] = 0;
        EM.porsTraded[m] = 0;
        EM.porsVisited[m] = 0;
        for (int n = 0; n < 5; n++) {
          EM.clanCnt[n] = 0; // doing twice
          EM.clanTraded[n] = 0;
          EM.porsClanCnt[m][n] = 0;
          EM.porsClanTraded[m][n] = 0;
          EM.clanVisited[n] = 0;
          EM.porsClanVisited[m][n] = 0;
          
        }// n
      }// m

      // now set the counts and planets and ships
      for (Econ t : eM.econs) {
        if (!t.getDie()) {
          EM.porsClanCnt[t.pors][t.clan]++;
          EM.clanCnt[t.clan]++;
          EM.porsCnt[t.pors]++;
          EM.econCnt++;
          if (t.pors == P) {
            eM.planets.add(t);
          }
          else {
            eM.ships.add(t);
          }
        }
      }

      // set up the preexisting names on the namelist
      int tyear;
      // yEcons the number of Econs we can have this year.
      int yEcons = envsPerYear[tyear = (eM.year < envsPerYear.length ? eM.year : envsPerYear.length - 1)];
      //dnow = new Date();
      System.out.println(since() + " tyear=" + tyear + " lEcons=" + lEcons + " yEcons=" + yEcons + "\n" + " econCnt=" + eM.econCnt);
      printMem();

      if (doStop || fatalError) {
        stateConst = STOPPED;
        paintStopped();
      }
      else {

        lNamesList = namesList.getSize();
        lEcons = eM.econCnt;
        // add this years new economies 
        //stateConst = CREATING;
        paintEconCreate();
        // randomize the first choice of clan
        double rand1 = Math.random();
        clanBias = (int) rand1 % 5; // 0-4
        for (envsLoop = lEcons; envsLoop < yEcons; envsLoop++) {
          // dnow = new Date();
          // econCnt = envsLoop;
          Thread.yield();
          econClan = (envsLoop + clanBias) % 5;
          System.out.println("------" + since() + "  envsLoop=" + envsLoop + " max econs this year=" + yEcons + " econCnt=" + eM.econCnt + " rand1=" + eM.df(rand1) + " clanBias=" + clanBias + " clan=" + econClan);
          eM.curEcon = newEcon(eM.initialWorth, econClan);  // include new of Econ
          curWorth = eM.curEcon.getWorth();
          eM.curEcon.as.setStat("yearCreate", eM.curEcon.pors, eM.curEcon.clan, curWorth, 1);
          System.out.println("++++++++" + since() + " after newEcon name=" + eM.curEcon.getName() + ", clan=" + eM.curEcon.clan + " econssize=" + eM.econs.size());
          printMem();
         // E.msgcnt = 0;
        }// end for envsLoop
      } // end dostop else 

      if (doStop || fatalError) {
        stateConst = STOPPED;
      }
      else {
        //stateConst = CREATEFUTURE;
        paintFutureFundEconCreate();
        int nClans = E.clan.values().length - 3;
        int finishedClans = 0; // end when all 5 clans can create no more econs
        for (int clansLoop = 0; clansLoop < nClans && finishedClans < 5; clansLoop++, finishedClans++) {
          econClan = (int) (clansLoop + clanBias) % 5;
          double limits3 = eM.econCnt - eM.econLimits3[0];
          double mDif = limits3 > E.PZERO ? limits3 / 5 : 1.;
          double clanWorth = eM.econCnt > eM.econLimits1[0] ? Math.max(eM.initialWorth[0] * 4., eM.clanFutureFunds[econClan] / ((eM.econLimits3[0] - eM.econCnt) / 5.)) : eM.initialWorth[0];
          if (eM.clanFutureFunds[econClan] > clanWorth) {
            System.out.println(since() + "  clan=" + econClan + " initial clan worth=" + clanWorth + " econCnt=" + eM.econCnt);
            // since the pors in not yet know, use initialWorth of planets
            finishedClans = 0;
            eM.curEcon = newEcon(eM.initialWorth, econClan);  // include new of Econ
            eM.curEcon.as.setStat("FutureCreate", eM.curEcon.pors, eM.curEcon.clan, curWorth, 1);
            //eM.clanFutureFunds[econClan] -= eM.initialWorth[eM.curEcon.pors];
            eM.clanFutureFunds[econClan] -= curWorth = eM.curEcon.getWorth();
            System.out.println("+++++++++++++++" + since() + " futureFunds newEcon name=" + eM.curEcon.getName() + ", clan=" + eM.curEcon.clan + " econssize=" + eM.econs.size());
          } // opasd  [\P]      
        } // end clansLoop

        // now initialize the first 2 Econ for logDisplay if it is null, the first time through
        if (eM.logEnvirn[0] == null || eM.hists[0] == null) {
          int lecon1 = eM.econs.size();
          Econ econ1 = eM.econs.get(0);

          setLogEnvirn(0, eM.econs.get(0));
          eM.hists[0] = eM.logEnvirn[0].hist;
          //  String msgLine1 = " Add eM.hists[0], setLogEnvirn 0, " + eM.year + "=year " + eM.econs.get(0).name + "=ship" + (eM.econs.get(0).getDie() ? " is dead" : " live");
          // System.out.println(dnow.toString() + " " + msgLine1);
        }
        if (eM.logEnvirn[1] == null || eM.hists[1] == null && eM.econs.size() > 2) {
          setLogEnvirn(1, eM.econs.get(1));
          eM.hists[1] = eM.logEnvirn[1].hist;
          //  String msgLine1 = dnow.toString() + " Add eM.hists[1], setLogEnvirn 1, " + eM.year + "=year " + eM.econs.get(1).name + "=ship " + (eM.econs.get(1).getDie() ? " is dead" : " live");
          //  System.out.println(msgLine1);
          //    System.out.println(dnow.toString() + " doYear=" + eM.year + " logE0=" + eM.logEnvirn[0].getName() + "," + eM.logEnvirn[1].getName());
        }
      } // end doStop
      if (doStop || fatalError) {
        stateConst = STOPPED;
      }
      else {
        // stateConst = STARTYR;
        paintStartYear();
        // ignored planetsStart 0:planets.size(). start the years
        for (planetsLoop = 0; planetsLoop < eM.planets.size(); planetsLoop++) {
          eM.curEcon = eM.planets.get(planetsLoop);
          String msgLine0 = since() + "Planet yearStart " + eM.year + " " + eM.curEcon.name + "=planet" + (eM.curEcon.getDie() ? " is dead" : " live");
          //  System.out.println(msgLine0);
          startEconState = (new Date()).getTime();
          paintCurDisplay(eM.curEcon);
          if (!eM.curEcon.getDie()) {
         //   E.msgcnt = 0;
            eM.curEcon.yearStart(0.);
            //      paintStartYear(eM.curEcon);
          }
          else {
            //      EM.gameRes.DEAD.set(eM.curEcon.pors, eM.curEcon.clan, 1.);

          }
        }
      }
      //shipsSizeN1 = ships.size()-1
      // do costs and trades here, ships initiate trades
      Econ[] wildCurs = new Econ[(int) eM.wildCursCnt[0][0]];
      // the oldest ships get first choice, and make the first trades
      // stateConst = TRADING;
      paintTrade(curEc, curEc);
      if (doStop || fatalError) {
        paintStopped();
        stateConst = STOPPED;
      }
      else {
        // go latest to earliest, smallest to largest
       
        for(int n=0;n< E.lclans;n++){ clanShipsDone[n] = 0; }
        for (shipsLoop = eM.ships.size() - 1; shipsLoop >= 0; shipsLoop--) {
          eM.curEcon = eM.ships.get(shipsLoop);
          startEconState = (new Date()).getTime();
          //paintCurDisplay(eM.curEcon);

          if (!eM.curEcon.getDie()) {  //live
            // ship selects its next planet, from offer list and wildCurs
            Econ cur1 = eM.curEcon;
            clanShipsDone[cur1.clan]++; // count clan of ship
            double distance = 0.0;
            ArrayList<Econ> tradablePlanets = new ArrayList<Econ>();
            int lTPlanets = (int)(eM.wildCursCnt[0][0]);
            int shipCnt = eM.ships.size()- 1 - shipsLoop;
             tradablePlanets = getWildCurs(shipCnt,lTPlanets);
             if(tradablePlanets.size() > 0){
            Econ cur2 = eM.curEcon.selectPlanet(tradablePlanets);
            if(cur2 != null){
          //  System.out.println(" @@@@@@Ship=" + eM.curEcon.getName() + ", loop select planet=" + cur2.getName() + " distance=" + eM.curEcon.mf(calcLY(eM.curEcon,cur2)));
            distance = calcLY(eM.curEcon, cur2);
            clearHist(eM.logEnvirn[1]);
            setLogEnvirn(1, cur2);  // set start2
            eM.hists[1] = eM.logEnvirn[1].hist;
            }
            clearHist(eM.logEnvirn[0]);
            eM.curEcon = cur1;
            setLogEnvirn(0, eM.curEcon);  // set start1
            eM.hists[0] = eM.logEnvirn[0].hist;
          //  distance = distance < .01 ? eM.nominalDistance[0] : distance; // add arbitrary distance if none
            eM.curEcon = cur1;
         //   E.msgcnt = 0;
            paintEconYearStart(eM.curEcon);
            eM.curEcon.yearStart(distance);
        //    E.msgcnt = 0;
            eM.curEcon = cur1;
            paintTrade(eM.curEcon, cur2);
            startEconState = (new Date()).getTime();
       //     eM.curEcon.sStartTrade(eM.curEcon, cur2);
            // paintTrade(eM.curEcon,cur2);
          }
          else {
            //       EM.gameRes.DEAD.set(eM.curEcon.pors, eM.curEcon.clan, 1.);

          }
          }

          System.out.println("================" + since() + " after ship barter year =" + eM.year + ", ship=" + eM.curEcon.name + (eM.curEcon.getDie() ? " is dead" : " is live"));
         
          printMem();
        }
        // after all trades, end year for all economies.
        // now initialize the first 2 Econ if one is null, the first time through
        if (eM.logEnvirn[0] == null || eM.hists[0] == null) {
          String msgLine1 = new Date().toString() + "Add eM.hists[0], setLogEnvirn 0, " + eM.year + "=year " + eM.econs.get(0).name + "=ship" + (eM.econs.get(0).getDie() ? " is dead" : " live");
          System.out.println(msgLine1);
          setLogEnvirn(0, eM.econs.get(0));
          eM.hists[0] = eM.econs.get(0).hist;

        }
        if (eM.logEnvirn[1] == null || eM.hists[1] == null) {
          setLogEnvirn(1, eM.econs.get(1));
          eM.hists[1] = eM.econs.get(1).hist;
        }
      } // else doStop
      // end each year and build the final namelist
      namesList.clear();
      stateConst = ENDYR;
      int maxEcons = eM.econs.size();
      if (doStop || fatalError) {
        stateConst = STOPPED;
      }
      else {
        for (envsLoop2 = 0; envsLoop2 < maxEcons; ++envsLoop2) {
          eM.curEcon = eM.econs.get(envsLoop2);
          startEconState = (new Date()).getTime();
          //    System.out.printf(new Date().toString() + " in doYear at envsLoop2 econ.yearEnd() name=" + eM.curEcon.name);
          // now reset the log environ to this current econ
          clearHist(eM.logEnvirn[0]);
          setLogEnvirn(0, eM.curEcon);  // set start1
          eM.hists[0] = eM.logEnvirn[0].hist;
       //   E.msgcnt = 0;
          eM.curEcon.yearEnd();
          EM.wasHere = "after eM.curEcon.yearEnd()";
          //   paintEconEndYear(eM.curEcon);

          //   System.out.print(new Date().toString() + " after year end" + eM.curEcon.name + "=ship " + (eM.curEcon.getDie() ? " is dead" : " is alive ") + groupNames[eM.curEcon.clan] + " " + eM.curEcon.name + " h=" + eM.curEcon.df(eM.curEcon.getHealth()) + ", age=" + eM.curEcon.getAge() + ", w=" + eM.curEcon.df(eM.curEcon.getWorth()));
          printMem();
          String disp1 = (eM.curEcon.getDie() ? " is dead " : " is alive") + groupNames[eM.curEcon.clan] + " " + eM.curEcon.name + " h="
                  + eM.curEcon.df(eM.curEcon.getHealth()) + ", age=" + eM.curEcon.age
                  + ", w=" + eM.curEcon.df(eM.curEcon.getWorth());
          //   System.out.println(new Date().toString() + disp1);
          namesList.add(envsLoop2, disp1);

        }
        long[][][] resii1 = eM.resI[0];
        long[][] resi2 = resii1[1];
        long[] resi23 = resi2[2];
        EM.wasHere = "before eM.doEndYear()";
        eM.doEndYear();
        EM.wasHere = "after eM.doEndYear()";
        long[][][] resii = eM.resI[0];
        long[][] resii2 = resii[1];
        long[] resi3 = resii2[2];
        //    gamePanelChange(5, -2, gamePanels, gameTextFields, gameSlidersP, gameSlidersS, fullVals, curVals);

        System.out.print(EM.curEcon.name + since() + " after gamePanelChange");
        printMem3();
      }
      EM.wasHere = "at end of doY&ear try";

    } // try
    catch (WasFatalError ex) {
      if (!resetOut) {
        eM.flushes();
      }
      System.err.println(EM.curEcon.name + since() + " WasFatalError found" + EM.andMore());
      ex.printStackTrace(System.err);
      // go to finally
    }
    catch (WasStopped ex) {
      if (!resetOut) {
        eM.flushes();
      }
      System.err.println(EM.curEcon.name + since() + " WasStopped found" + EM.andMore());
      ex.printStackTrace(System.err);
      // go to finally
    }
    catch (MyTestException ex) {
      if (!resetOut) {
        eM.flushes();
      }
      System.err.println(EM.curEcon.name + since() + "MyTestException found" + EM.andMore());
      ex.printStackTrace(System.err);
      if (!resetOut) {
        System.err.flush();
      }
      setFatalError();
    }
    catch (MyErrException ex) {
      if (!resetOut) {
        eM.flushes();
      }
      System.err.println(EM.curEcon.name + since() + " MyErrException=" + ex.getMessage() + EM.andMore());
      ex.printStackTrace(System.err);
      if (!resetOut) {
        eM.flushes();
      }
      setFatalError();
    }
    catch (MyErr ex) {
      if (!resetOut) {
        eM.flushes();
      }
      System.err.println(EM.curEcon.name +  since() + " MyErr=" + ex.getMessage() + EM.andMore());
      ex.printStackTrace(System.err);
      if (!resetOut) {
        eM.flushes();
      }
      setFatalError();
    }
    catch (MyMsgException ex) {
      if (!resetOut) {
        eM.flushes();;
      }
      System.err.println(since() + " MyMsgException found=" + ex.getMessage() + EM.andMore());
      ex.printStackTrace(System.err);
      if (!resetOut) {
        eM.flushes();
      }
      setFatalError();
    }
    catch (java.lang.Error ex) {
      if (!resetOut) {
        eM.flushes();
      }
      System.err.println(since() + " Caught Err cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + EM.andMore());

      ex.printStackTrace(System.err);
      if (!resetOut) {
        eM.flushes();
      }
      setFatalError();
    }
    catch (RuntimeException ex) {
      if (!resetOut) {
        eM.flushes();
      }
      System.err.println(since() +" Caught RuntimeException cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + EM.andMore());
      ex.printStackTrace(System.err);
      if (!resetOut) {
        eM.flushes();
      }
      setFatalError();
    }
    catch (Exception ex) {
      if (!resetOut) {
        eM.flushes();
      }
      System.err.println(since() + " Caught Exception=" + ex.toString() + " message=" + ex.getMessage() + EM.andMore());
      ex.printStackTrace(System.err);
      if (!resetOut) {
        eM.flushes();;
      }
      setFatalError();
    }
    finally {
      if (!resetOut) {
        eM.flushes();
      }
      /**
       * now initialize values for display
       */
//    E.incrFracStaffForRes[1][4]++;
      String xxx = since() + " In doYear finally econs=" + eM.econs.size();
      if (eM.curEcon != null && eM.curEcon.name != null) {
        xxx += " name=" + eM.curEcon.getName();
      }
      xxx += " econsCnt=" + eM.econCnt;

      System.out.println(xxx);
      int row = 0;
      row = displayLog();
      /**
       * initialize event for namesList, change not during a year
       */
      logEnvirnNamesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      ListSelectionModel rowSM = logEnvirnNamesList.getSelectionModel();
      rowSM.addListSelectionListener(new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
          //Ignore extra messages.
          NumberFormat whole = NumberFormat.getNumberInstance();
          whole.setMaximumFractionDigits(0);
          if (e.getValueIsAdjusting()) {
            System.out.println(since() + " Names: adjusting");
            return;
          }
          ListSelectionModel lsm = (ListSelectionModel) e.getSource();
          if (lsm.isSelectionEmpty()) {
            System.out.println(since() + "Names: No rows are selected.");
          }
          else {
            int selectedRow = lsm.getMinSelectionIndex();

            namesListRow = selectedRow;
            eM.curEcon = eM.econs.get(namesListRow);
            setLogEnvirn(E.dN, eM.curEcon);
            System.out.println(since() + "ListSelectionModel namesListRow=" + namesListRow + "name=" + eM.curEcon.name);

            int row = displayLog();
          }
        }
      });
    }// end finally

  } // end doYear

  void paintEconCreate() {
    setEconState(CREATING);
  }

  void paintFutureFundEconCreate() {
    setEconState(FUTUREFUNDCREATE);
  }

  void paintSearch() {

  }

  void paintStopped() {

  }

  void paintTrade(Econ curEc, Econ ec2) {

  }

  void paintEconYearStart(Econ ec1) {
  }

  void paintTradeRejected(Econ curEc, Econ ec2) {

  }

  void paintTradeLost(Econ curEc, Econ ec2) {

  }

  void paintSwaping() {

  }

  void paintWaiting() {
  }

  void paintCurDisplay(Econ curEc) {
    int numEcons = eM.econs.size();
    int rN=999999;
    String newLine = "\n";
    String line1 = "", line0 = "", line2 = "", line3 = "", line4 = "", line5 = "";
    //   controlPanels.setVisible(true);
    controlPanels.getComponent(5);
    controlPanels.setSelectedIndex(5);
    // display.setVisible(false);
    // displayPanel1.setVisible(false);
    // displayPanel2.setVisible(false);
    //displayPanel1EconName.setVisible(false);
    // displayPanel1Operation.setVisible(false);
    //displayPanel1SinceYearStart.setVisible(false);
    if (curEc != null) {
      displayPanel1EconName.setBackground(E.clan.values()[curEc.clan].getColor(curEc.pors));
      displayPanel1EconName.setText(curEc.name);
      econCnt = curEc.econCnt;
     // controlPanels.setSelectedIndex(5);
      int blip = 5;
      String linez =  "both=" + curEc.mf(eM.econCnt) + " Planets=" + curEc.mf(eM.porsCnt[E.P]) + " ships=" + curEc.mf(eM.porsCnt[E.S]) + newLine  + since () + sinceRunYear() + newLine;
      line0 = line1 = line2 = line3 = line4 = line5 = "";
      line0 = stateStringNames[stateConst];
      displayPanel0Text.setBackground(E.clan.values()[curEc.clan].getColor(curEc.pors));
      switch (stateConst) {
        case WAITING:
        case STARTING:
          displayPanel0Text.setText(
          "STARTING both=" + curEc.mf(eM.econCnt) + " Planets=" + curEc.mf(eM.porsCnt[E.P]) + " ships=" + curEc.mf(eM.porsCnt[E.S]) + newLine + "year" + eM.year +" " +  since() + sinceRunYear());
          break;
        case CREATING:
           displayPanel0Text.setText(
          "CREATING both=" + curEc.mf(eM.econCnt) + " Planets=" + curEc.mf(eM.porsCnt[E.P]) + " ships=" + curEc.mf(eM.porsCnt[E.S]) + newLine + "year" + eM.year +" " +  since() + sinceRunYear());
        case FUTUREFUNDCREATE:
          rN = eM.getStatrN( "bothCreate");
         
          displayPanel0Text.setText(
         " Creating " + eM.getCurCumPorsClanUnitSum(rN,EM.ICUR0,E.P,E.S+1,0,5) + " Planets " + eM.getCurCumPorsClanUnitSum(rN,EM.ICUR0,E.P,E.P+1,0,5) + " Ships " + eM.getCurCumPorsClanUnitSum(rN,EM.ICUR0,E.S,E.S+1,0,5) + newLine + "both " + curEc.mf(eM.econCnt) + " Planets=" + curEc.mf(eM.porsCnt[E.P]) + " ships=" + curEc.mf(eM.porsCnt[E.S]) + newLine + "year" + eM.year +" " +  since() + sinceRunYear()) ;

          break;
        case STARTYR:
          displayPanel0Text.setText(
         " START YEAR " + eM.getCurCumPorsClanUnitSum(rN,EM.ICUR0,E.P,E.S+1,0,5) + " Planets " + eM.getCurCumPorsClanUnitSum(rN,EM.ICUR0,E.P,E.P+1,0,5) + " Ships " + eM.getCurCumPorsClanUnitSum(rN,EM.ICUR0,E.S,E.S+1,0,5) + newLine + "both " + curEc.mf(eM.econCnt) + " Planets=" + curEc.mf(eM.porsCnt[E.P]) + " ships=" + curEc.mf(eM.porsCnt[E.S]) + newLine + "year" + eM.year +" " +  since() + sinceRunYear()) ;
          break;
        case SEARCH:
           displayPanel0Text.setText(
         " SEARCH " + curEc.name + newLine + "both " + curEc.mf(eM.econCnt) + " Planets=" + curEc.mf(eM.porsCnt[E.P]) + " ships=" + curEc.mf(eM.porsCnt[E.S]) + newLine + "year" + eM.year +" " +  since() + sinceRunYear()) ;
          break;
        case SWAPS:
         displayPanel0Text.setText(
         " SWAPS " + curEc.name + newLine + "both " + curEc.mf(eM.econCnt) + " Planets=" + curEc.mf(eM.porsCnt[E.P]) + " ships=" + curEc.mf(eM.porsCnt[E.S]) + newLine + "year" + eM.year +" " +  since() + sinceRunYear()) ;
          break;
        case TRADING:
          displayPanel1EconName.setBackground(E.clan.values()[curEc.clan].getColor(curEc.pors));
      displayPanel1EconName.setText(curEc.name);
          displayPanel0Text1.setText(eM.otherEcon.name);
          displayPanel0Text1.setBackground(E.clan.values()[eM.otherEcon.clan].getColor(eM.otherEcon.pors));
         displayPanel0Text.setText(
         " TRADING " + curEc.name + newLine + "both " + curEc.mf(eM.econCnt) + " Planets=" + curEc.mf(eM.porsCnt[E.P]) + " ships=" + curEc.mf(eM.porsCnt[E.S]) + newLine + "year" + eM.year +" " +  since() + sinceRunYear()) ;
         
          break;
        case ENDYR:
          displayPanel0Text.setText(
         " ENDYEAR " + curEc.name + newLine + "both " + curEc.mf(eM.econCnt) + " Planets=" + curEc.mf(eM.porsCnt[E.P]) + " ships=" + curEc.mf(eM.porsCnt[E.S]) + newLine + "year" + eM.year + " " + since() + sinceRunYear()) ;
          break;
        case STATS:
          controlPanels.setSelectedIndex(4);
        //  line5 = linez;
          break;
        default:

      }
    }
  //  displayPanel0Text.setText(line0 + line1 + line2 + line3 + line4 + line5);

    displayPanel1Operation.setText(stateStringNames[stateConst]);
    displayPanel1SinceYearStart.setText(sinceRunYear());
  //  displayPanel1EconName.setVisible(true);
  //  displayPanel1Operation.setVisible(true);
  //  displayPanel1SinceYearStart.setVisible(true);
  //  displayPanel1.setVisible(true);
  if(stateConst == STATS){
    display.setVisible(false);
    stats.setVisible(true);
  } else {
    display.setVisible(true);
  }
    controlPanels.setVisible(true);

    display.revalidate();
    display.repaint();
    if (stateCnt % 20 == 0) {
      System.out.print("??????? " + since("Start", startTime) + " " + stateStringNames[stateConst] + sameEconState + " Cnt=" + econCnt + " size=" + numEcons);
      if (curEc != null) {
        System.out.print(" Econ.name=" + curEc.name);
      }
      if (EM.wasHere != null || EM.wasHere2 != null) {
        System.out.print(", " + EM.wasHere);
      }
      System.out.println();
    }
  }

  void paintStartYear() {
    //   controlPanels.setVisible(true);
    curEc = EM.curEcon;
    controlPanels.getComponent(4);
    display.setVisible(false);
    displayPanel1.setVisible(false);
    displayPanel2.setVisible(false);
    displayPanel1EconName.setVisible(false);
    displayPanel1Operation.setVisible(false);
    displayPanel1SinceYearStart.setVisible(false);
    displayPanel1EconName.setBackground(E.clan.values()[curEc.clan].getColor(curEc.pors));
    displayPanel1EconName.setText(curEc.name);
    displayPanel1Operation.setText("Start Year");
    displayPanel1SinceYearStart.setText(sinceRunYear());

    displayPanel1EconName.setVisible(true);
    displayPanel1Operation.setVisible(true);
    displayPanel1SinceYearStart.setVisible(true);
    display.setVisible(true);
    controlPanels.setVisible(true);
    display.revalidate();
    display.repaint();
  }

  /**
   * set variable environment for the logs tab
   *
   * @param dN 0 or 1 for which portion of log table
   * @param En Econ for that table
   */
  public void setLogEnvirn(int dN, Econ En) {
    E.dN = dN;
    if (dN == 0) {
      eM.logEnvirn[0] = En;
      Object aa = E.clan.values()[En.clan];
      Start1Name.setText(eM.logEnvirn[dN].name);
      //  Start1Name.setForeground(E.clan.values()[eM.logEnvirn[dN].pors][eM.logEnvirn[dN].group]));
      //Start1Name.setForeground(E.clan.values()[eM.logEnvirn[dN].clan].getColor(eM.logEnvirn[dN].pors));
      //Start1Name.setBackground(E.clan.values()[eM.logEnvirn[dN].clan].getInvColor(eM.logEnvirn[dN].pors));
      //Start1Name.setBackground(new Color(-E.clanColors[eM.logEnvirn[dN].pors][eM.logEnvirn[dN].group]));
    }
    else {
      E.dN = 1;
      eM.logEnvirn[E.dN] = En;
      Start2Name.setText(eM.logEnvirn[dN].name);
      // Start2Name.setForeground(E.clan.values()[eM.logEnvirn[dN].clan].getColor(eM.logEnvirn[dN].pors));
      // Start2Name.setBackground(E.clan.values()[eM.logEnvirn[dN].clan].getInvColor(eM.logEnvirn[dN].pors));
      // Start2Name.setForeground(new Color(E.clanColors[eM.logEnvirn[dN].pors][eM.logEnvirn[dN].clan]));
      // Start2Name.setBackground(new Color(-E.clanColors[eM.logEnvirn[dN].pors][eM.logEnvirn[dN].clan]));
    }
    eM.hists[dN] = En.hists[0];  // applies to both 0,1
    //  System.out.println("setLogEnvirn int=" + dN + " name=" + En.getName() + " year=" + eM.year);
  }

  /**
   * called by a ship to select the next planet for a barter
   *
   * @param curEcon
   * @return
   */
  protected Econ selectPlanet(Econ curEcon) {
    int a = 3;
    return eM.planets.get(0);
  }

  /**
   * calculate the light years to the next planet for a ship
   *
   * @param curEcon the econ of the ship
   * @param cur2 the econ of the candidate planet
   * @return
   */
  protected double calcLY(Econ curEcon, Econ cur2) {
    double x = (curEcon.xpos - cur2.xpos);
    double y = (curEcon.ypos - cur2.ypos);
    double z = (curEcon.zpos - cur2.zpos);
    double xyz = Math.pow(x, 2.) + Math.pow(y, 2.) + Math.pow(z, 2.);
    return Math.sqrt(xyz);
  }

  /**
   * save the current position of a log display
   *
   * @param dN 0,1 which display environment
   * @param M position in that environment
   */
  void saveLogM(int dN, int M) {
    eM.logEnvirn[dN].logM[dN] = M;
  }

  /**
   * set the current position of a log display
   *
   * @param dN 0,1 display environment
   * @param M position in that environment
   */
  void setLogM(int dN, int M) {
    saveLogM(dN, M);
    if (dN == 0) {
      logM1Spinner.setValue(M);
      //   logM1Spinner.setBackground(E.clan.values()[eM.logEnvirn[dN].clan].getInvColor(eM.logEnvirn[dN].pors));
    }
    else {
      logM2Spinner.setValue(M);
      logM2Spinner.setBackground(E.clan.values()[eM.logEnvirn[dN].clan].getInvColor(eM.logEnvirn[dN].pors));
    }
  }

  void saveLogLen(int dN, int len) {
    eM.logEnvirn[dN].logLen[dN] = len;
  }

  void setLogLen(int dN, int len) {
    saveLogLen(dN, len);
    if (dN == 0) {
      LogDlen1Slider.setValue(len);
      //  LogDlen1Slider.setBackground(E.clan.values()[eM.logEnvirn[dN].clan].getInvColor(eM.logEnvirn[dN].pors));
    }
    else {
      LogDLen2Slider.setValue(len);
      // LogDLen2Slider.setBackground(E.clan.values()[eM.logEnvirn[dN].clan].getInvColor(eM.logEnvirn[dN].pors));
    }

  }

  void saveLogLev(int dN, int lev) {
    eM.logEnvirn[dN].logLev[dN] = lev;
  }

  void setLogLev(int dN, int lev) {
    saveLogLev(dN, lev);
    if (dN == 0) {
      logDLevel1Slider.setValue(lev);
      //   logDLevel1Slider.setBackground(E.clan.values()[eM.logEnvirn[dN].clan].getInvColor(eM.logEnvirn[dN].pors));
    }
    else {
      logDLevel2Slider.setValue(lev);
      //  logDLevel2Slider.setBackground(E.clan.values()[eM.logEnvirn[dN].clan].getInvColor(eM.logEnvirn[dN].pors));
    }
  }

  void savLogEnv(int dN, Econ en) {
    eM.logEnvirn[dN] = en;
  }

  public void putInitValues() {
    //  initLimitsHealthMaxSlider.setValue((int) Math.floor(E.resourceGrowth[E.pors]));
  }

  /**
   * before setting initHelperField save the current value up to 3 levels
   *
   * @param txt string to be placed in the helper field
   */
  public void setgameTextField(String txt) {
    E.savedgameTextField4 = E.savedgameTextField3;
    E.savedgameTextField3 = E.savedgameTextField2;
    E.savedgameTextField2 = E.savedgameTextField;
    E.savedgameTextField = gameTextField.getText();
    gameTextField.setText(txt);
  }

  /**
   * revert initHelperField up to 4 levels
   *
   */
  public void revertgameTextField() {
    gameTextField.setText(E.savedgameTextField);
    E.savedgameTextField = E.savedgameTextField2;
    E.savedgameTextField2 = E.savedgameTextField3;
    E.savedgameTextField3 = E.savedgameTextField4;
  }

  int[] curVals1 = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
//  EM.gameVals[] curVals = new EM.gameVals[10];
//  EM.gameVals[] fullVals = EM.gameVals.values();
  int[] d10 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
  int[] curVals = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
  int[] fullVals = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};

  /**
   * get any values already set into currentVals, ignore disabled elements then
   * disable each element
   *
   * @param currentVals
   * @param panelAR
   * @param textFieldsAR
   * @param gamePSliders
   * @param gameSSliders}
   */
  public void getGameValues(int[] currentVals1, JPanel[] panelAr, JTextField[] textFieldsAr, JSlider[] gamePSliders, JSlider[] gameSSliders) {
    double val = 0.;
    for (int p : d10) { // go through elements in this panel
      if (currentVals1[p] > -1 && panelAr[p].isEnabled()) {
        eM.gamePorS = E.P;
        eM.putVal(gamePSliders[p].getValue(), currentVals1[p], E.P, eM.gameClanStatus);
        if (gameSSliders[p].isEnabled()) {
          eM.gamePorS = E.S;
          eM.putVal(gameSSliders[p].getValue(), currentVals1[p], E.S, eM.gameClanStatus);
          // gameSSliders[p].setEnabled(false);
        }
        //  gamePSliders[p].setEnabled(false);

      }
      //  panelAr[p].setEnabled(false);
    }
  }

  /**
   * change a whole gamePanel
   *
   * @param clan 0-4 a clan, 5 the game master use eM.gameClanStatus
   * @param action -2 restart display from the first enum, 0 redisplay page 1 go
   * to the next page, -1 go to the previous page
   * @param panelAr array of 0-9 panels
   * @param textFieldsAr array of 0-9 descriptor fields
   * @param gamePSliders array of 0-9 planet sliders
   * @param gameSSliders array of 0-9 Ship sliders
   * @param unusedVals fullVals do not change many calls
   * @param currentVals1 the array of vv's at each of the 10 panels
   */
  public void gamePanelChange(int clan, int action, JPanel[] panelAr, JTextField[] textFieldsAr, JSlider[] gamePSliders, JSlider[] gameSSliders, int[] unusedVals, int[] currentVals1) {
    System.out.print("Enter gamePanelChange clan=" + clan + " gameClanStatus=" + eM.gameClanStatus + " action=" + action + " ");
    if (clan >= 0) {
      eM.gameClanStatus = clan;
    }
    if (eM.gameClanStatus == 5) {
      System.out.print("game master panel=" + eM.gPntr);
    }
    else {
      System.out.print("clan panel" + eM.gameClanStatus + "=" + eM.cPntr);
    }
    System.out.println(" " + new Date().toString());
    /**
     * put any values in the display into appropriate places in E. using
     * getGameValues
     */
    getGameValues(currentVals1, panelAr, textFieldsAr, gamePSliders, gameSSliders);

    nn = 9;
    if (action == -1) { // back up a panel if possible
      System.out.print("backup a pannel if possible to ");
      if (eM.gameClanStatus == 5) {
        eM.gPntr = Math.max(0, eM.gPntr - 1);
      }
      else {
        eM.cPntr = Math.max(0, eM.cPntr - 1);
      }
      System.out.println();
    }
    else if (action == -2) { // restart from the beginning
      if (eM.gameClanStatus == 5) {
        eM.gPntr = 0;  // rewind
        System.out.print("Restart at the first game master panel ");
      }
      else {
        eM.cPntr = 0;
        System.out.print("Restart at the first user panel clan=" + clan);
      }
      System.out.println(new Date().toString());
    }
    // go to the next set of panels
    else if (action > 0) {
      int savGPntr = eM.gPntr;
      int savCPntr = eM.cPntr;
      System.out.print("Move to the next ");
      if (eM.gameClanStatus == 5) {
        if (eM.gStart[eM.gPntr + 1] < 0) {
          if(E.debugGameTab)System.out.print("Remain at the current game master no additional panel" + eM.gPntr + " ");
        }
        else {
          eM.gPntr++;
          if(E.debugGameTab)System.out.print("Move to the next game master panel=" + eM.gPntr + " ");
        }
      }
      else {
        if (eM.cStart[eM.cPntr + 1] < 0) {
          if(E.debugGameTab)System.out.print("Remain at current user panel, not additonal panel=" + eM.cPntr);
        }
        else {
          eM.cPntr++;
         if(E.debugGameTab) System.out.print("Advance to the next user panel" + eM.cPntr);
        }
      }
    }
    // in any case now display upto 10  panels

    if (eM.gameClanStatus == 5) {
      eM.vv = eM.gStart[eM.gPntr];
      if(E.debugGameTab)System.out.println("Start the next game master panel at vv =" + eM.vv + " = " + eM.valS[eM.vv][0]);
    }
    else {
      eM.vv = eM.cStart[eM.cPntr];
      int ix = 0;
      int iy = eM.vv;
      if (iy <= eM.valS.length && eM.valS[iy] != null) {
        ix = iy;
      }
      if(E.debugGameTab)System.out.println("Start the next user panel at cPntr=" + eM.cPntr + " " + iy + ":" + ix + "=" + eM.valS[ix][0]);
    }
    nn = 0;
    int aclan = eM.gameClanStatus;
    while (eM.vv < eM.vvend && nn < 10) {
      if(E.debugGameTab){
      System.out.println("line nn=" + nn  + " gc=" + eM.valI[eM.vv][eM.modeC][0][0] + " clan=" + eM.gameClanStatus + " ??displaying??=" + eM.vv + " = " + eM.valS[eM.vv][0] );
     // System.out.print(nn);
     // System.out.print("nn=" + nn + " gc=" + eM.valI[eM.vv][eM.modeC][0][0]);
     // System.out.print(eM.gameClanStatus);
     // System.out.print(" ??displaying??=" + eM.vv + " = " + eM.valS[eM.vv][0]);
     // System.out.print(" nn=" + nn + " gc=" + eM.valI[eM.vv][eM.modeC][0][0]);
      }
      // is this vv  match the gameClanStatus
      if (eM.matchGameClanStatus(eM.vv)) {
        if(E.debugGameTab)System.out.println(" <<<<<DISPLAY clan planet=" + eM.gameClanStatus);
        // E.sysmsg(" <<<<<DISPLAY clan=" + eM.gameClanStatus);
        currentVals1[nn] = eM.vv;  // the display values
        panelAr[nn].setEnabled(true);
        panelAr[nn].setVisible(true);
        // gamePanel0.removeMouseListener(l);
        // add listeners for mouse entered and exited
        // do these end being duplicates?
        panelAr[nn].addMouseListener(new java.awt.event.MouseAdapter() {
          public void mouseEntered(java.awt.event.MouseEvent evt) {
            gamePanel0MouseEntered(evt);
          }

          public void mouseExited(java.awt.event.MouseEvent evt) {
            gamePanel0MouseExited(evt);
          }
        });

        panelAr[nn].setBackground(E.clan.values()[aclan].getColor(0));
        panelAr[nn].setForeground(E.clan.values()[aclan].getInvColor(0));
        textFieldsAr[nn].setText(eM.valS[currentVals1[nn]][0]);
        eM.gamePorS = E.P;
        gamePSliders[nn].setEnabled(true);
        // gamePSliders[nn].setForeground(Color.blue);
        gamePSliders[nn].setMajorTickSpacing(10);
        gamePSliders[nn].setMinorTickSpacing(2);
        gamePSliders[nn].setPaintLabels(true);
        gamePSliders[nn].setSnapToTicks(false);
        gamePSliders[nn].setVisible(true);
        gamePSliders[nn].setValue(eM.valI[currentVals1[nn]][eM.sliderC][eM.gamePorS][0]);
        
        eM.gamePorS = E.S;
        // is there an s entry, check valI
        int vv=0,vl=0,wl=0;
        double ww=0.;
        int v = (vl=eM.valI[currentVals1[nn]][eM.sliderC].length) > 1 ?(vv= eM.valI[currentVals1[nn]][eM.sliderC][eM.gamePorS][0] ): (vv= eM.valI[currentVals1[nn]][eM.sliderC][0][eM.gamePorS]);
      //  int w = (int)Math.floor((wl=eM.valD[currentVals1[nn]][0].length) > 1 ?(ww= eM.valD[currentVals1[nn]][0][1][0] ): (ww = eM.valD[currentVals1[nn]][0][0][1]));
        // enable staff slider if value > -1 the staff values exist as positive slider vals
        if (v > -1) {
          if(E.debugGameTab)System.out.println(" <<<<<DISPLAY ship valI=" + v + ", vl=" + vl + "line=" + nn + ", vv=" + currentVals1[nn] + ", desc=" + eM.valS[currentVals1[nn]][0] + ", clan=" + eM.gameClanStatus);
          // gamePSliders[nn].setForeground(Color.blue);
          gameSSliders[nn].setSnapToTicks(false);
          gameSSliders[nn].setForeground(Color.blue);
          gameSSliders[nn].setMajorTickSpacing(10);
          gameSSliders[nn].setMinorTickSpacing(2);
          gameSSliders[nn].setPaintLabels(true);
          gameSSliders[nn].setEnabled(true);
          gameSSliders[nn].setVisible(true);
          gameSSliders[nn].setValue(v);
          //     panelAr[nn].setBackground(shipBackgroundColor);
          //    panelAr[nn].setForeground(shipInvColor);
        }
        else {
          gameSSliders[nn].setEnabled(false);
          gameSSliders[nn].setVisible(false);
          if(E.debugGameTab)System.out.println(" <<<<< NO DISPLAY ship valI=" + v  + ", line=" + nn + ", vv=" + currentVals1[nn] + ", desc=" + eM.valS[currentVals1[nn]][0] + ", clan=" + eM.gameClanStatus);
        }
        nn++;
      }
      else {
       if(E.debugGameTab)System.out.println(" >>>>SKIP line=" + nn);
      }

      eM.vv++;
    }
    if (eM.vv >= eM.vvend) {
      int vv2 = eM.gameClanStatus == 5 ? eM.gStart[0] : eM.cStart[0];
      if(E.debugOutput)E.sysmsg("start over from" + eM.vv + " panel clan=" + eM.gameClanStatus + " action=" + action + " start=" + (eM.vv = vv2) + " length=" + eM.vvend + " ");
      System.out.println(new Date().toString());
      eM.gameDisplayNumber[eM.gameClanStatus] = eM.prevGameDisplayNumber[eM.gameClanStatus] = 0; // start over

    }
    // now disable the unused panels
    if (nn < 10) {
      while (nn < 10) {
        textFieldsAr[nn].setText("unused");
        panelAr[nn].setEnabled(false);
        panelAr[nn++].setVisible(false);
        System.out.print("disable panel nn=" + nn + " clan=" + eM.gameClanStatus);
        System.out.println(" " + new Date().toString());
      }
    }

    if(E.debugOutput){System.out.print(
            "exit gamePanelChange clan=" + eM.gameClanStatus + " action=" + action);
    System.out.println(
            " " + new Date().toString());
    }
  }

  private void gamePanel0MouseExited2(java.awt.event.MouseEvent evt) {
    if (gamePanel0.isEnabled() && curVals[0] > -1) {
      revertgameTextField();
    };
  }

  private void gamePanel1MouseEntered2(java.awt.event.MouseEvent evt) {
    if (gamePanel1.isEnabled() && curVals1[1] > -1) {
      setgameTextField(eM.getDetail(curVals[1]));
      gamePanel1.setToolTipText(eM.getDetail(curVals[1]));
    };
  }

  void setGameButtonColors() {
    Color oldRedColor = E.clan.RED.getColor(1);
    Color oldRedInvColor = E.clan.RED.getInvColor(1);
    System.out.printf("set colors clanRed color=%s %n ", Integer.toHexString(oldRedColor.getRGB()));
    System.out.printf("set colors clanRed invcolor=%s %n ", Integer.toHexString(oldRedInvColor.getRGB()));
    clanRed.setForeground(E.clan.RED.getColor(1));
    clanOrange.setForeground(E.clan.ORANGE.getColor(1));
    clanYellow.setForeground(E.clan.YELLOW.getColor(1));
    clanGreen.setForeground(E.clan.GREEN.getColor(1));
    clanBlue.setForeground(E.clan.BLUE.getColor(1));
    clanRed.setForeground(E.clan.RED.getColor(1));
    gameMaster.setForeground(E.clan.GAMEMASTER.getColor(1));
  }

  public void gameSliderChange(int num, int val, javax.swing.JSlider[] sliderAr) {
    sliderAr[num].setMajorTickSpacing(250);
    sliderAr[num].setMaximum(1000);
    sliderAr[num].setMinorTickSpacing(50);
    sliderAr[num].setPaintLabels(true);
    sliderAr[num].setPaintTicks(true);
    sliderAr[num].setValue(val);
    //   gameXtraPanel1.setBackground(new java.awt.Color(153, 255, 255));
    //   gameMaster.setBackground(new java.awt.Color(204, 204, 204));

    sliderAr[num].addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        LogDlen1SliderStateChanged(evt);
      }
    });
  }

  /**
   * this string holds pointers to longer explanations for given results
   */
  long testlist = EM.LIST3;
  String resExt[] = new String[200];
  long lists[] = {EM.LIST0,EM.LIST1,EM.LIST2,EM.LIST3,EM.LIST4,EM.LIST5,EM.LIST6,EM.LIST7,EM.LIST8,EM.LIST9,EM.LIST10,EM.LIST11,EM.LIST12,EM.LIST13,EM.LIST14,EM.LIST15,EM.LIST16,EM.LIST17,EM.LIST18,EM.LIST19,EM.LIST20};

  /**
   * this set array of arrays match the 20 radio buttons on the stats tab The
   * 0'th array is invoked by the 0 radio button etc. Each array contains an
   * array of one or more numbers that are filters for the response ENUM's in
   * class EM Each ENUM has up to 4 filters that must match at one "listx" in a
   * number the rest of the number specifies one or more outputs when a listx
   * matches The methods in Assets and associated classes invoke a selection of
   * EM.gameRes to store a value
   */
  long [] rowsm = {0L,
    0L | EM.THISYEAR | EM.THISYEARAVE | EM.THISYEARUNITS | EM.SUM |EM.BOTH | EM.CUMAVE | EM.CUM 
    ,eM.ROWS1 | EM.BOTH | EM.THISYEAR | EM.THISYEARAVE | EM.THISYEARUNITS | EM.CUMAVE | EM.CURUNITS
    ,eM.ROWS2 | EM.BOTH | EM.CUR | EM.CURAVE | EM.CURUNITS | EM.THISYEAR | EM.THISYEARAVE | EM.THISYEARUNITS | EM.CUM | EM.CUMAVE 
    ,eM.ROWS3 | EM.BOTH | EM.THISYEARUNITS | EM.CUR | EM.CURAVE | EM.CUMUNITS
  };
  long resLoops[][] = {
    {EM.list0 | EM.skipUnset,0L ,0L,0L,0L },
  {EM.list1| EM.skipUnset, EM.list1 , 0L ,0L,0L},
  {EM.list2| EM.skipUnset,EM.THISYEARAVE,0L,0L,0L },
  {EM.list3 |EM.skipUnset,0L ,0L },
  {EM.list4 | EM.skipUnset,0L ,0L},
  {EM.list5| EM.skipUnset,0L ,0L},
  {EM.list6 | EM.skipUnset,0L ,0L},
  {EM.list7 | EM.skipUnset,0L ,0L},
  {EM.list8 | EM.skipUnset,0L ,0L,0L,0L },
  {EM.list9 |EM.skipUnset,0L ,0L,0L,0L },
  {EM.list10 | EM.skipUnset,EM.CURAVE ,0L,0L,0L },
  {EM.list11 |EM.skipUnset,0L ,0L,0L,0L },
  {EM.LIST12 |EM.skipUnset,0L ,0L,0L,0L },
  {EM.LIST13 |EM.skipUnset,0L ,0L,0L,0L },
  {EM.LIST14 |EM.skipUnset,0L ,0L,0L,0L },
  {EM.LIST15 |EM.skipUnset,0L ,0L,0L,0L },
  {EM.LIST16 |EM.skipUnset,0L ,0L,0L,0L },
  {EM.LIST17  |EM.skipUnset,0L ,0L,0L,0L },
  {EM.LIST18  |EM.skipUnset,0L ,0L,0L,0L },
  {EM.LIST19 |EM.skipUnset,0L ,0L,0L,0L },
  {EM.LIST20 |EM.skipUnset,0L ,0L,0L,0L },
  };
  int m = 0, arow = 0;

  /**
   * list the results saved by setStats in Econs and Assets 
   *
   * @param fullRes
   */
  void listRes(double[] fullRes) {
    listRes(0, resLoops, fullRes);
  }

  static int listResNoteCount = 0;
  void listRes(int list, long resLoops[][], double[] fullRes) {
    arow = 0;
    statsField2.setText("year" + eM.year);
    int lrows = statsTable1.getRowCount();
    int cntLoops = 0;
    int [] rowsCnts = {100,0,0,0};
    int mm = 0; // count of selected rows
    int ii=0;
    long i = 0l;
    long i1 = resLoops[list][0];
    for (int aa=1;aa<5 && aa < resLoops[list].length; aa++) { 
        i = i1 | lists[list] | rowsm[aa] | resLoops[list][aa];

        if((listResNoteCount++ < 10)){
       System.out.printf("StarTrader.listRes resLoops[%d][%d] key%o row%d\n", list, aa,  i, arow);
         }
        arow = eM.putRows(statsTable1, resExt, arow, i);
        }

     
      
   
    // now blank the rest of the table
    System.out.println("listRes blank rest of table arow=" + arow );
    for (; arow < statsTable1.getRowCount() - 1; arow++) {
      statsTable1.setValueAt("----mt---", arow, 0);
      for (int mmm = 1; mmm < E.lclans * 2 + 1; mmm++) {
        statsTable1.setValueAt(":", arow, mmm);
      }
    }
    statsTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    ListSelectionModel rowSM2 = statsTable1.getSelectionModel();
    k = 0;
    //  System.out.println("displayLog lsm k=" + k + ", row=" + row + ", last=" + logLastM[k] + ", level=" + logLev[k] + ", logRowEnd=" + logRowEnd[k]);
    System.out.flush();
    rowSM2.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        //Ignore extra messages.
        NumberFormat whole = NumberFormat.getNumberInstance();
        whole.setMaximumFractionDigits(0);
        k = 0;
        if (e.getValueIsAdjusting()) {
          return;
        }

        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        if (lsm.isSelectionEmpty()) {
          //      System.out.println(since() + " No rows are selected.");
        }
        else {
          int selectedRow = lsm.getMinSelectionIndex();
          statsField.setText(stringTemp = resExt[selectedRow]);
          //    statsTable1.setToolTipText(stringTemp);
        } // 
      } // end valueChanged
    } // end ListSelectionListener
    ); // end addListSelectionListener
  }// for reslooops

}
