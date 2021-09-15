import React, {FC} from 'react';
import CancelIcon from '@material-ui/icons/Cancel';
import {DialogContentText, Input, Container} from '@material-ui/core';
import {Field, Form, Formik, FormikHelpers} from 'formik';
import { UserAddressModalStyled } from './UserAddressStyled';
import { Button, Row } from 'react-bootstrap';
import Box from "@material-ui/core/Box";
import {addUserAddress} from '../../api/userAddress';
import {RootStore} from '../../stores/RootStore';
import {inject, observer} from 'mobx-react';

interface UserAddressModalProps {
	close: any
}
export const UserAddressModal: FC<{ store?: RootStore } & UserAddressModalProps> = inject('store')(observer(({store, close}) => {
	const userStore = store?.userStore
	const addressStore = store?.addressStore

	const onSubmit = async (data: {
		firstname: string,
		lastname: string,
		address: string,
		zipcode: string,
		city: string,
		country: string,
	}, actions: FormikHelpers<any>) => {

		actions.resetForm();
		if (userStore?.user) {
			try {
				const res = await addUserAddress(userStore.user.id, data.firstname, data.lastname, data.address, data.zipcode, data.city, data.country);
				addressStore?.addAddress(res.data)
				close()
			}catch(error){
				console.log(error)
			}
		}
	};

	return (
		<UserAddressModalStyled>
			<Box  className="cancel"><CancelIcon style={{color: "#FFFFFF"}} onClick={() => close()}/></Box>
			<Container className="content" >
				<Row style={{alignItems: "center", display:"flex"}}>
					<Formik initialValues={{
						firstname: '',
						lastname: '',
						address: '',
						zipcode: '',
						city: '',
						country: '',
					}}
					        onSubmit={(values, actions) => onSubmit(values, actions)}>
						{({errors, touched, values, isSubmitting}) => (
							<Form className="registerForm">

								<DialogContentText style={{color: "#FFFFFF"}}>Firstname:</DialogContentText>
								<Field as={Input} style={{backgroundColor: "#FFFFFF", color: "#000000"}} name='firstname'
								       required
								       error={errors.firstname && touched.firstname ? errors.firstname : null}/>

								<DialogContentText style={{color: "#FFFFFF"}}>Lastname:</DialogContentText>
								<Field as={Input} style={{backgroundColor: "#FFFFFF", color: "#000000"}} name='lastname'
								       required
								       error={errors.lastname && touched.lastname ? errors.lastname : null}/>

								<DialogContentText style={{color: "#FFFFFF"}}>Address:</DialogContentText>
								<Field as={Input} style={{backgroundColor: "#FFFFFF", color: "#000000"}} name='address'
								       required
								       error={errors.address && touched.address ? errors.address : null}/>

								<DialogContentText style={{color: "#FFFFFF"}}>Zipcode:</DialogContentText>
								<Field as={Input} style={{backgroundColor: "#FFFFFF", color: "#000000"}} name='zipcode'
								       required
								       error={errors.zipcode && touched.zipcode ? errors.zipcode : null}/>

								<DialogContentText style={{color: "#FFFFFF"}}>City:</DialogContentText>
								<Field as={Input} style={{backgroundColor: "#FFFFFF", color: "#000000"}} name='city'
								       required
								       error={errors.city && touched.city ? errors.city : null}/>

								<DialogContentText style={{color: "#FFFFFF"}}>Country:</DialogContentText>
								<Field as={Input} style={{backgroundColor: "#FFFFFF", color: "#000000"}} name='country'
								       required
								       error={errors.country && touched.country ? errors.country : null}/>

								<div className="row">
									<Button type='submit' disabled={isSubmitting}
											variant="primary" >Save Address</Button>
								</div>
							</Form>
						)}
					</Formik>
				</Row>
			</Container>
		</UserAddressModalStyled>
	);
}));
