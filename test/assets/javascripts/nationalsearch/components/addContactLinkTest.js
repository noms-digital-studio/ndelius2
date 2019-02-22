import AddContactLink from './addContactLink'
import { expect } from 'chai'
import { shallow } from 'enzyme'
import { stub } from 'sinon'

describe('AddContactLink component', () => {
  context('link clicked', () => {
    it('addContact callback function called with offenderId', () => {
      const addContact = stub()
      const link = shallow(<AddContactLink offenderId={123}
                                           rankIndex={3}
                                           highlight={{ firstName: ['Jill'] }}
                                           addContact={addContact} />)

      link.find('a').simulate('click')

      expect(addContact).to.be.calledWith(123, 3, { firstName: ['Jill'] })
    })
  })
})
