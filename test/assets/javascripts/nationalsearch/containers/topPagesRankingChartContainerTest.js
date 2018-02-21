import {byTopPagesRanking} from './topPagesRankingChartContainer'
import {expect} from 'chai';


describe('topPagesRankingChartContainer', () => {
    describe('byTopPagesRanking', () => {
        it('empty rankings remains unchanged', () => {
            expect(byTopPagesRanking({})).to.eql({})
        })
        it('rankings limited to first 2 pages', () => {
            expect(byTopPagesRanking({"1": 10, "9": 5, "11": 2, "19": 4, "21": 1, "31": 2})).to.eql({"1": 10, "9": 5, "11": 2, "19": 4})
        })
    })

})

