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
package savanah

import grails.plugin.spock.UnitSpec
import spock.lang.Shared
import grails.test.mixin.Mock
import org.nanocan.rppa.spotting.LeftToRightSpotter
import org.nanocan.rppa.layout.SlideLayout
import org.nanocan.rppa.security.Person
import org.nanocan.rppa.layout.LayoutSpot
import org.nanocan.rppa.layout.PlateLayout
import org.nanocan.rppa.spotting.ExtractionRowWiseIterator
import org.nanocan.rppa.layout.WellLayout

/**
 * Created by IntelliJ IDEA.
 * User: mlist
 * Date: 19-10-12
 * Time: 13:35
 */
@Mock([Plate, PlateLayout, WellLayout, SlideLayout, Person, LayoutSpot])
class LeftToRightSpotterSpec extends UnitSpec{

    @Shared plates384
    @Shared plates96
    @Shared grailsApplication
    @Shared person = new Person(username: "test user", password: "yes", enabled: true
    , accountExpired: false, accountLocked: false, passwordExpired: false)

    def setup()
    {
        def plateLayout384 = new PlateLayout(name: "test layout 384", format: "384-well", cols: 24, rows: 16)
        plateLayout384.save()

        for (int col = 1; col <= plateLayout384.cols; col++) {
            for (int row = 1; row <= plateLayout384.rows; row++) {
                plateLayout384.addToWells(new WellLayout(col: col, row: row, layout: plateLayout384))
            }
        }
        plateLayout384.save()

        def plateLayout96 = new PlateLayout(name: "test layout 96", format: "96-well", cols: 12, rows: 8)
        plateLayout96.save()

        for (int col = 1; col <= plateLayout96.cols; col++) {
            for (int row = 1; row <= plateLayout96.rows; row++) {
                plateLayout96.addToWells(new WellLayout(col: col, row: row, layout: plateLayout96))
            }
        }
        plateLayout96.save()

        plates384 = []
        plates96 = []

        for(name in ["a", "b", "c", "d"]){
            def plate384 = new Plate(cols: 24, rows:  16, barcode: "${name}384bc", family: "daughter", format: "384-well", name: "${name}384", layout: plateLayout384)
            plate384.save()

            def plate96 = new Plate(cols: 12, rows:  8, barcode: "${name}96bc", family: "daughter", format: "96-well", name: "${name}96", layout: plateLayout96)
            plate96.save()

            plates384 << plate384
            plates96 << plate96
        }
    }

    def "test spotting of a single 384 well plate"()
    {
        setup:
        def iterator = new ExtractionRowWiseIterator(plate: plates384.first())
        def leftToRightSpotter = new LeftToRightSpotter(grailsApplication: grailsApplication)

        when:
        while(iterator.hasNext())
        {
            def extraction = iterator.next()
            leftToRightSpotter.spot384(extraction)
        }

        def slideLayout = leftToRightSpotter.slideLayout

        persistSlideLayout(slideLayout, leftToRightSpotter)

        println slideLayout.sampleSpots

        then:
        slideLayout.errors.toString() == "org.grails.datastore.mapping.validation.ValidationErrors: 0 errors"
        slideLayout.sampleSpots.size() == 384
        LayoutSpot.findByBlockGreaterThan(48) == null
        LayoutSpot.findByRowGreaterThan(4) == null
        LayoutSpot.findByColGreaterThan(2) == null
    }

    def "test spotting of two 384 well plates"()
    {
        setup:
        def iteratorFirst = new ExtractionRowWiseIterator(plate: plates384.first())
        def iteratorSecond = new ExtractionRowWiseIterator(plate: plates384.get(2))
        def leftToRightSpotter = new LeftToRightSpotter(grailsApplication: grailsApplication)

        when:
        while(iteratorFirst.hasNext()) leftToRightSpotter.spot384(iteratorFirst.next())
        while(iteratorSecond.hasNext()) leftToRightSpotter.spot384(iteratorSecond.next())

        def slideLayout = leftToRightSpotter.slideLayout

        persistSlideLayout(slideLayout, leftToRightSpotter)
        println slideLayout.sampleSpots

        then:
        slideLayout.errors.toString() == "org.grails.datastore.mapping.validation.ValidationErrors: 0 errors"
        slideLayout.sampleSpots.size() == 768
        LayoutSpot.findByBlockGreaterThan(48) == null
        LayoutSpot.findByRowGreaterThan(8) == null
        LayoutSpot.findByColGreaterThan(2) == null
        LayoutSpot.findByRowBetween(5,8) != null
    }

    def persistSlideLayout(SlideLayout slideLayout, LeftToRightSpotter leftToRightSpotter) {
        slideLayout.blocksPerRow = 12
        slideLayout.columnsPerBlock = 2
        slideLayout.depositionPattern = "[4,4,2,2,1,1]"
        slideLayout.numberOfBlocks = 48
        slideLayout.rowsPerBlock = leftToRightSpotter.currentSpottingRow
        slideLayout.title = "test layout"
        slideLayout.lastUpdatedBy = person
        slideLayout.createdBy = person

        slideLayout.save()
    }

