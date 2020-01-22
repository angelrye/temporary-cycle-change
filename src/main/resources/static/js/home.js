import { SearchCycleChange, NewCycleChange, HttpClient, evaluateAndReturnResponse, evaluateAndReturnResponses, evaluateEmailResponse, SearchCycleChangeByDateRange } from './rest-api.js'

$(function () {

    initialize.initializeComponents();

    (async () => {
        page.alert("Please wait while the application loads.", restPaths.getInfoStatus());

        const response = await HttpClient.get(restPaths.fetchDivisionUrl());
        evaluateAndReturnResponse(response)
            .then(data => {
                initialize.setDivisions(data._embedded.stringList);
                page.showDivisionTabs('HOME');
                page.alert(`Welcome, ${initialize.getUser().fullname}`, restPaths.getSuccessStatus());
            }, fReason => {
                throw new Error(fReason);
            }).catch(error => page.alert(error, restPaths.getErrorStatus()));
    })();

    // changes the double-down arrow to double-up arrow and vice versa
    $('body').on('click', 'a.baseScheduleButton', function () {
        const collapsibleDivId = $(this).siblings('div[class="collapse"]:first').attr("id");
        const faArrowId = $(this).children("i:first").attr("id");
        $("#" + collapsibleDivId).on('hidden.bs.collapse', function () {
            $("#" + faArrowId).removeClass();
            $("#" + faArrowId).addClass('fa fa-angle-double-down');
            $(this).off('hidden.bs.collapse');
        })

        $("#" + collapsibleDivId).on('shown.bs.collapse', function () {
            $("#" + faArrowId).removeClass();
            $("#" + faArrowId).addClass('fa fa-angle-double-up');
            $(this).off('shown.bs.collapse');
        })
    });

    /**
     * Checks and unchecks Cycle change requests
     */
    $('body').on('click', 'thead input[type="checkbox"]', function () {
        const isChecked = $(this).prop("checked");
        const parentId = $(this).closest("table").attr("id");

        $("#" + parentId + " tr:has(td)").find('input[type="checkbox"]').prop("checked", isChecked);
    });

    $('body').on({
        mouseenter: function () {
            let $editIcon = $($(this).find('i.fa-edit')[0]);
            $editIcon.show();
        },
        mouseleave: function (data) {
            let $editIcon = $($(this).find('i.fa-edit')[0]);
            $editIcon.hide();
        }
    }, 'table.cycleChangeTable tbody tr');

    $('body').on({
        mouseenter: function () {
            $(this).css('cursor', 'pointer');
        }
    }, 'i.fa-edit');

    $('body').on('click', 'i.fa-edit', function () {
        const $tr = $($(this).closest('tr')[0]);

        const $statusTxt = $($tr.find('span.statusTxt')[0]);

        if ($statusTxt.text() === 'REJECTED' || $statusTxt.text() === 'CANCELED' || $statusTxt.text() === 'APPROVED') {
            page.alert("You can't edit Rejected, Canceled or Approved schedule.", restPaths.getInfoStatus());
        } else {
            const $toShowElems = $tr.children().find('.showUponEdit');
            const $toHideElems = $tr.children().find('.hideUponEdit');

            const $effDateTxt = $($tr.find('span.effDate')[0]);
            const $offsiteTxt = $($tr.find('span.offsite')[0]);
            const $commentTxt = $($tr.find('span.commentTxt')[0]);

            const offsiteTxt = $offsiteTxt.text();
            if (offsiteTxt === undefined || offsiteTxt !== 'Offsite' || offsiteTxt.length <= 0) {
                $($tr.find('input.offsiteCheckbox')[0]).prop('checked', false);
            } else {
                $($tr.find('input.offsiteCheckbox')[0]).prop('checked', true);
            }

            $($tr.find('input.effDate')[0]).val($effDateTxt.text());
            $($tr.find('input.commentTxt')[0]).val($commentTxt.text());

            $toHideElems.hide();
            $toShowElems.show();
        }
    });

    $('body').on('click', 'tbody tr button.btn-danger', function () {
        const $tr = $(this).closest('tr');

        const $toHideElems = $tr.children().find('.showUponEdit');
        const $toShowElems = $tr.children().find('.hideUponEdit');
        $toHideElems.hide();
        $toShowElems.show();
    });

    $('body').on('click', 'tbody tr button.btn-primary', function () {

        const startDate = page.getStartDate();
        const endDate = page.getEndDate();

        if (startDate.length === 0 || endDate.length === 0) {
            page.alert('Select date first.', restPaths.getInfoStatus());
        } else {
            const $tr = $($(this).closest('tr')[0]);
            const $cycleChangeInputId = $tr.find('input.cycleChangeId');
            const $divisionId = $tr.find('input.divId');
            const $comment = $tr.find('input.commentTxt');
            const cycleChangeId = $($cycleChangeInputId)[0].value;
            const divisionId = $($divisionId)[0].value;
            const comment = $($comment)[0].value

            const $offsite = $($tr.find('input.offsiteCheckbox')[0]);
            const $runDate = $($tr.find('span.runDate')[0]);
            const $effDate = $($tr.find('input.effDate')[0]);

            const offsite = $offsite.prop('checked') === true ? '1' : '0';

            const cycleChange = {
                offsiteIndicator: offsite,
                id: cycleChangeId,
                divId: divisionId,
                runDate: $runDate.text(),
                effectiveDate: $effDate.val(),
                comment: comment
            };

            (async () => {
                try {
                    $(this).prop('disabled', true);

                    page.alert('Modifying cycle schedule...', restPaths.getInfoStatus());

                    await modifyCycleSchedule(cycleChange);
                    await showCycleChangeRequestSchedule(new Array(divisionId), startDate, endDate);

                    page.alert('Cycle schedule has been modified.', restPaths.getSuccessStatus());
                } catch (error) {
                    page.alert(error, restPaths.getErrorStatus());
                } finally {
                    $(this).prop('disabled', false);
                }

            })();
        }

    });

    $('#generateBtn').click(function () {

        const startDate = page.getStartDate();
        const endDate = page.getEndDate();

        if (startDate.length === 0 || endDate.length === 0) {
            page.alert('Select date first.', restPaths.getInfoStatus());
        } else {
            (async ($btn) => {
                try {
                    page.alert("Please wait while we load your schedules...", restPaths.getInfoStatus());
                    $(this).prop('disabled', true);

                    const activeDivision = $('a.nav-link.active').prev()[0].value;

                    await showBaseSchedule(new Array(activeDivision), startDate, endDate);
                    await showCycleChangeRequestSchedule(new Array(activeDivision), startDate, endDate);

                    page.alert("Loading other division schedules...", restPaths.getInfoStatus());
                    await showBaseSchedule(initialize.getDivisionsExcept(activeDivision), startDate, endDate);
                    await showCycleChangeRequestSchedule(initialize.getDivisionsExcept(activeDivision), startDate, endDate);

                    page.alert("Finished processing your request.", restPaths.getSuccessStatus());
                } catch (error) {
                    page.alert(error, restPaths.getErrorStatus());
                } finally {
                    $(this).prop('disabled', false);
                }
            })($(this));
        }

    });

    $('#cancelRequestBtn').click(function () {

        const ids = page.getSelectedCycleChangeId();
        const startDate = page.getStartDate();
        const endDate = page.getEndDate();
        const divisionId = page.getActiveDivision();

        if (ids === undefined || ids.length <= 0) {
            page.alert("Select atleast one (1) schedule to cancel.", restPaths.getInfoStatus());
        } else {
            page.alert("Canceling Cycle Change Schedules.", restPaths.getInfoStatus());

            (async () => {
                try {
                    $(this).prop('disabled', true);

                    await cancelCycleChangeRequest(ids);
                    await showCycleChangeRequestSchedule(new Array(divisionId), startDate, endDate);

                    page.alert("Refreshing other division schedules...", restPaths.getInfoStatus());
                    await showCycleChangeRequestSchedule(page.getAffectedDivisions(), startDate, endDate);

                    page.alert("Successfully canceled cycle change schedules.", restPaths.getSuccessStatus());
                } catch (error) {
                    page.alert(error, restPaths.getErrorStatus());
                } finally {
                    $(this).prop('disabled', false);
                }
            })();
        }
    });

    $('#requestApprovalBtn').click(function () {
        const ids = page.getSelectedCycleChangeId();
        const startDate = page.getStartDate();
        const endDate = page.getEndDate();
        const divisionId = page.getActiveDivision();

        if (ids === undefined || ids.length <= 0) {
            page.alert("Select atleast one (1) schedule to request for approval.", restPaths.getInfoStatus());
        } else {
            (async () => {
                try {
                    $(this).prop('disabled', true);

                    page.alert("Validating all entries.", restPaths.getInfoStatus());
                    await validateEntries(ids);

                    page.alert("Requesting for approval...", restPaths.getInfoStatus());
                    await requestForApproval(ids);
                    await sendForApprovalViaEmail(startDate, endDate);
                    await showCycleChangeRequestSchedule(new Array(divisionId), startDate, endDate)

                    page.alert("Refreshing other division schedules...", restPaths.getInfoStatus());
                    await showCycleChangeRequestSchedule(page.getAffectedDivisions(), startDate, endDate);

                    page.alert('Request for approval is successful. Approver has been notified.', restPaths.getSuccessStatus());
                } catch (error) {
                    page.alert(error, restPaths.getErrorStatus());
                } finally {
                    $(this).prop('disabled', false);
                }
            })();
        }
    });

    $("#rejectRunBtn").click(function () {
        const ids = page.getSelectedCycleChangeId();
        const startDate = page.getStartDate();
        const endDate = page.getEndDate();
        const divisionId = page.getActiveDivision();

        if (ids === undefined || ids.length <= 0) {
            page.alert("Select atleast one (1) schedule to reject.", restPaths.getInfoStatus());
        } else {
            (async () => {
                try {
                    $(this).prop('disabled', true);

                    page.alert("Rejecting request/s.", restPaths.getInfoStatus());

                    await rejectRequest(ids);
                    await showCycleChangeRequestSchedule(new Array(divisionId), startDate, endDate)

                    page.alert("Refreshing other division schedules...", restPaths.getInfoStatus());
                    await showCycleChangeRequestSchedule(page.getAffectedDivisions(), startDate, endDate);

                    page.alert('Request has been rejected. Submitter has been notified.', restPaths.getSuccessStatus());
                } catch (error) {
                    page.alert(error, restPaths.getErrorStatus());
                } finally {
                    $(this).prop('disabled', false);
                }
            })();
        }
    });

    $("#approveRunBtn").click(function () {
        const ids = page.getSelectedCycleChangeId();
        const startDate = page.getStartDate();
        const endDate = page.getEndDate();
        const divisionId = page.getActiveDivision();

        if (ids === undefined || ids.length <= 0) {
            page.alert("Select atleast one (1) schedule to approve.", restPaths.getInfoStatus());
        } else {
            (async () => {
                try {
                    $(this).prop('disabled', true);

                    page.alert("Approving request/s.", restPaths.getInfoStatus());

                    await approveRequest(ids);
                    await showCycleChangeRequestSchedule(new Array(divisionId), startDate, endDate);

                    page.alert("Refreshing other division schedules...", restPaths.getInfoStatus());
                    await showCycleChangeRequestSchedule(page.getAffectedDivisions(), startDate, endDate);

                    page.alert('Request has been approved. Submitter has been notified.', restPaths.getSuccessStatus());
                } catch (error) {
                    page.alert(error, restPaths.getErrorStatus());
                } finally {
                    $(this).prop('disabled', false);
                }
            })();
        }
    });

    $('#addNewSchedule').click(function () {
        const divisionId = page.getActiveDivision();
        const startDate = page.getStartDate();
        const endDate = page.getEndDate();

        const $runDate = $($('input#newRunDate')[0]);
        const $effDate = $($('input#newEffDate')[0]);
        const $offsite = $($('input#newOffsite')[0]);

        const runDateValue = $runDate.val();
        const effDateValue = $effDate.val();
        const offsite = $offsite.prop('checked') === true ? '1' : '0';

        if (runDateValue.length === 0 || effDateValue.length === 0) {
            page.alert('Select date first.', restPaths.getInfoStatus());
        } else {
            (async () => {
                try {
                    $(this).prop('disabled', true);

                    const newCycleChange = new NewCycleChange(divisionId, runDateValue, effDateValue, offsite);
                    if (startDate.length === 0 || endDate.length === 0) {
                        await createNewSchedule(newCycleChange);
                    } else {
                        await createNewSchedule(newCycleChange);
                        await showCycleChangeRequestSchedule(new Array(divisionId), startDate, endDate);
                    }

                    $(this).prev().click();

                    $runDate.val('');
                    $effDate.val('');
                    $offsite.prop('checked', false);

                    page.alert("New Cycle Change has been created.", restPaths.getSuccessStatus());
                } catch (error) {
                    page.alert(error, restPaths.getErrorStatus());
                } finally {
                    $(this).prop('disabled', false);
                }
            })();
        }
    });

    $('#cancelRunBtn').click(function () {
        const ids = page.getSelectedCycleChangeId();
        const startDate = page.getStartDate();
        const endDate = page.getEndDate();
        const divisionId = page.getActiveDivision();

        if (ids === undefined || ids.length <= 0) {
            page.alert("Select atleast one (1) schedule to cancel.", restPaths.getInfoStatus());
        } else {
            page.alert("Canceling Run for the selected Base Schedules.", restPaths.getInfoStatus());

            (async () => {
                try {
                    $(this).prop('disabled', true);

                    await cancelBaseScheduleRun(ids);
                    await showCycleChangeRequestSchedule(new Array(divisionId), startDate, endDate);

                    page.alert("Refreshing other division schedules...", restPaths.getInfoStatus());
                    await showCycleChangeRequestSchedule(page.getAffectedDivisions(), startDate, endDate);

                    page.alert("Successfully canceled base schedules.", restPaths.getSuccessStatus());
                } catch (error) {
                    page.alert(error, restPaths.getErrorStatus());
                } finally {
                    $(this).prop('disabled', false);
                }
            })();
        }
    });

    $('#validateBtn').click(function () {
        const ids = page.getSelectedCycleChangeId();
        
        if (ids === undefined || ids.length <= 0) {
            page.alert("Select atleast one (1) schedule to validate.", restPaths.getInfoStatus());
        } else {
            page.alert("Validating Cycle Change Schedule Requests.", restPaths.getInfoStatus());

            (async () => {
                try {
                    $(this).prop('disabled', true);

                    page.alert("Validating all entries.", restPaths.getInfoStatus());
                    await validateEntries(ids);

                    page.alert("Validation complete. No errors found.", restPaths.getSuccessStatus());
                } catch (error) {
                    page.alert(error, restPaths.getErrorStatus());
                } finally {
                    $(this).prop('disabled', false);
                }
            })();
        }
    });

    $("#emailBtn").click(function () {
        const startDate = page.getStartDate();
        const endDate = page.getEndDate();

        const sendToSelf = $('#chkSendSelf').prop('checked');

        if (startDate.length === 0 || endDate.length === 0) {
            page.alert('Select date first.', restPaths.getInfoStatus());
        } else {
            const criteria = {
                startDate: startDate,
                endDate: endDate,
                sendToSelf: sendToSelf
            }

            page.alert('Generating Report...', restPaths.getInfoStatus());

            (async () => {
                try {
                    $(this).prop('disabled', true);

                    await sendReportToEmail(criteria);

                    if (sendToSelf) {
                        page.alert('Report sent to your email.', restPaths.getSuccessStatus());
                    } else {
                        page.alert('Report sent to admins and to the affected divisions.', restPaths.getSuccessStatus());
                    }
                } catch (error) {
                    console.log(error);
                    page.alert('An error occurred while generating the report.', restPaths.getErrorStatus());
                } finally {
                    $(this).prop('disabled', false);
                }
            })();
        }
    });

    $('#closeModal').click(function () {
        const $runDate = $($('input#newRunDate')[0]);
        const $effDate = $($('input#newEffDate')[0]);
        const $offsite = $($('input#newOffsite')[0]);

        $runDate.val('');
        $effDate.val('');
        $offsite.prop('checked', false);

    });

    async function sendReportToEmail(criteria) {
        const reportUrl = restPaths.emailReportUrl();
        const newResponse = await HttpClient.post('POST', reportUrl, criteria);

        return await evaluateEmailResponse(newResponse);
    }

    async function createNewSchedule(newCycleChange) {
        const createNewCycleChangeURL = restPaths.newCycleChangeUrl();
        const newResponse = await HttpClient.post('POST', createNewCycleChangeURL, newCycleChange);

        return await evaluateAndReturnResponse(newResponse);
    }

    async function rejectRequest(ids) {
        const rejectUrl = restPaths.rejectUrl();
        const rejectResponse = await HttpClient.post('PUT', rejectUrl, ids);

        return await evaluateAndReturnResponse(rejectResponse);
    }

    async function approveRequest(ids) {
        const approvalUrl = restPaths.approvalUrl();
        const approvalResponse = await HttpClient.post('PUT', approvalUrl, ids);

        return await evaluateAndReturnResponse(approvalResponse);
    }

    async function requestForApproval(ids) {
        const forApprovalURL = restPaths.requestApprovalURL();
        const forApprovalResponse = await HttpClient.post('PUT', forApprovalURL, ids);

        return await evaluateAndReturnResponse(forApprovalResponse);
    }

    async function cancelCycleChangeRequest(ids) {
        const cancelUrl = restPaths.cancelCycleChangeScheduleURL();
        const cancelResponses = await HttpClient.post('PUT', cancelUrl, ids);

        return await evaluateAndReturnResponse(cancelResponses);
    }

    async function cancelBaseScheduleRun(ids) {
        const cancelUrl = restPaths.cancelRunURL();
        const cancelResponses = await HttpClient.post('PUT', cancelUrl, ids);

        return await evaluateAndReturnResponse(cancelResponses);
    }

    async function showBaseSchedule(divisions, startDate, endDate) {

        const baseUrls = restPaths.fetchBaseSchedule(divisions, startDate, endDate);
        const baseResponses = await HttpClient.getBatch(baseUrls);

        const promises = await evaluateAndReturnResponses(baseResponses);
        for await (let data of promises) {
            if (data._embedded === undefined) {
                page.alert("No records found.", restPaths.getInfoStatus());
            } else {
                page.showBaseSchedules(data._embedded.cycleChangeRequestList);
            }
        }
    }

    async function showCycleChangeRequestSchedule(divisions, startDate, endDate) {
        if (typeof divisions !== 'undefined' && divisions.length > 0) {
            const cycleChangeUrl = restPaths.fetchCycleChangeSchedule();

            const searchCycleChanges = divisions.map(division => {
                return new SearchCycleChange(division, startDate, endDate);
            });

            const cycleChanges = await HttpClient.postBatch('POST', cycleChangeUrl, searchCycleChanges);
            const promises = await evaluateAndReturnResponses(cycleChanges);
            for await (let data of promises) {
                if (data._embedded === undefined) {
                    page.alert('No records found.', restPaths.getInfoStatus());
                } else {
                    page.generateCycleChangeRequestSchedule(data._embedded.cycleChangeRequestList, 'Home');
                }
            }
        }
    }

    async function modifyCycleSchedule(cycleChange) {
        const modifyUrl = restPaths.modifyCycleChangeSchedule();

        const cycleChanges = await HttpClient.post('PUT', modifyUrl, cycleChange);
        return await evaluateAndReturnResponse(cycleChanges);
    }

    async function sendForApprovalViaEmail(startDate, endDate) {
        const sendApprovalToEmailUrl = restPaths.sendForApprovalViaEmailURL();
        const searchCriteria = new SearchCycleChangeByDateRange(startDate, endDate);

        const result = await HttpClient.post('POST', sendApprovalToEmailUrl, searchCriteria);
        return await evaluateEmailResponse(result);
    }

    async function validateEntries(ids) {
        const validateUrl = restPaths.validateEntries();

        const cycleChanges = await HttpClient.post('POST', validateUrl, ids);
        return await evaluateAndReturnResponse(cycleChanges);
    }

});