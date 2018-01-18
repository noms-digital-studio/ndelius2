import PropTypes from "prop-types";

const OffenderSearch = ({searchTerm, search}) => (
    <div>
        <input className="form-control padded" value={searchTerm} onChange={(event) => search(event.target.value)} placeholder="Find names, addresses, date of birth, CRN and more..." />
    </div>
);

OffenderSearch.propTypes = {
    searchTerm: PropTypes.string.isRequired,
    search: PropTypes.func.isRequired
};


export default OffenderSearch;