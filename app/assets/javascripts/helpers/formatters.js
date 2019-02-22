import moment from 'moment'

export const dateFromISO = date => moment(date, 'YYYY-MM-DD').format('DD/MM/YYYY')
