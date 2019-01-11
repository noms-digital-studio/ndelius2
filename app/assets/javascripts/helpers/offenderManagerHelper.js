const provider = offenderSummary => {
    const activeManager = activeOffenderManager(offenderSummary)
    if (activeManager && activeManager.probationArea) {
        return activeManager.probationArea.description
    }
}

const activeOffenderManager = offenderSummary => {
    if (offenderSummary.offenderManagers) {
        return offenderSummary
            .offenderManagers
            .filter(managers => managers.active === true)
            .shift()
    }
}

const officer = offenderSummary => {
    const activeManager = activeOffenderManager(offenderSummary)
    if (activeManager && activeManager.staff) {
        return activeManager.staff.forenames + ' ' + activeManager.staff.surname
    }
}

const staff = staff => {
    return staff &&  staff.forenames && staff.forenames.indexOf('Unallocated Staff') === -1  && `${staff.surname}, ${staff.forenames}` || 'Unallocated'
}

export {provider, officer, staff}