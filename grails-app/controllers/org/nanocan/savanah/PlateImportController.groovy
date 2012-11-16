package org.nanocan.savanah

import org.nanocan.savanah.experiment.Experiment
import org.nanocan.savanah.plates.Plate
import org.springframework.security.access.annotation.Secured
import org.nanocan.rppa.layout.SlideLayout
import org.nanocan.savanah.plates.PlateLayout
import org.nanocan.rppa.layout.NumberOfCellsSeeded
import org.nanocan.rppa.layout.CellLine
import org.nanocan.rppa.layout.Inducer
import org.nanocan.rppa.layout.Treatment
import org.nanocan.rppa.rnai.Sample

@Secured(['ROLE_USER'])
class PlateImportController {

    def plateImportService
    def springSecurityService

    def plateLayoutImport(){
        def plateLayouts

        if(params.experiment){
            def experiment  = Experiment.get(params.experiment)
            plateLayouts = experiment.plates
        }

        else plateLayouts = PlateLayout.list()

        [experiments: Experiment.list(), plateLayouts: plateLayouts]
    }

    def convertAndSavePlateLayout(){
        def plateLayouts = params.list("plateLayouts")
        def miracleNewLayout

        def numberOfCellsSeededMap = [:]
        def cellLineMap = [:]
        def inducerMap = [:]
        def treatmentMap = [:]
        def sampleMap = [:]

        params.each{k,v ->
            def indexOfSeparator = k.toString().indexOf('_') + 1

            if(k.toString().startsWith("numberOfCellsSeeded")) numberOfCellsSeededMap.put(k.toString().substring(indexOfSeparator), NumberOfCellsSeeded.findByName(v))
            else if(k.toString().startsWith("cellline")) cellLineMap.put(k.toString().substring(indexOfSeparator), CellLine.findByName(v))
            else if(k.toString().startsWith("inducer")) inducerMap.put(k.toString().substring(indexOfSeparator), Inducer.findByName(v))
            else if(k.toString().startsWith("treatment")) treatmentMap.put(k.toString().substring(indexOfSeparator), Treatment.findByName(v))
            else if(k.toString().startsWith("sample")) sampleMap.put(k.toString().substring(indexOfSeparator), Sample.findByName(v))
        }
        println numberOfCellsSeededMap
        println cellLineMap
        println inducerMap
        println treatmentMap
        println sampleMap
        //check plate names
        flash.message = "PlateLayout(s) with name(s) "
        boolean nameConflict = false
        plateLayouts.each{ layout ->
            if(org.nanocan.rppa.layout.PlateLayout.findByName(params."${layout}_tf") != null){
                flash.message += params."${layout}_tf" + " "
                nameConflict = true
            }
        }
        if (nameConflict)
        {
            flash.message += "exists already. Choose another name"
            params.platesSelected = plateLayouts.collect{PlateLayout.findByName(it).id}
            def newModel = [:]
            newModel.putAll(params)
            newModel.putAll(importSelectedPlateLayouts())
            println newModel
            render(view: "importSelectedPlateLayouts", model: newModel)
            return
        }

        plateLayouts.each{ layout ->
            miracleNewLayout = plateImportService.importPlateLayout(layout, params."${layout}_tf", cellLineMap, numberOfCellsSeededMap, inducerMap, treatmentMap, sampleMap)
        }

        flash.message = "The following plate layouts have been imported: ${plateLayouts} - ${plateLayouts.collect{params."${it}_tf"}}"

        if(plateLayouts.size() > 1)
            redirect(controller: "plateLayout", action:"list")
        else redirect(controller: "plateLayout", action: "show", id: miracleNewLayout.id)

    }

    def importSelectedPlateLayouts(){

        def plateLayouts = params.list("platesSelected").collect{ PlateLayout.get(it)}
        def numberOfCellsSeededList = []
        def cellLineList = []
        def inducerList = []
        def treatmentList = []
        def sampleList = []

        plateLayouts.each{ playout ->
            playout.wells.each{
                if(it.numberOfCellsSeeded) numberOfCellsSeededList << it.numberOfCellsSeeded
                if(it.cellLine) cellLineList << it.cellLine
                if(it.inducer) inducerList << it.inducer
                if(it.treatment) treatmentList << it.treatment
                if(it.sample) sampleList << it.sample
            }
        }

        numberOfCellsSeededList.unique()
        cellLineList.unique()
        inducerList.unique()
        treatmentList.unique()
        sampleList.unique()

        def titles = plateLayouts.collect{it.name}

        [numberOfCellsSeededList : numberOfCellsSeededList, cellLineList: cellLineList,
                inducerList: inducerList, treatmentList: treatmentList, sampleList: sampleList,
                titles: titles, plateLayouts: plateLayouts]
    }

    def plateImport(){
        def plates

        if(params.experiment){
            def experiment  = Experiment.get(params.experiment)
            plates = experiment.plates
        }

        else plates = Plate.list()

        [experiments: Experiment.list(), plates: plates]
    }

    def platesOrdered(){

        if (!params.list("plate[]"))
            render "No plates have been selected"

        else{
            def plates = params.list("plate[]").collect{
                Plate.get(it)
            }

            render(template: "sortedPlates", model: [sortedPlates: plates])
        }
    }

    def importSettings(){
        def plates = params.list("plateOrder")
        [plates: plates]
    }

    def processPlates(){

        def plates = params.list("plates")
        def xPerBlock = params.int("xPerBlock")
        def extractions = [:]

        plates.each{
            def excludedPlateExtractions = []
            for(int extraction in 1..params.int("numOfExtractions")){
                excludedPlateExtractions << params.boolean("Plate_"+it.toString()+"|Extraction_"+extraction+"|Field")
            }
            extractions.put(it, excludedPlateExtractions)
        }

        if(!params.title || params.title == "")
        {
            flash.message = "You have to give a title to this layout!"
            redirect(action: "plateImport")
            return
        }
        //check if title is taken
        if(SlideLayout.findByTitle(params.title))
        {
            flash.message = "Please select another title (this one already exists)."
            redirect(action: "plateImport")
            return
        }

        def slideLayout
        try{
            slideLayout = plateImportService.importPlates(params.title, plates, extractions, params.spottingOrientation,
                    params.extractorOperationMode,
                    params.depositionPattern, xPerBlock,
                    params.bottomLeftDilution, params.topLeftDilution,
                    params.topRightDilution, params.bottomRightDilution)
        } catch(Exception e)
        {
            flash.message = "Import failed with exception: " + e.getMessage()
            redirect(action: "plateImport")
            return
        }

        slideLayout.lastUpdatedBy = springSecurityService.currentUser
        slideLayout.createdBy = springSecurityService.currentUser

        if (slideLayout.save(flush: true, failOnError: true)) {
            redirect(controller: "slideLayout", action: "show", id: slideLayout.id)
        }
        else{
            flash.message = "import succeeded, but persisting the slide layout failed: " + slideLayout.errors.toString()
            redirect(action:  "plateImport")
        }
    }

    def blockSettings(){
        def rowWise = false

        if(params.blockOrder == "left-to-right"){
            rowWise = true
        }

        render template: "blockSettings", model: [rowWise: rowWise]
    }
}
