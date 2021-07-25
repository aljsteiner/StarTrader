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
 * @author albert steiner
 */
public class StrHist extends History {

  public StrHist(String str) {
    level = 10;
    int l = str.length();
    int k = 10;
    int i = Math.min(l, 15);
    title = str.substring(0, i);
    int j = Math.min(l, i = i + k);

    for (int m = 0; m < 10; m++, i += k) {
      j = Math.min(l, i + k);
      Ss[m] = (i > j ? "" : str.substring(i, j));  //blanks at end of str
    }
    
  }
}
