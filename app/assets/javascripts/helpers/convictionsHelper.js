import moment from 'moment'

const convictionDescription = conviction => {
  return conviction.sentence && `${conviction.sentence.description} (${conviction.sentence.originalLength} ${conviction.sentence.originalLengthUnits})`
    || conviction.latestCourtAppearanceOutcome.description
}

const convictionSorter = (first, second) => {
  return moment(second.referralDate, 'YYYY-MM-DD').diff(moment(first.referralDate, 'YYYY-MM-DD'))
}

const mainOffenceDescription = conviction => {
  return conviction.offences.filter(offence => offence.mainOffence).map(offence => `${offence.detail.description}`)
}

export { convictionDescription, convictionSorter, mainOffenceDescription }