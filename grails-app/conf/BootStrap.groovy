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
import grails.util.Environment
import org.nanocan.file.ResultFile
import org.nanocan.file.ResultFileConfig
import org.nanocan.rppa.scanner.Antibody

import org.nanocan.rppa.scanner.Slide

import org.nanocan.layout.SlideLayout
import org.nanocan.layout.CellLine
import org.nanocan.layout.LysisBuffer
import org.nanocan.layout.Dilution

import org.nanocan.layout.SpotType
import org.nanocan.layout.Inducer
import org.nanocan.layout.Sample
import grails.util.GrailsUtil
import org.nanocan.security.Role
import org.nanocan.security.Person
import org.nanocan.security.PersonRole
import org.nanocan.project.Project
import grails.converters.JSON
import org.nanocan.rppa.scanner.Spot
import org.nanocan.rppa.scanner.BlockShift

class BootStrap {

    def slideLayoutService
    def grailsApplication

    def init = { servletContext ->

        switch (Environment.current) {
            case Environment.DEVELOPMENT:

                initUserbase()
                //initMIRACLE()

                break

            case Environment.PRODUCTION:
                initUserbase()
                break

            case Environment.TEST:
                initUserbase()
                break

        }

        /* custom JSON output */
        JSON.registerObjectMarshaller(Spot) {

            def returnArray = [:]
            returnArray['FG'] = it.FG
            returnArray['BG'] = it.BG
            returnArray['Signal'] = it.signal
            returnArray['Block'] = it.block
            returnArray['Row'] = it.row
            returnArray['Column'] = it.col
            returnArray['SampleName'] = it.layoutSpot?.sample?.name?:"NA"
            returnArray['SampleType'] = it.layoutSpot?.sample?.type?:"NA"
            returnArray['TargetGene'] = it.layoutSpot?.sample?.target?:"NA"
            returnArray['CellLine'] = it.layoutSpot?.cellLine?.name?:"NA"
            returnArray['LysisBuffer'] = it.layoutSpot?.lysisBuffer?.name?:"NA"
            returnArray['DilutionFactor'] = it.layoutSpot?.dilutionFactor?.dilutionFactor?:"NA"
            returnArray['Inducer'] = it.layoutSpot?.inducer?.name?:"NA"
            returnArray['Treatment'] = it.layoutSpot?.treatment?.name?:"NA"
            returnArray['SpotType'] = it.layoutSpot?.spotType?.name?:"NA"
            returnArray['SpotClass'] = it.layoutSpot?.spotType?.type?:"NA"
            returnArray['NumberOfCellsSeeded'] = it.layoutSpot?.numberOfCellsSeeded?.name?:"NA"
            returnArray['Replicate'] = it.layoutSpot?.replicate?:"NA"
            returnArray['PlateRow'] = it.layoutSpot?.wellLayout?.row?:"NA"
            returnArray['PlateCol'] = it.layoutSpot?.wellLayout?.col?:"NA"
            returnArray['PlateLayout'] = it.layoutSpot?.wellLayout?.plateLayout?.id?:"NA"
            returnArray['Flag'] = it.flag
            returnArray['Diameter'] = it.diameter

            return returnArray
        }

        JSON.registerObjectMarshaller(BlockShift) {

            def returnArray = [:]
            returnArray['Block'] = it.blockNumber
            returnArray['hshift'] = it.horizontalShift
            returnArray['vshift'] = it.verticalShift

            return returnArray
        }
    }

    private void initMIRACLE(){
        new Dilution(color: "#aa0000", dilutionFactor: 0.4).save(flush: true, failOnError: true)
        new Dilution(color: "#00aa00", dilutionFactor: 0.16).save(flush:true, failOnError: true)
        new Dilution(color: "#0000aa", dilutionFactor: 0.064).save(flush: true, failOnError: true)
        new Dilution(color: "#aaaa00", dilutionFactor: 1.0).save(flush: true, failOnError: true)
    }

    private void initUserbase(){

        def adminRole = Role.findByAuthority("ROLE_ADMIN")
        if(!adminRole)  adminRole = new Role(authority: 'ROLE_ADMIN').save(flush: true, failOnError: true)
        def userRole = Role.findByAuthority("ROLE_USER")
        if(!userRole) userRole= new Role(authority: 'ROLE_USER').save(flush: true, failOnError: true)

        if(!Person.findByUsername("demo")){
            def testUser = new Person(username: 'demo', enabled: true, password: 'demo0815')
            testUser.save(flush: true, failOnError: true)
            PersonRole.create testUser, userRole, true
        }

        if(!Person.findByUsername("admin")){
            def adminUser = new Person(username: 'admin', enabled: true, password: 'NanoCAN')
            adminUser.save(flush: true, failOnError: true)
            PersonRole.create adminUser, adminRole, true
            PersonRole.create adminUser, userRole, true
        }
    }

