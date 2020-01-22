const page = (function () {

    const $alertDivContainer = $(document.querySelector("#alertContainer"));
    const $mainContent = $(document.querySelector("#mainContent"));

    const collapseBaseId_prefix = '#collapseBaseSchedule-div';
    const tblCCSchedId_prefix = '#tblCCSched-div';
    // const $navTabContent = $(document.querySelector("#navTabContent"));

    let usersDivisions = [];

    /**
     * Creates Bootstrap alert class
     * 
     * <div class="alert alert-success alert-dismissible fade show text-center">
     *      <span id="messageContentSuccess"></span>Success Message</span>
     *      <button type="button" class="close" data-dismiss="alert">&times;</button>
     * </div>
     * @param {*} status 
     * @param {*} message 
     */
    function createAlertClass(message, status) {
        const xIcon = '&times;';
        const $button = $('<button>', { 'type': 'button', 'class': 'close', 'data-dismiss': 'alert' });
        $button.append(xIcon);

        let classValue = 'alert alert-dismissible fade show text-center ';
        let $strong = $('<strong>');
        if (restPaths.getErrorStatus() === status) {
            classValue += 'alert-danger';
            $strong.append('Warning! ');
        } else if (restPaths.getSuccessStatus() === status) {
            classValue += 'alert-success';
            $strong.append('Success! ');
        } else if (restPaths.getInfoStatus() === status) {
            classValue += 'alert-info'
            $strong.append('Info! ');
        }

        const $spanMessage = $('<span>').append(message);

        const $divAlert = $('<div>', { 'class': classValue })
            .append($strong)
            .append($spanMessage)
            .append($button);

        return {
            div: $divAlert,
            status: status
        };
    }

    /**
     * Appends the Alert div to a alert message div container.
     * 
     * @param {*} $divAlert JQuery Div Element: Alert Div
     */
    function appendAlertClass(alert) {
        if (alert.status === restPaths.getSuccessStatus()) {
            $alertDivContainer.append(alert.div);
            setTimeout(function () { $alertDivContainer.hide("slow"); }, 3000);
        } else {
            $alertDivContainer.append(alert.div);
            $alertDivContainer.show();
        }
    }

    /**
     * Shows Alert with message in page.
     * 
     * @param {*} message message in Alert
     * @param {*} status HTTP status
     */
    function alert(message, status) {

        $alertDivContainer.empty();

        const compose = (f, g) => (message, status) => f(g(message, status))
        const showAlert = compose(appendAlertClass, createAlertClass);

        showAlert(message, status);
    }

    /**
     * Create the Navigation tabs for the divisions.
     * 
     * <ul class="nav nav-tabs text-uppercase">
     *      <li class="nav-item">
     *          <a href="#tab-div10" class="nav-link active" data-toggle="tab">Div 10</a>
     *      </li>
     *      <li class="nav-item">
     *          <a href="#tab-div19" class="nav-link" data-toggle="tab">Div 19</a>
     *      </li>
     *      <li class="nav-item">
     *          <a href="#tab-div25" class="nav-link" data-toggle="tab">Div 25</a>
     *      </li>
     *</ul>
     * 
     * //  @param {*} divisions User's division/s
     * @returns JQuery element: Clone of Main Content Div with UL navigation tab
     */
    function createNavigationTabs() {
        $mainContent.removeAttr('hidden'); // remove hidden attribute before cloning
        $mainContent.empty(); // clears main content before cloning

        let $clone = $mainContent.clone(true);
        const userDivisions = initialize.getDivisions();

        const $ulNav = $('<ul>', { 'class': 'nav nav-tabs text-uppercase' });
        let isFirstA = true;
        userDivisions.map(division => {
            const $liNav = $('<li>', { 'class': 'nav-item' });
            const $inputHidden = $('<input>', { 'type': 'hidden', 'value': `${division}` });

            let className = 'nav-link';
            if (isFirstA) {
                className += ' active';
                isFirstA = false;
            }
            const $aNavLink = $('<a>', { 'href': `#tab-div${division}`, 'class': className, 'data-toggle': 'tab' })
                .append(`Div ${division}`);
            $liNav.append($inputHidden).append($aNavLink);

            $ulNav.append($liNav);
        });

        $clone.append($ulNav);

        return $clone;
    }

    /**
     * Creates div element that will serve as the tab-pane.
     * 
     * <div id="navTabContent" class="tab-content card card-body">
     *      <div class="tab-pane fade show active" id="tab-div10">
     * 
     * @param {*} $clonedMainContent Cloned Element of #mainContent Div
     */
    function createNavigationTabPane($clonedMainContent) {
        let $mainContentClone = $clonedMainContent.clone(true);

        let $navTabContent = $('<div>', { 'id': 'navTabContent', 'class': 'tab-content card card-body' });

        const userDivisions = initialize.getDivisions();
        let isFirstDiv = true;
        userDivisions.map(division => {
            let className = 'tab-pane fade';
            if (isFirstDiv) {
                className += ' show active';
                isFirstDiv = false;
            }

            const $tabPane = $('<div>', { 'class': className, 'id': `tab-div${division}` });
            $navTabContent.append($tabPane);
        });

        $mainContentClone.append($navTabContent);
        return $mainContentClone;
    }

    /**
     * Creates Base Schedule Container
     * 
     * <div id="baseScheduleSection-div10" class="container">
     *      <a class="btn btn-dark btn-block text-left baseScheduleButton" data-toggle="collapse"
     *          href="#collapseBaseSchedule-div10" role="button" aria-expanded="false"
     *          aria-controls="collapseBaseSchedule-div10">
     *      
     *          <i id="faddBaseSchedule-div10" class="fa fa-angle-double-down"></i> Base Schedule
     *      </a>
     *      <div class="collapse" id="collapseBaseSchedule-div10">
     *          <div class="card card-body">
     *              <table class="table table-striped table-hover table-sm">
     *                  <thead class="thead-dark">
     *                       <tr>
     *                           <th>Offsite Batch Day</th>
     *                           <th>Run Day</th>
     *                           <th>Run Date</th>
     *                           <th>Effective Day</th>
     *                           <th>Effective Date</th>
     *                       </tr>
     *                   </thead>
     *                   <tbody></tbody>
     *              </table>
     *          </div>
     *      </div>
     * </div>
     * 
     * @param {*} $clonedMainContent Cloned div element of #mainComponent
     */
    function createBaseScheduleContainer($clonedMainContent) {

        let $mainContentClone = $clonedMainContent.clone(true);
        let arrTabPane = $mainContentClone.children('#navTabContent').find('.tab-pane');

        arrTabPane.map(index => {
            const $tabPane = $(arrTabPane[index]);
            const division = getDivisionFromTabPane($tabPane);

            const $baseScheduleContainer = $('<div>', { 'id': `baseScheduleSection-div${division}`, 'class': 'container' });
            const $aBtnToggler = $('<a>', {
                'class': 'btn btn-dark btn-block text-left baseScheduleButton',
                'data-toggle': 'collapse',
                'href': `${collapseBaseId_prefix}${division}`,
                'role': 'button',
                'aria-expanded': 'false',
                'aria-controls': `collapseBaseSchedule-div${division}`
            });
            const $iArrow = $('<i>', { 'id': `faddBaseSchedule-div${division}`, 'class': 'fa fa-angle-double-down' });
            $aBtnToggler.append($iArrow).append(' Base Schedule');

            const $collapseBaseSchedule = $('<div>', { 'class': 'collapse', 'id': `collapseBaseSchedule-div${division}` });
            const $cardBody = $('<div>', { 'class': 'card card-body' });
            const $table = $('<table>', { 'class': 'table table-striped table-hover table-sm baseScheduleTable' });

            const $thead = $('<thead>', { 'class': 'thead-dark' })
                .append($('<tr>')
                    .append($('<th>').append('Offsite Batch Day'))
                    .append($('<th>').append('Run Day'))
                    .append($('<th>').append('Run Date'))
                    .append($('<th>').append('Effective Day'))
                    .append($('<th>').append('Effective Date'))
                );

            $table.append($thead).append($('<tbody>'));
            $cardBody.append($table);
            $collapseBaseSchedule.append($cardBody);

            $baseScheduleContainer.append($aBtnToggler)
                .append($collapseBaseSchedule);

            $tabPane.append($baseScheduleContainer);
        });

        return $mainContentClone;
    }

    /**
     * Creates CycleChange Container
     * <!-- this is the parent to where we will attach the element created by this function -->
     *  <div class="tab-pane fade show active" id="tab-div10"> 
     *  
     *       <!-- This is the elements that this functions creates -->
     *       <br>
     *       <div class="container-fluid">
     *           <h2 class="btn btn-primary btn-block active text-left" aria-expanded="false"
     *                   aria-controls="collapseCycleChangeSchedule-div19">
     *               Cycle Change Schedule
     *           </h2>
     *           <div class="collapse show" id="collapseCycleChangeSchedule-div10">
     *               <div class="card card-body">
     *                   <table id="tblCCSched-div10" class="table table-striped table-hover table-sm cycleChangeTable"></table>
     *               </div>
     *           </div>
     *       </div>
     *  </div>
     * 
     * @param {*} $clonedMainContent Cloned Div Element of div(#mainContent) element
     */
    function createCycleChangeContainer($clonedMainContent) {

        let $mainContentClone = $clonedMainContent.clone(true);
        let arrTabPane = $mainContentClone.children('#navTabContent').find('.tab-pane');

        arrTabPane.map(index => {
            const $tabPane = $(arrTabPane[index]);
            const division = getDivisionFromTabPane($tabPane);

            const $br = $('<br>');
            const $divContainerFluid = $('<div>', { 'class': 'container-fluid' });

            const $h2 = $('<h2>', {
                'class': 'btn btn-primary btn-block active text-left',
                'aria-expanded': 'false',
                'aria-controls': `collapseCycleChangeSchedule-div${division}`
            }).append('Cycle Change Schedule');

            const $divCollapsible = $('<div>', {
                'id': `collapseCycleChangeSchedule-div${division}`,
                'class': 'collapse show'
            });

            const $cardBody = $('<div>', { 'class': 'card card-body' });
            const $table = $('<table>', {
                'id': `tblCCSched-div${division}`,
                'class': 'table table-striped table-hover table-sm cycleChangeTable w-auto small'
            });

            const $inputCheckbox = $('<input>', {
                'id': `selectAllChkBox-div${division}`,
                'class': 'parentCheckBox',
                'type': 'checkbox'
            });

            const $thead = $('<thead>', { 'class': 'thead-dark' })
                .append($('<tr>')
                    .append($('<th>', { 'class': 'offsiteColumn' }).append('Offsite Batch Day'))
                    .append($('<th>').append('Run Day'))
                    .append($('<th>', { 'class': 'dateColumn' }).append('Run Date'))
                    .append($('<th>').append('Effective Day'))
                    .append($('<th>', { 'class': 'dateColumn' }).append('Effective Date'))
                    .append($('<th>', { 'class': 'requestColumn' }).append('Cycle Chg Request'))
                    .append($('<th>', { 'class': 'commentColumn' }).append('Comment'))
                    .append($('<th>').append('Status'))
                    .append($('<th>', { 'class': 'createdByColumn history' }).append('Created By'))
                    .append($('<th>', { 'class': 'lastUpdatedByColumn history' }).append('Last Updated By'))
                    .append($('<th>', { 'class': 'lastUpdatedDateColumn history' }).append('Last Updated Date'))
                    .append($('<th>', { 'class': 'home' }).append($inputCheckbox))
                    .append($('<th>', { 'class': 'editIconColumn home' }))
                );

            $table.append($thead).append($('<tbody>'));
            $cardBody.append($table);
            $divCollapsible.append($cardBody);
            $divContainerFluid.append($h2).append($divCollapsible);

            $tabPane.append($br).append($divContainerFluid);
        });

        return $mainContentClone;
    }

    function createDivisionTabs($clonedMainContent) {
        // $mainContent.empty(); // clears content first
        $mainContent.append($clonedMainContent.children());
    }

    /**
     *  Composite function that will call all the functions that will create
     *  each Division tab content.
     * 
     * @param {*} divisions Array of Divisions
     */
    function showDivisionTabs(history) {
        const compose = (f, g) => (...args) => f(g(...args));
        const showTabs = (...fns) => fns.reduce(compose);

        if (history !== 'History') {
            showTabs(createDivisionTabs,
                hideHistoryFields,
                createCycleChangeContainer,
                createBaseScheduleContainer,
                createNavigationTabPane,
                createNavigationTabs
            )();
        } else {
            showTabs(createDivisionTabs,
                hideHomePageFields,
                createCycleChangeContainer,
                createNavigationTabPane,
                createNavigationTabs
            )();
        }
    }

    /**
     * Generates Base schedule data
     * 
     * <div class="collapse" id="collapseBaseSchedule-div10">
     *      <div class="card card-body">
     *         <table class="table table-striped table-hover table-sm">
     *             <tbody>
     *                 <tr>
     *                     <td>Offsite</td>
     *                     <td>Sunday</td>
     *                     <td>07/07/2019</td>
     *                     <td>Tuesday</td>
     *                     <td>07/09/2019</td>
     *                 </tr>
     *             </tbody>
     *         </table>
     *   </div>
     * @param {*} baseSchedules Array of Base Schedules
     */
    function showBaseSchedules(baseSchedules) {

        let $tbody;
        let $cloneTBody;

        baseSchedules.map(base => {

            if (typeof $tbody === 'undefined') {
                $tbody = $($(`${collapseBaseId_prefix}${base.divId}`).find('tbody')[0]);
                $tbody.empty();

                $cloneTBody = $tbody.clone();
            }

            let offsite = '';
            if (base.offsiteIndicator === '1') {
                offsite = 'Offsite';
            }

            let $tr = $('<tr>')
                .append($('<td>').append(offsite))
                .append($('<td>').append(base.runDayName))
                .append($('<td>').append(base.runDate))
                .append($('<td>').append(base.effectiveDayName))
                .append($('<td>').append(base.effectiveDate));

            $cloneTBody.append($tr);
        });

        $tbody.append($cloneTBody.children());
    }

    /**
     * <tr>
     *       <td>
     *           <input class="cycleChangeId" type="hidden" value="1">
     *           <span class="hideUponEdit offsite">Offsite</span>
     *           <input class="showUponEdit" type="checkbox">
     *       </td>
     *       <td>
     *           <span>Sunday</span>
     *       </td>
     *       <td>
     *           <span class="hideUponEdit runDate">07/07/2019</span>
     *           <input type="date" class="form-control datetimepicker showUponEdit runDate">
     *       </td>
     *       <td>
     *           <span>Tuesday</span>
     *       </td>
     *       <td>
     *           <span class="hideUponEdit effDate">07/09/2019</span>
     *           <input type="date" class="form-control datetimepicker showUponEdit effDate">
     *       </td>
     *       <td>
     *           <span class="hideUponEdit">Modify + Offsite</span>
     *           <button type="button" class="btn btn-primary showUponEdit"><i
     *                   class="fa fa-check"></i></button>
     *           <span class="showUponEdit"> </span>
     *           <button type="button" class="btn btn-danger showUponEdit"><i
     *                   class="fa fa-times"></i></button>
     *       </td>
     *       <td>
     *           <span class="hideUponEdit commentTxt"></span>
     *           <input type="text" class="showUponEdit commentTxt">
     *       </td>
     *       <td>
     *           <span></span>
     *       </td>
     *       <td><input type="checkbox" class="txnCheckbox"></td>
     *       <td class="editIcon"><i class="fa fa-edit"></i></td>
     *   </tr>
     * @param {*} cycleChangeRequestSchedules 
     */
    function showCycleChangeRequestSchedules(cycleChangeRequestSchedules) {
        let $tbody;
        let $cloneTBody;

        cycleChangeRequestSchedules.map(schedule => {
            if (typeof $tbody === 'undefined') {
                $tbody = $($(`${tblCCSchedId_prefix}${schedule.divId}`).find('tbody')[0]);
                $tbody.empty();

                $cloneTBody = $tbody.clone();
            }

            let offsite = '';
            if (schedule.offsiteIndicator === '1') {
                offsite = 'Offsite';
            }

            let $tr = $('<tr>');

            let $tdOffsite = $('<td>');
            let $inputId = $('<input>', { 'class': 'cycleChangeId', 'type': 'hidden', 'value': `${schedule.id}` });
            let $inputDivId = $('<input>', { 'class': 'divId', 'type': 'hidden', 'value': `${schedule.divId}` })
            let $spanOffsite = $('<span>', { 'class': 'hideUponEdit offsite' }).append(offsite);
            let $inputCheckbox = $('<input>', { 'class': 'offsiteCheckbox showUponEdit', 'type': 'checkbox' });
            $tdOffsite.append($inputDivId).append($inputId).append($spanOffsite).append($inputCheckbox);
            $tr.append($tdOffsite);

            let $tdRunDay = $('<td>');
            $tdRunDay.append($('<span>').append(schedule.runDayName));
            $tr.append($tdRunDay);

            let $tdRunDate = $('<td>');
            let $spanRunDate = $('<span>', { 'class': 'runDate' }).append(schedule.runDate);
            $tdRunDate.append($spanRunDate);
            $tr.append($tdRunDate);

            let $tdEffDay = $('<td>');
            $tdEffDay.append($('<span>').append(schedule.effectiveDayName));
            $tr.append($tdEffDay);

            let $tdEffDate = $('<td>');
            let $spanEffDate = $('<span>', { 'class': 'hideUponEdit effDate' }).append(schedule.effectiveDate);
            let $inputEffDate = $('<input>', { 'type': 'date', 'class': 'form-control datetimepicker showUponEdit effDate' });
            $tdEffDate.append($spanEffDate).append($inputEffDate);
            $tr.append($tdEffDate);

            let $tdRequestType = $('<td>');
            let $spanRequestType = $('<span>', { 'class': 'hideUponEdit' }).append(schedule.cycleChangeRequestType);
            let $buttonSave = $('<button>', { 'type': 'button', 'class': 'btn btn-primary showUponEdit' });
            let $spanSpace = $('<span>', { 'class': 'showUponEdit' }).append(" ");
            let $faCheck = $('<i>', { 'class': 'fa fa-check' });
            let $buttonCancel = $('<button>', { 'type': 'button', 'class': 'btn btn-danger showUponEdit' });
            let $faCancel = $('<li>', { 'class': 'fa fa-times' });
            $buttonSave.append($faCheck);
            $buttonCancel.append($faCancel);
            $tdRequestType.append($spanRequestType).append($buttonSave).append($spanSpace).append($buttonCancel);
            $tr.append($tdRequestType);

            if (schedule.changeStatusName === 'CANCELED' || schedule.changeStatusName === 'REJECTED') {
                $tr.addClass('strikeout');
            }

            let $tdComment = $('<td>');
            let $spanComment = $('<span>', { 'class': 'hideUponEdit commentTxt' }).append(schedule.comment);
            let $inputComment = $('<input>', { 'type': 'text', 'class': 'showUponEdit commentTxt' });
            $tdComment.append($spanComment).append($inputComment);
            $tr.append($tdComment);

            let $tdStatus = $('<td>');
            let $spanStatus = $('<span>', { 'class': 'statusTxt' }).append(schedule.changeStatusName);
            $tdStatus.append($spanStatus);
            $tr.append($tdStatus);

            let $tdCreatedBy = $('<td>', { 'class': 'history' });
            let $spanCreatedBy = $('<span>').append(schedule.createUserId);
            $tdCreatedBy.append($spanCreatedBy);
            $tr.append($tdCreatedBy);

            let $tdLastUpdatedBy = $('<td>', { 'class': 'history' });
            let $spanLastUpdatedBy = $('<span>').append(schedule.lastUpdatedUserId);
            $tdLastUpdatedBy.append($spanLastUpdatedBy);
            $tr.append($tdLastUpdatedBy);

            let $tdLastUpdateDate = $('<td>', { 'class': 'history' })
            let $spanLastUpdateDate = $('<span>').append(schedule.lastUpdateTs);
            $tdLastUpdateDate.append($spanLastUpdateDate);
            $tr.append($tdLastUpdateDate);

            let $tdCheckBox = $('<td>', { 'class': 'home' });
            let $txnCheckBox = $('<input>', { 'type': 'checkbox', 'class': 'txnCheckbox' });
            $tdCheckBox.append($txnCheckBox);
            $tr.append($tdCheckBox);

            let $tdEditIcon = $('<td>', { 'class': 'home' })
            let $faEditIcon = $('<i>', { 'class': 'fa fa-edit showUponEdit' });
            $tdEditIcon.append($faEditIcon);
            $tr.append($tdEditIcon);

            $cloneTBody.append($tr);
        });

        $cloneTBody.children().find('.showUponEdit').hide();
        return $tbody.append($cloneTBody.children());
    }

    function getDivisionFromTabPane($tabPane) {
        const tabDivision = $tabPane.attr('id');
        const length = 'tab-div'.length;
        const division = tabDivision.substring(length, tabDivision.length); // 'tab-div10', gets the position of 1

        return division;
    }

    function getActiveDivision() {
        const $activeTabPane = $($('div.tab-pane.active')[0]);
        return getDivisionFromTabPane($activeTabPane);
    }

    function getAffectedDivisions() {
        const $tabPanes = $('div.tab-pane');
        let affectedDivisions = [];
        const activeDiv = getActiveDivision();

        $tabPanes.map(index => {
            const $tab = $($tabPanes[index]);
            const div = getDivisionFromTabPane($tab);

            if (activeDiv !== div) {
                const checks = $tab.children().find('input.txnCheckbox:checked');

                if (typeof checks !== 'undefined' && checks.length > 0) {
                    affectedDivisions.push(getDivisionFromTabPane($tab));
                }
            }
        });

        return affectedDivisions;
    }

    function getAllDivisionsWithCheckedRequests() {
        const $tabPanes = $('div.tab-pane');
        let affectedDivisions = [];

        $tabPanes.map(index => {
            const $tab = $($tabPanes[index]);
            const div = getDivisionFromTabPane($tab);

            const checks = $tab.children().find('input.txnCheckbox:checked');

            if (typeof checks !== 'undefined' && checks.length > 0) {
                affectedDivisions.push(getDivisionFromTabPane($tab));
            }

        });

        return affectedDivisions;
    }

    function getSelectedCycleChangeId() {
        const $inputIds = $('input.txnCheckbox:checked').closest('tr').find('input.cycleChangeId');
        let ids = [];

        $inputIds.map(index => {
            const id = $inputIds[index].value
            ids.push(id);
        });

        return ids;
    }

    function getStartDate() {
        return new String(document.querySelector('#inputStartDate').value);
    }

    function getEndDate() {
        return new String(document.querySelector('#inputEndDate').value);
    }

    function generateCycleChangeRequestSchedule(cycleChangeRequestSchedules, pageName) {
        const compose = (f, g) => (...args) => f(g(...args));
        const page = (...fns) => fns.reduce(compose);

        if (pageName !== 'History') {
            page(hideHistoryFields,
                showCycleChangeRequestSchedules
            )(cycleChangeRequestSchedules);
        } else {
            page(hideHomePageFields,
                showCycleChangeRequestSchedules
            )(cycleChangeRequestSchedules);
        }

    }


    function hideHistoryFields($element) {
        $element.children().find('.history').hide();

        return $element;
    }

    function hideHomePageFields($element) {
        $element.children().find('.home').hide();

        return $element;
    }

    return {
        alert: alert,
        showDivisionTabs: showDivisionTabs,
        showBaseSchedules: showBaseSchedules,
        showCycleChangeRequestSchedules: showCycleChangeRequestSchedules,
        generateCycleChangeRequestSchedule: generateCycleChangeRequestSchedule,
        getActiveDivision: getActiveDivision,
        getAffectedDivisions: getAffectedDivisions,
        getAllDivisionsWithCheckedRequests: getAllDivisionsWithCheckedRequests,
        getSelectedCycleChangeId: getSelectedCycleChangeId,
        getStartDate: getStartDate,
        getEndDate: getEndDate,
    }
})();
