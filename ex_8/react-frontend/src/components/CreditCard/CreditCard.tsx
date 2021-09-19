import React, {FC} from 'react';
import CancelIcon from '@material-ui/icons/Cancel';
import {DialogContentText, Input, Container} from '@material-ui/core';
import {Field, Form, Formik, FormikHelpers} from 'formik';
import Box from "@material-ui/core/Box";
import {RootStore} from '../../stores/RootStore';
import {Button, Row} from 'react-bootstrap';
import {inject, observer} from 'mobx-react';
import {addCreditCard} from '../../api/creditCardService';
import {CreditCardModalStyled } from '../../styles/CreditCardStyled';


interface CreditCardModalProps {
	close: any
}

export const CreditCardModal: FC<{ store?: RootStore } & CreditCardModalProps> = inject('store')(observer(({store, close}) => {
	const userStore = store?.userStore
	const cardStore = store?.creditCardsStore

	const onSubmit = async (data: {
		cardholderName: string,
		number: string,
		expDate: string,
		cvcCode: string,
	}, actions: FormikHelpers<any>) => {

		actions.resetForm();
		if (userStore?.user) {
			try {
				const res = await addCreditCard(userStore.user.id, data.cardholderName, data.number, data.expDate, data.cvcCode);
				cardStore?.addCard(res.data)
				close()
			} catch (error) {
				console.log(error)
			}
		}
	};

	return (
		<CreditCardModalStyled>
			<Box  className="cancel"><CancelIcon style={{color: "#FFFFFF"}} onClick={() => close()}/></Box>
			<Container className="content" >
				<Row style={{alignItems: "center", display:"flex"}}>
					<Formik initialValues={{
						cardholderName: '',
						number: '',
						expDate: '',
						cvcCode: ''
					}}
					        onSubmit={(values, actions) => onSubmit(values, actions)}>
						{({errors, touched, values, isSubmitting}) => (
							<Form className="registerForm">

								<DialogContentText style={{color: "#FFFFFF"}}>Cardholder Name:</DialogContentText>
								<Field as={Input} style={{backgroundColor: "#FFFFFF", color: "#000000"}} name='cardholderName'
								       required
								       error={errors.cardholderName && touched.cardholderName ? errors.cardholderName : null}/>

								<DialogContentText style={{color: "#FFFFFF"}}>Number:</DialogContentText>
								<Field as={Input} style={{backgroundColor: "#FFFFFF", color: "#000000"}} name='number'
								       required
								       error={errors.number && touched.number ? errors.number : null}/>

								<DialogContentText style={{color: "#FFFFFF"}}>Expiration Date:</DialogContentText>
								<Field as={Input} style={{backgroundColor: "#FFFFFF", color: "#000000"}} name='expDate'
								       required
								       error={errors.expDate && touched.expDate ? errors.expDate : null}/>

								<DialogContentText style={{color: "#FFFFFF"}}>CVC Code:</DialogContentText>
								<Field as={Input} style={{backgroundColor: "#FFFFFF", color: "#000000"}} name='cvcCode'
								       required
								       error={errors.cvcCode && touched.cvcCode ? errors.cvcCode : null}/>

								<div className="row">
									<Button type='submit' disabled={isSubmitting}
											color="primary">Save Credit Card</Button>
								</div>
							</Form>
						)}
					</Formik>
				</Row>
			</Container>
		</CreditCardModalStyled>
	);
}));
