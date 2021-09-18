import React from "react";
import {withRouter} from 'react-router';
import {BasicView} from './components/BasicView';


const App = () => {
	return (
		<>
			<BasicView/>
		</>
	);
};

export default withRouter(App);
