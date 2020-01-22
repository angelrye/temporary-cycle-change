import { SearchCycleChange, HttpClient, evaluateAndReturnResponse, evaluateAndReturnResponses, evaluateEmailResponse } from './rest-api.js'

$(function() {

    initialize.initializeComponents();

    (async() => {
        page.alert("Please wait while the application loads.", restPaths.getInfoStatus());

        const response = await HttpClient.get(restPaths.fetchDivisionUrl());
        evaluateAndReturnResponse(response)
            .then(data => {
                initialize.setDivisions(data._embedded.stringList);
                page.showDivisionTabs('History');
                page.alert("Successfully loaded divisions.", restPaths.getSuccessStatus());
            }, fReason => {
                throw new Error(fReason);
            }).catch(error => page.alert(error, restPaths.getErrorStatus()));
    })();


    $('#showHistory').click(function() {

        const startDate = page.getStartDate();
        const endDate = page.getEndDate();

        if (startDate.length === 0 || endDate.length === 0) {
            page.alert('Select date first.', restPaths.getInfoStatus());
        } else {
            (async($btn) => {
                try {
                    page.alert("Please wait while we load your history schedules...", restPaths.getInfoStatus());
                    $(this).prop('disabled', true);

                    const activeDivision = $('a.nav-link.active').prev()[0].value;

                    await showCycleChangeRequestSchedule(new Array(activeDivision), startDate, endDate);
                    
                    page.alert("Loading other division history schedules...", restPaths.getInfoStatus());
                    await showCycleChangeRequestSchedule(initialize.getDivisionsExcept(activeDivision), startDate, endDate);

                    page.alert("Data loaded successfully.", restPaths.getSuccessStatus());
                } catch (error) {
                    page.alert(error, restPaths.getErrorStatus());
                } finally {
                    $(this).prop('disabled', false);
                }
            })($(this));
        }

    });

    async function showCycleChangeRequestSchedule(divisions, startDate, endDate) {
        const cycleChangeUrl = restPaths.historyUrl();

        const searchCycleChanges = divisions.map(division => {
            return new SearchCycleChange(division, startDate, endDate);
        });

        const cycleChanges = await HttpClient.postBatch('POST', cycleChangeUrl, searchCycleChanges);
        const promises = await evaluateAndReturnResponses(cycleChanges);
        for await (let data of promises) {
            if (data._embedded === undefined) {
                page.alert('No records found.', restPaths.getInfoStatus());
            } else {
                page.generateCycleChangeRequestSchedule(data._embedded.cycleChangeRequestList, 'History');
            }
        }
    }

});