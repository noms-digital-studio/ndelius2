import PropTypes from "prop-types";

const OffenderSearch = ({searchTerm, search}) => (
    <form className="padding-left-right" onSubmit={(event) => onSubmit(event, searchTerm, search)}>
        <p className='visually-hidden' id='search-description'>Results will be updated as you type</p>
        <input tabIndex="1" href='#' role='searchbox' aria-label='search' aria-describedby="search-description" autoFocus={true} name='searchTerms' className="form-control padded" value={searchTerm} onChange={event => search(event.target.value)} placeholder="Enter names, addresses, date of birth, identification numbers and more..." />
    </form>
);

OffenderSearch.propTypes = {
    searchTerm: PropTypes.string.isRequired,
    search: PropTypes.func.isRequired
};

const onSubmit = (event, searchTerm, search) => {
    search(searchTerm)
    event.preventDefault()
}

export default OffenderSearch;