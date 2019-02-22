import { labelMapper } from './searchFieldMatchChartContainer'
import { expect } from 'chai'

describe('searchFieldMatchChartContainer', () => {
  describe('labelMapper', () => {
    it('returns labels without prefixes', () => {
      expect(labelMapper({
        'otherIds.crn': 5,
        'firstName': 7,
        'surname': 12,
        'contactDetails.addresses.town': 1,
        'offenderAliases.surname': 3
      })).to.eql(['crn', 'firstName', 'surname', 'town', 'surname (alias)'])
    })
  })
})
