package org.nanocan.analysis

import org.nanocan.layout.LayoutSpot
import org.nanocan.layout.PlateLayout
import org.nanocan.layout.SlideLayout
import org.nanocan.plates.Plate
import org.nanocan.project.Experiment
import org.nanocan.project.Project
import org.nanocan.rppa.scanner.Slide
import org.springframework.security.access.annotation.Secured

@Secured(['ROLE_USER'])
class AnalysisController {

    def securityTokenService

    def start() {

        println params
        def projects = Project.list()

        def experiments = params.project?Experiment.findAllByProject(Project.get(params.project)):Experiment.list()

        def plateLayouts
        if(params.experiment) plateLayouts = Experiment.get(params.experiment).plateLayouts
        else if(params.project) plateLayouts = Project.get(params.project).experiments.collect({it.plateLayouts})
        else plateLayouts = PlateLayout.list()

        def slideLayouts
        if(params.plateLayout){
            def plateLayoutInstance = PlateLayout.get(params.plateLayout)
            slideLayouts = SlideLayout.findAllBySourcePlates(plateLayoutInstance?.plates?:[])
        }
        else if(params.experiment) slideLayouts = Experiment.get(params.experiment).slideLayouts
        else if(params.project) slideLayouts = Project.get(params.project).experiments.collect{it.slideLayouts}
        else slideLayouts = SlideLayout.list()

        def slides
        def plates
        def slideLayoutInstance

        if(params.slideLayout){
            slideLayoutInstance = SlideLayout.get(params.slideLayout)
            slides = Slide.findAllByLayout(slideLayoutInstance)
            plates = slideLayoutInstance.sourcePlates
        }

        [projects: projects,
                selectedProject: params.project,
                experiments: experiments,
                selectedExperiment: params.experiment,
                plateLayouts: plateLayouts,
                selectedPlateLayout: params.plateLayout,
                slideLayouts: slideLayouts,
                selectedSlideLayout: params.slideLayout,
                slides: slides,
                plates: plates,
                slideLayoutInstance: slideLayoutInstance]
    }

    def batchAnalysis(){
        def shinyBatchUrl = grailsApplication.config.shiny.batch.analysis

        if(!shinyBatchUrl){
            render "Shiny application not defined"
            return
        }

        def baseUrl = g.createLink(controller: "spotExport", absolute: true).toString()
        baseUrl = baseUrl.substring(0, baseUrl.size()-5)
        def spotExportLink = java.net.URLEncoder.encode(baseUrl, "UTF-8")

        def slides
        def plates

        if(params.processAll)
        {
            slides = params.list("allSlides").collect{Slide.get(it)}
            plates = params.list("allPlates").collect{Plate.get(it)}
        }

        else{
            slides = params.list("selectedSlides").collect{Slide.get(it)}
            plates = params.list("selectedPlates").collect{Plate.get(it)}
        }

        def slideSecurityTokens = slides.collect{securityTokenService.getSecurityToken(it)}.join("|")
        def plateSecurityTokens = plates.collect{securityTokenService.getSecurityToken(it)}.join("|")

        def analysisUrl = shinyBatchUrl + "?baseUrl=" + spotExportLink + "&plateSecurityTokens=" + plateSecurityTokens + "&slideSecurityTokens=" + slideSecurityTokens
        redirect(url: analysisUrl)
    }
}
