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
 * Save 5 years of stats for a set of interesting variables
 *
 * @author albert Steiner
 */
public class YrStats {
  /* [y1,y2,y3,y4,y5,T][Start,afterTrade,afterGrow,afterH] */

  double[][] rc = new double[6][4];
  double SG[][] = new double[6][4];
  double worth[][] = new double[6][4];

  YrStats() {

  }
}