    private void initSampleData() {

        def primaryAB = new Antibody(name: "btubulin 1", concentration: 5, concentrationUnit: "mM").save(flush:true, failOnError: true)
        def primaryAB2 = new Antibody(name: "btubulin 2", concentration: 5, concentrationUnit: "mM").save(flush:true, failOnError: true)

        def dilution = new Dilution(dilutionFactor:  1.0, color: "#e0e0e0").save(flush:true, failOnError: true)
        def dilutionA = new Dilution(dilutionFactor:  0.5, color: "#aa0000").save(flush:true, failOnError: true)
        def dilutionB = new Dilution(dilutionFactor:  0.25, color: "#00bb00").save(flush:true, failOnError: true)
        def dilutionC = new Dilution(dilutionFactor:  0.125, color: "#0000aa").save(flush:true, failOnError: true)

        new SpotType(name: "LB", type: "Buffer", color:  "#550000").save(flush:true, failOnError: true)
        new SpotType(name:  "IgG10", type: "Control", color:  "#000055").save(flush:true, failOnError: true)
        new SpotType(name: "IgG100", type: "Control", color:  "#0000aa").save(flush:true, failOnError: true)
        new SpotType(name: "Sample", type: "Sample", color:  "#005500").save(flush:true, failOnError: true)

        new Inducer(name: "Dox .5", concentration: 0.5, concentrationUnit: "mM", color: "#aa0000").save(flush:true, failOnError: true)
        new Inducer(name: "Dox .1", concentration: 0.1, concentrationUnit: "mM", color: "#550000").save(flush:true, failOnError: true)

        new Sample(name: "siRNA1", type: "siRNA", targetGene: "Gene1", color: "#a0a000").save(flush:true, failOnError: true)
        new Sample(name: "siRNA2", type: "siRNA", targetGene: "Gene2", color: "#00a0a0").save(flush:true, failOnError: true)
        new Sample(name: "siRNA3", type:  "siRNA", targetGene: "Gene3", color: "#a000a0").save(flush:true, failOnError: true)

        def resultFileConfig = new ResultFileConfig(
                name : "Default Config",
                blockCol: 'F',
                rowCol: 'H',
                columnCol: 'G',
                fgCol: 'M',
                bgCol: 'S',
                flagCol: 'L',
                xCol: 'I',
                yCol: 'J',
                diameterCol: 'K',
                skipLines: 25
        ).save(flush:true, failOnError: true)

        String fileName = "sampleData/2012-03-28 b-tubulin abcam abnova original.xls"
        String fileNameTwoColumns = "sampleData/2012-01-19 MLP - orginal.xls"

        def resultFile = new ResultFile(fileType: "Result", fileName: "2012-03-28 b-tubulin abcam abnova original.xls",
                filePath:  fileName, dateUploaded: new Date()).save(flush: true, failOnError: true)

        def resultFileTwoColums = new ResultFile(fileType: "Result", fileName: "2012-01-19 MLP - orginal.xls",
                filePath:  fileNameTwoColumns, dateUploaded: new Date()).save(flush: true, failOnError: true)

        def person = Person.get(1)

        def slideLayout = new SlideLayout(columnsPerBlock: 1, rowsPerBlock: 72, numberOfBlocks: 12,
                title: "Default Layout", depositionPattern: "[4,4,2,2,1,1]",
                createdBy: person, lastUpdatedBy: person).save(flush:true, failOnError: true)

        def slideLayoutTwoColumns = new SlideLayout(columnsPerBlock: 2, rowsPerBlock: 15, numberOfBlocks: 48,
                title: "Two Column Layout", depositionPattern: "[4,4,2,2,1,1]",
                createdBy: person, lastUpdatedBy: person).save(flush: true, failOnError: true)

        def slide = new Slide(experimenter: person, antibody: primaryAB,
                dateOfStaining: new Date(), laserWavelength: 635, resultFile: resultFile, resultImage: null,
                layout: slideLayout, photoMultiplierTube: 4, protocol:  null,
                createdBy: person, lastUpdatedBy: person).save(flush:true, failOnError: true)

        def slideB = new Slide(experimenter: person, antibody: primaryAB,
                dateOfStaining: new Date(), laserWavelength: 635, resultFile: resultFile, resultImage: null,
                layout: slideLayout, photoMultiplierTube: 4, protocol:  null,
                createdBy: person, lastUpdatedBy: person).save(flush:true, failOnError:  true)

        def slideC = new Slide(experimenter: person, antibody: primaryAB2,
                dateOfStaining: new Date(), laserWavelength: 635, resultFile: resultFile, resultImage: null,
                layout: slideLayout, photoMultiplierTube: 4, protocol:  null,
                createdBy: person, lastUpdatedBy: person).save(flush:true, failOnError:  true)

        def slideD = new Slide(experimenter: person, antibody: primaryAB2,
                dateOfStaining: new Date(), laserWavelength: 635, resultFile: resultFile, resultImage: null,
                layout: slideLayout, photoMultiplierTube: 4, protocol:  null,
                createdBy: person, lastUpdatedBy: person).save(flush:true, failOnError:  true)

        def slideTwoColumns = new Slide(experimenter: person, antibody: primaryAB2,
                dateOfStaining: new Date(), laserWavelength: 635, resultFile: resultFileTwoColums, resultImage: null,
                layout: slideLayoutTwoColumns, photoMultiplierTube: 4, protocol:  null,
                createdBy: person, lastUpdatedBy: person).save(flush:true, failOnError:  true)

        /* slide layout    */

        def cellLine = new CellLine(name: "Co2", color: "#bb0000").save(flush:true, failOnError: true)
        def cellLineCo9 = new CellLine(name: "Co9", color: "#00bb00").save(flush:true, failOnError: true)
        def lysisBuffer = new LysisBuffer(name: "LB 5", concentration: 5, concentrationUnit: "mM", color: "#00aaaa").save(flush:true, failOnError: true)
        def lysisBuffer10 = new LysisBuffer(name: "LB 10", concentration: 10, concentrationUnit: "mM", color: "#0000bb").save(flush:true, failOnError: true)

        slideLayoutService.createSampleSpots(slideLayout)
        slideLayoutService.createSampleSpots(slideLayoutTwoColumns)

        def project = new Project(projectTitle: "Test project", projectDescription: "A project to test", createdBy: person, lastUpdatedBy: person).save(flush:true, failOnError: true)
    }

    def destroy = {
    }
}
