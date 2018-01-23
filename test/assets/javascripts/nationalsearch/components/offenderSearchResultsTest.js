import OffenderSearchResults  from './offenderSearchResults'
import {expect} from 'chai';
import {shallow} from 'enzyme';
import {offender} from '../test-helper'

describe('OffenderSearchResults component', () => {
    context('with no results', () => {
        it('no summaries rendered', () => {
            const results = shallow(<OffenderSearchResults results={noResults()}/>)
            expect(results.find('Connect(OffenderSearchSummary)')).to.have.length(0)
        })
    })
    context('with one result', () => {
        it('one summary rendered', () => {
            const results = shallow(<OffenderSearchResults results={oneResult()}/>)
            expect(results.find('Connect(OffenderSearchSummary)')).to.have.length(1)
        })
    })
    context('with many results', () => {
        it('many summaries rendered', () => {
            const results = shallow(<OffenderSearchResults results={twoResults()} />)
            expect(results.find('Connect(OffenderSearchSummary)')).to.have.length(2)
        })
    })
})

const noResults = () => []
const oneResult = () => [offender({offenderId: '123'})]
const twoResults = () => [offender({offenderId: '123'}), offender({offenderId: '234'})]

