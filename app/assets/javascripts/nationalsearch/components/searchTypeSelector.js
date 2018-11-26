import React from 'react'
import PropTypes from 'prop-types';

const searchTypeSelector = ({searchType, searchTypeChanged, search, searchTerm, probationAreasFilter}) => (
    <div>
        <div className='bold-small'>
            Match all terms
        </div>
        <div className='form-group' style={{marginTop: '15px', marginBottom: '15px'}}>
            <fieldset className='inline'>
                <div className="multiple-choice">
                    <input type="radio" id="match-all-terms-yes" name="match-all-terms" value="exact"
                           checked={searchType === 'exact'}
                           onChange={
                               event => {
                                   search(searchTerm, event.target.value, probationAreasFilter)
                                   searchTypeChanged(event.target.value)
                               }
                           }
                    />
                    <label htmlFor="match-all-terms-yes">Yes</label>
                </div>
                <div className="multiple-choice">
                    <input type="radio" id="match-all-terms-no" name="match-all-terms" value="broad"
                           checked={searchType === 'broad'}
                           onChange={
                               event => {
                                   search(searchTerm, event.target.value, probationAreasFilter)
                                   searchTypeChanged(event.target.value)
                               }
                           }
                    />
                    <label htmlFor="match-all-terms-no">No</label>
                </div>
            </fieldset>
        </div>

    </div>

);

const typeChanged = (searchType, searchTypeChanged) => {
    searchTypeChanged(searchType)
}

searchTypeSelector.propTypes = {
    searchType: PropTypes.string.isRequired,
    searchTypeChanged: PropTypes.func.isRequired,
    search: PropTypes.func.isRequired
};

export default searchTypeSelector;