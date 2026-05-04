import React from 'react';
import { Text, StyleSheet } from 'react-native';

import { Portal } from 'react-native-paper';

import { RadioButton } from '@components/RadioButton/RadioButton';
import { ThemeColors } from '@theme/types';
import { useAppSettings } from '@hooks/persisted';
import { getString } from '@strings/translations';
import { Modal } from '@components';
import { SwipeAction } from '@hooks/persisted/useSettings';
import { StringMap } from '@strings/types';

interface SwipeActionModalProps {
  actionType: 'left' | 'right';
  currentAction: SwipeAction;
  modalVisible: boolean;
  hideModal: () => void;
  theme: ThemeColors;
}

const swipeActionList: { value: SwipeAction; label: string }[] = [
  { value: 'disabled', label: 'swipeActionDisabled' },
  { value: 'bookmark', label: 'swipeActionBookmark' },
  { value: 'markAsRead', label: 'swipeActionMarkAsRead' },
  { value: 'download', label: 'swipeActionDownload' },
];

const SwipeActionModal: React.FC<SwipeActionModalProps> = ({
  theme,
  actionType,
  currentAction,
  hideModal,
  modalVisible,
}) => {
  const { setAppSettings } = useAppSettings();

  return (
    <Portal>
      <Modal visible={modalVisible} onDismiss={hideModal}>
        <Text style={[styles.modalHeader, { color: theme.onSurface }]}>
          {getString(
            actionType === 'left' ? 'swipeActionLeft' : 'swipeActionRight',
          )}
        </Text>
        {swipeActionList.map(action => (
          <RadioButton
            key={action.value}
            status={currentAction === action.value}
            onPress={() => {
              if (actionType === 'left') {
                setAppSettings({ swipeActionLeft: action.value });
              } else {
                setAppSettings({ swipeActionRight: action.value });
              }
              hideModal();
            }}
            label={getString(action.label as keyof StringMap)}
            theme={theme}
          />
        ))}
      </Modal>
    </Portal>
  );
};

export default SwipeActionModal;

const styles = StyleSheet.create({
  modalHeader: {
    fontSize: 24,
    marginBottom: 10,
  },
});
