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

/**
 *
 * @author albert Steiner ii
 */
public class Tstats {

  private String tit1, tit2;
  /**
   * ustats [current,1yr,yr2,yr3,yr4,yr5,5yr] [P,S][R,O,Y,G,B,T][0-8counts]
   * methods pick which to set and which to print
   */
  private double[][][][] ustats = new double[3][][][];
  private double[] drange = new double[8];
  private int lineNo = 0;
  private boolean finished = true;
  javax.swing.JTable table;

  public Tstats(String title1, String title2, double r0, double r1, double r2, double r3, double r4, double r5, double r6, double r7) {
    int i, j, k, l;
    for (i = 0; i < 3; i++) {
      ustats[i] = new double[2][][];
      for (j = 0; j < 2; j++) {
        ustats[i][j] = new double[5][];
        for (k = 0; k < 5; k++) {
          ustats[i][j][k] = new double[9];
          for (l = 0; l < 9; l++) {
            ustats[i][j][k][l] = 0.;
          }
        }
      }
    }
    tit1 = title1;
    tit2 = title2;
    drange[0] = r0;
    drange[1] = r1;
    drange[2] = r2;
    drange[3] = r3;
    drange[4] = r4;
    drange[5] = r5;
    drange[6] = r6;
    drange[7] = r7;
    //
  }

  public void set(int term, Econ en, double val) {
    if (!en.getDie()) {
      int pors = en.getPors();
      int clan = en.getClan();
      int rang = 8;
      for (int i = 0; i < 8; i++) {
        if (drange[i] > val) {
          ustats[term][pors][clan][i] += 1.;
          break;
        }
      }
    }
  }

  public void start(javax.swing.JTable table1) {
    table = table1;
    finished = false;
    lineNo = 0;
    int i, j, k, l;
    for (i = 0; i < 3; i++) {
      for (j = 0; j < 2; j++) {
        for (k = 0; k < 5; k++) {
          for (l = 0; l < 9; l++) {
            ustats[i][j][k][l] = 0.;
          }
        }
      }
    }
  }

  public void done(javax.swing.JTable table1) {
    int max = table1.getRowCount();
    for (int k = 0; k < 10 && lineNo < max; k++) {  // write nothing to full table
      table.setValueAt("-------", lineNo, k);

    }

    for (++lineNo; lineNo < max; lineNo++) {
      for (int j = 0; j < 10; j++) {
        table.setValueAt("", lineNo, j);
      }
    }
    finished = true;
  }

}
