import React, {FC} from 'react';
import {AppBar, IconButton, Toolbar, Typography} from '@material-ui/core';
import {inject, observer} from 'mobx-react';
import {useHistory} from 'react-router';
import {RootStore} from '../../stores/RootStore';


export const Page: FC<{store?: RootStore}> = inject("store")(observer(({store, children}) => {
	const userStore = store?.userStore
	const history = useHistory()

	return (
		<>
			<AppBar position="static">
				<Toolbar>
					<IconButton edge="start" onClick={() => history.push('/')}>
						<img height='55px' src='images/kouros.png' alt='store'/>
					</IconButton>
					<Typography style={{flexGrow: 1, fontWeight: 1000, fontSize: 28, textAlign: 'center'}} color='inherit'>
						Fragrance Store
					</Typography>
						<div>
							<IconButton color='inherit' onClick={() => history.push('/cart')}>
								<img height='50px' src='images/basket.png' alt='store'/>
							</IconButton>
							<IconButton onClick={() => {
								if (!userStore?.user) {
									history.push('/history')
									history.push('/login')
								} else {
									history.push('/history')
								}
							}}>
								<img height='50px' src='images/user-icon.png' alt='store'/>
							</IconButton>
						</div>
				</Toolbar>
			</AppBar>
			{children}
		</>
	);
}));
