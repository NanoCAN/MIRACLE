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
package org.nanocan.rppa.spotting

import org.nanocan.layout.SlideLayout
import org.nanocan.layout.WellLayout
import org.nanocan.layout.LayoutSpot
import org.nanocan.layout.Dilution

/**
 * Created by IntelliJ IDEA.
 * User: mlist
 * Date: 18-10-12
 * Time: 16:45
 */
abstract class Spotter {

    def slideLayout = new SlideLayout()
    def maxSpottingRows
    def maxSpottingColumns
    def maxExtractorColumns
    def maxExtractorRows
    def grailsApplication
    def currentSpottingColumn = 1
    def currentSpottingRow = 1
    def matchingMaps
    def defaultSpotType
    def defaultLysisBuffer

    Spotter(Map map)
    {
        this.grailsApplication = map.grailsApplication
        this.maxSpottingRows = map.maxSpottingRows?:18
        this.maxSpottingColumns = map.maxSpottingColumns?:2
        this.maxExtractorColumns = map.maxExtractorColumns?:12
        this.maxExtractorRows = map.maxExtractorRows?:4
        this.matchingMaps = map.matchingMaps
        this.defaultSpotType = map.defaultSpotType
        this.defaultLysisBuffer = map.defaultLysisBuffer
    }

    def spot384(def extraction) {

        def wells = extraction[0]
        int replicate = extraction[1]
        def plate = extraction[2]
        def extractionSorted = sortExtraction(wells)

        extractionSorted.each{
            createLayoutSpot(it, replicate, null, it.row, it.col, plate)
        }

        nextSpot()
    }

    def spot96as384(def extraction){
        spot96as384(extraction, null)
    }

    def spot96as384(def extraction, dilutionPattern) {

        def parseRow = {row -> 1 + (row - 1) * 2}
        def parseColumn = {col -> 1 + (col - 1) * 2}

        def wells = extraction[0]
        int replicate = extraction[1]
        int plate = extraction[2]

        def extractionSorted = sortExtraction(wells)

        extractionSorted.each {

            def row = parseRow(it.row)
            def col = parseColumn(it.col)

            for(int subCol in 0..1)
            {
                for(int subRow in 0..1)
                {
                    createLayoutSpot(it, replicate.toString(), dilutionPattern[subRow][subCol], (row + subRow), (col + subCol), plate)
                }
            }
        }

        nextSpot()
    }

    abstract void nextSpot()

    abstract def sortExtraction(def extraction)

    boolean isFull(){
        if (currentSpottingColumn > maxSpottingColumns || currentSpottingRow > maxSpottingRows) return true
        else return false
    }

    def calculateBlockFromRowAndCol(int row, int col)
    {
        //get extractor position from plate position
        def modRow = (row % maxExtractorRows)
        if(modRow == 0 ) modRow = maxExtractorRows

        def modCol = (col % maxExtractorColumns)
        if (modCol == 0) modCol = maxExtractorColumns

        //convert into block number
        (((modRow - 1) * maxExtractorColumns) + modCol)
    }

    def createLayoutSpot(wellLayout, replicate, dilutionFactor, row, column, plate){

        if (isFull()) throw new Exception("Layout is full. Can not add ${wellLayout}.")

        def props = [:]

        for(prop in ["cellLine", "inducer", "treatment", "numberOfCellsSeeded", "sample"])
        {
            def propInstance
            wellLayout.attach()

            if (wellLayout."${prop}")  propInstance = wellLayout."${prop}"

            props.put(prop, propInstance)
        }

        def wellLayoutMiracle = null

        //spot type
        if(wellLayout instanceof WellLayout)
        {
            wellLayoutMiracle = wellLayout
            props.put("spotType", wellLayout.spotType)
        }

        if (dilutionFactor) dilutionFactor = Dilution.get(dilutionFactor)

        def newLayoutSpot = new LayoutSpot(wellLayout: wellLayoutMiracle, replicate: replicate, block: calculateBlockFromRowAndCol(row, column),
                cellLine: props.cellLine, dilutionFactor: dilutionFactor, col: currentSpottingColumn, row: currentSpottingRow, inducer: props.inducer,
                lysisBuffer: defaultLysisBuffer, treatment: props.treatment, numberOfCellsSeeded: props.numberOfCellsSeeded,
                spotType: props.spotType?:defaultSpotType, sample: props.sample, layout: slideLayout, plateId: plate)

        slideLayout.addToSampleSpots(newLayoutSpot)
    }
}
