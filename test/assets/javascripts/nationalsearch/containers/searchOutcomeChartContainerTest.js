import {labelMapper, onlySearchEvents} from './searchOutcomeChartContainer'
import {expect} from 'chai';


describe('searchOutcomeChartContainer', () => {
    describe('onlySearchEvents', () => {
        it('empty outcome events remains unchanged', () => {
            expect(onlySearchEvents({})).to.eql({})
        })
        it('events we don\'t understand are removed', () => {
            expect(onlySearchEvents({
                'animal-dogs': 99,
                'search-index': 1,
                'search-request': 2,
                'search-results': 3,
                'search-offender-details': 4,
                'search-add-contact': 5,
                'search-legacy-search': 6,
                'animal-sheep': 99,
            })).to.eql({
                'search-index': 1,
                'search-request': 2,
                'search-results': 3,
                'search-offender-details': 4,
                'search-add-contact': 5,
                'search-legacy-search': 6
            })
        })
    })
    describe('labelMapper', () => {
        it('returns labels for each type', () => {
            expect(labelMapper({
                'search-index': 1,
                'search-request': 2,
                'search-results': 3,
                'search-offender-details': 4,
                'search-add-contact': 5,
                'search-legacy-search': 6
            })).to.eql(["No search","Search abandoned","Search abandoned","Offender selected","Offender add contact","Old search"])
        })
    })

})

