import { byPageRanking } from './pageRankingChartContainer'
import { expect } from 'chai'

describe('pageRankingChartContainer', () => {
  describe('byPageRanking', () => {
    it('empty rankings remains unchanged', () => {
      expect(byPageRanking({})).to.eql({})
    })
    it('rankings grouped by every 10 results', () => {
      expect(byPageRanking({ '1': 10, '9': 5, '11': 2, '19': 4, '21': 1 })).to.eql({ '1': 15, '2': 6, '3': 1 })
    })
  })
})
