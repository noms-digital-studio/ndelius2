import PropTypes from "prop-types";

const OffenderSearch = ({searchTerm, search}) => (
    <form className="padding-left-right" onSubmit={(event) => onSubmit(event, searchTerm, search)}>
        <input autoFocus={true} name='searchTerms' className="form-control padded" value={searchTerm} onChange={event => search(event.target.value)} placeholder="Find names, addresses, date of birth, CRN and more..." />
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