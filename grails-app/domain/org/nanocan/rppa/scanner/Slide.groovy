/*
 * Copyright (C) 2014
 * Center for Excellence in Nanomedicine (NanoCAN)
 * Molecular Oncology
 * University of Southern Denmark
 * ###############################################
 * Written by:	Markus List
 * Contact: 	mlist'at'health'.'sdu'.'dk
 * Web:			http://www.nanocan.org/miracle/
 * ###########################################################################
 *	
 *	This file is part of MIRACLE.
 *
 *  MIRACLE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with this program. It can be found at the root of the project page.
 *	If not, see <http://www.gnu.org/licenses/>.
 *
 * ############################################################################
 */
package org.nanocan.rppa.scanner

import org.nanocan.file.ResultFile
import org.nanocan.file.ResultFileConfig
import org.nanocan.layout.SlideLayout
import org.nanocan.security.Person

class Slide{

    Date dateOfStaining

    Date dateCreated
    Date lastUpdated

    Person createdBy
    Person lastUpdatedBy

    String barcode
    Person experimenter
    int laserWavelength
    int photoMultiplierTube
    ResultFile resultFile
    ResultFile resultImage
    ResultFile protocol
    SlideLayout layout
    String title
    String comments
    ResultFileConfig lastConfig
    String uuid
    String photoMultiplierTubeSetting

    static hasMany = [spots: Spot, blockShifts: BlockShift]

    Antibody antibody

    static searchable = {
        except = ["spots", "blockShifts", "lastConfig", "resultFile", "resultImage", "protocol", "layout"]
        antibody component: true
    }

    static constraints = {
         uuid nullable: true
         lastConfig nullable: true
         barcode nullable: true
         laserWavelength min: 1, max: 1000
         protocol nullable: true
         resultImage nullable: true
         comments nullable: true
         title nullable: true
         photoMultiplierTubeSetting inList: ["high", "low"], nullable: true
    }

    static mapping = {
        comments type:"text"
        spots cascade: "all-delete-orphan"
        blockShifts cascade:  "all-delete-orphan"
    }

    String toString()
    {
        if(!title){
          if(!barcode) return("id" + id + "_" + antibody.toString() + "_" + photoMultiplierTube + "(" + photoMultiplierTubeSetting+")")
            else return("id"+ id + "_" + barcode + "_" + antibody.toString() + "_" + photoMultiplierTube + "(" + photoMultiplierTubeSetting+")")
        }
        else return("id" + id + "_" + barcode + "_" + title + "_" + antibody.toString() + "_" + photoMultiplierTube + "(" + photoMultiplierTubeSetting+")")
    }
}
