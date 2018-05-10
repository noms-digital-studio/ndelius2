import SatisfactionPage from './satisfactionPage'
import { ratingData, generateXAxisLabels } from './satisfactionPage'
import {expect} from 'chai';
import {shallow} from 'enzyme';
import {stub} from 'sinon';

describe('SatisfactionPage component', () => {
    context('on mount', () => {
        it('fetch satisfaction counts is dispatched', () => {
            const fetchSatisfactionCounts = stub();
            const changeYear = stub();
            shallow(<SatisfactionPage fetchSatisfactionCounts={fetchSatisfactionCounts} changeYear={changeYear}/>);

            expect(fetchSatisfactionCounts).to.be.calledOnce
        })
    })

    context('refresh button clicked', () => {
        it('fetch satisfaction counts is dispatched for mount and click', () => {
            const fetchSatisfactionCounts = stub();
            const changeYear = stub();
            const page = shallow(<SatisfactionPage fetchSatisfactionCounts={fetchSatisfactionCounts} changeYear={changeYear}/>)

            fetchSatisfactionCounts.reset()

            page.find({type: 'button'}).simulate('click')

            expect(fetchSatisfactionCounts).to.be.calledOnce
        })
    })


    describe('rendering', () => {
        let page
        beforeEach(() => {
            page = shallow(<SatisfactionPage fetchSatisfactionCounts={stub()} changeYear={stub()}/>)
        });

        it('displays weekly satisfaction counts', () => {
            expect(page.find('#description').text()).to.equal('Weekly Satisfaction Counts');
        });
    })

})

describe('test `private` functions', () => {
    it('returns an empty array if there is no data', () => {
        expect(ratingData(null, 1, '2018')).to.eql([])
    })

    it('returns array of counts for each week with zero filled gaps', () => {
        const rawData = [
                    {
                        "yearAndWeek": "2018-6",
                        "count": 66
                    },
                    {
                        "yearAndWeek": "2018-4",
                        "count": 44
                    },
                    {
                        "yearAndWeek": "2018-3",
                        "count": 33
                    },
                    {
                        "yearAndWeek": "2018-2",
                        "count": 22
                    },
                    {
                        "yearAndWeek": "2018-1",
                        "count": 11
                    }
                ];

        expect(ratingData(rawData, 7, '2018')).to.eql([0, 11, 22, 33, 44, 0, 66])
    })

    it('generates X axis labels', () => {
        expect(generateXAxisLabels('2018', 3)).to.eql(['2018-1', '2018-2', '2018-3']);
    })

})



