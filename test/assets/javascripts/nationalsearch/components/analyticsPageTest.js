import AnalyticsPage  from './analyticsPage'
import {expect} from 'chai';
import {shallow} from 'enzyme';
import {stub} from 'sinon';
import {THIS_YEAR, THIS_WEEK} from '../actions/analytics'

describe('AnalyticsPage component', () => {
    context('on mount', () => {
        it('fetch counts is dispatched', () => {
            const fetchVisitCounts = stub()
            shallow(<AnalyticsPage fetchVisitCounts={fetchVisitCounts} currentTimeRange={THIS_YEAR}/>)

            expect(fetchVisitCounts).to.be.calledOnce
        })
    })

    context('on props received', () => {
        it('fetch counts is dispatched for mount and props change', () => {
            const fetchVisitCounts = stub()
            const page = shallow(<AnalyticsPage fetchVisitCounts={fetchVisitCounts}  currentTimeRange={THIS_YEAR}/>)

            fetchVisitCounts.reset()

            page.setProps({currentTimeRange: THIS_WEEK})

            expect(fetchVisitCounts).to.be.calledOnce
        })
    })

    context('refresh button clicked', () => {
        it('fetch counts is dispatched for mount and click', () => {
            const fetchVisitCounts = stub()
            const page = shallow(<AnalyticsPage fetchVisitCounts={fetchVisitCounts} currentTimeRange={THIS_YEAR}/>)

            fetchVisitCounts.reset()

            page.find({type: 'button'}).simulate('click')

            expect(fetchVisitCounts).to.be.calledOnce
        })
    })


    describe('rendering', () => {
        let page
        beforeEach(() => {
            page = shallow(<AnalyticsPage fetchVisitCounts={stub()} currentTimeRange={THIS_YEAR}/>)
        })

        it('displays unique visitor count', () => {
            expect(page.find({description: 'Unique visits'}).exists()).to.be.true
        })
        it('displays all visitor count', () => {
            expect(page.find({description: 'All visits'}).exists()).to.be.true
        })
        it('displays all searches count', () => {
            expect(page.find({description: 'All searches'}).exists()).to.be.true
        })
        it('displays offender details page rank', () => {
            expect(page.find({description: 'Offender details clicks - ranking across pages'}).exists()).to.be.true
        })
        it('displays offender details top 2 pages ranking', () => {
            expect(page.find({description: 'Offender details clicks - ranking within top 2 pages'}).exists()).to.be.true
        })
        it('displays search outcomes', () => {
            expect(page.find({description: 'Search visit outcome'}).exists()).to.be.true
        })
        it('displays duration to search', () => {
            expect(page.find({description: 'Duration to find offender'}).exists()).to.be.true
        })
        it('displays filter counts', () => {
            expect(page.find({description: 'Final search in session filter'}).exists()).to.be.true
        })
        it('displays search field match', () => {
            expect(page.find({description: 'Top field matches'}).exists()).to.be.true
        })
    })
})

