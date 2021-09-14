import React from "react";
import {withRouter} from 'react-router';
import {StockPage} from './components/StockPage';


const App = () => {
	return (
		<>
			<StockPage/>
		</>
	);
};

export default withRouter(App);