    def "test fail with spotting of too many plates"()
    {
        setup:
        def leftToRightSpotter = new LeftToRightSpotter(grailsApplication: grailsApplication, maxSpottingRows: 15)

        when:
        for(def plate in plates384){
            println "adding ${plate}"
            def iterator = new ExtractionRowWiseIterator(plate: plate)
            while(iterator.hasNext())
                leftToRightSpotter.spot384(iterator.next())
        }
        println leftToRightSpotter.slideLayout.sampleSpots

        then:
        Exception e = thrown()
        e.message.startsWith("Layout is full.") == true
    }

    def "test spotting of a single extraction of a 96 well plate"()
    {
        setup:
        def iterator = new ExtractionRowWiseIterator(plate: plates96.first(),  extractorCols: 6, extractorRows: 2)
        def leftToRightSpotter = new LeftToRightSpotter(grailsApplication: grailsApplication)

        when:
        def extraction = iterator.next()
        leftToRightSpotter.spot96as384(extraction)

        def slideLayout = leftToRightSpotter.slideLayout

        persistSlideLayout(slideLayout, leftToRightSpotter)

        println slideLayout.sampleSpots

        then:
        slideLayout.errors.toString() == "org.grails.datastore.mapping.validation.ValidationErrors: 0 errors"
        slideLayout.sampleSpots.size() == 48
        LayoutSpot.findByBlockGreaterThan(48) == null
        LayoutSpot.findByRowGreaterThan(1) == null
        LayoutSpot.findByColGreaterThan(1) == null
    }

    def "test spotting of two extractions of a 96 well plate"()
    {
        setup:
        def iterator = new ExtractionRowWiseIterator(plate: plates96.first(),  extractorCols: 6, extractorRows: 2)
        def leftToRightSpotter = new LeftToRightSpotter(grailsApplication: grailsApplication)

        when:
        leftToRightSpotter.spot96as384(iterator.next())
        leftToRightSpotter.spot96as384(iterator.next())

        def slideLayout = leftToRightSpotter.slideLayout

        persistSlideLayout(slideLayout, leftToRightSpotter)

        println slideLayout.sampleSpots

        then:
        slideLayout.errors.toString() == "org.grails.datastore.mapping.validation.ValidationErrors: 0 errors"
        slideLayout.sampleSpots.size() == 96
        LayoutSpot.findByBlockGreaterThan(48) == null
        LayoutSpot.findByRowGreaterThan(1) == null
        LayoutSpot.findByColGreaterThan(2) == null
    }

    def "test spotting of three extractions of a 96 well plate"()
    {
        setup:
        def iterator = new ExtractionRowWiseIterator(plate: plates96.first(), extractorCols: 6, extractorRows: 2)
        def leftToRightSpotter = new LeftToRightSpotter(grailsApplication: grailsApplication)

        when:
        leftToRightSpotter.spot96as384(iterator.next())
        leftToRightSpotter.spot96as384(iterator.next())
        leftToRightSpotter.spot96as384(iterator.next())

        def slideLayout = leftToRightSpotter.slideLayout

        persistSlideLayout(slideLayout, leftToRightSpotter)

        println slideLayout.sampleSpots

        then:
        slideLayout.errors.toString() == "org.grails.datastore.mapping.validation.ValidationErrors: 0 errors"
        slideLayout.sampleSpots.size() == 144
        LayoutSpot.findByBlockGreaterThan(48) == null
        LayoutSpot.findByRowGreaterThan(2) == null
        LayoutSpot.findByColGreaterThan(2) == null
    }



    def "test spotting of a single 96 well plate"()
    {
        setup:
        def iterator = new ExtractionRowWiseIterator(plate: plates96.first(), extractorCols: 6, extractorRows: 2)
        def leftToRightSpotter = new LeftToRightSpotter(grailsApplication: grailsApplication)

        when:
        while(iterator.hasNext())
        {
            def extraction = iterator.next()
            leftToRightSpotter.spot96as384(extraction)
        }

        def slideLayout = leftToRightSpotter.slideLayout

        persistSlideLayout(slideLayout, leftToRightSpotter)

        println slideLayout.sampleSpots

        then:
        slideLayout.errors.toString() == "org.grails.datastore.mapping.validation.ValidationErrors: 0 errors"
        slideLayout.sampleSpots.size() == 384
        LayoutSpot.findByBlockGreaterThan(48) == null
        LayoutSpot.findByRowGreaterThan(4) == null
        LayoutSpot.findByColGreaterThan(2) == null
    }


    def "test spotting with four 96 well plates as 384 diluted"()
    {
        setup:
        def leftToRightSpotter = new LeftToRightSpotter(grailsApplication: grailsApplication)

        when:
        for(def plate in plates96){
            println "adding ${plate}"
            def iterator = new ExtractionRowWiseIterator(plate: plate, extractorCols: 6, extractorRows: 2)
            while(iterator.hasNext())
                leftToRightSpotter.spot96as384(iterator.next())
        }

        def slideLayout = leftToRightSpotter.slideLayout

        persistSlideLayout(slideLayout, leftToRightSpotter)

        println slideLayout.sampleSpots

        then:
        slideLayout.sampleSpots.size() == 1536
        LayoutSpot.findByBlockGreaterThan(48) == null
        LayoutSpot.findByRowGreaterThan(16) == null
        LayoutSpot.findByColGreaterThan(2) == null
        LayoutSpot.findByRow(16) != null
    }

}
