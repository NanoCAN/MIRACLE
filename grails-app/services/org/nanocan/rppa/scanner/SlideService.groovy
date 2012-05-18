package org.nanocan.rppa.scanner

import org.nanocan.rppa.layout.LayoutSpot
import org.nanocan.rppa.layout.SlideLayout
import org.hibernate.FetchMode
import org.springframework.web.multipart.commons.CommonsMultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest

class SlideService {

    def excelImportService
    def imageConvertService

    def dealWithFileUploads(def request, def params)
    {
        CommonsMultipartFile resultFile
        CommonsMultipartFile resultImage
        CommonsMultipartFile protocol

        /* if new files have been uploaded we take them instead */
        if(request instanceof MultipartHttpServletRequest)
        {
            MultipartHttpServletRequest mpr = (MultipartHttpServletRequest)request;

            resultFile = (CommonsMultipartFile) mpr.getFile("resultFileInput");
            resultImage = (CommonsMultipartFile) mpr.getFile("resultImageInput");
            protocol = (CommonsMultipartFile) mpr.getFile("protocolInput");

            if(!resultFile.empty) params["resultFile.id"] = createResultFile(resultFile, "Result").id
            else params.remove("resultFileInput")

            if(!resultImage.empty) params["resultImage.id"] = createResultFile(resultImage, "Image").id
            else params.remove("resultImageInput")

            if(!protocol.empty) params["protocol.id"] = createResultFile(protocol, "Protocol").id
            else params.remove("protocolInput")
        }

        return params
    }

    def createResultFile(def resultFile, String type)
    {
        if(!resultFile.empty) {

            def currentDate = new java.util.Date()
            long timestamp = currentDate.getTime()
            def filePath = "upload/" + timestamp.toString() + "_" + resultFile.originalFilename

            resultFile.transferTo( new File(filePath) )

            if(type == "Image")
            {
               imageConvertService.createZoomifyImage("upload", filePath, ['numCPUCores': -1])
            }

            def newResultFile = new ResultFile(fileType: type, fileName: (resultFile.originalFilename as String), filePath: filePath, dateUploaded:  currentDate as Date)

            if(newResultFile.save(flush: true))
            {
                log.info "saving of file ${filePath} was successful"
                return newResultFile
            }

            else
            {
                log.warn "could not save file to ${filePath}."
                return null
            }
        }
    }

    def getSheets(def slideInstance)
    {
        def resultFile = slideInstance.resultFile

        ResultFileImporter importer = new ResultFileImporter()

        def fis = new FileInputStream(resultFile.filePath)

        importer.readFromStream(fis)
        def sheets = importer.getSheets()

        fis.close();

        return sheets
    }

    def sessionFactory
    def progressService
    def slideLayoutService

    def processResultFile(def slideInstance, String sheetName, ResultFileConfig rfc)
    {
        ResultFileImporter importer = new ResultFileImporter()

        def fis = new FileInputStream(slideInstance.resultFile.filePath)

        importer.readFromStream(fis)

        def spots = importer.getSpots(sheetName, rfc)

        fis.close()

        //distribute the remaining 20% of the progress bar (80% used by excelimportservice)
        def numberOfSpots = spots.size()
        def onePercent = (int) (numberOfSpots / 20)
        def nextStep = onePercent
        def currentPercent = 80

        def depositionList = slideLayoutService.parseDepositions(slideInstance.layout.depositionPattern)
        int deposLength = depositionList.size()

        spots.eachWithIndex{ obj, i ->

            def newSpot = new Spot(obj)
            slideInstance.addToSpots(newSpot).save()

            int layoutColumn = (((obj.col as Integer)-1) / deposLength)
            //we don't want to start with zero
            layoutColumn++

            int layId = slideInstance.layout.id

            def layoutSpots = LayoutSpot.where() {
                ( layout.id == (layId as Long)
                && block == (obj.block as Integer)
                && row == (obj.row as Integer)
                && col == layoutColumn )
            }

            if(layoutSpots.list().size() == 0){
                progressService.setProgressBarValue("excelimport", 100)
                return "Error: No layout information for spot ${newSpot}"
            }
            layoutSpots.list().first().addToSpots(newSpot).save()

            if(i == nextStep)
            {
                nextStep += onePercent
                progressService.setProgressBarValue("excelimport", currentPercent++)
            }
        }

        return null
    }

    def cleanGorm = {
        sessionFactory.currentSession.flush()
        sessionFactory.currentSession.clear()
        org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP.get().clear()
        System.gc()
    }

    def exportToCSV(def slideInstance, def separator, def withBlockShifts, def withLayout) {

        println separator
        println slideInstance
        def layId = slideInstance.layout.id

        def spotCriteria = Spot.createCriteria().list{

            createAlias('layoutSpot', 'lspot')
            createAlias('lspot.layout', 'lout')

            eq("lout.id", layId )

            projections {
                property("block")
                property("col")
                property("row")
                property("FG")
                property("BG")
                property("x")
                property("y")
                property("diameter")
                property("flag")
                property("lspot.cellLine")
                property("lspot.lysisBuffer")
                property("lspot.dilutionFactor")
                property("lspot.inducer")
                property("lspot.spotType")
                property("lspot.sample")
            }
        }

        return spotCriteria
    }
}