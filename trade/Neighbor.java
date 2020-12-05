/*
 * Copyright (C) 2015 albert
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

 Offer is the communication object between 2 bartering economies.  They revise
 the offer, and return it so that the ship Econ process moves to the
 next barter.   Offer contains a lot of helper data,
 Some data for the final trade if the offer is accepted.
 Some data to enable a ship to select the next trading partner
 */
package trade;

/**
 *
 * @author albert Steiner
 */
public class Neighbor {

  double newPosD = 0; // newEc.xpos+100 + (ypos+100)*200 + (zpos+100)*40000;
  Econ aEc;    // from newEd
  double lYears;  // from myEd to newEc

  /**
   * constructor for the Neighbor, assume xpos,ypos,zpos all&ge;-100,&le; 100;
   *
   * @param myEc this of the calling economy
   * @param newEc the other guy's EC
   */
  public Neighbor(Econ myEc, Econ newEc) {
    newPosD = newEc.xpos + 100. + (newEc.ypos + 100.) * 200. + (newEc.zpos + 100.) * 40000.;
    aEc = newEc;
    double m1 = myEc.xpos + 100;
    double m2 = myEc.ypos + 100;
    double m3 = myEc.zpos + 100;
    double n1 = newEc.xpos + 100;
    double n2 = newEc.ypos + 100;
    double n3 = newEc.zpos + 100;
    double l1 = m1 - n1;
    double l2 = m2 - n2;
    double l3 = m3 - n3;
    lYears = Math.sqrt(l1 * l1 + l2 * l2 + l3 * l3);
  }

}
